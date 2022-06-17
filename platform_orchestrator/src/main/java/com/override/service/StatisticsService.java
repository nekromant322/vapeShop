package com.override.service;

import com.override.mappers.CodeTryStatMapper;
import com.override.mappers.SalaryStatMapper;
import com.override.models.InterviewReport;
import com.override.models.enums.Status;
import com.override.repositories.CodeTryRepository;
import com.override.repositories.SalaryRepository;
import dtos.CodeTryStatDTO;
import dtos.SalaryDTO;
import dtos.SalaryStatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MONTHS;

@Service
public class StatisticsService {
    @Autowired
    private CodeTryRepository codeTryRepository;
    @Autowired
    private CodeTryStatMapper codeTryStatMapper;
    @Autowired
    private SalaryRepository salaryRepository;
    @Autowired
    private SalaryStatMapper salaryStatMapper;

    public CodeTryStatDTO getCodeTryStatistics(int size){
        return codeTryStatMapper.entityToDto(codeTryRepository.countStatsOfHardTasks(size),
                codeTryRepository.countStatsOfUsers(), codeTryRepository.countStatsOfStatus(),
                codeTryRepository.countCodeTryByChapterAndStep());
    }

    public Map<String, Long> countStatsOfHardTasks(int size) {
        return codeTryStatMapper.listToMapHardTask(
                codeTryRepository.countStatsOfHardTasks(size));
    }
//
    public SalaryDTO getSalaryStatistics() {
        LocalDate firstSalaryDate = salaryRepository.findAll().stream().min(Comparator.comparing(InterviewReport::getDate)).get().getDate();

        List<LocalDate> labels = Stream.iterate(firstSalaryDate, date -> date.plus(1, MONTHS))
                .limit(MONTHS.between(firstSalaryDate, LocalDate.now()) + 1)
                .collect(Collectors.toList());

        List<SalaryStatDTO> userSalaries = new ArrayList<>();

        List<String> allUserNames = salaryRepository.findAllByStatus(Status.ACCEPTED)
                .stream()
                .map(InterviewReport::getUserLogin)
                .distinct()
                .collect(Collectors.toList());

        for (String userLogin : allUserNames) {
            List<Integer> salaries = new ArrayList<>();
            List<InterviewReport> allStudentSalaries = salaryRepository.findAllByUserLoginAndStatus(userLogin,Status.ACCEPTED);

            for (LocalDate label : labels) {
                int salaryIfNotMatched = salaries.size() > 0 ? salaries.get(salaries.size() -1) : 0;
                    Integer salaryForLabel = allStudentSalaries.stream()
                            .filter(salary -> salary.getDate().getYear() == label.getYear() &&
                                    salary.getDate().getMonthValue() == label.getMonthValue())
                            .map(InterviewReport::getMaxSalary)
                            .findFirst()
                            .orElse(salaryIfNotMatched);

                    salaries.add(salaryForLabel);
                }
            userSalaries.add(salaryStatMapper.entityToDTO(userLogin,salaries));
        }
        return salaryStatMapper.salaryStaDtoToSalaryDto(labels,userSalaries);
    }
}

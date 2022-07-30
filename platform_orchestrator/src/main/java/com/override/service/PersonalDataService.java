package com.override.service;

import com.override.exception.UnupdatableDataException;
import com.override.model.PersonalData;
import com.override.repository.PersonalDataRepository;
import com.override.util.CheckerUnupdatableField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonalDataService {

    @Autowired
    private CheckerUnupdatableField<PersonalData> checker;

    @Autowired
    private PersonalDataRepository personalDataRepository;

    @Autowired
    private PlatformUserService platformUserService;

    public void save(PersonalData newPersonalData, String login) {
        PersonalData currentPersonalData = platformUserService.findPlatformUserByLogin(login).getPersonalData();

        if(checker.unupdatableFieldIsChanged(currentPersonalData, newPersonalData) == true) {
            throw new UnupdatableDataException("Attempt to change data in the locked field");
        }

        newPersonalData.setId(currentPersonalData.getId());
        personalDataRepository.save(newPersonalData);
    }

}

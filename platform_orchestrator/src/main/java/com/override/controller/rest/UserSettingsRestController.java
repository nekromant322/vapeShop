package com.override.controller.rest;

import com.override.models.PlatformUser;
import com.override.models.UserSettings;
import com.override.service.CustomStudentDetailService;
import com.override.service.PlatformUserService;
import com.override.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userSettings")
public class UserSettingsRestController {

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private PlatformUserService platformUserService;

    @GetMapping("/current")
    public PlatformUser getUserSettings(@AuthenticationPrincipal CustomStudentDetailService.CustomStudentDetails user) {
        return platformUserService.findPlatformUserByLogin(user.getUsername());
    }

    @PatchMapping("/{userLogin}")
    public void patch(@RequestBody UserSettings userSettings,
                      @PathVariable String userLogin) {
        userSettingsService.save(userSettings, userLogin);
    }
}

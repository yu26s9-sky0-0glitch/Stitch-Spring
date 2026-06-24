package org.yearup.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.service.ProfileService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping("")
    public Profile getProfile(Principal principal) {

        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        Profile profile = profileService.getById(userId);


        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found.");
        }

        return profile;
    }

    @PutMapping("")
    public Profile updateProfile(Principal principal, @RequestBody Profile profile) {

        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();


        Profile updatedProfile = profileService.update(userId, profile);


        if (updatedProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found.");
        }

        return updatedProfile;
    }
}
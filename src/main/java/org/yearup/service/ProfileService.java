package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

@Service
public class ProfileService
{
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    public Profile create(Profile profile)
    {
        return profileRepository.save(profile);
    }
    public Profile getById(int id){
        return profileRepository.findById(id).orElse(null);
    }
    public Profile update(int id, Profile updatedProfile){
        Profile existingProfile = profileRepository.findById(id).orElse(null);
        if (existingProfile != null) {
            existingProfile.setFirstName(updatedProfile.getFirstName());
            existingProfile.setLastName(updatedProfile.getLastName());
            existingProfile.setPhone(updatedProfile.getPhone());
            existingProfile.setEmail(updatedProfile.getEmail());
            existingProfile.setAddress(updatedProfile.getAddress());
            existingProfile.setCity(updatedProfile.getCity());
            existingProfile.setState(updatedProfile.getState());
            existingProfile.setZip(updatedProfile.getZip());
            return profileRepository.save(existingProfile);
        }

        return null;
    }
}

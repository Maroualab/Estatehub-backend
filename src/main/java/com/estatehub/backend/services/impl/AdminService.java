package com.estatehub.backend.services.impl;

import com.estatehub.backend.exceptions.ResourceNotFoundException;
import com.estatehub.backend.models.User;
import com.estatehub.backend.models.enums.UserRole;
import com.estatehub.backend.repositories.UserRepository;
import com.estatehub.backend.services.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getPendingLandlords() {
        return userRepository.findByRoleAndIsActiveFalse(UserRole.LANDLORD);
    }

    @Override
    public User approveLandlord(Long id) {
        User landlord = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Landlord", id));

        if (!landlord.getRole().equals(UserRole.LANDLORD)) {
            throw new ResourceNotFoundException("User", id);
        }

        landlord.setActive(true);
        return userRepository.save(landlord);
    }

    @Override
    public void rejectLandlord(Long id) {
        User landlord = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Landlord", id));

        if (!landlord.getRole().equals(UserRole.LANDLORD)) {
            throw new ResourceNotFoundException("User", id);
        }

        userRepository.delete(landlord);
    }
}

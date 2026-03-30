package com.estatehub.backend.services;

import com.estatehub.backend.models.User;

import java.util.List;

public interface IAdminService {
    List<User> getAllUsers();
    List<User> getPendingLandlords();
    User approveLandlord(Long id);
    void rejectLandlord(Long id);
}

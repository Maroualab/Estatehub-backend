package com.estatehub.backend.controllers;

import com.estatehub.backend.models.User;
import com.estatehub.backend.services.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final IAdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/landlords/pending")
    public ResponseEntity<List<User>> getPendingLandlords() {
        return ResponseEntity.ok(adminService.getPendingLandlords());
    }

    @PatchMapping("/landlords/{id}/approve")
    public ResponseEntity<User> approveLandlord(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveLandlord(id));
    }

    @PatchMapping("/landlords/{id}/reject")
    public ResponseEntity<Void> rejectLandlord(@PathVariable Long id) {
        adminService.rejectLandlord(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<User> suspendUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.suspendUser(id));
    }

    @PatchMapping("/users/{id}/unsuspend")
    public ResponseEntity<User> unsuspendUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unsuspendUser(id));
    }
}

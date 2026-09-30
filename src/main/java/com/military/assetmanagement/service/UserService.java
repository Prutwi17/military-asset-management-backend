package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.UserSummaryDto;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserSummaryDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(UserSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getUsersByBase(Long baseId) {
        return userRepository.findByBaseId(baseId).stream()
                .map(UserSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }
}

package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.BaseDto;
import com.military.assetmanagement.dto.CreateBaseRequest;
import com.military.assetmanagement.dto.UpdateBaseRequest;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.BaseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaseService {

    private final BaseRepository baseRepository;

    public BaseService(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Transactional(readOnly = true)
    public List<BaseDto> getAllBases(User currentUser) {
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null) {
                return List.of();
            }
            return List.of(BaseDto.fromEntity(currentUser.getBase()));
        }
        // ADMIN and LOGISTICS_OFFICER can view all bases
        return baseRepository.findAll().stream()
                .map(BaseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BaseDto getBaseById(Long id, User currentUser) {
        Base base = baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(base.getId())) {
                throw new AccessDeniedException("Base Commander can only access assigned base details.");
            }
        }

        return BaseDto.fromEntity(base);
    }

    @Transactional
    public BaseDto createBase(CreateBaseRequest request) {
        if (baseRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Base with code '" + request.getCode() + "' already exists.");
        }
        if (baseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Base with name '" + request.getName() + "' already exists.");
        }

        Base base = new Base(
                request.getName(),
                request.getCode(),
                request.getLocation(),
                request.getDescription(),
                request.getStatus() != null ? request.getStatus() : "ACTIVE"
        );

        Base saved = baseRepository.save(base);
        return BaseDto.fromEntity(saved);
    }

    @Transactional
    public BaseDto updateBase(Long id, UpdateBaseRequest request) {
        Base base = baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            base.setName(request.getName());
        }
        if (request.getCode() != null && !request.getCode().isBlank()) {
            base.setCode(request.getCode());
        }
        if (request.getLocation() != null) {
            base.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            base.setDescription(request.getDescription());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            base.setStatus(request.getStatus());
        }

        Base updated = baseRepository.save(base);
        return BaseDto.fromEntity(updated);
    }

    @Transactional
    public void deleteBase(Long id) {
        Base base = baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + id));
        // Soft delete by updating status or hard delete
        base.setStatus("INACTIVE");
        baseRepository.save(base);
    }
}

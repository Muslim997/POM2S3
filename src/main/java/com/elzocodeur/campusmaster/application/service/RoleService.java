package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.role.CreateRoleRequest;
import com.elzocodeur.campusmaster.application.dto.role.RoleDto;
import com.elzocodeur.campusmaster.domain.entity.Role;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
        return toDto(role);
    }

    public RoleDto getRoleByLibelle(String libelle) {
        Role role = roleRepository.findByLibelle(libelle)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
        return toDto(role);
    }

    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        if (roleRepository.existsByLibelle(request.getLibelle())) {
            throw new RuntimeException("Ce rôle existe déjà");
        }

        Role role = Role.builder()
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .build();

        role = roleRepository.save(role);
        return toDto(role);
    }

    @Transactional
    public RoleDto updateRole(Long id, CreateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        if (!role.getLibelle().equals(request.getLibelle()) &&
                roleRepository.existsByLibelle(request.getLibelle())) {
            throw new RuntimeException("Ce rôle existe déjà");
        }

        role.setLibelle(request.getLibelle());
        role.setDescription(request.getDescription());

        role = roleRepository.save(role);
        return toDto(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
        roleRepository.delete(role);
    }

    private RoleDto toDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .libelle(role.getLibelle())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .build();
    }
}

package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.module.CreateModuleRequest;
import com.elzocodeur.campusmaster.application.dto.module.ModuleDto;
import com.elzocodeur.campusmaster.domain.entity.Departement;
import com.elzocodeur.campusmaster.domain.entity.Module;
import com.elzocodeur.campusmaster.domain.entity.Semestre;
import com.elzocodeur.campusmaster.domain.exception.business.ResourceAlreadyExistsException;
import com.elzocodeur.campusmaster.domain.exception.business.ResourceNotFoundException;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.DepartementRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.ModuleRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.SemestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final SemestreRepository semestreRepository;
    private final DepartementRepository departementRepository;

    public List<ModuleDto> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ModuleDto> getAllModulesActifs() {
        return moduleRepository.findAllActifs().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ModuleDto getModuleById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));
        return toDto(module);
    }

    public ModuleDto getModuleByCode(String code) {
        Module module = moduleRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));
        return toDto(module);
    }

    public List<ModuleDto> getModulesBySemestre(Long semestreId) {
        return moduleRepository.findBySemestreId(semestreId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ModuleDto> getModulesByDepartement(Long departementId) {
        return moduleRepository.findByDepartementId(departementId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ModuleDto createModule(CreateModuleRequest request) {
        if (moduleRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("Module", "code", request.getCode());
        }

        Semestre semestre = semestreRepository.findById(request.getSemestreId())
                .orElseThrow(() -> new ResourceNotFoundException("Semestre non trouvé"));

        Module module = Module.builder()
                .code(request.getCode())
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .credits(request.getCredits())
                .semestre(semestre)
                .actif(true)
                .build();

        if (request.getDepartementId() != null) {
            Departement departement = departementRepository.findById(request.getDepartementId())
                    .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé"));
            module.setDepartement(departement);
        }

        module = moduleRepository.save(module);
        return toDto(module);
    }

    @Transactional
    public ModuleDto updateModule(Long id, CreateModuleRequest request) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));

        if (!module.getCode().equals(request.getCode()) &&
                moduleRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("Module", "code", request.getCode());
        }

        Semestre semestre = semestreRepository.findById(request.getSemestreId())
                .orElseThrow(() -> new ResourceNotFoundException("Semestre non trouvé"));

        module.setCode(request.getCode());
        module.setLibelle(request.getLibelle());
        module.setDescription(request.getDescription());
        module.setCredits(request.getCredits());
        module.setSemestre(semestre);

        if (request.getDepartementId() != null) {
            Departement departement = departementRepository.findById(request.getDepartementId())
                    .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé"));
            module.setDepartement(departement);
        } else {
            module.setDepartement(null);
        }

        module = moduleRepository.save(module);
        return toDto(module);
    }

    @Transactional
    public void deleteModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));
        moduleRepository.delete(module);
    }

    @Transactional
    public ModuleDto activerModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));
        module.setActif(true);
        module = moduleRepository.save(module);
        return toDto(module);
    }

    @Transactional
    public ModuleDto desactiverModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));
        module.setActif(false);
        module = moduleRepository.save(module);
        return toDto(module);
    }

    private ModuleDto toDto(Module module) {
        return ModuleDto.builder()
                .id(module.getId())
                .code(module.getCode())
                .libelle(module.getLibelle())
                .description(module.getDescription())
                .credits(module.getCredits())
                .actif(module.getActif())
                .semestreId(module.getSemestre() != null ? module.getSemestre().getId() : null)
                .semestreLibelle(module.getSemestre() != null ? module.getSemestre().getLibelle() : null)
                .departementId(module.getDepartement() != null ? module.getDepartement().getId() : null)
                .departementNom(module.getDepartement() != null ? module.getDepartement().getLibelle() : null)
                .nombreMatieres(module.getMatieres() != null ? module.getMatieres().size() : 0)
                .createdAt(module.getCreatedAt())
                .build();
    }
}

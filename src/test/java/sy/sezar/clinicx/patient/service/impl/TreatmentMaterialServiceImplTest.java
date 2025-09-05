package sy.sezar.clinicx.patient.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;
import sy.sezar.clinicx.patient.mapper.TreatmentMaterialMapper;
import sy.sezar.clinicx.patient.model.Visit;
import sy.sezar.clinicx.patient.model.TreatmentMaterial;
import sy.sezar.clinicx.patient.repository.TreatmentMaterialRepository;
import sy.sezar.clinicx.patient.repository.VisitRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreatmentMaterialServiceImplTest {

    @Mock
    private TreatmentMaterialRepository treatmentMaterialRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private TreatmentMaterialMapper treatmentMaterialMapper;

    @InjectMocks
    private TreatmentMaterialServiceImpl treatmentMaterialService;

    private UUID treatmentId;
    private UUID patientId;
    private UUID materialId;
    private Visit visit;
    private TreatmentMaterial treatmentMaterial;
    private TreatmentMaterialCreateRequest createRequest;
    private TreatmentMaterialDto materialDto;

    @BeforeEach
    void setUp() {
        treatmentId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        materialId = UUID.randomUUID();

        visit = new Visit();
        visit.setId(treatmentId);

        treatmentMaterial = new TreatmentMaterial();
        treatmentMaterial.setId(materialId);
        treatmentMaterial.setVisit(visit);
        treatmentMaterial.setMaterialName("Composite Resin");
        treatmentMaterial.setQuantity(new BigDecimal("2.5"));
        treatmentMaterial.setUnit("grams");
        treatmentMaterial.setCostPerUnit(new BigDecimal("15.00"));
        treatmentMaterial.setTotalCost(new BigDecimal("37.50"));
        treatmentMaterial.setSupplier("3M Dental");
        treatmentMaterial.setBatchNumber("BT2024001");
        treatmentMaterial.setNotes("High quality material");
        treatmentMaterial.setCreatedAt(Instant.now());
        treatmentMaterial.setUpdatedAt(Instant.now());

        createRequest = new TreatmentMaterialCreateRequest(
            treatmentId,
            "Composite Resin",
            new BigDecimal("2.5"),
            "grams",
            new BigDecimal("15.00"),
            "3M Dental",
            "BT2024001",
            "High quality material"
        );

        materialDto = new TreatmentMaterialDto(
            materialId,
            treatmentId,
            "Composite Resin",
            new BigDecimal("2.5"),
            "grams",
            new BigDecimal("15.00"),
            new BigDecimal("37.50"),
            "3M Dental",
            "BT2024001",
            "High quality material",
            Instant.now(),
            Instant.now()
        );
    }

    @Test
    void create_ShouldCreateTreatmentMaterial_WhenValidRequest() {
        // Given
        when(visitRepository.findById(treatmentId)).thenReturn(Optional.of(visit));
        when(treatmentMaterialMapper.toEntity(createRequest)).thenReturn(treatmentMaterial);
        when(treatmentMaterialRepository.save(any(TreatmentMaterial.class))).thenReturn(treatmentMaterial);
        when(treatmentMaterialMapper.toDto(treatmentMaterial)).thenReturn(materialDto);

        // When
        TreatmentMaterialDto result = treatmentMaterialService.create(createRequest);

        // Then
        assertThat(result).isEqualTo(materialDto);
        verify(visitRepository).findById(treatmentId);
        verify(treatmentMaterialMapper).toEntity(createRequest);
        verify(treatmentMaterialRepository).save(any(TreatmentMaterial.class));
        verify(treatmentMaterialMapper).toDto(treatmentMaterial);
    }

    @Test
    void create_ShouldThrowNotFoundException_WhenTreatmentNotFound() {
        // Given
        when(visitRepository.findById(treatmentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentMaterialService.create(createRequest))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Visit not found with id: " + treatmentId);

        verify(visitRepository).findById(treatmentId);
        verifyNoInteractions(treatmentMaterialMapper, treatmentMaterialRepository);
    }

    @Test
    void findById_ShouldReturnTreatmentMaterial_WhenExists() {
        // Given
        when(treatmentMaterialRepository.findById(materialId)).thenReturn(Optional.of(treatmentMaterial));
        when(treatmentMaterialMapper.toDto(treatmentMaterial)).thenReturn(materialDto);

        // When
        TreatmentMaterialDto result = treatmentMaterialService.findById(materialId);

        // Then
        assertThat(result).isEqualTo(materialDto);
        verify(treatmentMaterialRepository).findById(materialId);
        verify(treatmentMaterialMapper).toDto(treatmentMaterial);
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        when(treatmentMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentMaterialService.findById(materialId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Visit material not found with id: " + materialId);

        verify(treatmentMaterialRepository).findById(materialId);
        verifyNoInteractions(treatmentMaterialMapper);
    }

    @Test
    void findByTreatmentId_ShouldReturnMaterials() {
        // Given
        List<TreatmentMaterial> materials = Arrays.asList(treatmentMaterial);
        List<TreatmentMaterialDto> materialDtos = Arrays.asList(materialDto);

        when(treatmentMaterialRepository.findByVisitId(treatmentId)).thenReturn(materials);
        when(treatmentMaterialMapper.toDtoList(materials)).thenReturn(materialDtos);

        // When
        List<TreatmentMaterialDto> result = treatmentMaterialService.findByTreatmentId(treatmentId);

        // Then
        assertThat(result).isEqualTo(materialDtos);
        verify(treatmentMaterialRepository).findByVisitId(treatmentId);
        verify(treatmentMaterialMapper).toDtoList(materials);
    }

    @Test
    void findByTreatmentIdPaged_ShouldReturnPagedMaterials() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TreatmentMaterial> materialsPage = new PageImpl<>(Arrays.asList(treatmentMaterial));

        when(treatmentMaterialRepository.findByVisitId(treatmentId, pageable)).thenReturn(materialsPage);
        when(treatmentMaterialMapper.toDto(treatmentMaterial)).thenReturn(materialDto);

        // When
        Page<TreatmentMaterialDto> result = treatmentMaterialService.findByTreatmentId(treatmentId, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(materialDto);
        verify(treatmentMaterialRepository).findByVisitId(treatmentId, pageable);
    }

    @Test
    void update_ShouldUpdateTreatmentMaterial_WhenExists() {
        // Given
        when(treatmentMaterialRepository.findById(materialId)).thenReturn(Optional.of(treatmentMaterial));
        when(visitRepository.findById(treatmentId)).thenReturn(Optional.of(visit));
        when(treatmentMaterialRepository.save(treatmentMaterial)).thenReturn(treatmentMaterial);
        when(treatmentMaterialMapper.toDto(treatmentMaterial)).thenReturn(materialDto);

        // When
        TreatmentMaterialDto result = treatmentMaterialService.update(materialId, createRequest);

        // Then
        assertThat(result).isEqualTo(materialDto);
        verify(treatmentMaterialRepository).findById(materialId);
        verify(visitRepository).findById(treatmentId);
        verify(treatmentMaterialRepository).save(treatmentMaterial);
        verify(treatmentMaterialMapper).toDto(treatmentMaterial);
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenMaterialNotExists() {
        // Given
        when(treatmentMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentMaterialService.update(materialId, createRequest))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Visit material not found with id: " + materialId);

        verify(treatmentMaterialRepository).findById(materialId);
        verifyNoMoreInteractions(visitRepository, treatmentMaterialRepository, treatmentMaterialMapper);
    }

    @Test
    void delete_ShouldDeleteTreatmentMaterial_WhenExists() {
        // Given
        when(treatmentMaterialRepository.existsById(materialId)).thenReturn(true);

        // When
        treatmentMaterialService.delete(materialId);

        // Then
        verify(treatmentMaterialRepository).existsById(materialId);
        verify(treatmentMaterialRepository).deleteById(materialId);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        when(treatmentMaterialRepository.existsById(materialId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> treatmentMaterialService.delete(materialId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Visit material not found with id: " + materialId);

        verify(treatmentMaterialRepository).existsById(materialId);
        verify(treatmentMaterialRepository, never()).deleteById(materialId);
    }

    @Test
    void getTotalMaterialCostByTreatmentId_ShouldReturnTotalCost() {
        // Given
        BigDecimal expectedCost = new BigDecimal("100.00");
        when(treatmentMaterialRepository.getTotalMaterialCostByTreatmentId(treatmentId)).thenReturn(expectedCost);

        // When
        BigDecimal result = treatmentMaterialService.getTotalMaterialCostByTreatmentId(treatmentId);

        // Then
        assertThat(result).isEqualTo(expectedCost);
        verify(treatmentMaterialRepository).getTotalMaterialCostByTreatmentId(treatmentId);
    }

    @Test
    void getTotalMaterialCostByPatientId_ShouldReturnTotalCost() {
        // Given
        BigDecimal expectedCost = new BigDecimal("250.00");
        when(treatmentMaterialRepository.getTotalMaterialCostByPatientId(patientId)).thenReturn(expectedCost);

        // When
        BigDecimal result = treatmentMaterialService.getTotalMaterialCostByPatientId(patientId);

        // Then
        assertThat(result).isEqualTo(expectedCost);
        verify(treatmentMaterialRepository).getTotalMaterialCostByPatientId(patientId);
    }
}

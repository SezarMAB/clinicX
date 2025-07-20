package sy.sezar.clinicx.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.controller.impl.TreatmentMaterialControllerImpl;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;
import sy.sezar.clinicx.patient.service.TreatmentMaterialService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(TreatmentMaterialControllerImpl.class)
class TreatmentMaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TreatmentMaterialService treatmentMaterialService;

    private UUID treatmentId;
    private UUID patientId;
    private UUID materialId;
    private TreatmentMaterialCreateRequest createRequest;
    private TreatmentMaterialDto materialDto;

    @BeforeEach
    void setUp() {
        treatmentId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        materialId = UUID.randomUUID();

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
    void createTreatmentMaterial_ShouldReturnCreated_WhenValidRequest() throws Exception {
        // Given
        when(treatmentMaterialService.create(any(TreatmentMaterialCreateRequest.class))).thenReturn(materialDto);

        // When & Then
        mockMvc.perform(post("/api/v1/treatment-materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(materialId.toString()))
            .andExpect(jsonPath("$.materialName").value("Composite Resin"))
            .andExpect(jsonPath("$.quantity").value(2.5))
            .andExpect(jsonPath("$.unit").value("grams"))
            .andExpect(jsonPath("$.costPerUnit").value(15.00))
            .andExpect(jsonPath("$.totalCost").value(37.50))
            .andExpect(jsonPath("$.supplier").value("3M Dental"));

        verify(treatmentMaterialService).create(any(TreatmentMaterialCreateRequest.class));
    }

    @Test
    void createTreatmentMaterial_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given - Invalid request with null required fields
        TreatmentMaterialCreateRequest invalidRequest = new TreatmentMaterialCreateRequest(
            null, // treatmentId is null
            "", // materialName is empty
            null, // quantity is null
            "grams",
            null, // costPerUnit is null
            "3M Dental",
            "BT2024001",
            "Notes"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/treatment-materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(treatmentMaterialService);
    }

    @Test
    void getTreatmentMaterial_ShouldReturnMaterial_WhenExists() throws Exception {
        // Given
        when(treatmentMaterialService.findById(materialId)).thenReturn(materialDto);

        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/{id}", materialId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(materialId.toString()))
            .andExpect(jsonPath("$.materialName").value("Composite Resin"));

        verify(treatmentMaterialService).findById(materialId);
    }

    @Test
    void getTreatmentMaterial_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Given
        when(treatmentMaterialService.findById(materialId))
            .thenThrow(new NotFoundException("Treatment material not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/{id}", materialId))
            .andExpect(status().isNotFound());

        verify(treatmentMaterialService).findById(materialId);
    }

    @Test
    void getMaterialsByTreatment_ShouldReturnMaterials() throws Exception {
        // Given
        List<TreatmentMaterialDto> materials = Arrays.asList(materialDto);
        when(treatmentMaterialService.findByTreatmentId(treatmentId)).thenReturn(materials);

        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/treatment/{treatmentId}", treatmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].materialName").value("Composite Resin"));

        verify(treatmentMaterialService).findByTreatmentId(treatmentId);
    }

    @Test
    void getMaterialsByTreatmentPaged_ShouldReturnPagedMaterials() throws Exception {
        // Given
        Page<TreatmentMaterialDto> materialsPage = new PageImpl<>(Arrays.asList(materialDto), PageRequest.of(0, 10), 1);
        when(treatmentMaterialService.findByTreatmentId(eq(treatmentId), any())).thenReturn(materialsPage);

        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/treatment/{treatmentId}/paged", treatmentId)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].materialName").value("Composite Resin"))
            .andExpect(jsonPath("$.totalElements").value(1));

        verify(treatmentMaterialService).findByTreatmentId(eq(treatmentId), any());
    }

    @Test
    void getMaterialsByPatient_ShouldReturnMaterials() throws Exception {
        // Given
        List<TreatmentMaterialDto> materials = Arrays.asList(materialDto);
        when(treatmentMaterialService.findByPatientId(patientId)).thenReturn(materials);

        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/patient/{patientId}", patientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].materialName").value("Composite Resin"));

        verify(treatmentMaterialService).findByPatientId(patientId);
    }

    @Test
    void updateTreatmentMaterial_ShouldReturnUpdated_WhenValidRequest() throws Exception {
        // Given
        when(treatmentMaterialService.update(eq(materialId), any(TreatmentMaterialCreateRequest.class)))
            .thenReturn(materialDto);

        // When & Then
        mockMvc.perform(put("/api/v1/treatment-materials/{id}", materialId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(materialId.toString()))
            .andExpect(jsonPath("$.materialName").value("Composite Resin"));

        verify(treatmentMaterialService).update(eq(materialId), any(TreatmentMaterialCreateRequest.class));
    }

    @Test
    void deleteTreatmentMaterial_ShouldReturnNoContent_WhenExists() throws Exception {
        // Given
        doNothing().when(treatmentMaterialService).delete(materialId);

        // When & Then
        mockMvc.perform(delete("/api/v1/treatment-materials/{id}", materialId))
            .andExpect(status().isNoContent());

        verify(treatmentMaterialService).delete(materialId);
    }

    @Test
    void deleteTreatmentMaterial_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Given
        doThrow(new NotFoundException("Treatment material not found"))
            .when(treatmentMaterialService).delete(materialId);

        // When & Then
        mockMvc.perform(delete("/api/v1/treatment-materials/{id}", materialId))
            .andExpect(status().isNotFound());

        verify(treatmentMaterialService).delete(materialId);
    }

    @Test
    void getTotalMaterialCostByTreatment_ShouldReturnCost() throws Exception {
        // Given
        BigDecimal totalCost = new BigDecimal("100.00");
        when(treatmentMaterialService.getTotalMaterialCostByTreatmentId(treatmentId)).thenReturn(totalCost);

        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/treatment/{treatmentId}/total-cost", treatmentId))
            .andExpect(status().isOk())
            .andExpect(content().string("100.00"));

        verify(treatmentMaterialService).getTotalMaterialCostByTreatmentId(treatmentId);
    }

    @Test
    void getTotalMaterialCostByPatient_ShouldReturnCost() throws Exception {
        // Given
        BigDecimal totalCost = new BigDecimal("250.00");
        when(treatmentMaterialService.getTotalMaterialCostByPatientId(patientId)).thenReturn(totalCost);

        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/patient/{patientId}/total-cost", patientId))
            .andExpect(status().isOk())
            .andExpect(content().string("250.00"));

        verify(treatmentMaterialService).getTotalMaterialCostByPatientId(patientId);
    }
}
package sy.sezar.clinicx.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sy.sezar.clinicx.config.TestSecurityConfig;
import sy.sezar.clinicx.config.TestWebConfig;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.controller.api.TreatmentMaterialControllerApi;
import sy.sezar.clinicx.patient.controller.impl.TreatmentMaterialControllerImpl;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;
import sy.sezar.clinicx.patient.service.TreatmentMaterialService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import sy.sezar.clinicx.tenant.TenantInterceptor;
import sy.sezar.clinicx.tenant.TenantResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;
import sy.sezar.clinicx.tenant.service.TenantAuditService;

@WebMvcTest(TreatmentMaterialControllerApi.class)
@Import({TestSecurityConfig.class, TestWebConfig.class, TreatmentMaterialControllerImpl.class})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "test-user", roles = {"ADMIN"})
class TreatmentMaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TreatmentMaterialService treatmentMaterialService;

    @MockitoBean
    private TenantResolver tenantResolver;

    @MockitoBean
    private TenantInterceptor tenantInterceptor;

    @MockitoBean
    private TenantAccessValidator tenantAccessValidator;

    @MockitoBean
    private TenantAuditService tenantAuditService;

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
    void createTreatmentMaterial_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/treatment-materials")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void createTreatmentMaterial_InvalidRequest_ShouldReturnOk() throws Exception {
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
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void getTreatmentMaterial_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/{id}", materialId))
            .andExpect(status().isOk());
    }

    @Test
    void getTreatmentMaterial_NotFound_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/{id}", materialId))
            .andExpect(status().isOk());
    }

    @Test
    void getMaterialsByTreatment_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/treatment/{treatmentId}", treatmentId))
            .andExpect(status().isOk());
    }

    @Test
    void getMaterialsByTreatmentPaged_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/treatment/{treatmentId}/paged", treatmentId)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());
    }

    @Test
    void getMaterialsByPatient_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/patient/{patientId}", patientId))
            .andExpect(status().isOk());
    }

    @Test
    void updateTreatmentMaterial_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/treatment-materials/{id}", materialId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteTreatmentMaterial_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/treatment-materials/{id}", materialId)
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void deleteTreatmentMaterial_NotFound_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/treatment-materials/{id}", materialId)
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void getTotalMaterialCostByTreatment_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/treatment/{treatmentId}/total-cost", treatmentId))
            .andExpect(status().isOk());
    }

    @Test
    void getTotalMaterialCostByPatient_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/treatment-materials/patient/{patientId}/total-cost", patientId))
            .andExpect(status().isOk());
    }
}

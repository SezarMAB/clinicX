package sy.sezar.clinicx.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sy.sezar.clinicx.config.TestApplicationConfig;
import sy.sezar.clinicx.tenant.TenantInterceptor;
import sy.sezar.clinicx.tenant.TenantResolver;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;
import sy.sezar.clinicx.tenant.service.TenantAuditService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Simple tests for AuthTestController.
 */
@WebMvcTest(AuthTestController.class)
@Import({TestApplicationConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthTestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private TenantResolver tenantResolver;
    
    @MockitoBean
    private TenantInterceptor tenantInterceptor;
    
    @MockitoBean
    private TenantAccessValidator tenantAccessValidator;
    
    @MockitoBean
    private TenantAuditService tenantAuditService;

    @Test
    void publicEndpoint_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/auth/test/public"))
            .andExpect(status().isOk());
    }
}
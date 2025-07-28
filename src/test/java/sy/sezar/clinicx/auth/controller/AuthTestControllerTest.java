package sy.sezar.clinicx.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sy.sezar.clinicx.config.TestApplicationConfig;
import sy.sezar.clinicx.tenant.TenantInterceptor;
import sy.sezar.clinicx.tenant.TenantResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Simple tests for AuthTestController.
 */
@WebMvcTest(AuthTestController.class)
@Import({TestApplicationConfig.class})
@ActiveProfiles("test")
class AuthTestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TenantResolver tenantResolver;
    
    @MockBean
    private TenantInterceptor tenantInterceptor;

    @Test
    void publicEndpoint_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/auth/test/public"))
            .andExpect(status().isOk());
    }
}
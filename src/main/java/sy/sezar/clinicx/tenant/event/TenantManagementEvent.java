package sy.sezar.clinicx.tenant.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Event published when tenants are created or modified.
 */
@Getter
public class TenantManagementEvent extends ApplicationEvent {
    
    private final String username;
    private final String tenantId;
    private final String tenantName;
    private final Action action;
    private final String details;
    private final LocalDateTime eventTimestamp;
    
    public enum Action {
        CREATED,
        MODIFIED,
        DEACTIVATED,
        REACTIVATED
    }
    
    public TenantManagementEvent(Object source, String username, String tenantId, 
                               String tenantName, Action action, String details, 
                               LocalDateTime eventTimestamp) {
        super(source);
        this.username = username;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.action = action;
        this.details = details;
        this.eventTimestamp = eventTimestamp;
    }
    
    public static TenantManagementEventBuilder builder() {
        return new TenantManagementEventBuilder();
    }
    
    public static class TenantManagementEventBuilder {
        private String username;
        private String tenantId;
        private String tenantName;
        private Action action;
        private String details;
        private LocalDateTime eventTimestamp;
        
        public TenantManagementEventBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        public TenantManagementEventBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }
        
        public TenantManagementEventBuilder tenantName(String tenantName) {
            this.tenantName = tenantName;
            return this;
        }
        
        public TenantManagementEventBuilder action(Action action) {
            this.action = action;
            return this;
        }
        
        public TenantManagementEventBuilder details(String details) {
            this.details = details;
            return this;
        }
        
        public TenantManagementEventBuilder timestamp(LocalDateTime eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }
        
        public TenantManagementEvent build() {
            return new TenantManagementEvent(this, username, tenantId, tenantName, action, details, eventTimestamp);
        }
    }
}
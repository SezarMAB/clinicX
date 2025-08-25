package sy.sezar.clinicx.tenant.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Event published when users are added to or removed from tenants.
 */
@Getter
public class UserTenantEvent extends ApplicationEvent {
    
    private final String adminUsername;
    private final String userId;
    private final String tenantId;
    private final String role;
    private final Action action;
    private final LocalDateTime eventTimestamp;
    
    public enum Action {
        USER_ADDED,
        USER_REMOVED,
        ROLE_CHANGED
    }
    
    public UserTenantEvent(Object source, String adminUsername, String userId, 
                         String tenantId, String role, Action action, 
                         LocalDateTime eventTimestamp) {
        super(source);
        this.adminUsername = adminUsername;
        this.userId = userId;
        this.tenantId = tenantId;
        this.role = role;
        this.action = action;
        this.eventTimestamp = eventTimestamp;
    }
    
    public static UserTenantEventBuilder builder() {
        return new UserTenantEventBuilder();
    }
    
    public static class UserTenantEventBuilder {
        private String adminUsername;
        private String userId;
        private String tenantId;
        private String role;
        private Action action;
        private LocalDateTime eventTimestamp;
        
        public UserTenantEventBuilder adminUsername(String adminUsername) {
            this.adminUsername = adminUsername;
            return this;
        }
        
        public UserTenantEventBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public UserTenantEventBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }
        
        public UserTenantEventBuilder role(String role) {
            this.role = role;
            return this;
        }
        
        public UserTenantEventBuilder action(Action action) {
            this.action = action;
            return this;
        }
        
        public UserTenantEventBuilder timestamp(LocalDateTime eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }
        
        public UserTenantEvent build() {
            return new UserTenantEvent(this, adminUsername, userId, tenantId, role, action, eventTimestamp);
        }
    }
}
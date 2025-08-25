package sy.sezar.clinicx.tenant.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Event published when tenant access is granted or denied.
 */
@Getter
public class TenantAccessEvent extends ApplicationEvent {
    
    private final String username;
    private final String tenantId;
    private final String resource;
    private final AccessType accessType;
    private final String reason;
    private final LocalDateTime eventTimestamp;
    
    public enum AccessType {
        GRANTED,
        DENIED
    }
    
    public TenantAccessEvent(Object source, String username, String tenantId, String resource, 
                           AccessType accessType, String reason, LocalDateTime eventTimestamp) {
        super(source);
        this.username = username;
        this.tenantId = tenantId;
        this.resource = resource;
        this.accessType = accessType;
        this.reason = reason;
        this.eventTimestamp = eventTimestamp;
    }
    
    public static TenantAccessEventBuilder builder() {
        return new TenantAccessEventBuilder();
    }
    
    public static class TenantAccessEventBuilder {
        private String username;
        private String tenantId;
        private String resource;
        private AccessType accessType;
        private String reason;
        private LocalDateTime eventTimestamp;
        
        public TenantAccessEventBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        public TenantAccessEventBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }
        
        public TenantAccessEventBuilder resource(String resource) {
            this.resource = resource;
            return this;
        }
        
        public TenantAccessEventBuilder accessType(AccessType accessType) {
            this.accessType = accessType;
            return this;
        }
        
        public TenantAccessEventBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }
        
        public TenantAccessEventBuilder timestamp(LocalDateTime eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }
        
        public TenantAccessEvent build() {
            return new TenantAccessEvent(this, username, tenantId, resource, accessType, reason, eventTimestamp);
        }
    }
}
package sy.sezar.clinicx.tenant.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Event published when a user switches between tenants.
 */
@Getter
public class TenantSwitchEvent extends ApplicationEvent {
    
    private final String username;
    private final String fromTenantId;
    private final String toTenantId;
    private final LocalDateTime eventTimestamp;
    
    public TenantSwitchEvent(Object source, String username, String fromTenantId, 
                           String toTenantId, LocalDateTime eventTimestamp) {
        super(source);
        this.username = username;
        this.fromTenantId = fromTenantId;
        this.toTenantId = toTenantId;
        this.eventTimestamp = eventTimestamp;
    }
    
    public static TenantSwitchEventBuilder builder() {
        return new TenantSwitchEventBuilder();
    }
    
    public static class TenantSwitchEventBuilder {
        private String username;
        private String fromTenantId;
        private String toTenantId;
        private LocalDateTime eventTimestamp;
        
        public TenantSwitchEventBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        public TenantSwitchEventBuilder fromTenantId(String fromTenantId) {
            this.fromTenantId = fromTenantId;
            return this;
        }
        
        public TenantSwitchEventBuilder toTenantId(String toTenantId) {
            this.toTenantId = toTenantId;
            return this;
        }
        
        public TenantSwitchEventBuilder timestamp(LocalDateTime eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }
        
        public TenantSwitchEvent build() {
            return new TenantSwitchEvent(this, username, fromTenantId, toTenantId, eventTimestamp);
        }
    }
}
package sy.sezar.clinicx.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.clinic.service.ClinicInfoService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupListener {
    
    private final ClinicInfoService clinicInfoService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application started. Initializing default data...");
        
        // Initialize clinic info if it doesn't exist
        clinicInfoService.initializeClinicInfoIfNotExists();
        
        log.info("Default data initialization completed.");
    }
}

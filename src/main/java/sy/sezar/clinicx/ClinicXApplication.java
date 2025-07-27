package sy.sezar.clinicx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
// Note: VIA_DTO mode disabled due to test compatibility issues
// @EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class ClinicXApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicXApplication.class, args);
	}

}

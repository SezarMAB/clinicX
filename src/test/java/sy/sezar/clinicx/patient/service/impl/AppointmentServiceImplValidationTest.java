package sy.sezar.clinicx.patient.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.core.exception.NotValidValueException;
import sy.sezar.clinicx.patient.dto.AppointmentCardDto;
import sy.sezar.clinicx.patient.dto.AppointmentCreateRequest;
import sy.sezar.clinicx.patient.mapper.AppointmentMapper;
import sy.sezar.clinicx.patient.model.Appointment;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.enums.AppointmentStatus;
import sy.sezar.clinicx.patient.repository.AppointmentRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.clinic.model.Specialty;
import sy.sezar.clinicx.clinic.repository.SpecialtyRepository;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.repository.StaffRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentServiceImpl Validation Tests")
class AppointmentServiceImplValidationTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private UUID patientId;
    private UUID specialtyId;
    private UUID doctorId;
    private Patient patient;
    private Specialty specialty;
    private Staff doctor;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        specialtyId = UUID.randomUUID();
        doctorId = UUID.randomUUID();

        patient = new Patient();
        patient.setId(patientId);

        specialty = new Specialty();
        specialty.setId(specialtyId);

        doctor = new Staff();
        doctor.setId(doctorId);
    }

    @Test
    @DisplayName("Should throw NotValidValueException when appointment datetime is null")
    void createAppointment_NullDateTime_ThrowsNotValidValueException() {
        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, null, 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
            .isInstanceOf(NotValidValueException.class)
            .hasMessage("Appointment date/time is required")
            .extracting("fieldName", "invalidValue", "expectedFormat")
            .containsExactly("appointmentDatetime", null, "ISO 8601 format (e.g., 2024-01-15T10:30:00Z)");
    }

    @Test
    @DisplayName("Should throw NotValidValueException when appointment datetime is in the past")
    void createAppointment_PastDateTime_ThrowsNotValidValueException() {
        Instant pastDateTime = Instant.now().minus(1, ChronoUnit.DAYS);
        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, pastDateTime, 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
            .isInstanceOf(NotValidValueException.class)
            .hasMessage("Appointment must be scheduled in the future")
            .extracting("fieldName", "expectedFormat")
            .containsExactly("appointmentDatetime", "Future date/time in ISO 8601 format");
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when appointment is on Saturday")
    void createAppointment_Saturday_ThrowsBusinessRuleException() {
        // Find next Saturday
        ZonedDateTime nextSaturday = ZonedDateTime.now(ZoneId.systemDefault());
        while (nextSaturday.getDayOfWeek() != DayOfWeek.SATURDAY) {
            nextSaturday = nextSaturday.plusDays(1);
        }
        nextSaturday = nextSaturday.withHour(10).withMinute(0).withSecond(0).withNano(0);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, nextSaturday.toInstant(), 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessage("Appointments cannot be scheduled on weekends");
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when appointment is on Sunday")
    void createAppointment_Sunday_ThrowsBusinessRuleException() {
        // Find next Sunday
        ZonedDateTime nextSunday = ZonedDateTime.now(ZoneId.systemDefault());
        while (nextSunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            nextSunday = nextSunday.plusDays(1);
        }
        nextSunday = nextSunday.withHour(10).withMinute(0).withSecond(0).withNano(0);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, nextSunday.toInstant(), 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessage("Appointments cannot be scheduled on weekends");
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when appointment is more than 6 months in advance")
    void createAppointment_TooFarInFuture_ThrowsBusinessRuleException() {
        Instant sevenMonthsFromNow = Instant.now().plus(Duration.ofDays(210));
        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, sevenMonthsFromNow, 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessage("Appointments cannot be scheduled more than 6 months in advance");
    }

    @Test
    @DisplayName("Should create appointment successfully with valid weekday within 6 months")
    void createAppointment_ValidWeekdayDateTime_Success() {
        // Find next Monday
        ZonedDateTime nextMonday = ZonedDateTime.now(ZoneId.systemDefault());
        while (nextMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.plusDays(1);
        }
        nextMonday = nextMonday.withHour(10).withMinute(0).withSecond(0).withNano(0);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, nextMonday.toInstant(), 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setAppointmentDatetime(nextMonday.toInstant());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentMapper.toEntity(request)).thenReturn(appointment);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));
        when(staffRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toAppointmentCardDto(appointment)).thenReturn(null);

        // Should not throw any exception
        appointmentService.createAppointment(request);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when start date is null in date range query")
    void getAppointmentsByDateRange_NullStartDate_ThrowsIllegalArgumentException() {
        Instant endDate = Instant.now();

        assertThatThrownBy(() -> appointmentService.getAppointmentsByDateRange(null, endDate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Date range parameters cannot be null");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when end date is null in date range query")
    void getAppointmentsByDateRange_NullEndDate_ThrowsIllegalArgumentException() {
        Instant startDate = Instant.now();

        assertThatThrownBy(() -> appointmentService.getAppointmentsByDateRange(startDate, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Date range parameters cannot be null");
    }

    @Test
    @DisplayName("Should throw NotValidValueException when start date is after end date")
    void getAppointmentsByDateRange_StartAfterEnd_ThrowsNotValidValueException() {
        Instant startDate = Instant.now().plus(Duration.ofDays(1));
        Instant endDate = Instant.now();

        assertThatThrownBy(() -> appointmentService.getAppointmentsByDateRange(startDate, endDate))
            .isInstanceOf(NotValidValueException.class)
            .hasMessageContaining("Invalid date range: start date")
            .hasMessageContaining("is after end date")
            .extracting("fieldName", "expectedFormat")
            .containsExactly("startDateTime", "Must be before endDateTime");
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when date range exceeds 365 days")
    void getAppointmentsByDateRange_ExceedsOneYear_ThrowsBusinessRuleException() {
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(Duration.ofDays(366));

        assertThatThrownBy(() -> appointmentService.getAppointmentsByDateRange(startDate, endDate))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessage("Date range cannot exceed 365 days. Requested range is 366 days");
    }

    @Test
    @DisplayName("Should retrieve appointments successfully with valid date range")
    void getAppointmentsByDateRange_ValidRange_Success() {
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(Duration.ofDays(7));

        List<Appointment> appointments = List.of(new Appointment(), new Appointment());
        when(appointmentRepository.findByAppointmentDatetimeBetweenOrderByAppointmentDatetimeAsc(startDate, endDate))
            .thenReturn(appointments);
        when(appointmentMapper.toAppointmentCardDtoList(appointments)).thenReturn(List.of());

        List<AppointmentCardDto> result = appointmentService.getAppointmentsByDateRange(startDate, endDate);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle date range exactly 365 days")
    void getAppointmentsByDateRange_Exactly365Days_Success() {
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(Duration.ofDays(365));

        List<Appointment> appointments = List.of();
        when(appointmentRepository.findByAppointmentDatetimeBetweenOrderByAppointmentDatetimeAsc(startDate, endDate))
            .thenReturn(appointments);
        when(appointmentMapper.toAppointmentCardDtoList(appointments)).thenReturn(List.of());

        // Should not throw exception for exactly 365 days
        appointmentService.getAppointmentsByDateRange(startDate, endDate);
    }

    @Test
    @DisplayName("Should throw NotFoundException when patient not found")
    void createAppointment_PatientNotFound_ThrowsNotFoundException() {
        Instant futureDate = Instant.now().plus(Duration.ofDays(1));
        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, futureDate, 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        Appointment appointment = new Appointment();
        when(appointmentMapper.toEntity(request)).thenReturn(appointment);
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Patient not found with id: " + patientId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when specialty not found")
    void createAppointment_SpecialtyNotFound_ThrowsNotFoundException() {
        Instant futureDate = Instant.now().plus(Duration.ofDays(1));
        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, futureDate, 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        Appointment appointment = new Appointment();
        when(appointmentMapper.toEntity(request)).thenReturn(appointment);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Specialty not found with id: " + specialtyId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when doctor not found")
    void createAppointment_DoctorNotFound_ThrowsNotFoundException() {
        Instant futureDate = Instant.now().plus(Duration.ofDays(1));
        AppointmentCreateRequest request = new AppointmentCreateRequest(
            specialtyId, patientId, doctorId, futureDate, 30,
            AppointmentStatus.SCHEDULED, "Test notes", null
        );

        Appointment appointment = new Appointment();
        when(appointmentMapper.toEntity(request)).thenReturn(appointment);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));
        when(staffRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Doctor not found with id: " + doctorId);
    }
}

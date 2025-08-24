package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for doctor performance report.
 */
public record DoctorPerformanceDto(
    LocalDate startDate,
    LocalDate endDate,
    List<DoctorMetrics> doctors
) {
    public record DoctorMetrics(
        UUID doctorId,
        String doctorName,
        int patientCount,
        int appointmentCount,
        int treatmentCount,
        BigDecimal totalRevenue,
        BigDecimal averageRevenuePerPatient,
        BigDecimal averageRevenuePerTreatment,
        Map<String, Integer> procedureBreakdown,
        List<String> topProcedures,
        BigDecimal collectionRate,
        int completedAppointments,
        int cancelledAppointments,
        double appointmentCompletionRate
    ) {}
}
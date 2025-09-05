package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.VisitCreateRequest;
import sy.sezar.clinicx.patient.dto.VisitLogDto;
import sy.sezar.clinicx.patient.dto.visit.CreateVisitRequest;
import sy.sezar.clinicx.patient.dto.visit.VisitDetailsDto;
import sy.sezar.clinicx.patient.dto.visit.VisitDto;
import sy.sezar.clinicx.patient.model.Visit;

/**
 * Mapper for converting between Visit entity and its DTOs.
 * Updated for multi-procedure visit model.
 */
@Mapper(componentModel = "spring", uses = {ProcedureMapper.class})
public interface VisitMapper {

    // Visit -> VisitDto (summary without procedures)
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.fullName", target = "patientName")
    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(source = "provider.id", target = "providerId")
    @Mapping(source = "provider.fullName", target = "providerName")
    @Mapping(target = "totalCost", expression = "java(visit.getTotalCost())")
    @Mapping(target = "overallStatus", expression = "java(visit.getOverallStatus())")
    @Mapping(target = "procedureCount", expression = "java(visit.getProcedureCount())")
    VisitDto toDto(Visit visit);
    
    List<VisitDto> toDtoList(List<Visit> visits);

    // Visit -> VisitDetailsDto (full details with procedures)
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.fullName", target = "patientName")
    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(source = "provider.id", target = "providerId")
    @Mapping(source = "provider.fullName", target = "providerName")
    @Mapping(source = "procedures", target = "procedures")
    VisitDetailsDto toDetailsDto(Visit visit);

    // Visit -> VisitLogDto (for backward compatibility if needed)
    @Mapping(source = "id", target = "visitId")
    @Mapping(source = "date", target = "visitDate")
    @Mapping(source = "time", target = "visitTime")
    @Mapping(target = "visitType", constant = "TREATMENT")
    @Mapping(target = "toothNumber", expression = "java(getFirstToothNumber(visit))")
    @Mapping(target = "visitName", expression = "java(getProcedureNames(visit))")
    @Mapping(source = "provider.fullName", target = "doctorName")
    @Mapping(target = "durationMinutes", expression = "java(getTotalDuration(visit))")
    @Mapping(target = "cost", expression = "java(visit.getTotalCost())")
    @Mapping(target = "status", expression = "java(mapOverallStatusToTreatmentStatus(visit))")
    @Mapping(source = "notes", target = "notes")
    @Mapping(target = "nextAppointment", ignore = true)
    VisitLogDto toVisitLogDto(Visit visit);
    
    List<VisitLogDto> toVisitLogDtoList(List<Visit> visits);

    // CreateVisitRequest -> Visit (using default method to handle builder)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "procedures", ignore = true)
    default Visit toEntity(CreateVisitRequest request) {
        if (request == null) {
            return null;
        }
        
        return Visit.builder()
            .date(request.date())
            .time(request.time())
            .notes(request.notes())
            .build();
    }

    // Legacy VisitCreateRequest -> Visit (using default method to handle builder)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "procedures", ignore = true)
    default Visit toVisit(VisitCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        return Visit.builder()
            .date(request.visitDate())
            .time(request.visitTime())
            .notes(request.visitNotes())
            .build();
    }

    // Helper methods for mapping expressions
    default Integer getFirstToothNumber(Visit visit) {
        if (visit.getProcedures() == null || visit.getProcedures().isEmpty()) {
            return null;
        }
        return visit.getProcedures().stream()
                .map(p -> p.getToothNumber())
                .filter(t -> t != null)
                .findFirst()
                .orElse(null);
    }

    default String getProcedureNames(Visit visit) {
        if (visit.getProcedures() == null || visit.getProcedures().isEmpty()) {
            return "No procedures";
        }
        return visit.getProcedures().stream()
                .map(p -> p.getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    default Integer getTotalDuration(Visit visit) {
        if (visit.getProcedures() == null || visit.getProcedures().isEmpty()) {
            return 0;
        }
        return visit.getProcedures().stream()
                .map(p -> p.getDurationMinutes() != null ? p.getDurationMinutes() : 0)
                .reduce(0, Integer::sum);
    }

    default sy.sezar.clinicx.patient.model.enums.TreatmentStatus mapOverallStatusToTreatmentStatus(Visit visit) {
        String status = visit.getOverallStatus();
        if (status == null) {
            return sy.sezar.clinicx.patient.model.enums.TreatmentStatus.PLANNED;
        }
        
        switch (status) {
            case "COMPLETED":
                return sy.sezar.clinicx.patient.model.enums.TreatmentStatus.COMPLETED;
            case "IN_PROGRESS":
                return sy.sezar.clinicx.patient.model.enums.TreatmentStatus.IN_PROGRESS;
            case "CANCELLED":
                return sy.sezar.clinicx.patient.model.enums.TreatmentStatus.CANCELLED;
            case "PLANNED":
            case "NO_PROCEDURES":
            default:
                return sy.sezar.clinicx.patient.model.enums.TreatmentStatus.PLANNED;
        }
    }
}

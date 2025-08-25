package sy.sezar.clinicx.patient.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.ChartDataDto;
import sy.sezar.clinicx.patient.dto.ChartPayload;
import sy.sezar.clinicx.patient.dto.ChartToothDto;
import sy.sezar.clinicx.patient.model.DentalChart;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.repository.DentalChartRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.service.DentalChartService;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of DentalChartService using JSONB-based dental chart storage.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DentalChartServiceImpl implements DentalChartService {

    private final DentalChartRepository dentalChartRepository;
    private final PatientRepository patientRepository;

    @Override
    public ChartDataDto getPatientDentalChart(UUID patientId) {
        log.info("Getting dental chart data for patient: {}", patientId);

        if (patientId == null) {
            log.error("Patient ID cannot be null for dental chart retrieval");
            throw new IllegalArgumentException("Patient ID cannot be null");
        }

        DentalChart dentalChart = dentalChartRepository.findByPatientId(patientId)
                .orElseGet(() -> {
                    log.info("Creating new dental chart for patient: {}", patientId);
                    return createDefaultDentalChart(patientId);
                });

        return convertChartPayloadToChartDataDto(dentalChart.getChartData());
    }

    @Override
    @Transactional
    public ChartToothDto updateToothCondition(UUID patientId, String toothId, ChartToothDto toothData) {
        log.info("Updating tooth {} for patient: {}", toothId, patientId);

        DentalChart dentalChart = dentalChartRepository.findByPatientId(patientId)
                .orElseGet(() -> createDefaultDentalChart(patientId));

        ChartPayload.Tooth tooth = dentalChart.getChartData().getTeeth().get(toothId);
        if (tooth == null) {
            log.error("Tooth {} not found in dental chart for patient: {}", toothId, patientId);
            throw new NotFoundException("Tooth " + toothId + " not found for patient: " + patientId);
        }

        // Update tooth data from DTO
        tooth.setCondition(toothData.condition());
        if (toothData.notes() != null) {
            tooth.setNotes(toothData.notes());
        }
        if (toothData.surfaces() != null) {
            toothData.surfaces().forEach((surfaceName, surfaceDto) -> {
                ChartPayload.Surface surface = tooth.getSurfaces().get(surfaceName);
                if (surface != null) {
                    surface.setCondition(surfaceDto.condition());
                    // Note: Surface notes are stored at tooth level, not surface level
                }
            });
        }
        if (toothData.flags() != null) {
            tooth.getFlags().setImpacted(toothData.flags().impacted());
            tooth.getFlags().setMobile(toothData.flags().mobile());
            tooth.getFlags().setPeriapical(toothData.flags().periapical());
            tooth.getFlags().setAbscess(toothData.flags().abscess());
        }

        tooth.setLastTreatmentDate(LocalDate.now().toString());
        dentalChartRepository.save(dentalChart);

        return convertToothToChartToothDto(toothId, tooth);
    }

    @Override
    public ChartToothDto getToothDetails(UUID patientId, String toothId) {
        log.info("Getting tooth {} details for patient: {}", toothId, patientId);

        DentalChart dentalChart = dentalChartRepository.findByPatientId(patientId)
                .orElseThrow(() -> {
                    log.error("Dental chart not found for patient: {}", patientId);
                    return new NotFoundException("Dental chart not found for patient: " + patientId);
                });

        ChartPayload.Tooth tooth = dentalChart.getChartData().getTeeth().get(toothId);
        if (tooth == null) {
            log.error("Tooth {} not found in dental chart for patient: {}", toothId, patientId);
            throw new NotFoundException("Tooth " + toothId + " not found for patient: " + patientId);
        }

        return convertToothToChartToothDto(toothId, tooth);
    }

    @Override
    @Transactional
    public void updateSurfaceCondition(UUID patientId, String toothId, String surfaceName,
                                       String condition, String notes) {
        log.info("Updating surface {} of tooth {} for patient: {}", surfaceName, toothId, patientId);

        DentalChart dentalChart = dentalChartRepository.findByPatientId(patientId)
                .orElseThrow(() -> new NotFoundException("Dental chart not found for patient: " + patientId));

        ChartPayload.Tooth tooth = dentalChart.getChartData().getTeeth().get(toothId);
        if (tooth == null) {
            throw new NotFoundException("Tooth " + toothId + " not found for patient: " + patientId);
        }

        ChartPayload.Surface surface = tooth.getSurfaces().get(surfaceName);
        if (surface == null) {
            throw new NotFoundException("Surface " + surfaceName + " not found for tooth " + toothId);
        }

        surface.setCondition(condition);
        // Note: Surface notes would be stored at tooth level if needed

        dentalChartRepository.save(dentalChart);
    }

    @Override
    @Transactional
    public ChartDataDto initializeDentalChart(UUID patientId) {
        log.info("Initializing dental chart for patient: {}", patientId);

        // Check if chart already exists
        if (dentalChartRepository.findByPatientId(patientId).isPresent()) {
            throw new IllegalStateException("Dental chart already exists for patient: " + patientId);
        }

        DentalChart dentalChart = createDefaultDentalChart(patientId);
        return convertChartPayloadToChartDataDto(dentalChart.getChartData());
    }

    @Transactional
    private DentalChart createDefaultDentalChart(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        DentalChart dentalChart = new DentalChart();
        dentalChart.setPatient(patient);
        dentalChart.setChartData(ChartPayload.createDefault());

        return dentalChartRepository.save(dentalChart);
    }

    private ChartDataDto convertChartPayloadToChartDataDto(ChartPayload chartPayload) {
        ChartDataDto.MetaDto meta = new ChartDataDto.MetaDto(
            chartPayload.getMeta().getVersion(),
            chartPayload.getMeta().getLastUpdated(),
            chartPayload.getMeta().getUpdatedBy()
        );

        Map<String, ChartToothDto> teeth = new java.util.HashMap<>();
        chartPayload.getTeeth().forEach((toothId, tooth) -> {
            teeth.put(toothId, convertToothToChartToothDto(toothId, tooth));
        });

        return new ChartDataDto(meta, teeth);
    }

    private ChartToothDto convertToothToChartToothDto(String toothId, ChartPayload.Tooth tooth) {
        Map<String, ChartToothDto.SurfaceDto> surfaces = new java.util.HashMap<>();
        tooth.getSurfaces().forEach((name, surface) -> {
            surfaces.put(name, new ChartToothDto.SurfaceDto(
                surface.getCondition(),
                null // Surface level notes not supported in current model
            ));
        });

        ChartToothDto.FlagsDto flags = new ChartToothDto.FlagsDto(
            tooth.getFlags().isImpacted(),
            tooth.getFlags().isMobile(),
            tooth.getFlags().isPeriapical(),
            tooth.getFlags().isAbscess()
        );

        return new ChartToothDto(
            toothId,
            tooth.getCondition(),
            surfaces,
            flags,
            tooth.getNotes()
        );
    }
}

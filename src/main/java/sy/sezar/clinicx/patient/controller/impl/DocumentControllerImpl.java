package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.DocumentControllerApi;
import sy.sezar.clinicx.patient.dto.DocumentSummaryDto;
import sy.sezar.clinicx.patient.service.DocumentService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class DocumentControllerImpl implements DocumentControllerApi {

    private final DocumentService documentService;

    @Override
    public ResponseEntity<Page<DocumentSummaryDto>> getPatientDocuments(UUID patientId, Pageable pageable) {
        log.info("Retrieving documents for patient ID: {} with pagination: {}", patientId, pageable);
        Page<DocumentSummaryDto> documents = documentService.getPatientDocuments(patientId, pageable);
        return ResponseEntity.ok(documents);
    }

    @Override
    public ResponseEntity<DocumentSummaryDto> getDocumentById(UUID id) {
        log.info("Retrieving document with ID: {}", id);
        DocumentSummaryDto document = documentService.findDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @Override
    public ResponseEntity<Void> deleteDocument(UUID id) {
        log.info("Deleting document with ID: {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
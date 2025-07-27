package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.DocumentSummaryDto;
import sy.sezar.clinicx.patient.mapper.DocumentMapper;
import sy.sezar.clinicx.patient.model.Document;
import sy.sezar.clinicx.patient.repository.DocumentRepository;
import sy.sezar.clinicx.patient.service.DocumentService;

import java.util.UUID;

/**
 * Implementation of DocumentService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Override
    public Page<DocumentSummaryDto> getPatientDocuments(UUID patientId, Pageable pageable) {
        log.info("Getting documents for patient: {} with pagination: {}", patientId, pageable);

        Page<Document> documents = documentRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable);
        log.info("Found {} documents (page {} of {}) for patient: {}",
                documents.getNumberOfElements(), documents.getNumber() + 1, documents.getTotalPages(), patientId);

        return documents.map(documentMapper::toDocumentSummaryDto);
    }

    @Override
    public DocumentSummaryDto findDocumentById(UUID documentId) {
        log.info("Finding document by ID: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> {
                    log.error("Document not found with ID: {}", documentId);
                    return new NotFoundException("Document not found with ID: " + documentId);
                });

        log.debug("Found document: {} for patient: {} (type: {})",
                document.getFileName(), document.getPatient().getId(), document.getType());

        return documentMapper.toDocumentSummaryDto(document);
    }

    @Override
    @Transactional
    public void deleteDocument(UUID documentId) {
        log.info("Deleting document with ID: {}", documentId);

        // First find the document to get additional context for logging
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> {
                    log.error("Cannot delete - document not found with ID: {}", documentId);
                    return new NotFoundException("Document not found with ID: " + documentId);
                });

        UUID patientId = document.getPatient().getId();
        String fileName = document.getFileName();
        log.debug("Deleting document: {} for patient: {} (type: {})",
                fileName, patientId, document.getType());

        documentRepository.deleteById(documentId);
        log.info("Successfully deleted document with ID: {} (file: {}) for patient: {}",
                documentId, fileName, patientId);
    }
}

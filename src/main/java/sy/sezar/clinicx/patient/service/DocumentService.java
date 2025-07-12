package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.DocumentSummaryDto;

import java.util.UUID;

/**
 * Service interface for managing patient documents.
 */
public interface DocumentService {

    /**
     * Gets all documents for a patient with pagination.
     */
    Page<DocumentSummaryDto> getPatientDocuments(UUID patientId, Pageable pageable);

    /**
     * Finds a document by ID.
     */
    DocumentSummaryDto findDocumentById(UUID documentId);

    /**
     * Deletes a document.
     */
    void deleteDocument(UUID documentId);
}

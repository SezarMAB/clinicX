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
        log.debug("Getting documents for patient: {}", patientId);

        Page<Document> documents = documentRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable);
        return documents.map(documentMapper::toDocumentSummaryDto);
    }

    @Override
    public DocumentSummaryDto findDocumentById(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found with ID: " + documentId));

        return documentMapper.toDocumentSummaryDto(document);
    }

    @Override
    @Transactional
    public void deleteDocument(UUID documentId) {
        log.info("Deleting document with ID: {}", documentId);

        if (!documentRepository.existsById(documentId)) {
            throw new NotFoundException("Document not found with ID: " + documentId);
        }

        documentRepository.deleteById(documentId);
    }
}

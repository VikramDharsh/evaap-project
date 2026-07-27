package com.evaap.repository;

import com.evaap.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUserIdAndDocumentType(Long userId, String documentType);

    Optional<Document> findByIdAndUserId(Long id, Long userId);
}
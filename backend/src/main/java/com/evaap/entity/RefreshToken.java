package com.evaap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * NOTE: This table does not exist yet in Evaap-DB-Demo1.sql.
 * You need to add a migration/ALTER script for this before ddl-auto=validate
 * will let the app start once this entity is in use. Suggested DDL:
 *
 * CREATE TABLE refresh_tokens (
 *     id BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     user_id BIGINT NOT NULL,
 *     token_hash VARCHAR(255) NOT NULL UNIQUE,
 *     expires_at TIMESTAMP NOT NULL,
 *     revoked BOOLEAN DEFAULT FALSE,
 *     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 *     FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
 * );
 * CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", length = 255, nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked")
    @Builder.Default
    private Boolean revoked = false;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}

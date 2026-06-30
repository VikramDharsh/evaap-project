package com.evaap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "education")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "institution_name", length = 255, nullable = false)
    private String institutionName;

    @Column(length = 100)
    private String qualification;

    @Column(name = "degree_name", length = 150)
    private String degreeName;

    @Column(length = 150)
    private String specialization;

    @Column(name = "start_year", columnDefinition = "YEAR")
    private Integer startYear;

    @Column(name = "end_year", columnDefinition = "YEAR")
    private Integer endYear;

    @Column(precision = 4, scale = 2)
    private BigDecimal cgpa;

    @Column(name = "currently_studying")
    @Builder.Default
    private Boolean currentlyStudying = false;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}

package com.example.edvhelper.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime; // Import for the chequeTime attribute

@Entity
@Builder
@Table(name = "fiscal_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiscalId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "cheque_date")
    private LocalDate chequeDate;

    @Column(name = "fiscal_id", nullable = false)
    private String fiscalId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FiscalIdStatusEnum status;

    @Column(name = "return_amount")
    private BigDecimal returnAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @Override
    public String toString() {
        return fiscalId;
    }
}

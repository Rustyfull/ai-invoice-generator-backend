package com.elite.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Item {

    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String  name;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    private BigDecimal taxPercent;

    @Column(nullable = false)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id", nullable = false)
    @ToString.Exclude
    private Invoice invoice;
}

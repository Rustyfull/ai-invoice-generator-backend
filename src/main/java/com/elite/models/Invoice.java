package com.elite.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Invoice {
    @Id
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    private LocalDateTime invoiceDate;

    private LocalDateTime dueDate;


    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Item> items = new ArrayList<>();


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name ="companyName", column = @Column(name = "bill_from_company_name")),
            @AttributeOverride(name = "email", column = @Column(name = "bill_from_email")),
            @AttributeOverride(name = "address", column = @Column(name = "bill_from_address")),
            @AttributeOverride(name = "phone", column = @Column(name = "bill_from_phone"))
    })
    private BillFrom billFrom;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "companyName", column = @Column(name = "bill_to_company_name")),
            @AttributeOverride(name = "email", column = @Column(name = "bill_to_email")),
            @AttributeOverride(name = "address", column = @Column(name = "bill_to_address")),
            @AttributeOverride(name = "phone",column = @Column(name = "bill_to_phone"))
    })
    private BillTo billTo;

    private String notes;

    private String paymentTerms;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal subtotal;
    private BigDecimal taxTotal;
    private BigDecimal total;












}

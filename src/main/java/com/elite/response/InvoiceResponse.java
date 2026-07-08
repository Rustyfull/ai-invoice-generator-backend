package com.elite.response;


import com.elite.dtos.BillFromDto;
import com.elite.dtos.BillToDto;
import com.elite.dtos.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvoiceResponse {
    private String id;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private BillFromDto billFrom;
    private BillToDto billTo;
    private List<ItemDto> items;
    private String notes;
    private String paymentTerms;
    private BigDecimal subTotal;
    private BigDecimal taxTotal;
    private BigDecimal total;
}

package com.elite.request;


import com.elite.dtos.BillFromDto;
import com.elite.dtos.BillToDto;
import com.elite.dtos.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateInvoiceRequest {
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private BillFromDto billFromDto;
    private BillToDto billToDto;
    private List<ItemDto> items;
    private String notes;
    private String paymentTerms;
    private String status;

}

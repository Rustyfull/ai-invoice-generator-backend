package com.elite.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExtractedInvoiceResponse {
    private String clientName;
    private String email;
    private String address;
    List<ExtractedItemDto> items;
}

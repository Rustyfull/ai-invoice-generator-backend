package com.elite.mapper;

import com.elite.dtos.BillFromDto;
import com.elite.dtos.BillToDto;
import com.elite.dtos.ItemDto;
import com.elite.models.Invoice;
import com.elite.response.InvoiceResponse;

public class InvoiceMapper {

    public static InvoiceResponse map(Invoice invoice) {
        return InvoiceResponse
                .builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .billFrom(
                        BillFromDto
                                .builder()
                                .companyName(invoice.getBillFrom().getCompanyName())
                                .email(invoice.getBillFrom().getEmail())
                                .address(invoice.getBillFrom().getAddress())
                                .phone(invoice.getBillFrom().getPhone())
                                .build()
                )
                .billTo(
                        BillToDto
                                .builder()
                                .clientName(invoice.getBillTo().getClientName())
                                .email(invoice.getBillTo().getEmail())
                                .address(invoice.getBillTo().getAddress())
                                .phone(invoice.getBillTo().getPhone())
                                .build()
                ).items(
                        invoice.getItems().stream()
                                .map(
                                        item -> ItemDto
                                                .builder()
                                                .name(item.getName())
                                                .quantity(item.getQuantity())
                                                .unitPrice(item.getUnitPrice())
                                                .taxPercent(item.getTaxPercent())
                                                .total(item.getTotal())
                                                .build()
                                ).toList()

                ).notes(
                        invoice.getNotes()
                ).paymentTerms(invoice.getPaymentTerms())
                .subTotal(invoice.getSubtotal())
                .taxTotal(invoice.getTaxTotal())
                .total(invoice.getTotal())
                .build();
    }
}

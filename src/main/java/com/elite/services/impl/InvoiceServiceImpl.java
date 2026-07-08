package com.elite.services.impl;

import com.elite.dtos.BillFromDto;
import com.elite.dtos.BillToDto;
import com.elite.dtos.ItemDto;
import com.elite.exceptions.InvoiceNotFoundException;
import com.elite.exceptions.UserNotAuthenticatedException;
import com.elite.mapper.InvoiceMapper;
import com.elite.models.*;
import com.elite.repository.InvoiceRepository;
import com.elite.request.CreateInvoiceRequest;
import com.elite.request.UpdateInvoiceRequest;
import com.elite.response.InvoiceResponse;
import com.elite.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final AuthServiceImpl authService;
    private final InvoiceRepository invoiceRepository;



    @Override
    public InvoiceResponse createInvoice(CreateInvoiceRequest req) {
        var user = getMe();
        Map<String, BigDecimal> map = processCalculation(req.getItems());
        var invoice = Invoice.builder()
                .user(user)
                .invoiceNumber(req.getInvoiceNumber())
                .invoiceDate(req.getInvoiceDate())
                .dueDate(req.getDueDate())
                .billFrom(
                        BillFrom
                                .builder()
                                .companyName(req.getBillFromDto().getCompanyName())
                                .email(req.getBillFromDto().getEmail())
                                .address(req.getBillFromDto().getAddress())
                                .phone(req.getBillFromDto().getPhone())
                                .build()
                )
                .billTo(
                        BillTo
                                .builder()
                                .clientName(req.getBillToDto().getClientName())
                                .email(req.getBillToDto().getEmail())
                                .address(req.getBillToDto().getAddress())
                                .phone(req.getBillToDto().getPhone())
                                .build()
                )
                .notes(req.getNotes())
                .paymentTerms(req.getPaymentTerms())
                .subtotal(
                        map.get("subTotal")
                )
                .taxTotal(
                        map.get("taxTotal")
                )
                .total(
                        map.get("total")
                )
                .build();

        invoice.setItems(
                req.getItems().stream().map(item -> Item.builder()
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .taxPercent(item.getTaxPercent())
                        .total(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .invoice(invoice)
                        .build())
                        .collect(Collectors.toList())
        );



        var savedInvoice = invoiceRepository.save(invoice);

      return InvoiceMapper.map(savedInvoice);
    }

    @Override
    public Page<InvoiceResponse> getInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(InvoiceMapper::map);
    }

    @Override
    public InvoiceResponse getInvoiceById(String id) {
        return invoiceRepository.findById(id).map(InvoiceMapper::map).orElseThrow(
                () -> new InvoiceNotFoundException("Invoice not found")
        );
    }

    @Override
    public InvoiceResponse updateInvoice(UpdateInvoiceRequest req, String id) {
        var invoice = invoiceRepository.findById(id).orElseThrow(
                () -> new InvoiceNotFoundException("Invoice not found")
        );

        if(req.getInvoiceNumber() != null) {
            invoice.setInvoiceNumber(req.getInvoiceNumber());
        }

        if(req.getInvoiceDate() != null) {
            invoice.setInvoiceDate(req.getInvoiceDate());
        }


        if(req.getDueDate() != null) {
            invoice.setDueDate(req.getDueDate());
        }


        if(req.getBillFromDto() != null) {
            BillFromDto billFromDto = req.getBillFromDto();
            if(billFromDto.getCompanyName() != null){
                invoice.getBillFrom().setCompanyName(billFromDto.getCompanyName());
            }

            if(billFromDto.getEmail() != null){
                invoice.getBillFrom().setEmail(billFromDto.getEmail());
            }

            if(billFromDto.getAddress() != null){
                invoice.getBillFrom().setAddress(billFromDto.getAddress());
            }

            if(billFromDto.getPhone() != null){
                invoice.getBillFrom().setPhone(billFromDto.getPhone());
            }
        }



        if(req.getBillToDto() != null) {
            BillToDto billToDto = req.getBillToDto();

            if (billToDto.getClientName() != null){
                invoice.getBillTo().setClientName(billToDto.getClientName());
            }


            if(billToDto.getEmail() != null){
                invoice.getBillTo().setEmail(billToDto.getEmail());
            }

            if(billToDto.getAddress() != null){
                invoice.getBillTo().setAddress(billToDto.getAddress());
            }

            if(billToDto.getPhone() != null){
                invoice.getBillTo().setPhone(billToDto.getPhone());
            }
        }


        if(req.getNotes() != null) {
            invoice.setNotes(req.getNotes());
        }

        if(req.getPaymentTerms() != null) {
            invoice.setPaymentTerms(req.getPaymentTerms());
        }


        if (req.getStatus() != null) {
            invoice.setStatus(Status.valueOf(req.getStatus().toUpperCase()));
        }


        if(req.getItems() != null) {
            Map<String, BigDecimal> map = processCalculation(req.getItems());
            invoice.setSubtotal(map.get("subTotal"));
            invoice.setTaxTotal(map.get("taxTotal"));
            invoice.setTotal(map.get("total"));

            invoice.setItems(
                    req.getItems().stream().map(item -> Item
                            .builder()
                                    .name(item.getName())
                                    .quantity(item.getQuantity())
                                    .unitPrice(item.getUnitPrice())
                                    .taxPercent(item.getTaxPercent())
                                    .total(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                    .invoice(invoice)
                            .build())
                            .collect(Collectors.toList())
            );
        }


        var updatedInvoice = invoiceRepository.save(invoice);

        return InvoiceMapper.map(updatedInvoice);


    }

    @Override
    public void deleteInvoice(String id) {
        var invoice = invoiceRepository.findById(id).orElseThrow(
                () -> new InvoiceNotFoundException("Invoice not found")
        );
        invoiceRepository.delete(invoice);
    }


    private User getMe(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        return (User) authentication.getPrincipal();


    }


    private Map<String, BigDecimal> processCalculation(List<ItemDto> items){
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        for(ItemDto item : items){
            subTotal = subTotal.add(BigDecimal.valueOf(item.getQuantity()).multiply(item.getUnitPrice()));
            taxTotal = taxTotal.add(BigDecimal.valueOf(item.getQuantity()).multiply(item.getUnitPrice()).multiply(item.getTaxPercent() == null ? BigDecimal.ZERO : item.getTaxPercent()).divide(BigDecimal.valueOf(100), MathContext.DECIMAL128));
        }
        HashMap<String, BigDecimal> map  = new HashMap<>();
        map.put("subTotal", subTotal);
        map.put("taxTotal", taxTotal);
        map.put("total", subTotal.add(taxTotal));

        return map;
    }
}

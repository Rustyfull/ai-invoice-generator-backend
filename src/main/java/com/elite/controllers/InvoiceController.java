package com.elite.controllers;




import com.elite.request.CreateInvoiceRequest;
import com.elite.request.UpdateInvoiceRequest;
import com.elite.response.InvoiceResponse;
import com.elite.services.impl.InvoiceServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceServiceImpl invoiceService;


    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody @Valid CreateInvoiceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(req));
    }




    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable("id") String id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));

    }




    @GetMapping
    public ResponseEntity<Page<InvoiceResponse>> getAllInvoices(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        return ResponseEntity.ok(invoiceService.getInvoices(pageable));
    }



    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> updateInvoice(@PathVariable("id") String id, @RequestBody @Valid UpdateInvoiceRequest req) {
        return ResponseEntity.ok(invoiceService.updateInvoice(req, id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable("id") String id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(
                Map.of("message", "Invoice deleted successfully")
        );

    }






}

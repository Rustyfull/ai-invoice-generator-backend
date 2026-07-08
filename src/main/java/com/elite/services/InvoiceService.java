package com.elite.services;

import com.elite.request.CreateInvoiceRequest;
import com.elite.request.UpdateInvoiceRequest;
import com.elite.response.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface  InvoiceService {

    InvoiceResponse createInvoice(CreateInvoiceRequest req);
    Page<InvoiceResponse> getInvoices(Pageable pageable);
    InvoiceResponse getInvoiceById(String id);
    InvoiceResponse updateInvoice(UpdateInvoiceRequest req, String id);
    void deleteInvoice(String id);
}

package com.elite.services;

import com.elite.dtos.ExtractedInvoiceResponse;
import com.elite.request.ParseInvoiceRequest;
import com.elite.response.InsightsResponse;
import com.elite.response.ReminderEmail;


public interface AiService {
    ExtractedInvoiceResponse parseInvoiceFromText(ParseInvoiceRequest req);
    ReminderEmail generateReminderEmail(String invoiceId);
    InsightsResponse getDashboardSummary();
}

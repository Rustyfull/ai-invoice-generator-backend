package com.elite.controllers;


import com.elite.dtos.ExtractedInvoiceResponse;
import com.elite.request.ParseInvoiceRequest;
import com.elite.response.InsightsResponse;
import com.elite.response.ReminderEmail;
import com.elite.services.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {
    private final AiService aiService;



    @PostMapping("/parse-text")
    public ResponseEntity<ExtractedInvoiceResponse> parseInvoiceFromText(@RequestBody @Valid ParseInvoiceRequest req){
        return ResponseEntity.ok(aiService.parseInvoiceFromText(req));
    }


    @GetMapping("/generate-reminder/{invoiceId}")
    public ResponseEntity<ReminderEmail> generateReminderEmail(@PathVariable("invoiceId")String invoiceId){
        return ResponseEntity.ok(aiService.generateReminderEmail(invoiceId));
    }


    @GetMapping("/dashboard-summary")
    public ResponseEntity<InsightsResponse> getDashboardSummary(){
        return ResponseEntity.ok(aiService.getDashboardSummary());
    }
}

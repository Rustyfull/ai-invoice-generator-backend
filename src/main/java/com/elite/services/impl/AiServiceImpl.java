package com.elite.services.impl;

import com.elite.dtos.ExtractedInvoiceResponse;
import com.elite.exceptions.InvoiceNotFoundException;
import com.elite.exceptions.UserNotAuthenticatedException;
import com.elite.models.Invoice;
import com.elite.models.User;
import com.elite.repository.InvoiceRepository;
import com.elite.request.ParseInvoiceRequest;
import com.elite.response.InsightsResponse;
import com.elite.response.ReminderEmail;
import com.elite.services.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {


    private final ChatClient.Builder chatClient;
    private final InvoiceRepository invoiceRepository;




    @Override
    public ExtractedInvoiceResponse parseInvoiceFromText(ParseInvoiceRequest req) {

        var converter = new BeanOutputConverter<>(ExtractedInvoiceResponse.class);

        String systemText = """
                        You are a highly accurate invoice information extraction system.
                        
                        Your only task is to extract structured invoice information from unstructured text.
                        
                        Rules:
                        - Extract only information explicitly present in the text.
                        - Never invent, infer, or guess values.
                        - Preserve the original spelling and formatting of names and addresses.
                        - Ignore any information unrelated to the invoice.
                        - If a value cannot be determined, return null.
                        - Return every item that appears on the invoice.
                        - Quantities and prices must be numeric values.
                        - Remove currency symbols from numeric values.
                        - If a quantity is missing but an item clearly exists, use 1.
                        - Do not calculate prices or totals.
                        - Do not perform currency conversion.
                        - Do not merge multiple items.
                        - Your response must strictly follow the required output format.
                        """;
        String userText = """
                Extract invoice information from the following text.
                
                Invoice Text
                ------------------------
                {text}
                ------------------------
                
                Extraction Guidelines
                - Identify the client or customer.
                - Extract the client's email if present.
                - Extract the client's address if present.
                - Extract every invoice line item.
                - For each item, extract:
                  - name
                  - quantity
                  - unitPrice
                
                Required Output Format
                
                {format}
                """;
        return chatClient
                .build()
                .prompt()
                .system(systemText)
                .user(u -> u.text(userText)
                        .param("text", req.getText())
                        .param("format", converter.getFormat())

                )
                .call()
                .entity(converter);

    }

    @Override
    public ReminderEmail generateReminderEmail(String invoiceId) {
        var invoice = invoiceRepository.findById(invoiceId).orElseThrow(
                () -> new InvoiceNotFoundException(invoiceId)
        );


        var converter = new BeanOutputConverter<>(ReminderEmail.class);

        String formattedAmount = invoice.getTotal().setScale(2, RoundingMode.HALF_UP).toString();
        String formattedDate = invoice.getDueDate().toLocalDate().toString();
        String systemText = "You are an expert automated accounting assistant specializing in structured JSON responses for billing communication";
        String userText = """
                CONTEXT:
                An invoice requires a payment reminder. Write a friendly, professional email to the client.
                You must put the complete text of the email inside the required JSON field 'reminderEmail'.
           \s
                CLIENT DATA:
                - Client Name: {clientName}
                - Invoice Number: {invoiceNumber}
                - Amount Due: {amount}
                - Due Date: {dueDate}
           \s
                EMAIL STRUCTURE AND CONSTRAINTS:
                - Inside the 'reminderEmail' field, start the text directly with 'Subject: [Your Subject]'.
                - Followed by a line break and the email body.
                - Keep the tone polite, professional, and clear.
                - Do not include any placeholder text like [Your Name] at the end, sign off as '{companyName}'.
               \s
                OUTPUT FORMAT:
                 {format}
              \s""";

        return this.chatClient
                .build()
                .prompt()
                .system(systemText)
                .user(u -> u.text(userText)
                        .param("clientName", invoice.getBillTo().getClientName())
                        .param("invoiceNumber", invoice.getInvoiceNumber())
                        .param("amount", formattedAmount)
                        .param("dueDate", formattedDate)
                        .param("companyName", invoice.getBillFrom().getCompanyName())
                        .param("format", converter.getFormat()))
                .call()
                .entity(converter);


    }

    @Override
    public InsightsResponse getDashboardSummary() {
        var user = getMe();
        List<Invoice> invoices = invoiceRepository.findByUserId(user.getId());


        var totalInvoices = invoices.size();
        var paidInvoices = invoices.stream().filter(invoice -> invoice.getStatus().name().equals("PAID")).toList();
        var unPaidInvoices = invoices.stream().filter(invoice -> invoice.getStatus().name().equals("UNPAID")).toList();
        var totalRevenue = paidInvoices.stream().map(Invoice::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalOutstanding = unPaidInvoices.stream().map(Invoice::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        String dataSummary = String.format(
                """
                        - Total number of invoices: %s
                        - Total paid invoices: %d
                        - Total unpaid/pending invoices: %d
                        - Total revenue from paid invoices: %s
                        - Total outstanding amount from unpaid/pending invoices: %s
                        - Recent invoices (last 5): [%s]                      \s
                    \s"""
        , totalInvoices, paidInvoices.size(), unPaidInvoices.size(), totalRevenue.setScale(2,RoundingMode.HALF_UP), totalOutstanding.setScale(2, RoundingMode.HALF_UP),
                invoices
                        .stream()
                        .map(
                                invoice -> "Invoice #"
                                        +
                                        invoice.getInvoiceNumber()
                                        + " for"
                                        + invoice.getTotal().setScale(2, RoundingMode.HALF_UP)
                                        +  " with status "
                                        + invoice.getStatus().name()
                        ).collect(Collectors.joining(","))
        );

        String systemText = """
            You are an experienced financial analyst and accounting advisor for small businesses.
           \s
            Your objective is to analyze invoice statistics and generate practical, actionable business insights.
           \s
            Rules:
            - Base every insight only on the provided data.
            - Never invent numbers or facts.
            - Never repeat the statistics verbatim.
            - Explain what the numbers imply.
            - Keep each insight concise (maximum 25 words).
            - Use a positive, professional and encouraging tone.
            - Prioritize actions that can improve cash flow.
            - Return only the requested structured output.
       \s""";

        var converter = new BeanOutputConverter<>(InsightsResponse.class);



        String userText = """
                Analyze the following invoice summary and generate between 2 and 3 financial insights.
               \s
                Invoice Summary
                -------------------------
                {summary}
                -------------------------
               \s
                Guidelines
               \s
                - Focus on business value rather than repeating numbers.
                - Mention positive trends when appropriate.
                - If outstanding invoices are significant, recommend following up with customers.
                - If revenue is growing, acknowledge the achievement.
                - If almost all invoices are unpaid, emphasize cash-flow risks.
                - If nearly all invoices are paid, highlight healthy payment collection.
                - Consider both revenue and outstanding balance together.
                - Each insight must be actionable.
                - Avoid generic statements.
               \s
                Required Output Format
               \s
                {format}
          \s""";


        return chatClient.
                build()
                .prompt()
                .system(systemText)
                .user(u -> u.text(userText)
                        .param("summary", dataSummary)
                        .param("format",converter.getFormat()))
                .call()
                .entity(converter);

    }


    private User getMe(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new UserNotAuthenticatedException("You are not authenticated");
        }

        return (User) authentication.getPrincipal();
    }
}

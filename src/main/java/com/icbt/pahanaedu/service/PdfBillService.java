package com.icbt.pahanaedu.service;

import com.icbt.pahanaedu.model.Bill;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

@Service
public class PdfBillService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    
    /**
     * Generate PDF bill for an order
     */
    public byte[] generateBillPdf(Bill bill) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            // Create fonts
            PdfFont titleFont = PdfFontFactory.createFont();
            PdfFont headerFont = PdfFontFactory.createFont();
            PdfFont normalFont = PdfFontFactory.createFont();
            
            // Title
            Paragraph title = new Paragraph("PAHANA EDU BOOKSHOP")
                .setFont(titleFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE)
                .setBold();
            document.add(title);
            
            Paragraph subtitle = new Paragraph("Invoice / Bill")
                .setFont(headerFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold();
            document.add(subtitle);
            
            // Add some space
            document.add(new Paragraph("\n"));
            
            // Bill Information Table
            Table billInfoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100));
            
            // Left column - Bill details
            Cell leftCell = new Cell()
                .setBorder(null)
                .add(new Paragraph("Bill Number: " + bill.getBillNumber()).setFont(normalFont).setBold())
                .add(new Paragraph("Order Date: " + bill.getOrderDate().format(DATE_FORMATTER)).setFont(normalFont))
                .add(new Paragraph("Order Status: " + bill.getOrderStatus()).setFont(normalFont))
                .add(new Paragraph("Payment Status: " + bill.getPaymentStatus()).setFont(normalFont));
            
            // Right column - Company details
            Cell rightCell = new Cell()
                .setBorder(null)
                .add(new Paragraph("Pahana Edu Bookshop").setFont(normalFont).setBold())
                .add(new Paragraph("Colombo, Sri Lanka").setFont(normalFont))
                .add(new Paragraph("Phone: +94 11 234 5678").setFont(normalFont))
                .add(new Paragraph("Email: info@pahanaedu.lk").setFont(normalFont));
            
            billInfoTable.addCell(leftCell);
            billInfoTable.addCell(rightCell);
            document.add(billInfoTable);
            
            // Add space
            document.add(new Paragraph("\n"));
            
            // Customer Information
            Paragraph customerHeader = new Paragraph("Bill To:")
                .setFont(headerFont)
                .setFontSize(12)
                .setBold();
            document.add(customerHeader);
            
            Paragraph customerInfo = new Paragraph()
                .setFont(normalFont)
                .add("Customer: " + bill.getCustomerName() + "\n")
                .add("Phone: " + bill.getCustomerPhone() + "\n");
            
            if (bill.getCustomerEmail() != null && !bill.getCustomerEmail().isEmpty()) {
                customerInfo.add("Email: " + bill.getCustomerEmail() + "\n");
            }
            
            if (bill.getDeliveryAddress() != null && !bill.getDeliveryAddress().isEmpty()) {
                customerInfo.add("Address: " + bill.getDeliveryAddress() + "\n");
            }
            
            document.add(customerInfo);
            
            // Add space
            document.add(new Paragraph("\n"));
            
            // Items Table
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{10, 50, 20, 20}))
                .setWidth(UnitValue.createPercentValue(100));
            
            // Table headers
            itemsTable.addHeaderCell(createHeaderCell("No.", headerFont));
            itemsTable.addHeaderCell(createHeaderCell("Item", headerFont));
            itemsTable.addHeaderCell(createHeaderCell("Author", headerFont));
            itemsTable.addHeaderCell(createHeaderCell("Price", headerFont));
            
            // Table rows
            int itemNumber = 1;
            
            for (Bill.OrderItem item : bill.getItems()) {
                itemsTable.addCell(createDataCell(String.valueOf(itemNumber++), normalFont));
                itemsTable.addCell(createDataCell(item.getItemTitle(), normalFont));
                itemsTable.addCell(createDataCell(item.getItemAuthor() != null ? item.getItemAuthor() : "-", normalFont));
                itemsTable.addCell(createDataCell("Rs. " + CURRENCY_FORMAT.format(item.getItemPrice()), normalFont));
            }
            
            document.add(itemsTable);
            
            // Add space
            document.add(new Paragraph("\n"));
            
            // Totals Table
            Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                .setWidth(UnitValue.createPercentValue(100));
            
            // Subtotal
            totalsTable.addCell(createTotalLabelCell("Subtotal:", normalFont));
            totalsTable.addCell(createTotalValueCell("Rs. " + CURRENCY_FORMAT.format(bill.getSubtotal()), normalFont));
            
            // Tax (if applicable)
            if (bill.getTaxAmount() != null && bill.getTaxAmount() > 0) {
                totalsTable.addCell(createTotalLabelCell("Tax:", normalFont));
                totalsTable.addCell(createTotalValueCell("Rs. " + CURRENCY_FORMAT.format(bill.getTaxAmount()), normalFont));
            }
            
            // Shipping (if applicable)
            if (bill.getDeliveryCharge() != null && bill.getDeliveryCharge() > 0) {
                totalsTable.addCell(createTotalLabelCell("Shipping:", normalFont));
                totalsTable.addCell(createTotalValueCell("Rs. " + CURRENCY_FORMAT.format(bill.getDeliveryCharge()), normalFont));
            }
            
            // Total
            totalsTable.addCell(createTotalLabelCell("TOTAL:", headerFont, true));
            totalsTable.addCell(createTotalValueCell("Rs. " + CURRENCY_FORMAT.format(bill.getTotalAmount()), headerFont, true));
            
            document.add(totalsTable);
            
            // Add space
            document.add(new Paragraph("\n"));
            
            // Payment Information
            if (bill.getPaymentMethod() != null && !bill.getPaymentMethod().isEmpty()) {
                Paragraph paymentInfo = new Paragraph("Payment Method: " + bill.getPaymentMethod())
                    .setFont(normalFont)
                    .setBold();
                document.add(paymentInfo);
            }
            
            // Notes (if any)
            if (bill.getNotes() != null && !bill.getNotes().trim().isEmpty()) {
                document.add(new Paragraph("\n"));
                Paragraph notesHeader = new Paragraph("Notes:")
                    .setFont(headerFont)
                    .setBold();
                document.add(notesHeader);
                
                Paragraph notes = new Paragraph(bill.getNotes())
                    .setFont(normalFont);
                document.add(notes);
            }
            
            // Footer
            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Thank you for your business!")
                .setFont(normalFont)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic();
            document.add(footer);
            
            Paragraph disclaimer = new Paragraph("This is a computer-generated invoice.")
                .setFont(normalFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
            document.add(disclaimer);
        }
        
        return baos.toByteArray();
    }
    
    private Cell createHeaderCell(String content, PdfFont font) {
        return new Cell()
            .add(new Paragraph(content).setFont(font).setBold())
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER);
    }
    
    private Cell createDataCell(String content, PdfFont font) {
        return new Cell()
            .add(new Paragraph(content).setFont(font))
            .setTextAlignment(TextAlignment.LEFT);
    }
    
    private Cell createTotalLabelCell(String content, PdfFont font) {
        return createTotalLabelCell(content, font, false);
    }
    
    private Cell createTotalLabelCell(String content, PdfFont font, boolean isBold) {
        Cell cell = new Cell()
            .add(new Paragraph(content).setFont(font))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(null);
        
        if (isBold) {
            cell.getChildren().forEach(element -> {
                if (element instanceof Paragraph) {
                    ((Paragraph) element).setBold();
                }
            });
        }
        
        return cell;
    }
    
    private Cell createTotalValueCell(String content, PdfFont font) {
        return createTotalValueCell(content, font, false);
    }
    
    private Cell createTotalValueCell(String content, PdfFont font, boolean isBold) {
        Cell cell = new Cell()
            .add(new Paragraph(content).setFont(font))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(null);
        
        if (isBold) {
            cell.getChildren().forEach(element -> {
                if (element instanceof Paragraph) {
                    ((Paragraph) element).setBold();
                }
            });
        }
        
        return cell;
    }
}

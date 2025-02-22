package main.java.others;

import main.Entities.Purchase;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.IOException;

public class WordReceiptGenerator {

    public static void generateReceipt(Purchase[] purchases, String filePath) {
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("ЧЕК");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        titleRun.setFontFamily("Arial");
        title.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);

        XWPFParagraph line = document.createParagraph();
        line.setBorderBottom(org.apache.poi.xwpf.usermodel.Borders.SINGLE);
        line.setSpacingAfter(15);

        XWPFParagraph firstText = document.createParagraph();
        firstText.setSpacingBefore(15);

        for (Purchase purchase : purchases) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun run = paragraph.createRun();
            run.setText(String.format("Дата: %s", purchase.getDate()));
            run.setBold(true);
            run.addBreak();

            run = paragraph.createRun();
            run.setText(String.format("Имя покупателя: %s", purchase.getCustomer().getFirstName()));
            run.addBreak();

            run = paragraph.createRun();
            run.setText(String.format("Фамилия покупателя: %s", purchase.getCustomer().getLastName()));
            run.addBreak();

            run = paragraph.createRun();
            run.setText(String.format("Товар: %s", purchase.getProduct().getProductName()));
            run.addBreak();

            run = paragraph.createRun();
            run.setText(String.format("Количество: %d", purchase.getQuantity()));
            run.addBreak();

            run = paragraph.createRun();
            run.setText(String.format("Сотрудник: %s", purchase.getEmployee().getPerson().getSurname()));
            run.addBreak();

            run = paragraph.createRun();
            run.setText(String.format("Сумма: %.2f BYN", purchase.getPayAmount()));
            run.setBold(true);
            run.setFontSize(12);
            run.setColor("FF0000");
            run.addBreak();

            XWPFParagraph lineSeparator = document.createParagraph();
            lineSeparator.setBorderBottom(org.apache.poi.xwpf.usermodel.Borders.SINGLE);
        }

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

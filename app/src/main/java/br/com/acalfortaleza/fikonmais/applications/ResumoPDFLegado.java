package br.com.acalfortaleza.fikonmais.applications;

import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public final class ResumoPDFLegado {

    private ResumoPDFLegado() {
    }

    public static void add(Document document, String[] cliente, String[] consultor, String[] totais,
                           Font titleFont, Font textFont, Font totalFont, boolean valoresNegociaveis) {
        try {
            PdfPTable summary = new PdfPTable(new float[]{50f, 50f});
            summary.setWidthPercentage(100);
            summary.setSpacingAfter(7);
            summary.setKeepTogether(true);

            PdfPCell detailsCell = new PdfPCell();
            detailsCell.setBorder(PdfPCell.NO_BORDER);
            detailsCell.setPaddingRight(8);
            addSection(detailsCell, "Cliente", cliente, titleFont, textFont);
            addSection(detailsCell, "Consultor", consultor, titleFont, textFont);
            if (valoresNegociaveis) {
                Paragraph negotiable = new Paragraph("Valores negociáveis",
                        new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.RED));
                detailsCell.addElement(negotiable);
            }
            summary.addCell(detailsCell);

            PdfPCell totalsCell = new PdfPCell();
            totalsCell.setBorder(PdfPCell.NO_BORDER);
            totalsCell.setPaddingLeft(8);
            Paragraph totalsParagraph = new Paragraph();
            totalsParagraph.setAlignment(Element.ALIGN_RIGHT);
            if (totais != null) {
                for (String total : totais) {
                    if (total == null || total.trim().isEmpty()) {
                        continue;
                    }
                    Font font = total.startsWith("Valor total dos itens com frete:")
                            || total.startsWith("Valor total com frete:")
                            ? totalFont : textFont;
                    totalsParagraph.add(new Phrase(total, font));
                    totalsParagraph.add(new Phrase("\n"));
                }
            }
            totalsCell.addElement(totalsParagraph);
            summary.addCell(totalsCell);

            document.add(summary);
        } catch (Exception e) {
            Log.e("ResumoPDFLegado", "Erro ao adicionar resumo legado do PDF", e);
        }
    }

    private static void addSection(PdfPCell parent, String title, String[] lines,
                                   Font titleFont, Font textFont) {
        Paragraph section = new Paragraph();
        section.add(new Phrase(title + "\n", titleFont));
        if (lines != null) {
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    section.add(new Phrase(line + "\n", textFont));
                }
            }
        }
        section.setSpacingAfter(5);
        parent.addElement(section);
    }
}

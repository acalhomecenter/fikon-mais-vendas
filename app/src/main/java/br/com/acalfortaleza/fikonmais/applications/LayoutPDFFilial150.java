package br.com.acalfortaleza.fikonmais.applications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCellEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import br.com.acalfortaleza.fikonmais.R;

public class LayoutPDFFilial150 {

    private static final BaseColor HEADER_COLOR = new BaseColor(195, 222, 239);
    private static final BaseColor TEXT_COLOR = new BaseColor(35, 35, 35);
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
    private static final Font SECTION_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(13, 35, 85));
    private static final Font TEXT_FONT = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, TEXT_COLOR);
    private static final Font LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, TEXT_COLOR);
    private static final Font TOTAL_LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(13, 35, 85));
    private static final Font TOTAL_VALUE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static final Font TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

    private LayoutPDFFilial150() {
    }

    public static boolean isFilial150(String codfil) {
        return "150".equals(codfil == null ? null : codfil.trim());
    }

    public static void addHeader(Document document, Context context, String tipoDocumento, String numero, String data) {
        try {
            PdfPTable header = new PdfPTable(new float[]{25f, 50f, 25f});
            header.setWidthPercentage(100);

            Bitmap bitmap = ((BitmapDrawable) context.getDrawable(R.drawable.logoempresas)).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image logo = Image.getInstance(stream.toByteArray());
            logo.scaleToFit(120, 82);

            PdfPCell logoCell = new PdfPCell(logo, true);
            logoCell.setBorder(PdfPCell.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            header.addCell(logoCell);

            PdfPCell companyCell = new PdfPCell();
            companyCell.setBorder(PdfPCell.NO_BORDER);
            companyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            companyCell.setVerticalAlignment(Element.ALIGN_TOP);
            companyCell.setPaddingTop(0);
            companyCell.addElement(centered("ACAL EMPRESAS", TITLE_FONT));
            companyCell.addElement(centered("Rua Padre Cicero, 400 Rodolfo Teofilo - Fortaleza - CE", TEXT_FONT));
            companyCell.addElement(centered("CNPJ: 07.201.916/0001-59", TEXT_FONT));
            companyCell.addElement(centered(tipoDocumento + " número: " + numero, TEXT_FONT));
            companyCell.addElement(centered("Gerado: " + data, TEXT_FONT));
            header.addCell(companyCell);

            PdfPCell spacerCell = new PdfPCell();
            spacerCell.setBorder(PdfPCell.NO_BORDER);
            header.addCell(spacerCell);

            header.setSpacingAfter(8);
            document.add(header);
        } catch (Exception e) {
            Log.e("LayoutPDFFilial150", "Erro ao adicionar cabeçalho do PDF", e);
        }
    }

    public static void addSummary(Document document,
                                  String[] cliente,
                                  String[] gerente,
                                  String[] consultor,
                                  String[] entrega,
                                  String[] financeiro,
                                  String[] condicoes) {
        try {
            PdfPTable summary = new PdfPTable(new float[]{50f, 50f});
            summary.setWidthPercentage(100);
            summary.setSpacingAfter(8);

            PdfPCell left = new PdfPCell();
            left.setBorder(PdfPCell.NO_BORDER);
            left.setPaddingRight(8);
            addSection(left, "Cliente", cliente, true);
            if (gerente != null && gerente.length > 0) {
                addSection(left, "Gerente Comercial", gerente, true);
            }
            if (consultor != null && consultor.length > 0) {
                addSection(left, "Consultor Comercial", consultor, true);
            }
            if (entrega != null && entrega.length > 0) {
                addSection(left, "Endereço de entrega", entrega, true);
            }
            summary.addCell(left);

            PdfPCell right = new PdfPCell();
            right.setBorder(PdfPCell.NO_BORDER);
            right.setPaddingLeft(8);
            addSection(right, "Resumo financeiro", financeiro, false);
            addSection(right, "Condições comerciais", condicoes, false);
            summary.addCell(right);

            document.add(summary);
        } catch (Exception e) {
            Log.e("LayoutPDFFilial150", "Erro ao adicionar resumo do PDF", e);
        }
    }

    public static void addProductTable(Document document, String[] headers, ArrayList<String[]> rows) {
        try {
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingAfter(8);
            table.setHeaderRows(1);
            table.setWidths(getColumnWidths(headers.length));

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, TABLE_HEADER_FONT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(HEADER_COLOR);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (String[] row : rows) {
                for (int i = 0; i < headers.length; i++) {
                    PdfPCell cell;
                    String value = i < row.length ? row[i] : "";
                    if (i == 2 && hasPalletOpening(row, headers.length)) {
                        value += "\nValor de R$ 300,00, referente à abertura de pallet, incluída no valor total.";
                    }
                    if (i == 0) {
                        Image image = loadProductImage(value);
                        cell = image == null ? new PdfPCell(new Phrase("")) : new PdfPCell(image, true);
                    } else {
                        cell = new PdfPCell(new Phrase(value == null ? "" : value, TEXT_FONT));
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(i == 2 ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);
                }
            }

            document.add(table);
        } catch (Exception e) {
            Log.e("LayoutPDFFilial150", "Erro ao adicionar tabela de produtos", e);
        }
    }

    private static boolean hasPalletOpening(String[] row, int headerCount) {
        return row.length > headerCount && Boolean.parseBoolean(row[headerCount]);
    }

    public static void addTerms(Document document) {
        try {
            PdfPTable terms = new PdfPTable(1);
            terms.setWidthPercentage(100);
            PdfPCell titleCell = new PdfPCell(new Phrase("Informações gerais:", SECTION_FONT));
            titleCell.setBorder(PdfPCell.NO_BORDER);
            titleCell.setPaddingTop(2);
            terms.addCell(titleCell);

            String[] termsText = {
                    "- As entregas serão realizadas exclusivamente no pavimento térreo ou na garagem, conforme estipulam as normas de segurança e logística.",
                    "- De acordo com o Código de Defesa do Consumidor, produtos personalizados ou feitos sob encomenda não estão sujeitos à devolução, exceto em casos de defeito de fabricação.",
                    "- Informamos que as tonalidades dos produtos podem apresentar variações em relação às amostras expostas em loja, fato inerente ao processo de fabricação.",
                    "- É imprescindível que todos os materiais sejam conferidos antes da instalação. Não serão aceitas reclamações ou solicitações de troca após a aplicação, salvo em situações de vício oculto."
            };
            for (String text : termsText) {
                PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.DARK_GRAY)));
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(1);
                terms.addCell(cell);
            }
            document.add(terms);
        } catch (Exception e) {
            Log.e("LayoutPDFFilial150", "Erro ao adicionar informações gerais", e);
        }
    }

    private static void addSection(PdfPCell parent, String title, String[] lines, boolean inlineFields) {
        PdfPTable section = inlineFields
                ? new PdfPTable(1)
                : new PdfPTable(new float[]{52f, 48f});
        section.setWidthPercentage(100);
        section.setSpacingAfter(5);

        PdfPCell heading = new PdfPCell(new Phrase(title, SECTION_FONT));
        heading.setBorder(PdfPCell.NO_BORDER);
        heading.setCellEvent(new RoundedBackground(HEADER_COLOR, 7f, 3f));
        heading.setPadding(5);
        heading.setPaddingLeft(7);
        heading.setColspan(inlineFields ? 1 : 2);
        section.addCell(heading);

        if (lines != null) {
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                if (inlineFields) {
                    addInlineField(section, line);
                } else if (isTotalWithFreight(line)) {
                    addTotalWithFreight(section, line);
                } else {
                    addLabelValue(section, line);
                }
            }
        }

        parent.addElement(section);
    }

    private static void addInlineField(PdfPTable section, String line) {
        String[] parts = splitLabelValue(line);
        Phrase phrase = new Phrase();
        phrase.add(new Phrase(parts[0], LABEL_FONT));
        if (!parts[1].isEmpty()) {
            phrase.add(new Phrase(" " + parts[1], TEXT_FONT));
        }

        PdfPCell cell = new PdfPCell(phrase);
        cell.setColspan(1);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingLeft(5);
        cell.setPaddingTop(2);
        cell.setPaddingBottom(2);
        section.addCell(cell);
    }

    private static void addLabelValue(PdfPTable section, String line) {
        String[] parts = splitLabelValue(line);
        PdfPCell labelCell = new PdfPCell(new Phrase(parts[0], LABEL_FONT));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPaddingLeft(5);
        labelCell.setPaddingTop(2);
        labelCell.setPaddingBottom(2);
        section.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(parts[1], TEXT_FONT));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPaddingRight(5);
        valueCell.setPaddingTop(2);
        valueCell.setPaddingBottom(2);
        section.addCell(valueCell);
    }

    private static void addTotalWithFreight(PdfPTable section, String line) {
        String[] parts = splitLabelValue(line);
        PdfPTable total = new PdfPTable(new float[]{52f, 48f});
        total.setWidthPercentage(100);

        PdfPCell labelCell = new PdfPCell(new Phrase(parts[0], TOTAL_LABEL_FONT));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPaddingLeft(7);
        labelCell.setPaddingTop(5);
        labelCell.setPaddingBottom(5);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        total.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(parts[1], TOTAL_VALUE_FONT));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valueCell.setPaddingRight(7);
        valueCell.setPaddingTop(5);
        valueCell.setPaddingBottom(5);
        total.addCell(valueCell);

        PdfPCell totalCell = new PdfPCell(total);
        totalCell.setColspan(2);
        totalCell.setBorder(PdfPCell.NO_BORDER);
        totalCell.setCellEvent(new RoundedBackground(HEADER_COLOR, 8f, 0f));
        totalCell.setPadding(0);
        section.addCell(totalCell);
    }

    private static String[] splitLabelValue(String line) {
        int separator = line.indexOf(':');
        if (separator < 0) {
            return new String[]{line, ""};
        }
        return new String[]{line.substring(0, separator + 1), line.substring(separator + 1).trim()};
    }

    private static boolean isTotalWithFreight(String line) {
        return line.startsWith("Valor total dos itens com frete:")
            || line.startsWith("Valor total com frete:");
    }

    private static final class RoundedBackground implements PdfPCellEvent {
        private final BaseColor color;
        private final float radius;
        private final float inset;

        private RoundedBackground(BaseColor color, float radius, float inset) {
            this.color = color;
            this.radius = radius;
            this.inset = inset;
        }

        @Override
        public void cellLayout(PdfPCell cell, com.itextpdf.text.Rectangle position, PdfContentByte[] canvases) {
            PdfContentByte background = canvases[PdfPTable.BACKGROUNDCANVAS];
            background.saveState();
            background.setColorFill(color);
            background.roundRectangle(
                    position.getLeft() + inset,
                    position.getBottom() + inset,
                    position.getWidth() - inset * 2,
                    position.getHeight() - inset * 2,
                    radius
            );
            background.fill();
            background.restoreState();
        }
    }

    private static Paragraph centered(String text, Font font) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(2);
        return paragraph;
    }

    private static float[] getColumnWidths(int columnCount) {
        float[] widths = new float[columnCount];
        if (columnCount == 4) {
            return new float[]{12f, 15f, 58f, 15f};
        }
        if (columnCount == 6) {
            return new float[]{10f, 13f, 38f, 11f, 14f, 14f};
        }
        if (columnCount == 7) {
            return new float[]{9f, 12f, 35f, 10f, 10f, 12f, 12f};
        }
        for (int i = 0; i < columnCount; i++) {
            widths[i] = 100f / columnCount;
        }
        return widths;
    }

    private static Image loadProductImage(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        try {
            if (source.startsWith("http://") || source.startsWith("https://")) {
                HttpURLConnection connection = (HttpURLConnection) new URL(source).openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                try (InputStream input = connection.getInputStream();
                     ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                    }
                    Image image = Image.getInstance(output.toByteArray());
                    image.scaleToFit(48, 32);
                    return image;
                } finally {
                    connection.disconnect();
                }
            }

            File file = new File(source);
            if (file.exists() && file.length() > 0) {
                try (InputStream input = new FileInputStream(file);
                     ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                    }
                    Image image = Image.getInstance(output.toByteArray());
                    image.scaleToFit(48, 32);
                    return image;
                }
            }
        } catch (Exception e) {
            Log.w("LayoutPDFFilial150", "Não foi possível carregar imagem do produto", e);
        }
        return null;
    }
}

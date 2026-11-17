package br.com.acalfortaleza.fikonmais.applications;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import br.com.acalfortaleza.fikonmais.BuildConfig;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.NomeAmbiente;

public class TemplatePDFOrcamento {

    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private Font fSubTitulo = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
    private Font fTexto = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
    private Font fTextoGray = new Font(Font.FontFamily.HELVETICA, 5, Font.BOLD, BaseColor.LIGHT_GRAY);
    private Font fTextHigh = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.DARK_GRAY);
    private Font fTextHighRed = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.RED);
    private PdfPCell pdfcell;
    private Image image;
    private LoginHelper helperLogin;
    private HashMap<String, String> usuarioLogado;
    private Drawable d;


    public TemplatePDFOrcamento(Context context) {

        this.context = context;
        helperLogin = new LoginHelper(context);
        usuarioLogado = helperLogin.GetUsuarioSalvo();

    }


    public void openDocument(String numorc) {

        createFile(numorc);

        try {


            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            pdfWriter.setPageEvent(new MyFooter());
            document.open();
            if (LayoutPDFFilial150.isFilial150(usuarioLogado.get("codfil"))) {
                return;
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int height = displayMetrics.heightPixels;
            int widht = displayMetrics.widthPixels - 300;

            if(usuarioLogado.get("codfil").equals("3") || usuarioLogado.get("codfil").equals("21")){

                d = context.getDrawable(R.drawable.logoconceito);

            } else if(usuarioLogado.get("codfil").equals("501")) {

                d = context.getDrawable(R.drawable.logoshowroom);
            }else if (usuarioLogado.get("codfil").equals("150")){
                d = context.getDrawable(R.drawable.logoempresas);
            }else{
                d = context.getDrawable(R.drawable.logohomecenter);
            }

            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAbsolutePosition(27, document.getPageSize().getHeight() - 100);
            image.setAlignment(Element.ALIGN_LEFT);
            image.scaleToFit(80, 80);
            document.add(image);


        } catch (Exception e) {
            Log.e("openDocument", e.toString());
        }

    }

    public boolean isLayoutFilial150() {
        return LayoutPDFFilial150.isFilial150(usuarioLogado.get("codfil"));
    }

    public void addCabecalhoFilial150(String numero, String data) {
        LayoutPDFFilial150.addHeader(document, context, "Orçamento", numero, data);
    }

    public void addResumoFilial150(String[] cliente, String[] gerente, String[] consultor,
                                   String[] entrega, String[] financeiro, String[] condicoes) {
        LayoutPDFFilial150.addSummary(document, cliente, gerente, consultor, entrega, financeiro, condicoes);
    }

    public void createTableFilial150(String[] cabecalho, ArrayList<String[]> itens) {
        LayoutPDFFilial150.addProductTable(document, cabecalho, itens);
    }

    public void addTermoFilial150() {
        LayoutPDFFilial150.addTerms(document);
    }

    public void addResumoLegado(String[] cliente, String[] consultor, String[] totais, boolean valoresNegociaveis) {
        ResumoPDFLegado.add(document, cliente, consultor, totais, fTitulo, fTextHigh, fSubTitulo, valoresNegociaveis);
    }

    private Phrase footer() {
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
        Phrase p = new Phrase("Disponibilidade de estoque, mediante consulta.",font);
        return p;
    }

    public void addTermo() {
        try {

            paragraph = new Paragraph();
            paragraph.setSpacingBefore(10);
            addParagraphFilhoConultor(new Paragraph("Informações:", fTitulo));
            addParagraphFilhoConultor(new Paragraph("- As entregas serão realizadas exclusivamente no pavimento térreo ou na garagem, conforme estipulam\n"+"as normas de segurança e logística.\n", fTextHigh));
            addParagraphFilhoConultor(new Paragraph("- De acordo com o Código de Defesa do Consumidor, produtos personalizados ou feitos sob encomenda \n"+"não estão sujeitos a devolução, exceto em casos de defeito de fabricação\n", fTextHigh));
            addParagraphFilhoConultor(new Paragraph("- Informamos que as tonalidades dos produtos podem apresentar variações em relação às amostras expostas em loja,\n"+"fato que é inerente ao processo de fabricação e não constitui defeito.\n", fTextHigh));
            addParagraphFilhoConultor(new Paragraph("- É imprescindível que todos os materiais sejam conferidos antes da instalação. Consoante a legislação vigente,\n"+"não serão aceitas reclamações ou solicitações de troca após o produto ter sido aplicado, salvo em situações de vício oculto.\n", fTextHigh));


            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addConsultor", e.toString());
        }
    }




    private void createFile(String numorc) {

        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "OrcamentosPDF");

        if (!folder.exists()) {

            folder.mkdirs();

        }

        if (new File(folder, "Orcamento_" + numorc + "_Acal.pdf").exists()) {
            new File(folder, "Orcamento_" + numorc + "_Acal.pdf").delete();

            pdfFile = new File(folder, "Orcamento_" + numorc + "_Acal.pdf");
        } else {
            pdfFile = new File(folder, "Orcamento_" + numorc + "_Acal.pdf");
        }


    }

    public void closeDocument() {

        pdfWriter.setPageCount(document.getPageNumber());
        document.close();

    }

    public void addMetaData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);

    }


    public void addTitulo(String titulo, String cnpj, String endereco, String subTitulo, String date) {
        try {
            paragraph = new Paragraph();
            addParagraphFilho(new Paragraph(titulo, fTitulo));
            addParagraphFilho(new Paragraph(endereco, fSubTitulo));
            addParagraphFilho(new Paragraph(cnpj, fSubTitulo));
            addParagraphFilho(new Paragraph(subTitulo, fSubTitulo));
            addParagraphFilho(new Paragraph("Gerado: " + date, fTextHigh));
            paragraph.setSpacingAfter(25);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addTitulo", e.toString());
        }
    }

    public void addCliente(String nome, String cgccpf, String email, String fone) {
        try {
            paragraph = new Paragraph();
            addParagraphFilhoCliente(new Paragraph("Cliente", fTitulo));
            addParagraphFilhoCliente(new Paragraph(nome, fTextHigh));
            addParagraphFilhoCliente(new Paragraph(cgccpf, fTextHigh));
            addParagraphFilhoCliente(new Paragraph(email, fTextHigh));
            addParagraphFilhoCliente(new Paragraph(fone, fTextHigh));
            paragraph.setSpacingAfter(4);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addCliente", e.toString());
        }
    }


    public void addClientePj(String nome, String fantasia, String cgccpf, String email, String fone) {
        try {
            paragraph = new Paragraph();
            addParagraphFilhoCliente(new Paragraph("Cliente", fTitulo));
            addParagraphFilhoCliente(new Paragraph(nome, fTextHigh));
            addParagraphFilhoCliente(new Paragraph(fantasia, fTextHigh));
            addParagraphFilhoCliente(new Paragraph(cgccpf, fTextHigh));
            addParagraphFilhoCliente(new Paragraph(email, fTextHigh));
            addParagraphFilhoCliente(new Paragraph(fone, fTextHigh));
            paragraph.setSpacingAfter(4);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addClientePj", e.toString());
        }
    }

    public void addTotais(String totitens, String desconto, String frete, String total, String peso, String validade, String condicao,String parcelas) {
        try {
            paragraph = new Paragraph();
            addParagraphFilhoTotais(new Paragraph(totitens, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(desconto, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(total, fSubTitulo));
            addParagraphFilhoTotais(new Paragraph(frete, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(peso, fTextHigh));
            addParagraphFilhoTotais(new Paragraph("Condição de pagamento:" , fSubTitulo));
            addParagraphFilhoTotais(new Paragraph(condicao, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(parcelas, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(validade, fTextHigh));
            paragraph.setSpacingAfter(7);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addTotais", e.toString());
        }
    }

    public void addTotaisComFrete(String totitens, String desconto,String frete, String total, String totalfrete, String peso, String validade, String condicao,String parcelas) {
        try {
            paragraph = new Paragraph();
            addParagraphFilhoTotais(new Paragraph(totitens, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(desconto, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(total, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(frete, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(totalfrete, fSubTitulo));
            addParagraphFilhoTotais(new Paragraph(peso, fTextHigh));
            addParagraphFilhoTotais(new Paragraph("Condição de pagamento:" , fSubTitulo));
            addParagraphFilhoTotais(new Paragraph(condicao, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(parcelas, fTextHigh));
            addParagraphFilhoTotais(new Paragraph(validade, fTextHigh));
            paragraph.setSpacingAfter(7);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addTotais", e.toString());
        }
    }

    public void addConsultor(String consultor, String email, String contato, boolean valoresNegociaveis) {
        try {
            paragraph = new Paragraph();
            addParagraphFilhoConultor(new Paragraph("Consultor", fTitulo));
            addParagraphFilhoConultor(new Paragraph(consultor, fTextHigh));
            addParagraphFilhoConultor(new Paragraph(email, fTextHigh));
            addParagraphFilhoConultor(new Paragraph(contato, fTextHigh));
            if(usuarioLogado.get("codfil").equals("3") && valoresNegociaveis) {
                addParagraphFilhoConultor(new Paragraph("Valores negociáveis", fTextHighRed));
            }
            paragraph.setSpacingAfter(1);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addConsultor", e.toString());
        }
    }

    private void addParagraphFilho(Paragraph paragraphFilho) {
        paragraphFilho.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(paragraphFilho);

    }

    private void addParagraphFilhoCliente(Paragraph paragraphFilho) {
        paragraphFilho.setAlignment(Element.ALIGN_LEFT);
        paragraph.add(paragraphFilho);

    }


    private void addParagraphFilhoTotais(Paragraph paragraphFilho) {
        paragraphFilho.setAlignment(Element.ALIGN_RIGHT);
        paragraph.add(paragraphFilho);

    }

    private void addParagraphFilhoConultor(Paragraph paragraphFilho) {
        paragraphFilho.setAlignment(Element.ALIGN_LEFT);
        paragraph.add(paragraphFilho);

    }


    public void addParagraph(String text) {
        try {
            paragraph = new Paragraph(text, fTitulo);
            paragraph.setAlignment(Element.ALIGN_BOTTOM);
            paragraph.setSpacingBefore(10);
            document.add(paragraph);

        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }

    }


    public void createTableSemPreco(String[] cabecalho, ArrayList<String[]> itens) {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fTexto);
            float[] columnWidths = new float[]{10f, 10f, 30f, 10f};
            PdfPTable pdftable = new PdfPTable(cabecalho.length);
            pdftable.setWidths(columnWidths);
            pdftable.setWidthPercentage(100);

            int indexC = 0;
            while (indexC < cabecalho.length) {

                switch (indexC) {
                    case 0:

                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdfcell.setRowspan(1);
                        pdftable.addCell(pdfcell);
                        break;

                    case 1:
                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdftable.addCell(pdfcell);
                        break;

                    case 2:
                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdftable.addCell(pdfcell);
                        break;

                    case 3:
                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdftable.addCell(pdfcell);
                        break;


                    default:
                        break;


                }

            }

            for (int indexR = 0; indexR < itens.size(); indexR++) {
                String[] row = itens.get(indexR);
                for (indexC = 0; indexC < cabecalho.length; indexC++) {
                    switch (indexC) {
                        case 0:

                            if (row[indexC] != null && !row[indexC].equals("")) {

                                try {


                                    String codeStrLog = (row.length > 1 ? row[1] : "");
                                    Log.d("ORC_PDF_ITEM_IMG_SRC", "COD " + codeStrLog + " src " + row[indexC]);
                                    Image img = getImageFromPathOrUrl(row[indexC]);
                                    pdfcell = img != null ? new PdfPCell(img) : new PdfPCell(new Phrase(""));


                                } catch (Exception e) {
                                    pdfcell = new PdfPCell(new Phrase(""));
                                    pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setFixedHeight(35);
                                    pdfcell.setPadding(5);
                                    pdftable.addCell(pdfcell);
                                    break;

                                }


                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setFixedHeight(35);
                                pdfcell.setPadding(5);
                                pdftable.addCell(pdfcell);
                                break;
                            } else {
                                pdfcell = new PdfPCell(new Phrase(""));
                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setFixedHeight(35);
                                pdfcell.setPadding(5);
                                pdftable.addCell(pdfcell);
                                break;
                            }


                        case 1:
                            pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setPadding(10);
                            pdftable.addCell(pdfcell);
                            break;

                        case 2:

                            boolean aberturaPallet = Boolean.parseBoolean(row[4]);
                            AdicionarDescricao(aberturaPallet, pdftable, row[indexC]);
                            break;

                        case 3:
                            pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setPadding(10);
                            pdftable.addCell(pdfcell);
                            break;


                        default:
                            break;

                    }


                }
            }

            paragraph.add(pdftable);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("createTable", e.toString());
        }
    }



    public void createTableAmbientesSemPreco(String[] cabecalho, HashMap<Long, ArrayList<String[]>> itensAmbientes, HashMap<Long, String> totaisAmbientes ,List<NomeAmbiente> nomesAmbientes) {
        try {

            long maxIdAmbiente = -1;
            for (Map.Entry<Long, ArrayList<String[]>> map: itensAmbientes.entrySet()) {
                if(map.getKey()>maxIdAmbiente){
                    maxIdAmbiente = map.getKey();
                }
            }

            for (long i=0;i<=maxIdAmbiente;i++) {

                if(!itensAmbientes.containsKey(i)){
                    continue;
                }

                Long idAmbiente = i;
                ArrayList<String[]> itens = itensAmbientes.get(i);

                if(idAmbiente>0) {
                    String separador = "";
                    for (int j = 0; j < 196; j++) {
                        separador += "-";
                    }
                    paragraph = new Paragraph(separador, fSubTitulo);
                    paragraph.setSpacingBefore(5);
                    document.add(paragraph);
                }

                for (NomeAmbiente nomeAmbiente: nomesAmbientes) {

                    if(nomeAmbiente.getId()==idAmbiente){

                        paragraph = new Paragraph(nomeAmbiente.getDescricao(),fSubTitulo);
                        paragraph.setSpacingBefore(1);
                        paragraph.setSpacingAfter(10);
                        document.add(paragraph);
                        break;

                    }

                }

                paragraph = new Paragraph();
                paragraph.setFont(fTexto);
                float[] columnWidths = new float[]{10f, 10f, 30f, 10f};
                PdfPTable pdftable = new PdfPTable(cabecalho.length);
                pdftable.setWidths(columnWidths);
                pdftable.setWidthPercentage(100);

                int indexC = 0;
                while (indexC < cabecalho.length) {

                    switch (indexC) {
                        case 0:

                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdfcell.setRowspan(1);
                            pdftable.addCell(pdfcell);
                            break;

                        case 1:
                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdftable.addCell(pdfcell);
                            break;

                        case 2:
                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdftable.addCell(pdfcell);
                            break;

                        case 3:
                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdftable.addCell(pdfcell);
                            break;


                        default:
                            break;


                    }

                }

                for (int indexR = 0; indexR < itens.size(); indexR++) {
                    String[] row = itens.get(indexR);
                    for (indexC = 0; indexC < cabecalho.length; indexC++) {

                        switch (indexC) {
                            case 0:

                                if (row[indexC] != null && !row[indexC].equals("")) {

                                    try {


                                        String codeStrLog = (row.length > 1 ? row[1] : "");
                                        Log.d("ORC_PDF_ITEM_IMG_SRC", "COD " + codeStrLog + " src " + row[indexC]);
                                        Image img = getImageFromPathOrUrl(row[indexC]);
                                        pdfcell = img != null ? new PdfPCell(img) : new PdfPCell(new Phrase(""));


                                    } catch (Exception e) {
                                        pdfcell = new PdfPCell(new Phrase(""));
                                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        pdfcell.setFixedHeight(35);
                                        pdfcell.setPadding(5);
                                        pdftable.addCell(pdfcell);
                                        break;

                                    }


                                    pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setFixedHeight(35);
                                    pdfcell.setPadding(5);
                                    pdftable.addCell(pdfcell);
                                    break;
                                } else {
                                    pdfcell = new PdfPCell(new Phrase(""));
                                    pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setFixedHeight(35);
                                    pdfcell.setPadding(5);
                                    pdftable.addCell(pdfcell);
                                    break;
                                }


                            case 1:
                                pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setPadding(10);
                                pdftable.addCell(pdfcell);
                                break;

                            case 2:

                                boolean aberturaPallet = Boolean.parseBoolean(row[4]);
                                AdicionarDescricao(aberturaPallet, pdftable, row[indexC]);
                                break;

                            case 3:
                                pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setPadding(10);
                                pdftable.addCell(pdfcell);
                                break;


                            default:
                                break;

                        }


                    }
                }

                paragraph.add(pdftable);
                document.add(paragraph);

                paragraph = new Paragraph("Total do ambiente: " + totaisAmbientes.get(i), fTextHigh);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                paragraph.setSpacingBefore(1);
                document.add(paragraph);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("createTable", e.toString());
        }
    }

    public void createTableComPreco(String[] cabecalho, ArrayList<String[]> itens) {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fTexto);
            float[] columnWidths = new float[]{10f, 10f, 30f, 10f, 12f, 13f};
            PdfPTable pdftable = new PdfPTable(cabecalho.length);
            pdftable.setWidths(columnWidths);
            pdftable.setWidthPercentage(100);
            PdfPCell pdfcell;
            int indexC = 0;
            while (indexC < cabecalho.length) {

                switch (indexC) {
                    case 0:

                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdfcell.setRowspan(1);
                        pdftable.addCell(pdfcell);
                        break;

                    case 1:
                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdftable.addCell(pdfcell);
                        break;

                    case 2:
                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdftable.addCell(pdfcell);
                        break;

                    case 3:
                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdftable.addCell(pdfcell);
                        break;

                    case 4:
                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdftable.addCell(pdfcell);
                        break;

                    case 5:
                        pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        pdftable.addCell(pdfcell);
                        break;


                    default:
                        break;


                }

            }

            for (int indexR = 0; indexR < itens.size(); indexR++) {
                String[] row = itens.get(indexR);
                for (indexC = 0; indexC < cabecalho.length; indexC++) {

                    switch (indexC) {
                        case 0:

                            if (row[indexC] != null && !row[indexC].equals("")) {

                                try {


                                    String codeStrLog = (row.length > 1 ? row[1] : "");
                                    Log.d("ORC_PDF_ITEM_IMG_SRC", "COD " + codeStrLog + " src " + row[indexC]);
                                    Image img = getImageFromPathOrUrl(row[indexC]);
                                    pdfcell = img != null ? new PdfPCell(img) : new PdfPCell(new Phrase(""));


                                } catch (Exception e) {
                                    pdfcell = new PdfPCell(new Phrase(""));
                                    pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setFixedHeight(25);
                                    pdfcell.setPadding(5);
                                    pdftable.addCell(pdfcell);
                                    break;

                                }


                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setFixedHeight(25);
                                pdfcell.setPadding(5);
                                pdftable.addCell(pdfcell);
                                break;
                            } else {
                                pdfcell = new PdfPCell(new Phrase(""));
                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setFixedHeight(25);
                                pdfcell.setPadding(5);
                                pdftable.addCell(pdfcell);
                                break;
                            }


                        case 1:
                            pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setPadding(10);
                            pdftable.addCell(pdfcell);
                            break;

                        case 2:

                            boolean aberturaPallet = Boolean.parseBoolean(row[6]);
                            AdicionarDescricao(aberturaPallet, pdftable, row[indexC]);
                            break;

                        case 3:
                            pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setPadding(10);
                            pdftable.addCell(pdfcell);
                            break;

                        case 4:
                            pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setPadding(10);
                            pdftable.addCell(pdfcell);
                            break;

                        case 5:
                            pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setPadding(10);
                            pdftable.addCell(pdfcell);
                            break;

                        default:
                            break;

                    }


                }
            }

            paragraph.add(pdftable);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("createTable", e.toString());
        }
    }


    public void createTableAmbientesComPreco(String[] cabecalho, HashMap<Long, ArrayList<String[]>> itensAmbientes, HashMap<Long, String> totaisAmbientes, List<NomeAmbiente> nomesAmbientes) {
        try {

            long maxIdAmbiente = -1;
            for (Map.Entry<Long, ArrayList<String[]>> map: itensAmbientes.entrySet()) {
                if(map.getKey()>maxIdAmbiente){
                    maxIdAmbiente = map.getKey();
                }
            }

            for (long i=0;i<=maxIdAmbiente;i++) {

                if(!itensAmbientes.containsKey(i)){
                    continue;
                }

                Long idAmbiente = i;
                ArrayList<String[]> itens = itensAmbientes.get(i);

                if(idAmbiente>0) {
                    String separador = "";
                    for (int j = 0; j < 196; j++) {
                        separador += "-";
                    }
                    paragraph = new Paragraph(separador, fSubTitulo);
                    paragraph.setSpacingBefore(5);
                    document.add(paragraph);
                }

                for (NomeAmbiente nomeAmbiente: nomesAmbientes) {

                    if(nomeAmbiente.getId()==idAmbiente){

                        paragraph = new Paragraph(nomeAmbiente.getDescricao(),fSubTitulo);
                        paragraph.setSpacingBefore(1);
                        paragraph.setSpacingAfter(10);
                        document.add(paragraph);
                        break;

                    }

                }

                paragraph = new Paragraph();
                paragraph.setFont(fTexto);
                float[] columnWidths = new float[]{10f, 10f, 30f, 10f, 12f, 13f};
                PdfPTable pdftable = new PdfPTable(cabecalho.length);
                pdftable.setWidths(columnWidths);
                pdftable.setWidthPercentage(100);
                PdfPCell pdfcell;
                int indexC = 0;
                while (indexC < cabecalho.length) {

                    switch (indexC) {
                        case 0:

                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdfcell.setRowspan(1);
                            pdftable.addCell(pdfcell);
                            break;

                        case 1:
                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdftable.addCell(pdfcell);
                            break;

                        case 2:
                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdftable.addCell(pdfcell);
                            break;

                        case 3:
                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdftable.addCell(pdfcell);
                            break;

                        case 4:
                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdftable.addCell(pdfcell);
                            break;

                        case 5:
                            pdfcell = new PdfPCell(new Phrase(cabecalho[indexC++], fSubTitulo));
                            pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            pdfcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            pdftable.addCell(pdfcell);
                            break;


                        default:
                            break;


                    }

                }

                for (int indexR = 0; indexR < itens.size(); indexR++) {
                    String[] row = itens.get(indexR);
                    for (indexC = 0; indexC < cabecalho.length; indexC++) {

                        switch (indexC) {
                            case 0:

                                if (row[indexC] != null && !row[indexC].equals("")) {

                                    try {


                                        String codeStrLog = (row.length > 1 ? row[1] : "");
                                        Log.d("ORC_PDF_ITEM_IMG_SRC", "COD " + codeStrLog + " src " + row[indexC]);
                                        Image img = getImageFromPathOrUrl(row[indexC]);
                                        pdfcell = img != null ? new PdfPCell(img) : new PdfPCell(new Phrase(""));


                                    } catch (Exception e) {
                                        pdfcell = new PdfPCell(new Phrase(""));
                                        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                        pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        pdfcell.setFixedHeight(25);
                                        pdfcell.setPadding(5);
                                        pdftable.addCell(pdfcell);
                                        break;

                                    }


                                    pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setFixedHeight(25);
                                    pdfcell.setPadding(5);
                                    pdftable.addCell(pdfcell);
                                    break;
                                } else {
                                    pdfcell = new PdfPCell(new Phrase(""));
                                    pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    pdfcell.setFixedHeight(25);
                                    pdfcell.setPadding(5);
                                    pdftable.addCell(pdfcell);
                                    break;
                                }


                            case 1:
                                pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setPadding(10);
                                pdftable.addCell(pdfcell);
                                break;

                            case 2:

                                boolean aberturaPallet = Boolean.parseBoolean(row[6]);
                                AdicionarDescricao(aberturaPallet, pdftable, row[indexC]);
                                break;

                            case 3:
                                pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setPadding(10);
                                pdftable.addCell(pdfcell);
                                break;

                            case 4:
                                pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setPadding(10);
                                pdftable.addCell(pdfcell);
                                break;

                            case 5:
                                pdfcell = new PdfPCell(new Phrase(row[indexC], fTexto));
                                pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                pdfcell.setPadding(10);
                                pdftable.addCell(pdfcell);
                                break;

                            default:
                                break;

                        }


                    }
                }

                paragraph.add(pdftable);
                document.add(paragraph);

                paragraph = new Paragraph("Total do ambiente: " + totaisAmbientes.get(i), fTextHigh);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                paragraph.setSpacingBefore(1);
                document.add(paragraph);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("createTable", e.toString());
        }

    }

    class MyFooter extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            ColumnText.showTextAligned(cb, Element.ALIGN_BOTTOM, footer(),
                    (document.right() - document.left()) / 2 + document.leftMargin()+100,
                    document.bottom() - 20, 0);
        }




    }


    public void viewPdf(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), "Você nao possui um aplicativo para visualizar PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri uri = Uri.fromFile(pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), "Você nao possui um aplicativo para visualizar PDF", Toast.LENGTH_SHORT).show();


            }

        }

    }

    public Image getBitmapFromURL(String  url) {
        try {
            URL src = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) src.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(200f,200f);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (BadElementException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Image getImageFromPathOrUrl(String src) throws IOException, BadElementException {
        if (src == null || src.equals("")) return null;
        if (src.startsWith("http://") || src.startsWith("https://")) {
            Log.d("ORC_PDF_ITEM_IMG_LOAD", "Carregando via URL " + src);
            return getBitmapFromURL(src);
        }
        File f = new File(src);
        if (f.exists() && f.length() > 0) {
            Log.d("ORC_PDF_ITEM_IMG_LOAD", "Carregando via arquivo " + src);
            Image img = Image.getInstance(src);
            img.scaleAbsolute(200f,200f);
            return img;
        }
        Log.d("ORC_PDF_ITEM_IMG_LOAD", "Imagem não encontrada " + src);
        return null;
    }

    public Image GetImagemUrl(final String url) throws IOException, BadElementException {

        final ImageView bitmapImageView = new ImageView(context);


             Picasso.get().load(url).into(bitmapImageView, new Callback(){
                 @Override
                 public void onSuccess() {

                     Bitmap bitmap = ((BitmapDrawable) bitmapImageView.getDrawable()).getBitmap();
                     ByteArrayOutputStream stream = new ByteArrayOutputStream();
                     bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                     try {
                         image = Image.getInstance(stream.toByteArray());
                     } catch (BadElementException e) {
                         e.printStackTrace();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }

                 }

                 @Override
                 public void onError(Exception e) {

                 }


             });


        return image;

    }

    public File getFile(){

        return new File(pdfFile.getPath());

    }

    private void AdicionarDescricao(boolean aberturaPallet, PdfPTable pdftable, String descricao){

        PdfPCell pdfcell = new PdfPCell(new Phrase(descricao, fTexto));
        pdfcell.setVerticalAlignment(Element.ALIGN_CENTER);
        pdfcell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfcell.setPaddingLeft(10);

        if(aberturaPallet) {

            PdfPTable tablePallet = new PdfPTable(1);
            pdfcell.setBorder(PdfPCell.NO_BORDER);
            tablePallet.addCell(pdfcell);

            PdfPCell cellPallet = new PdfPCell(new Phrase("Valor de R$ 300,00, referente à abertura de pallet, incluída no valor total", fTextoGray));
            cellPallet.setVerticalAlignment(Element.ALIGN_CENTER);
            cellPallet.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellPallet.setPaddingLeft(10);
            cellPallet.setBorder(PdfPCell.NO_BORDER);
            cellPallet.setVerticalAlignment(Element.ALIGN_CENTER);
            cellPallet.setHorizontalAlignment(Element.ALIGN_LEFT);

            tablePallet.addCell(cellPallet);
            pdftable.addCell(tablePallet);

        }
        else {
            pdftable.addCell(pdfcell);
        }

    }


}

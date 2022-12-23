package com.prueba.pruebaAPI.funciones.generacionPDF;

import com.prueba.pruebaAPI.funciones.S3.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.prueba.pruebaAPI.dominio.*;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GeneracionPDF {

    private String urlPublica = "";

    public GeneracionPDF() {
    }

    public void generarPDF(Comprobante informacion, String[] informacionCAE) throws FileNotFoundException {

        Document document = new Document(PageSize.A4, 20, 20, 25, 20);
        try {
            try {
                //colocar nombre de acuerdo a los datos
                PdfWriter.getInstance(document, new FileOutputStream("temporal.pdf"));
            } catch (FileNotFoundException | DocumentException ex) {
                Logger.getLogger(GeneracionPDF.class.getName()).log(Level.SEVERE, null, ex);
            }

            document.open();

            document.add(crearHeader("ORIGINAL"));
            document.add(crearDivision(informacion));

            PdfPTable contenedor_Info = new PdfPTable(2);
            contenedor_Info.setWidthPercentage(100);
            contenedor_Info.addCell(crearInfoEmpresa(informacion));
            contenedor_Info.addCell(crearInfoFactura(informacion, informacionCAE));

            document.add(contenedor_Info);

            document.add(saltoLinea(1));

            if (informacion.getPeriodoFacturado() != null) {
                document.add(periodoFacturado(informacion));
                document.add(saltoLinea(1));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                if (informacion.getCbuEmisor() != null) {
                    document.add(generarCBUEmisor(informacion));
                    document.add(saltoLinea(1));
                }
            }

            document.add(generarInformacionCliente1(informacion));
            document.add(generarInformacionCliente2(informacion));

            if (informacion.getFeCabReq().getCbteTipo() == 3
                    || informacion.getFeCabReq().getCbteTipo() == 8
                    || informacion.getFeCabReq().getCbteTipo() == 203
                    || informacion.getFeCabReq().getCbteTipo() == 208) {
                document.add(generarInformacionCliente3(informacion));
            } else {
                document.add(generarInformacionCliente3());
            }

            document.add(saltoLinea(1));

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                document.add(generarOpcionTransferencia());
                document.add(saltoLinea(1));
            }

            if (informacion.getFacturaDetalleAlicIVA(0) == 0) {
                document.add(crearTablaInformacionB(informacion));
            } else {
                document.add(crearTablaInformacionA(informacion));
            }

            int cantInac = 0;
            boolean iva0 = false, iva2_5 = false, iva5 = false, iva10_5 = false, iva21 = false, iva27 = false;

            for (Iva iva : informacion.getFeDetReq().getIva()) {
                if (iva.getID() == 3) {
                    iva0 = true;
                }

                if (iva.getID() == 9) {
                    iva2_5 = true;
                }

                if (iva.getID() == 8) {
                    iva5 = true;
                }

                if (iva.getID() == 4) {
                    iva10_5 = true;
                }

                if (iva.getID() == 5) {
                    iva21 = true;
                }

                if (iva.getID() == 6) {
                    iva27 = true;
                }
            }

            if (!iva0) {
                cantInac++;
            }

            if (!iva2_5) {
                cantInac++;
            }

            if (!iva5) {
                cantInac++;
            }

            if (!iva10_5) {
                cantInac++;
            }

            if (!iva21) {
                cantInac++;
            }

            if (!iva27) {
                cantInac++;
            }

            document.add(saltoLinea(5));

            //3er parte
            if (informacion.getFeCabReq().getCbteTipo() == 1 || informacion.getFeCabReq().getCbteTipo() == 2 || informacion.getFeCabReq().getCbteTipo() == 3) {

                document.add(generarImportesIva1(informacion));

                if (iva27) {
                    document.add(generarImportesIva2(informacion));
                }

                if (iva21) {
                    document.add(generarImportesIva3(informacion));
                }

                if (iva10_5) {
                    document.add(generarImportesIva4(informacion));
                }

                if (iva5) {
                    document.add(generarImportesIva5(informacion));
                }

                if (iva2_5) {
                    document.add(generarImportesIva6(informacion));
                }

                if (iva0) {
                    document.add(generarImportesIva7(informacion));
                }

                document.add(generarImportesIva8(informacion));
                document.add(generarImportesIva9(informacion));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 6 || informacion.getFeCabReq().getCbteTipo() == 7 || informacion.getFeCabReq().getCbteTipo() == 8) {
                document.add(generarImportesIva0(informacion));
                document.add(generarImportesIva8(informacion));
                document.add(generarImportesIva9(informacion));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                document.add(saltoLinea(1));
                document.add(generarOpcionTransferenciaLeyenda());
            }

            document.add(saltoLinea(2));

            document.add(crearFooter(informacionCAE));

            //document.add(saltoLinea(2));
            document.newPage();

            //Segunda pagina
            document.add(crearHeader("DUPLICADO"));
            document.add(crearDivision(informacion));

            PdfPTable contenedor_Info2 = new PdfPTable(2);
            contenedor_Info2.setWidthPercentage(100);
            contenedor_Info2.addCell(crearInfoEmpresa(informacion));
            contenedor_Info2.addCell(crearInfoFactura(informacion, informacionCAE));

            document.add(contenedor_Info);

            document.add(saltoLinea(1));

            if (informacion.getPeriodoFacturado() != null) {
                document.add(periodoFacturado(informacion));
                document.add(saltoLinea(1));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                if (informacion.getCbuEmisor() != null) {
                    document.add(generarCBUEmisor(informacion));
                    document.add(saltoLinea(1));
                }
            }

            document.add(generarInformacionCliente1(informacion));
            document.add(generarInformacionCliente2(informacion));

            if (informacion.getFeCabReq().getCbteTipo() == 3
                    || informacion.getFeCabReq().getCbteTipo() == 8
                    || informacion.getFeCabReq().getCbteTipo() == 203
                    || informacion.getFeCabReq().getCbteTipo() == 208) {
                document.add(generarInformacionCliente3(informacion));
            } else {
                document.add(generarInformacionCliente3());
            }

            document.add(saltoLinea(1));

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                document.add(generarOpcionTransferencia());
                document.add(saltoLinea(1));
            }

            if (informacion.getFacturaDetalleAlicIVA(0) == 0) {
                document.add(crearTablaInformacionB(informacion));
            } else {
                document.add(crearTablaInformacionA(informacion));
            }

            document.add(saltoLinea(5));

            //3er parte
            if (informacion.getFeCabReq().getCbteTipo() == 1 || informacion.getFeCabReq().getCbteTipo() == 2 || informacion.getFeCabReq().getCbteTipo() == 3) {

                document.add(generarImportesIva1(informacion));

                if (iva27) {
                    document.add(generarImportesIva2(informacion));
                }

                if (iva21) {
                    document.add(generarImportesIva3(informacion));
                }

                if (iva10_5) {
                    document.add(generarImportesIva4(informacion));
                }

                if (iva5) {
                    document.add(generarImportesIva5(informacion));
                }

                if (iva2_5) {
                    document.add(generarImportesIva6(informacion));
                }

                if (iva0) {
                    document.add(generarImportesIva7(informacion));
                }

                document.add(generarImportesIva8(informacion));
                document.add(generarImportesIva9(informacion));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 6 || informacion.getFeCabReq().getCbteTipo() == 7 || informacion.getFeCabReq().getCbteTipo() == 8) {
                document.add(generarImportesIva0(informacion));
                document.add(generarImportesIva8(informacion));
                document.add(generarImportesIva9(informacion));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                document.add(saltoLinea(1));
                document.add(generarOpcionTransferenciaLeyenda());
            }

            document.add(saltoLinea(2));

            document.add(crearFooter(informacionCAE));

            //document.add(saltoLinea(2));
            document.newPage();

            //Tercera pagina
            document.add(crearHeader("TRIPLICADO"));
            document.add(crearDivision(informacion));

            PdfPTable contenedor_Info3 = new PdfPTable(2);
            contenedor_Info3.setWidthPercentage(100);
            contenedor_Info3.addCell(crearInfoEmpresa(informacion));
            contenedor_Info3.addCell(crearInfoFactura(informacion, informacionCAE));

            document.add(contenedor_Info);

            document.add(saltoLinea(1));

            if (informacion.getPeriodoFacturado() != null) {
                document.add(periodoFacturado(informacion));
                document.add(saltoLinea(1));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                if (informacion.getCbuEmisor() != null) {
                    document.add(generarCBUEmisor(informacion));
                    document.add(saltoLinea(1));
                }
            }

            document.add(generarInformacionCliente1(informacion));
            document.add(generarInformacionCliente2(informacion));

            if (informacion.getFeCabReq().getCbteTipo() == 3
                    || informacion.getFeCabReq().getCbteTipo() == 8
                    || informacion.getFeCabReq().getCbteTipo() == 203
                    || informacion.getFeCabReq().getCbteTipo() == 208) {
                document.add(generarInformacionCliente3(informacion));
            } else {
                document.add(generarInformacionCliente3());
            }

            document.add(saltoLinea(1));

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                document.add(generarOpcionTransferencia());
                document.add(saltoLinea(1));
            }

            if (informacion.getFacturaDetalleAlicIVA(0) == 0) {
                document.add(crearTablaInformacionB(informacion));
            } else {
                document.add(crearTablaInformacionA(informacion));
            }

            document.add(saltoLinea(5));

            //3er parte
            if (informacion.getFeCabReq().getCbteTipo() == 1 || informacion.getFeCabReq().getCbteTipo() == 2 || informacion.getFeCabReq().getCbteTipo() == 3) {

                document.add(generarImportesIva1(informacion));

                if (iva27) {
                    document.add(generarImportesIva2(informacion));
                }

                if (iva21) {
                    document.add(generarImportesIva3(informacion));
                }

                if (iva10_5) {
                    document.add(generarImportesIva4(informacion));
                }

                if (iva5) {
                    document.add(generarImportesIva5(informacion));
                }

                if (iva2_5) {
                    document.add(generarImportesIva6(informacion));
                }

                if (iva0) {
                    document.add(generarImportesIva7(informacion));
                }

                document.add(generarImportesIva8(informacion));
                document.add(generarImportesIva9(informacion));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 6 || informacion.getFeCabReq().getCbteTipo() == 7 || informacion.getFeCabReq().getCbteTipo() == 8) {
                document.add(generarImportesIva0(informacion));
                document.add(generarImportesIva8(informacion));
                document.add(generarImportesIva9(informacion));
            }

            if (informacion.getFeCabReq().getCbteTipo() == 201
                    || informacion.getFeCabReq().getCbteTipo() == 206) {
                document.add(saltoLinea(1));
                document.add(generarOpcionTransferenciaLeyenda());
            }

            document.add(saltoLinea(2));

            document.add(crearFooter(informacionCAE));

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneracionPDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        subirPDFServidorAWSs3(informacion, informacionCAE[7]);
    }

    public PdfPTable periodoFacturado(Comprobante informacion) throws DocumentException {
        PdfPTable periodoFacturado = new PdfPTable(3);

        PdfPCell desde = new PdfPCell();
        desde.setBorder(0);
        desde.setBorderWidthLeft(1);
        desde.setBorderWidthTop(1);
        desde.setBorderWidthBottom(1);
        desde.setPaddingTop(5);
        desde.setPaddingBottom(5);
        desde.setPaddingLeft(5);
        PdfPTable desdeTable = generarNegrita("Período Facturado Desde:", informacion.getPeriodoFacturado().getPfDesde(), 50, 50);
        desde.addElement(desdeTable);

        PdfPCell hasta = new PdfPCell();
        hasta.setBorder(0);
        hasta.setBorderWidthBottom(1);
        hasta.setBorderWidthTop(1);
        hasta.setPaddingTop(5);
        PdfPTable hastaTable = generarNegrita("Hasta: ", informacion.getPeriodoFacturado().getPfHasta(), 30, 70);
        hasta.addElement(hastaTable);

        PdfPCell vtoPago = new PdfPCell();
        vtoPago.setBorder(0);
        vtoPago.setBorderWidthTop(1);
        vtoPago.setBorderWidthRight(1);
        vtoPago.setBorderWidthBottom(1);
        vtoPago.setPaddingTop(5);
        PdfPTable vtoPagoTable = generarNegrita("Fecha de Vto. para el pago: ", informacion.getPeriodoFacturado().getPfVencimiento(), 50, 50);
        vtoPago.addElement(vtoPagoTable);

        periodoFacturado.setWidthPercentage(100);
        periodoFacturado.setWidths(new float[]{40, 20, 40});
        periodoFacturado.addCell(desde);
        periodoFacturado.addCell(hasta);
        periodoFacturado.addCell(vtoPago);

        return periodoFacturado;
    }

    public void subirPDFServidorAWSs3(Comprobante informacion, String nroComprobante) {
        MultipartFile multipartFile = null;
        String tipoFact = "";

        switch ((int) informacion.getFeCabReq().getCbteTipo()) {
            case 1:
                tipoFact = "FacturaA";
                break;
            case 2:
                tipoFact = "DébitoA";
                break;
            case 3:
                tipoFact = "CréditoA";
                break;
            case 6:
                tipoFact = "FacturaB";
                break;
            case 7:
                tipoFact = "DébitoB";
                break;
            case 8:
                tipoFact = "CréditoB";
                break;
            case 201:
                tipoFact = "FacturaAFCE";
                break;
            case 202:
                tipoFact = "DébitoAFCE";
                break;
            case 203:
                tipoFact = "CréditoAFCE";
                break;
            case 206:
                tipoFact = "FacturaBFCE";
                break;
            case 207:
                tipoFact = "DébitoBFCE";
                break;
            case 208:
                tipoFact = "CréditoBFCE";
                break;
        }

        try {

            multipartFile = new MockMultipartFile("prueba.pdf", new FileInputStream(new File("temporal.pdf")));
            String fileName = "recursos/FE/" + informacion.getEmpresa().getNombre() + "/" + informacion.getCliente().getDniCuit() + "/" + tipoFact + "-" + informacion.getFeCabReq().getPtoVta() + "-" + nroComprobante + ".pdf";

            S3ManagerUtils s3ManagerUtils = new S3ManagerUtils();
            s3ManagerUtils.uploadMultipartFile(fileName, multipartFile.getInputStream(), multipartFile.getContentType(), true);
            urlPublica = s3ManagerUtils.generatePresignedURL(fileName);
        } catch (Exception ex) {
            Logger.getLogger(GeneracionPDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Soy un URL: " + urlPublica);
        System.out.println("Respuesta: " + "recursos/FE/" + informacion.getEmpresa().getNombre() + "/" + informacion.getCliente().getDniCuit() + "/" + informacion.getFeCabReq().getCbteTipo() + "/" + nroComprobante + ".pdf");
    }

    private PdfPTable saltoLinea(int cantSaltos) {
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        PdfPCell celda = new PdfPCell();
        celda.setBorder(0);

        Phrase phrase = new Phrase("");
        celda.addElement(phrase);
        for (int i = 0; i <= cantSaltos; i++) {
            linea.addCell(celda);
        }

        return linea;
    }

    private PdfPTable generarNegrita(String phrase1, String phrase2, float width1, float width2) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(0);

        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(0);
        Phrase cell1Phrase = new Phrase(phrase1);
        cell1Phrase.font().setSize(8);
        cell1Phrase.font().setStyle(Font.BOLD);
        cell1.setPhrase(cell1Phrase);

        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(0);
        Phrase cell2Phrase = new Phrase(phrase2);
        cell2Phrase.font().setSize(8);
        cell2.setPhrase(cell2Phrase);

        table.addCell(cell1);
        table.addCell(cell2);
        table.setWidths(new float[]{width1, width2});

        return table;
    }

    private PdfPTable generarEspacios(String atributo, String valor) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(0);

        PdfPCell cell0 = new PdfPCell();
        cell0.setBorder(0);

        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(0);
        Phrase cell1Phrase = new Phrase(atributo);
        cell1Phrase.font().setSize(8);
        cell1Phrase.font().setStyle(Font.BOLD);
        cell1.setPhrase(cell1Phrase);

        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(0);
        Phrase cell2Phrase = new Phrase(valor);
        cell2Phrase.font().setSize(8);
        cell2Phrase.font().setStyle(Font.BOLD);
        cell2.setPhrase(cell2Phrase);
        cell2.setHorizontalAlignment(2);

        table.addCell(cell0);
        table.addCell(cell1);
        table.addCell(cell2);
        table.setWidths(new float[]{70, 20, 10});

        return table;
    }

    private PdfPTable generarEspacios(String atributo, String valor, boolean impTotal) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(0);

        PdfPCell cell0 = new PdfPCell();
        cell0.setBorder(0);

        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(0);
        Phrase cell1Phrase = new Phrase(atributo);
        cell1Phrase.font().setSize(impTotal ? 10 : 8);
        cell1Phrase.font().setStyle(Font.BOLD);
        cell1.setPhrase(cell1Phrase);

        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(0);
        Phrase cell2Phrase = new Phrase(valor);
        cell2Phrase.font().setSize(impTotal ? 10 : 8);
        cell2Phrase.font().setStyle(Font.BOLD);
        cell2.setPhrase(cell2Phrase);
        cell2.setHorizontalAlignment(2);

        table.addCell(cell0);
        table.addCell(cell1);
        table.addCell(cell2);
        table.setWidths(new float[]{70, 20, 10});

        return table;
    }

    private PdfPTable generarInformacionCliente1(Comprobante informacion) throws DocumentException {
        PdfPTable cliente = new PdfPTable(2);

        PdfPCell dniCuit = new PdfPCell();
        dniCuit.setBorder(0);
        dniCuit.setBorderWidthLeft(1);
        dniCuit.setBorderWidthTop(1);
        dniCuit.setPaddingTop(5);
        dniCuit.setPaddingLeft(5);

        if (informacion.getCliente().getDniCuit().length() == 7 || informacion.getCliente().getDniCuit().length() == 8) {
            PdfPTable dniCuitTable = generarNegrita("DNI:", informacion.getCliente().getDniCuit(), 12, 88);
            dniCuit.addElement(dniCuitTable);
        }

        if (informacion.getCliente().getDniCuit().length() == 10 || informacion.getCliente().getDniCuit().length() == 11) {
            PdfPTable dniCuitTable = generarNegrita("CUIT:", informacion.getCliente().getDniCuit(), 12, 88);
            dniCuit.addElement(dniCuitTable);
        }

        PdfPCell apNomRazSoc = new PdfPCell();
        apNomRazSoc.setBorder(0);
        apNomRazSoc.setBorderWidthRight(1);
        apNomRazSoc.setBorderWidthTop(1);
        apNomRazSoc.setPaddingTop(5);

        PdfPTable apNomRazSocTable = generarNegrita("Apellido y nombre / Rázon Social: ", informacion.getCliente().getApNoRaSo().toUpperCase(), 45, 55);
        apNomRazSoc.addElement(apNomRazSocTable);

        cliente.setWidthPercentage(100);
        cliente.setWidths(new float[]{40, 60});
        cliente.addCell(dniCuit);
        cliente.addCell(apNomRazSoc);

        return cliente;
    }

    private PdfPTable generarInformacionCliente2(Comprobante informacion) throws DocumentException {
        PdfPTable cliente2 = new PdfPTable(2);

        PdfPCell condIva = new PdfPCell();
        condIva.setBorder(0);
        condIva.setBorderWidthLeft(1);
        condIva.setPaddingTop(5);
        condIva.setPaddingBottom(5);
        condIva.setPaddingLeft(5);
        PdfPTable condIvaTable = generarNegrita("Condición frente al IVA:", informacion.getCliente().getCondIva(), 40, 60);
        condIva.addElement(condIvaTable);

        PdfPCell domicilio = new PdfPCell();
        domicilio.setBorder(0);
        domicilio.setBorderWidthRight(1);
        domicilio.setPaddingTop(5);
        domicilio.setPaddingBottom(5);
        if (informacion.getCliente().getDniCuit().length() == 7 || informacion.getCliente().getDniCuit().length() == 8) {
            PdfPTable domicilioTable = generarNegrita("Domicilio:", informacion.getCliente().getDomicilio(), 20, 80);
            domicilio.addElement(domicilioTable);
        }
        if (informacion.getCliente().getDniCuit().length() == 10 || informacion.getCliente().getDniCuit().length() == 11) {
            PdfPTable domicilioTable = generarNegrita("Domicilio Comercial:", informacion.getCliente().getDomicilio(), 20, 80);
            domicilio.addElement(domicilioTable);
        }

        cliente2.setWidthPercentage(100);
        cliente2.setWidths(new float[]{50, 50});
        cliente2.addCell(condIva);
        cliente2.addCell(domicilio);

        return cliente2;
    }

    private PdfPTable generarInformacionCliente3() throws DocumentException {
        PdfPTable cliente3 = new PdfPTable(1);

        PdfPCell condVenta = new PdfPCell();
        condVenta.setBorder(0);
        condVenta.setBorderWidthLeft(1);
        condVenta.setBorderWidthRight(1);
        condVenta.setBorderWidthBottom(1);
        condVenta.setPaddingLeft(5);
        condVenta.setPaddingBottom(10);
        PdfPTable condVentaTable = generarNegrita("Condición de Venta:", "Otra", 17, 83);
        condVenta.addElement(condVentaTable);

        cliente3.setWidthPercentage(100);
        cliente3.addCell(condVenta);

        return cliente3;
    }

    private PdfPTable generarInformacionCliente3(Comprobante informacion) throws DocumentException {
        PdfPTable cliente3 = new PdfPTable(2);

        PdfPCell condVenta = new PdfPCell();
        condVenta.setBorder(0);
        condVenta.setBorderWidthLeft(1);
        condVenta.setBorderWidthBottom(1);
        condVenta.setPaddingLeft(5);
        condVenta.setPaddingBottom(10);
        PdfPTable condVentaTable = generarNegrita("Condición de Venta:", "Otra", 35, 65);
        condVenta.addElement(condVentaTable);

        PdfPCell electronicaMiPyMEs = new PdfPCell();
        electronicaMiPyMEs.setBorder(0);
        electronicaMiPyMEs.setBorderWidthRight(1);
        electronicaMiPyMEs.setBorderWidthBottom(1);
        electronicaMiPyMEs.setPaddingLeft(5);
        electronicaMiPyMEs.setPaddingBottom(10);

        if (informacion.getFeCabReq().getCbteTipo() == 3) {
            PdfPTable electronicaMiPyMEsTable = generarNegrita("Fac. A:",
                    String.format("%0" + 5 + "d", informacion.getFeDetReq().getCbtesAsoc()[0].getPtoVta())
                    + "-" + String.format("%0" + 8 + "d", informacion.getFeDetReq().getCbtesAsoc()[0].getTipo()), 17, 83);
            electronicaMiPyMEs.addElement(electronicaMiPyMEsTable);
        }

        if (informacion.getFeCabReq().getCbteTipo() == 8) {
            PdfPTable electronicaMiPyMEsTable = generarNegrita("Fac. B:", String.format("%0" + 5 + "d", informacion.getFeDetReq().getCbtesAsoc()[0].getPtoVta())
                    + "-" + String.format("%0" + 8 + "d", informacion.getFeDetReq().getCbtesAsoc()[0].getTipo()), 17, 83);
            electronicaMiPyMEs.addElement(electronicaMiPyMEsTable);
        }

        if (informacion.getFeCabReq().getCbteTipo() == 203) {
            PdfPTable electronicaMiPyMEsTable = generarNegrita("Factura de cré (FCE) A:", String.format("%0" + 5 + "d", informacion.getFeDetReq().getCbtesAsoc()[0].getPtoVta())
                    + "-" + String.format("%0" + 8 + "d", informacion.getFeDetReq().getCbtesAsoc()[0].getTipo()), 17, 83);
            electronicaMiPyMEs.addElement(electronicaMiPyMEsTable);
        }

        if (informacion.getFeCabReq().getCbteTipo() == 208) {
            PdfPTable electronicaMiPyMEsTable = generarNegrita("Factura de cré (FCE) B:", String.format("%0" + 5 + "d", informacion.getFeDetReq().getCbtesAsoc()[0].getPtoVta())
                    + "-" + String.format("%0" + 8 + "d", informacion.getFeDetReq().getCbtesAsoc()[0].getTipo()), 17, 83);
            electronicaMiPyMEs.addElement(electronicaMiPyMEsTable);
        }

        cliente3.setWidthPercentage(100);
        cliente3.setWidths(new float[]{50, 50});
        cliente3.addCell(condVenta);
        cliente3.addCell(electronicaMiPyMEs);

        return cliente3;
    }

    private PdfPTable generarCBUEmisor(Comprobante informacion) throws DocumentException {
        PdfPTable cbuEmisor = new PdfPTable(1);

        PdfPCell cbuEmisorCell = new PdfPCell();
        cbuEmisorCell.setBorder(0);
        cbuEmisorCell.setBorderWidthTop(1);
        cbuEmisorCell.setBorderWidthRight(1);
        cbuEmisorCell.setBorderWidthLeft(1);
        cbuEmisorCell.setBorderWidthBottom(1);
        cbuEmisorCell.setPaddingLeft(200);
        PdfPTable cbuEmisorTable = generarNegrita("CBU del Emisor:", informacion.getCbuEmisor(), 20, 80);
        cbuEmisorCell.addElement(cbuEmisorTable);

        cbuEmisor.setWidthPercentage(100);
        cbuEmisor.addCell(cbuEmisorCell);

        return cbuEmisor;
    }

    private PdfPTable generarOpcionTransferencia() throws DocumentException {
        PdfPTable opcionTransferencia = new PdfPTable(1);

        PdfPCell opcionTransferenciaCell = new PdfPCell();
        opcionTransferenciaCell.setBorder(0);
        opcionTransferenciaCell.setBorderWidthTop(1);
        opcionTransferenciaCell.setBorderWidthRight(1);
        opcionTransferenciaCell.setBorderWidthLeft(1);
        opcionTransferenciaCell.setBorderWidthBottom(1);
        opcionTransferenciaCell.setPaddingLeft(5);
        PdfPTable opcionTransferenciaCellTable = generarNegrita("Opcion de Transferencia:", "Sistema de Circulación Abierta", 20, 80);
        opcionTransferenciaCell.addElement(opcionTransferenciaCellTable);

        opcionTransferencia.setWidthPercentage(100);
        opcionTransferencia.addCell(opcionTransferenciaCell);

        return opcionTransferencia;
    }

    private PdfPTable generarOpcionTransferenciaLeyenda() throws DocumentException {
        PdfPTable opcionTransferencia = new PdfPTable(1);

        PdfPCell opcionTransferenciaCell = new PdfPCell();
        opcionTransferenciaCell.setBorder(0);
        opcionTransferenciaCell.setBorderColor(Color.red.darker());
        opcionTransferenciaCell.setBorderColorBottom(Color.red.darker());
        opcionTransferenciaCell.setBorderColorTop(Color.red.darker());
        opcionTransferenciaCell.setBorderColorRight(Color.red.darker());
        opcionTransferenciaCell.setBorderColorLeft(Color.red.darker());
        opcionTransferenciaCell.setBorderWidthTop(1);
        opcionTransferenciaCell.setBorderWidthRight(1);
        opcionTransferenciaCell.setBorderWidthLeft(1);
        opcionTransferenciaCell.setBorderWidthBottom(1);
        opcionTransferenciaCell.setPadding(5);
        opcionTransferenciaCell.setPaddingBottom(10);
        Phrase optTransf = new Phrase("Luego de su aceptación tácita o expresa, "
                + "esta Factura de Crédito Electrónica MiPyMEs será transmitida a "
                + "El Sistema de Circulación Abierta de Facturas de Crédito Electrónicas "
                + "MiPyMEs, para su circulación y negociación, incluso en los Mercados de Valores,"
                + "en este caso, a través de un Agente de Depósito Colectivo o agentes "
                + "que cumplan similares funciones.");
        optTransf.font().setSize(6);
        optTransf.font().setStyle(Font.ITALIC);
        //optTransf.font().setStyle(Font.BOLD);
        optTransf.font().setColor(Color.red.darker());

        opcionTransferenciaCell.addElement(optTransf);

        opcionTransferencia.setWidthPercentage(100);
        opcionTransferencia.addCell(opcionTransferenciaCell);

        return opcionTransferencia;
    }

    private PdfPTable generarImportesIva0(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva0 = new PdfPTable(1);
        float total = 0;

        for (DetalleFactura detalle : informacion.getDetalleFactura()) {
            total += total + detalle.getSubtotal();
        }

        PdfPCell subtotal = new PdfPCell();
        subtotal.setHorizontalAlignment(2);
        subtotal.setBorder(-1);
        subtotal.setBorderWidthLeft(1);
        subtotal.setBorderWidthRight(1);
        subtotal.setBorderWidthTop(1);
        subtotal.setPaddingTop(3);
        subtotal.setPaddingRight(7);

        PdfPTable table = generarEspacios("Subtotal: $", String.valueOf(total));
        subtotal.addElement(table);

        importeIva0.setWidthPercentage(100);
        importeIva0.addCell(subtotal);

        return importeIva0;
    }

    private PdfPTable generarImportesIva1(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva = new PdfPTable(1);

        PdfPCell importeNetoGravado = new PdfPCell();
        importeNetoGravado.setHorizontalAlignment(2);
        importeNetoGravado.setBorder(0);
        importeNetoGravado.setBorderWidthLeft(1);
        importeNetoGravado.setBorderWidthRight(1);
        importeNetoGravado.setBorderWidthTop(1);
        importeNetoGravado.setPaddingTop(3);
        importeNetoGravado.setPaddingRight(7);

        PdfPTable table = null;
        String texto = String.valueOf(informacion.getFeDetReq().getImpNeto());
        if (texto.charAt(texto.length() - 1) == '0') {
            table = generarEspacios("Importe Neto Gravado: $", texto.concat("0"));
        } else {
            table = generarEspacios("Importe Neto Gravado: $", String.valueOf(informacion.getFeDetReq().getImpNeto()));
        }

        importeNetoGravado.addElement(table);

        importeIva.setWidthPercentage(100);
        importeIva.addCell(importeNetoGravado);

        return importeIva;
    }

    private PdfPTable generarImportesIva2(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva2 = new PdfPTable(1);
        double total = 0;

        PdfPCell importeIva27 = new PdfPCell();
        importeIva27.setBorder(0);
        PdfPTable table = null;
        for (Iva iva : informacion.getFeDetReq().getIva()) {
            if (iva.getID() == 6) {
                total += total + iva.getImporte();
            }
        }

        if (total != 0) {
            importeIva27.setHorizontalAlignment(2);
            importeIva27.setBorderWidthLeft(1);
            importeIva27.setBorderWidthRight(1);
            importeIva27.setPaddingTop(3);
            importeIva27.setPaddingRight(7);

            String texto = String.valueOf(total);
            if (texto.charAt(texto.length() - 1) == '0') {
                table = generarEspacios("IVA 27%: $", texto.replace('.', ',') + "0");
            } else {
                table = generarEspacios("IVA 27%: $", texto);
            }
        }

        importeIva27.addElement(table);

        importeIva2.setWidthPercentage(100);
        importeIva2.addCell(importeIva27);

        return importeIva2;
    }

    private PdfPTable generarImportesIva3(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva3 = new PdfPTable(1);
        double total = 0;

        PdfPCell importeIva21 = new PdfPCell();
        importeIva21.setBorder(0);
        PdfPTable table = null;

        for (Iva iva : informacion.getFeDetReq().getIva()) {
            if (iva.getID() == 5) {
                total += total + iva.getImporte();
            }
        }

        if (total != 0) {
            importeIva21.setHorizontalAlignment(2);
            importeIva21.setBorderWidthLeft(1);
            importeIva21.setBorderWidthRight(1);
            importeIva21.setPaddingTop(3);
            importeIva21.setPaddingRight(7);

            String texto = String.valueOf(total);
            if (texto.charAt(texto.length() - 1) == '0') {
                table = generarEspacios("IVA 21%: $", texto.replace('.', ',') + "0");
            } else {
                table = generarEspacios("IVA 21%: $", texto);
            }
        }

        importeIva21.addElement(table);

        importeIva3.setWidthPercentage(100);
        importeIva3.addCell(importeIva21);

        return importeIva3;
    }

    private PdfPTable generarImportesIva4(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva4 = new PdfPTable(1);
        double total = 0;

        PdfPCell importeIva105 = new PdfPCell();
        importeIva105.setBorder(0);
        PdfPTable table = null;

        for (Iva iva : informacion.getFeDetReq().getIva()) {
            if (iva.getID() == 4) {
                total += total + iva.getImporte();
            }
        }

        if (total != 0) {
            importeIva105.setHorizontalAlignment(2);
            importeIva105.setBorderWidthLeft(1);
            importeIva105.setBorderWidthRight(1);
            importeIva105.setPaddingTop(3);
            importeIva105.setPaddingRight(7);

            String texto = String.valueOf(total);
            if (texto.charAt(texto.length() - 1) == '0') {
                table = generarEspacios("IVA 10,5%: $", texto.replace('.', ',') + "0");
            } else {
                table = generarEspacios("IVA 10,5%: $", texto);
            }
        }

        importeIva105.addElement(table);

        importeIva4.setWidthPercentage(100);
        importeIva4.addCell(importeIva105);

        return importeIva4;
    }

    private PdfPTable generarImportesIva5(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva5 = new PdfPTable(1);
        double total = 0;

        PdfPCell importeIva05 = new PdfPCell();
        importeIva05.setBorder(0);
        PdfPTable table = null;

        for (Iva iva : informacion.getFeDetReq().getIva()) {
            if (iva.getID() == 8) {
                total += total + iva.getImporte();
            }
        }

        if (total != 0) {
            importeIva05.setHorizontalAlignment(2);
            importeIva05.setBorderWidthLeft(1);
            importeIva05.setBorderWidthRight(1);
            importeIva05.setPaddingTop(3);
            importeIva05.setPaddingRight(7);

            String texto = String.valueOf(total);
            if (texto.charAt(texto.length() - 1) == '0') {
                table = generarEspacios("IVA 5%: $", texto.replace('.', ',') + "0");
            } else {
                table = generarEspacios("IVA 5%: $", texto);
            }
        }

        importeIva05.addElement(table);

        importeIva5.setWidthPercentage(100);
        importeIva5.addCell(importeIva05);

        return importeIva5;
    }

    private PdfPTable generarImportesIva6(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva6 = new PdfPTable(1);
        double total = 0;

        PdfPCell importeIva25 = new PdfPCell();
        importeIva25.setBorder(0);
        PdfPTable table = null;

        for (Iva iva : informacion.getFeDetReq().getIva()) {
            if (iva.getID() == 9) {
                total += total + iva.getImporte();
            }
        }

        if (total != 0) {
            importeIva25.setHorizontalAlignment(2);
            importeIva25.setBorderWidthLeft(1);
            importeIva25.setBorderWidthRight(1);
            importeIva25.setPaddingTop(3);
            importeIva25.setPaddingRight(7);

            String texto = String.valueOf(total);
            if (texto.charAt(texto.length()) == '0') {
                table = generarEspacios("IVA 2,5%: $", texto.replace('.', ',') + "0");
            } else {
                table = generarEspacios("IVA 2,5%: $", texto);
            }
        }

        importeIva25.addElement(table);

        importeIva6.setWidthPercentage(100);
        importeIva6.addCell(importeIva25);

        return importeIva6;
    }

    private PdfPTable generarImportesIva7(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva7 = new PdfPTable(1);
        double total = 0;

        PdfPCell importeIva0 = new PdfPCell();
        importeIva0.setBorder(0);
        PdfPTable table = null;

        for (Iva iva : informacion.getFeDetReq().getIva()) {
            if (iva.getID() == 3) {
                total += total + iva.getImporte();
            }
        }

        if (total != 0) {
            importeIva0.setHorizontalAlignment(2);
            importeIva0.setBorderWidthLeft(1);
            importeIva0.setBorderWidthRight(1);
            importeIva0.setPaddingTop(3);
            importeIva0.setPaddingRight(7);

            String texto = String.valueOf(total);
            if (texto.charAt(texto.length() - 1) == '0') {
                table = generarEspacios("IVA 0%: $", texto.replace('.', ',') + "0");
            } else {
                table = generarEspacios("IVA 0%: $", texto);
            }
        }

        importeIva0.addElement(table);

        importeIva7.setWidthPercentage(100);
        importeIva7.addCell(importeIva0);

        return importeIva7;
    }

    private PdfPTable generarImportesIva8(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva8 = new PdfPTable(1);

        PdfPCell importeOtrosTributos = new PdfPCell();
        importeOtrosTributos.setHorizontalAlignment(2);
        importeOtrosTributos.setBorder(0);
        importeOtrosTributos.setBorderWidthLeft(1);
        importeOtrosTributos.setBorderWidthRight(1);
        importeOtrosTributos.setPaddingTop(3);
        importeOtrosTributos.setPaddingRight(7);

        PdfPTable table = null;
        String texto = String.valueOf(informacion.getFeDetReq().getImpOpEx());
        if (texto.charAt(texto.length() - 1) == '0') {
            table = generarEspacios("Importe Otros Tributos: $", texto.replace('.', ',') + "0");
        } else {
            table = generarEspacios("Importe Otros Tributos: $", texto);
        }

        importeOtrosTributos.addElement(table);

        importeIva8.setWidthPercentage(100);
        importeIva8.addCell(importeOtrosTributos);

        return importeIva8;
    }

    private PdfPTable generarImportesIva9(Comprobante informacion) throws DocumentException {
        PdfPTable importeIva9 = new PdfPTable(1);

        PdfPCell importeTotal = new PdfPCell();
        importeTotal.setHorizontalAlignment(2);
        importeTotal.setBorder(0);
        importeTotal.setBorderWidthLeft(1);
        importeTotal.setBorderWidthRight(1);
        importeTotal.setBorderWidthBottom(1);
        importeTotal.setPaddingTop(3);
        importeTotal.setPaddingRight(7);
        importeTotal.setPaddingBottom(5);

        PdfPTable table = generarEspacios("Importe Total: $", String.valueOf(informacion.getFeDetReq().getImpTotal()), true);
        importeTotal.addElement(table);

        importeIva9.setWidthPercentage(100);
        importeIva9.addCell(importeTotal);

        return importeIva9;
    }

    private PdfPTable crearHeader(String tipoFactura) {
        PdfPTable contenedor_header = new PdfPTable(1);
        PdfPCell header = new PdfPCell();

        Phrase titulo = new Phrase(tipoFactura);
        titulo.font().setStyle(Font.BOLD);
        titulo.font().setSize(14);
        header.setPaddingTop(7);
        header.setPaddingBottom(7);
        header.setPhrase(titulo);
        header.setHorizontalAlignment(1);
        contenedor_header.addCell(header);
        contenedor_header.setWidthPercentage(100);
        return contenedor_header;
    }

    private PdfPTable crearDivision(Comprobante informacion) {
        PdfPTable contenedor_division = new PdfPTable(new float[]{46, 8, 46});
        contenedor_division.setWidthPercentage(100);

        // Phrase de la empresa
        PdfPCell header_Empresa = new PdfPCell();
        Phrase nombreEmpresa = new Phrase(informacion.getEmpresa().getNombre());
        nombreEmpresa.font().setStyle(Font.BOLD);
        nombreEmpresa.font().setSize(16);
        header_Empresa.setBorder(0);
        header_Empresa.setBorder(Rectangle.LEFT);
        header_Empresa.setPhrase(nombreEmpresa);
        header_Empresa.setHorizontalAlignment(1);
        header_Empresa.setPaddingTop(10);
        contenedor_division.addCell(header_Empresa);

        contenedor_division.addCell(crearCategoria(informacion));

        // Phrase de la empresa
        PdfPCell contenedor_Factura = new PdfPCell();
        Phrase nombreFactura = null;
        if (informacion.getFeCabReq().getCbteTipo() == 1 || informacion.getFeCabReq().getCbteTipo() == 6) {
            nombreFactura = new Phrase("FACTURA");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 2 || informacion.getFeCabReq().getCbteTipo() == 7) {
            nombreFactura = new Phrase("NOTA DE DÉBIDO");

        }

        if (informacion.getFeCabReq().getCbteTipo() == 3 || informacion.getFeCabReq().getCbteTipo() == 8) {
            nombreFactura = new Phrase("NOTA DE CRÉDITO");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 201 || informacion.getFeCabReq().getCbteTipo() == 206) {
            nombreFactura = new Phrase("FACTURA DE CRÉDITO ELECTRÓNICA MiPyME (FCE)");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 202 || informacion.getFeCabReq().getCbteTipo() == 207) {
            nombreFactura = new Phrase("NOTA DE DÉBITO ELECTRÓNICA MiPyME (FCE)");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 203 || informacion.getFeCabReq().getCbteTipo() == 208) {
            nombreFactura = new Phrase("NOTA DE CRÉDITO ELECTRÓNICA MIPYMES (FCE)");
        }

        nombreFactura.font().setStyle(Font.BOLD);
        nombreFactura.font().setSize(16);
        contenedor_Factura.setBorder(Rectangle.RIGHT);
        contenedor_Factura.setPhrase(nombreFactura);
        contenedor_Factura.setHorizontalAlignment(0);
        contenedor_Factura.setPaddingTop(10);
        contenedor_Factura.setPaddingLeft(15);
        contenedor_division.addCell(contenedor_Factura);

        return contenedor_division;
    }

    private PdfPTable crearCategoria(Comprobante informacion) {
        PdfPTable contenedor_categoria = new PdfPTable(1);
        contenedor_categoria.setWidthPercentage(15);

        PdfPCell categoria = new PdfPCell();

        Phrase codigoCategoria = null;
        Phrase nombreCategoria = null;

        if (informacion.getFeCabReq().getCbteTipo() == 1) {
            codigoCategoria = new Phrase("A");
            nombreCategoria = new Phrase("CÓD. 001");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 2) {
            codigoCategoria = new Phrase("A");
            nombreCategoria = new Phrase("CÓD. 002");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 3) {
            codigoCategoria = new Phrase("A");
            nombreCategoria = new Phrase("CÓD. 003");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 6) {
            codigoCategoria = new Phrase("B");
            nombreCategoria = new Phrase("CÓD. 006");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 7) {
            codigoCategoria = new Phrase("B");
            nombreCategoria = new Phrase("CÓD. 007");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 8) {
            codigoCategoria = new Phrase("B");
            nombreCategoria = new Phrase("CÓD. 008");
        }

        // ELECTRÓNICA MiPyME (FCE) 
        if (informacion.getFeCabReq().getCbteTipo() == 201) {
            codigoCategoria = new Phrase("A");
            nombreCategoria = new Phrase("CÓD. 201");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 202) {
            codigoCategoria = new Phrase("A");
            nombreCategoria = new Phrase("CÓD. 202");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 203) {
            codigoCategoria = new Phrase("A");
            nombreCategoria = new Phrase("CÓD. 203");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 206) {
            codigoCategoria = new Phrase("B");
            nombreCategoria = new Phrase("CÓD. 206");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 207) {
            codigoCategoria = new Phrase("B");
            nombreCategoria = new Phrase("CÓD. 207");
        }

        if (informacion.getFeCabReq().getCbteTipo() == 208) {
            codigoCategoria = new Phrase("B");
            nombreCategoria = new Phrase("CÓD. 208");
        }

        codigoCategoria.font().setStyle(Font.BOLD);
        codigoCategoria.font().setSize(19);
        categoria.setBorder(-1);
        categoria.setPhrase(codigoCategoria);
        categoria.setHorizontalAlignment(1);
        contenedor_categoria.addCell(categoria);

        PdfPCell codCategoria = new PdfPCell();

        nombreCategoria.font().setStyle(Font.BOLD);
        nombreCategoria.font().setSize(8);
        codCategoria.setBorder(-1);
        codCategoria.setPhrase(nombreCategoria);
        codCategoria.setHorizontalAlignment(1);
        contenedor_categoria.addCell(codCategoria);

        return contenedor_categoria;
    }

    private PdfPCell crearInfoEmpresa(Comprobante informacion) throws DocumentException {
        PdfPCell informacionEmpresa = new PdfPCell();
        informacionEmpresa.setBorder(Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
        informacionEmpresa.setPaddingTop(35);

        PdfPTable crearInfoEmpresaTable = new PdfPTable(1);

        PdfPCell razonSocial = new PdfPCell();
        razonSocial.setBorder(0);
        PdfPTable razonSocialTable = generarNegrita("Razón Social: ", informacion.getEmpresa().getRazonSocial(), 23, 77);
        razonSocial.addElement(razonSocialTable);

        PdfPCell domicCom = new PdfPCell();
        domicCom.setBorder(0);
        PdfPTable domicComTable = generarNegrita("Domicilio Comercial: ", informacion.getEmpresa().getDomicilioComercial(), 33, 67);
        domicCom.addElement(domicComTable);

        PdfPCell condIva = new PdfPCell();
        condIva.setBorder(0);
        PdfPTable condIvaTable = generarNegrita("Condición frente al IVA: ", informacion.getEmpresa().getCondIva(), 38, 62);
        condIva.addElement(condIvaTable);

        crearInfoEmpresaTable.setWidthPercentage(100);
        crearInfoEmpresaTable.addCell(razonSocial);
        crearInfoEmpresaTable.addCell(domicCom);
        crearInfoEmpresaTable.addCell(condIva);

        informacionEmpresa.setPaddingLeft(4);
        informacionEmpresa.addElement(crearInfoEmpresaTable);

        return informacionEmpresa;
    }

    private PdfPCell crearInfoFactura(Comprobante informacion, String[] informacionCAE) throws DocumentException {
        PdfPCell informacionFactura = new PdfPCell();
        informacionFactura.setBorder(Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);

        PdfPTable informacionFacturaTable = new PdfPTable(1);

        PdfPCell ptoVtaCompNro = new PdfPCell();
        ptoVtaCompNro.setPaddingLeft(30);
        ptoVtaCompNro.setBorder(0);
        Phrase ptoVta_NroComp = new Phrase("Punto de Venta:  " + String.format("%0" + 5 + "d", informacion.getFeCabReq().getPtoVta()) + "          Comp. Nro:  " + String.format("%0" + 8 + "d", Integer.valueOf(informacionCAE[7])));
        ptoVta_NroComp.font().setSize(8);
        ptoVta_NroComp.font().setStyle(Font.BOLD);
        ptoVtaCompNro.addElement(ptoVta_NroComp);

        PdfPCell fchEmision = new PdfPCell();
        fchEmision.setPaddingLeft(30);
        fchEmision.setBorder(0);
        Phrase fechaEmision = new Phrase("Fecha de emisión:  " + informacion.getFeDetReq().getCbteFch().substring(5, 7) + "/" + informacion.getFeDetReq().getCbteFch().substring(3, 5) + "/" + informacion.getFeDetReq().getCbteFch().substring(0, 4));
        fechaEmision.font().setSize(8);
        fechaEmision.font().setStyle(Font.BOLD);
        fchEmision.addElement(fechaEmision);

        String cuit = null;
        if (String.valueOf(informacion.getAuth().getCuit()).length() == 10) {
            cuit = String.valueOf(informacion.getAuth().getCuit()).substring(0, 2) + "-" + String.valueOf(informacion.getAuth().getCuit()).substring(2, 9) + "-" + String.valueOf(informacion.getAuth().getCuit()).charAt(9);
        }
        if (String.valueOf(informacion.getAuth().getCuit()).length() == 11) {
            cuit = String.valueOf(informacion.getAuth().getCuit()).substring(0, 2) + "-" + String.valueOf(informacion.getAuth().getCuit()).substring(2, 10) + "-" + String.valueOf(informacion.getAuth().getCuit()).charAt(10);
        }
        PdfPCell cuitFactura = new PdfPCell();
        cuitFactura.setPaddingLeft(28);
        cuitFactura.setBorder(0);
        PdfPTable cuitFacturaTable = generarNegrita("CUIT: ", cuit, 15, 85);
        cuitFactura.addElement(cuitFacturaTable);

        PdfPCell ingBrutos = new PdfPCell();
        ingBrutos.setPaddingLeft(28);
        ingBrutos.setBorder(0);
        PdfPTable ingBrutosTable = generarNegrita("Ingresos Brutos: ", informacion.getEmpresa().getIngBrutos(), 30, 60);
        ingBrutos.addElement(ingBrutosTable);

        PdfPCell fchInicioAct = new PdfPCell();
        fchInicioAct.setPaddingLeft(28);
        fchInicioAct.setBorder(0);
        PdfPTable fchInicioActTable = generarNegrita("Fecha de Inicio de Actividades: ", informacion.getEmpresa().getFchIniAct(), 55, 45);
        fchInicioAct.addElement(fchInicioActTable);

        informacionFacturaTable.setWidthPercentage(100);
        informacionFacturaTable.addCell(ptoVtaCompNro);
        informacionFacturaTable.addCell(fchEmision);
        informacionFacturaTable.addCell(cuitFactura);
        informacionFacturaTable.addCell(ingBrutos);
        informacionFacturaTable.addCell(fchInicioAct);

        informacionFactura.setPaddingLeft(10);
        informacionFactura.addElement(informacionFacturaTable);

        return informacionFactura;
    }

    private PdfPTable crearTablaInformacionB(Comprobante informacion) {
        PdfPTable tablaInformacion = new PdfPTable(new float[]{8, 35, 8, 8, 15, 8, 10, 10});
        tablaInformacion.setWidthPercentage(100);

        // Creacion de la columna codigo
        PdfPCell codigo = new PdfPCell();
        Phrase codigoText = new Phrase("Código");
        codigoText.font().setSize(8);
        codigoText.font().setStyle(Font.BOLD);
        codigo.setHorizontalAlignment(1);
        codigo.setPhrase(codigoText);
        codigo.setBackgroundColor(Color.LIGHT_GRAY);
        codigo.setPaddingBottom(8);
        tablaInformacion.addCell(codigo);

        // Creacion de la columna producto/servicio
        PdfPCell prodServ = new PdfPCell();
        Phrase prodServText = new Phrase("Producto / Servicio");
        prodServText.font().setSize(8);
        prodServText.font().setStyle(Font.BOLD);
        prodServ.setPhrase(prodServText);
        prodServ.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(prodServ);

        // Creacion de la columna cantidad
        PdfPCell cantidad = new PdfPCell();
        Phrase cantidadText = new Phrase("Cantidad");
        cantidadText.font().setSize(8);
        cantidadText.font().setStyle(Font.BOLD);
        cantidad.setHorizontalAlignment(1);
        cantidad.setPhrase(cantidadText);
        cantidad.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(cantidad);

        // Creacion de la columna unidad medida
        PdfPCell uMedida = new PdfPCell();
        Phrase uMedidaText = new Phrase("U. Medida");
        uMedidaText.font().setSize(8);
        uMedidaText.font().setStyle(Font.BOLD);
        uMedida.setHorizontalAlignment(1);
        uMedida.setPhrase(uMedidaText);
        uMedida.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(uMedida);

        // Creacion de la columna precio unitario
        PdfPCell pUnitario = new PdfPCell();
        Phrase pUnitarioText = new Phrase("Precio Unit.");
        pUnitarioText.font().setSize(8);
        pUnitarioText.font().setStyle(Font.BOLD);
        pUnitario.setHorizontalAlignment(1);
        pUnitario.setPhrase(pUnitarioText);
        pUnitario.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(pUnitario);

        // Creacion de la columna producto/servicio
        PdfPCell bonif = new PdfPCell();
        Phrase bonifText = new Phrase("% Bonif.");
        bonifText.font().setSize(8);
        bonifText.font().setStyle(Font.BOLD);
        bonif.setHorizontalAlignment(1);
        bonif.setPhrase(bonifText);
        bonif.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(bonif);

        // Creacion de la columna codigo
        PdfPCell impBonif = new PdfPCell();
        Phrase impBonifText = new Phrase("Imp. Bonif");
        impBonifText.font().setSize(8);
        impBonifText.font().setStyle(Font.BOLD);
        impBonif.setHorizontalAlignment(1);
        impBonif.setPhrase(impBonifText);
        impBonif.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(impBonif);

        // Creacion de la columna producto/servicio
        PdfPCell subtotal = new PdfPCell();
        Phrase subtotalText = new Phrase("Subtotal");
        subtotalText.font().setSize(8);
        subtotalText.font().setStyle(Font.BOLD);
        subtotal.setHorizontalAlignment(1);
        subtotal.setPhrase(subtotalText);
        subtotal.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(subtotal);

        for (DetalleFactura detalleFactura : informacion.getDetalleFactura()) {

            // Carga de la columna de codigo
            PdfPCell infoCodigo = new PdfPCell();
            Phrase infoCodigoTxt = new Phrase(String.valueOf(detalleFactura.getCodigo()));
            infoCodigoTxt.font().setSize(8);
            infoCodigo.setPhrase(infoCodigoTxt);
            infoCodigo.setHorizontalAlignment(1);
            infoCodigo.setBorder(0);
            tablaInformacion.addCell(infoCodigo);

            // Carga de la columna de producto/servicio
            PdfPCell infoProdServ = new PdfPCell();
            Phrase infoProdServTxt = new Phrase(detalleFactura.getProdServ());
            infoProdServTxt.font().setSize(8);
            infoProdServ.setPhrase(infoProdServTxt);
            infoProdServ.setBorder(0);
            tablaInformacion.addCell(infoProdServ);

            // Carga de la columna de cantidad
            PdfPCell infoCantidad = new PdfPCell();
            Phrase infoCantidadTxt = new Phrase(String.valueOf(detalleFactura.getCantidad()));
            infoCantidadTxt.font().setSize(8);
            infoCantidad.setPhrase(infoCantidadTxt);
            infoCantidad.setHorizontalAlignment(1);
            infoCantidad.setBorder(0);
            tablaInformacion.addCell(infoCantidad);

            // Carga de la columna de unidad de medida
            PdfPCell infoUMedida = new PdfPCell();
            Phrase infoUMedidaTxt = new Phrase(detalleFactura.getUnidadMedida());
            infoUMedidaTxt.font().setSize(8);
            infoUMedida.setPhrase(infoUMedidaTxt);
            infoUMedida.setHorizontalAlignment(1);
            infoUMedida.setBorder(0);
            tablaInformacion.addCell(infoUMedida);

            // Carga de la columna de precio unitario
            PdfPCell infoPrecUnit = new PdfPCell();
            Phrase infoPrecUnitTxt = new Phrase(String.valueOf(detalleFactura.getPrecioUnitario()));
            infoPrecUnitTxt.font().setSize(8);
            infoPrecUnit.setPhrase(infoPrecUnitTxt);
            infoPrecUnit.setHorizontalAlignment(1);
            infoPrecUnit.setBorder(0);
            tablaInformacion.addCell(infoPrecUnit);

            // Carga de la columna de porcentaje bonificacion
            PdfPCell infoPorcBonif = new PdfPCell();
            Phrase infoPorcBonifTxt = new Phrase(String.valueOf(detalleFactura.getBonificacion()));
            infoPorcBonifTxt.font().setSize(8);
            infoPorcBonif.setPhrase(infoPorcBonifTxt);
            infoPorcBonif.setHorizontalAlignment(1);
            infoPorcBonif.setBorder(0);
            tablaInformacion.addCell(infoPorcBonif);

            // Carga de la columna de imp bonificacion
            PdfPCell infoImpBonif = new PdfPCell();
            Phrase infoImpBonifTxt = new Phrase(String.valueOf(detalleFactura.getImpBonificacion()));
            infoImpBonifTxt.font().setSize(8);
            infoImpBonif.setPhrase(infoImpBonifTxt);
            infoImpBonif.setHorizontalAlignment(1);
            infoImpBonif.setBorder(0);
            tablaInformacion.addCell(infoImpBonif);

            // Carga de la columna de subtotal
            PdfPCell infoSubtotal = new PdfPCell();
            Phrase infoSubtotalTxt = new Phrase(String.valueOf(detalleFactura.getSubtotal()));
            infoSubtotalTxt.font().setSize(8);
            infoSubtotal.setPhrase(infoSubtotalTxt);
            infoSubtotal.setHorizontalAlignment(1);
            infoSubtotal.setBorder(0);
            tablaInformacion.addCell(infoSubtotal);
        }

        return tablaInformacion;
    }

    public static PdfPTable crearTablaInformacionA(Comprobante informacion) {
        PdfPTable tablaInformacion = new PdfPTable(new float[]{/*cod*/8,/*p/s*/ 30,/*cant*/ 8,/*uMed*/ 8,/*pUn*/ 12,/*%B*/ 7,/*subt*/ 10,/*aIVA*/ 7,/*subtIVA*/ 10});
        tablaInformacion.setWidthPercentage(100);

        // Creacion de la columna codigo
        PdfPCell codigo = new PdfPCell();
        Phrase codigoText = new Phrase("Código");
        codigoText.font().setSize(8);
        codigoText.font().setStyle(Font.BOLD);
        codigo.setHorizontalAlignment(1);
        codigo.setPhrase(codigoText);
        codigo.setBackgroundColor(Color.LIGHT_GRAY);
        codigo.setPaddingBottom(8);
        tablaInformacion.addCell(codigo);

        // Creacion de la columna producto/servicio
        PdfPCell prodServ = new PdfPCell();
        Phrase prodServText = new Phrase("Producto / Servicio");
        prodServText.font().setSize(8);
        prodServText.font().setStyle(Font.BOLD);
        prodServ.setPhrase(prodServText);
        prodServ.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(prodServ);

        // Creacion de la columna cantidad
        PdfPCell cantidad = new PdfPCell();
        Phrase cantidadText = new Phrase("Cantidad");
        cantidadText.font().setSize(8);
        cantidadText.font().setStyle(Font.BOLD);
        cantidad.setHorizontalAlignment(1);
        cantidad.setPhrase(cantidadText);
        cantidad.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(cantidad);

        // Creacion de la columna unidad medida
        PdfPCell uMedida = new PdfPCell();
        Phrase uMedidaText = new Phrase("U. medida");
        uMedidaText.font().setSize(8);
        uMedidaText.font().setStyle(Font.BOLD);
        uMedida.setHorizontalAlignment(1);
        uMedida.setPhrase(uMedidaText);
        uMedida.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(uMedida);

        // Creacion de la columna precio unitario
        PdfPCell pUnitario = new PdfPCell();
        Phrase pUnitarioText = new Phrase("Precio Unit.");
        pUnitarioText.font().setSize(8);
        pUnitarioText.font().setStyle(Font.BOLD);
        pUnitario.setHorizontalAlignment(1);
        pUnitario.setPhrase(pUnitarioText);
        pUnitario.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(pUnitario);

        // Creacion de la columna producto/servicio
        PdfPCell bonif = new PdfPCell();
        Phrase bonifText = new Phrase("% Bonif.");
        bonifText.font().setSize(8);
        bonifText.font().setStyle(Font.BOLD);
        bonif.setHorizontalAlignment(1);
        bonif.setPhrase(bonifText);
        bonif.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(bonif);

        // Creacion de la columna Subtotal
        PdfPCell subtotal = new PdfPCell();
        Phrase subtotalText = new Phrase("Subtotal");
        subtotalText.font().setSize(8);
        subtotalText.font().setStyle(Font.BOLD);
        subtotal.setHorizontalAlignment(1);
        subtotal.setPhrase(subtotalText);
        subtotal.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(subtotal);

        // Creacion de la columna Alicuota IVA
        PdfPCell alicuotaIVA = new PdfPCell();
        Phrase alicuotaIVAText = new Phrase("Alicuota IVA");
        alicuotaIVAText.font().setSize(8);
        alicuotaIVAText.font().setStyle(Font.BOLD);
        alicuotaIVA.setHorizontalAlignment(1);
        alicuotaIVA.setPhrase(alicuotaIVAText);
        alicuotaIVA.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(alicuotaIVA);

        // Creacion de la columna Subtotal c/IVA
        PdfPCell subtotalIVA = new PdfPCell();
        Phrase subtotalIVAText = new Phrase("Subtotal c/IVA");
        subtotalIVAText.font().setSize(8);
        subtotalIVAText.font().setStyle(Font.BOLD);
        subtotalIVA.setHorizontalAlignment(1);
        subtotalIVA.setPhrase(subtotalIVAText);
        subtotalIVA.setBackgroundColor(Color.LIGHT_GRAY);
        tablaInformacion.addCell(subtotalIVA);

        for (DetalleFactura detalleFactura : informacion.getDetalleFactura()) {

            // Carga de la columna de codigo
            PdfPCell infoCodigo = new PdfPCell();
            Phrase infoCodigoTxt = new Phrase(String.valueOf(detalleFactura.getCodigo()));
            infoCodigoTxt.font().setSize(8);
            infoCodigo.setPhrase(infoCodigoTxt);
            infoCodigo.setHorizontalAlignment(1);
            infoCodigo.setBorder(0);
            tablaInformacion.addCell(infoCodigo);

            // Carga de la columna de producto/servicio
            PdfPCell infoProdServ = new PdfPCell();
            Phrase infoProdServTxt = new Phrase(detalleFactura.getProdServ());
            infoProdServTxt.font().setSize(8);
            infoProdServ.setPhrase(infoProdServTxt);
            infoProdServ.setBorder(0);
            tablaInformacion.addCell(infoProdServ);

            // Carga de la columna de cantidad
            PdfPCell infoCantidad = new PdfPCell();
            Phrase infoCantidadTxt = new Phrase(String.valueOf(detalleFactura.getCantidad()));
            infoCantidadTxt.font().setSize(8);
            infoCantidad.setPhrase(infoCantidadTxt);
            infoCantidad.setHorizontalAlignment(1);
            infoCantidad.setBorder(0);
            tablaInformacion.addCell(infoCantidad);

            // Carga de la columna de unidad de medida
            PdfPCell infoUMedida = new PdfPCell();
            Phrase infoUMedidaTxt = new Phrase(detalleFactura.getUnidadMedida());
            infoUMedidaTxt.font().setSize(8);
            infoUMedida.setPhrase(infoUMedidaTxt);
            infoUMedida.setHorizontalAlignment(1);
            infoUMedida.setBorder(0);
            tablaInformacion.addCell(infoUMedida);

            // Carga de la columna de precio unitario
            PdfPCell infoPrecUnit = new PdfPCell();
            Phrase infoPrecUnitTxt = new Phrase(String.valueOf(detalleFactura.getPrecioUnitario()));
            infoPrecUnitTxt.font().setSize(8);
            infoPrecUnit.setPhrase(infoPrecUnitTxt);
            infoPrecUnit.setHorizontalAlignment(1);
            infoPrecUnit.setBorder(0);
            tablaInformacion.addCell(infoPrecUnit);

            // Carga de la columna de porcentaje bonificacion
            PdfPCell infoPorcBonif = new PdfPCell();
            Phrase infoPorcBonifTxt = new Phrase(String.valueOf(detalleFactura.getBonificacion()));
            infoPorcBonifTxt.font().setSize(8);
            infoPorcBonif.setPhrase(infoPorcBonifTxt);
            infoPorcBonif.setHorizontalAlignment(1);
            infoPorcBonif.setBorder(0);
            tablaInformacion.addCell(infoPorcBonif);

            // Carga de la columna de subtotal
            PdfPCell infoSubtotal = new PdfPCell();
            Phrase infoSubtotalTxt = new Phrase(String.valueOf(detalleFactura.getSubtotal()));
            infoSubtotalTxt.font().setSize(8);
            infoSubtotal.setPhrase(infoSubtotalTxt);
            infoSubtotal.setHorizontalAlignment(1);
            infoSubtotal.setBorder(0);
            tablaInformacion.addCell(infoSubtotal);

            // Carga de la columna de alicuota IVA
            PdfPCell infoAlicIva = new PdfPCell();
            Phrase infoAlicIVATxt = new Phrase(String.valueOf(detalleFactura.getAlicuotaIVA()));
            infoAlicIVATxt.font().setSize(8);
            infoAlicIva.setPhrase(infoAlicIVATxt);
            infoAlicIva.setHorizontalAlignment(1);
            infoAlicIva.setBorder(0);
            tablaInformacion.addCell(infoAlicIva);

            // Carga de la columna de subtotalConIVA
            PdfPCell infoSubtotalCIVA = new PdfPCell();
            Phrase infoSubtotalCIVATxt = new Phrase(String.valueOf(detalleFactura.getSubTotalCIVA()));
            infoSubtotalCIVATxt.font().setSize(8);
            infoSubtotalCIVA.setPhrase(infoSubtotalCIVATxt);
            infoSubtotalCIVA.setHorizontalAlignment(1);
            infoSubtotalCIVA.setBorder(0);
            tablaInformacion.addCell(infoSubtotalCIVA);
        }

        return tablaInformacion;
    }

    private PdfPTable crearFooter(String[] informacionCAE) {
        PdfPTable tablaFooter = new PdfPTable(new float[]{20, 22, 12, 46});
        tablaFooter.setWidthPercentage(100);

        // Aplicacion de imagen
        PdfPCell cellQR1 = new PdfPCell();
        Image imagenQR = null;
        try {
            InputStream archivo = new ClassPathResource("images/logoQR.png").getInputStream();
            imagenQR = Image.getInstance(archivo.readAllBytes());
            cellQR1.setImage(imagenQR);
            cellQR1.setBorder(-1);
        } catch (BadElementException | IOException ex) {
            Logger.getLogger(GeneracionPDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Aplicacion del logo de Afip
        PdfPCell cellQR2 = new PdfPCell();
        cellQR2.addElement(generarLogoAfip());
        cellQR2.setBorder(-1);

        // Aplicacion del numero de pagina
        Phrase txtPagina = new Phrase("Pág. 1/1");
        txtPagina.font().setSize(12);
        txtPagina.font().setStyle(Font.BOLD);
        PdfPCell cellQR3 = new PdfPCell(txtPagina);
        cellQR3.setPaddingTop(15);
        cellQR3.setBorder(-1);
        cellQR3.setHorizontalAlignment(1);

        PdfPCell cellQR4 = new PdfPCell();
        cellQR4.addElement(generarInfoCAE(informacionCAE));
        cellQR4.setPaddingTop(15);
        cellQR4.setBorder(-1);

        tablaFooter.addCell(cellQR1);
        tablaFooter.addCell(cellQR2);
        tablaFooter.addCell(cellQR3);
        tablaFooter.addCell(cellQR4);

        return tablaFooter;
    }

    private PdfPTable generarLogoAfip() {
        PdfPTable tablaLogoAfip = new PdfPTable(1);
        PdfPCell cellQR2 = new PdfPCell();
        Image imagenLogo = null;
        try {
            InputStream archivo = new ClassPathResource("images/logo_afip.jpg").getInputStream();
            imagenLogo = Image.getInstance(archivo.readAllBytes());
            imagenLogo.setWidthPercentage(70);
            cellQR2.setImage(imagenLogo);
            cellQR2.setBorder(-1);
            cellQR2.setPaddingTop(10);
        } catch (BadElementException | IOException ex) {
            Logger.getLogger(GeneracionPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
        Phrase textoLogoAfip = new Phrase("Comprobante Autorizado");
        textoLogoAfip.font().setSize(9);
        textoLogoAfip.font().setStyle(Font.BOLD);
        tablaLogoAfip.addCell(cellQR2);
        PdfPCell textoLogoAfipTxt = new PdfPCell(textoLogoAfip);
        textoLogoAfipTxt.setBorder(-1);
        tablaLogoAfip.addCell(textoLogoAfipTxt);

        return tablaLogoAfip;
    }

    private PdfPTable generarInfoCAE(String[] informacionCAE) {
        PdfPTable tablasCAE = new PdfPTable(new float[]{55, 45});

        //Linea de CAE
        Phrase txtCae = new Phrase("CAE: ");
        txtCae.font().setStyle(Font.BOLD);
        txtCae.font().setSize(9);
        PdfPCell cae = new PdfPCell();
        cae.setPhrase(txtCae);
        cae.setBorder(-1);
        cae.setHorizontalAlignment(2);
        tablasCAE.addCell(cae);

        Phrase txtCaeInfo = new Phrase(informacionCAE[5] + "\n\n");
        txtCaeInfo.font().setSize(10);
        PdfPCell caeInfo = new PdfPCell();
        caeInfo.setPhrase(txtCaeInfo);
        caeInfo.setBorder(-1);
        tablasCAE.addCell(caeInfo);

        //Linea de Vencimiento de CAE
        Phrase txtCaeVto = new Phrase("Fecha de Vto. del CAE: ");
        txtCaeVto.font().setStyle(Font.BOLD);
        txtCaeVto.font().setSize(9);
        PdfPCell caeVto = new PdfPCell();
        caeVto.setPhrase(txtCaeVto);
        caeVto.setBorder(-1);
        caeVto.setHorizontalAlignment(2);
        tablasCAE.addCell(caeVto);

        Phrase txtCaeVtoInfo = new Phrase(informacionCAE[6].substring(6, 8) + "/" + informacionCAE[6].substring(4, 6) + "/" + informacionCAE[6].substring(0, 4));
        txtCaeVtoInfo.font().setSize(10);
        PdfPCell caeVtoInfo = new PdfPCell();
        caeVtoInfo.setPhrase(txtCaeVtoInfo);
        caeVtoInfo.setBorder(-1);
        tablasCAE.addCell(caeVtoInfo);

        return tablasCAE;
    }

    public String getUrlPublica() {
        return urlPublica;
    }

}

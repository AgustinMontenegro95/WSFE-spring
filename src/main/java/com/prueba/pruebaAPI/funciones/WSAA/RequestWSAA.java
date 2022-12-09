package com.prueba.pruebaAPI.funciones.WSAA;

//Import del servidor de horario de otro paquete
import com.prueba.pruebaAPI.funciones.horarioServerAfip.HorarioAfip;

// Import de Tipo Input / Output
import java.io.*;
import java.security.InvalidAlgorithmParameterException;

// Import de tipo Security
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;

// Import de formateo de fecha
import java.text.SimpleDateFormat;

// Import de utilidades
import java.util.ArrayList;
import java.util.Date;

// Import de XML
import javax.xml.rpc.ParameterMode;

// Import de Axis
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.Base64;
import org.apache.axis.encoding.XMLType;
import org.bouncycastle.cms.CMSException;

// Import de BouncyCastle
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RequestWSAA {

    // Objeto para obtener horario del servidor de AFIP
    HorarioAfip horario = new HorarioAfip();

    //Constructor vacio
    public RequestWSAA() {
    }

    /* - 1er Paso -
    Generacion del mensaje TRA (XML de solicitud)
    Parametros de entrada: ****
    Parametro de salida: LoginTicketRequest (String) = Mensaje XML de solicitud que ira encriptado en el CMS */
    private String generarTRA() {

        String LoginTicketRequest_xml;

        // Formato de la fecha y hora valido por el servidor
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        // Se crea los momentos de generacion y de expiracion que iran en el XML
        Date momentoGen = new Date(horario.getNTPDate());
        Date momentoVen = new Date(momentoGen.getTime()+ 150000);

        // Generacion de un ID unico, creado en base al momento de generacion del objeto momentoGen
        String uniqueId = Long.toString(momentoGen.getTime() / 1000);

        // Asignacion de la estrucura del XML y los parametros obtenidos
        LoginTicketRequest_xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<loginTicketRequest version=\"1.0\">"
                + "<header>"
                + "<uniqueId>" + uniqueId + "</uniqueId>"
                + "<generationTime>" + formatter.format(momentoGen) + "</generationTime>"
                + "<expirationTime>" + formatter.format(momentoVen) + "</expirationTime>"
                + "</header>"
                + "<service>" + "wsfe" + "</service>"
                + "</loginTicketRequest>";

        return LoginTicketRequest_xml;
    }

    /* - 2do Paso -
    Generacion del mensaje CMS 
    Parametros de entrada: ****
    Parametro de salida: mensajeCMS (byte[]) = Cadena de bytes que representa el mensaje CMS a enviar */
    private byte[] generarCMS() {

        //Objetos utilizados
        PrivateKey privKey = null;
        X509Certificate pCertificate = null;
        byte[] mensajeCMS = null;
        CertStore cstore = null;
        String LoginTicketRequest_xml;

        try {
            // Se crea el almacen para la Key y el Certificado de tipo PKCS12 (tipo que admite el tipo de certificado.pfx)
            KeyStore ks = KeyStore.getInstance("PKCS12");

            // Se crea un InputStream para cargar el certificado junto con la clave en el almacen de key (ks) y luego se cierra el InputStream
            try {
                InputStream hiloEntrada = new FileInputStream("C:\\Users\\maximiliano.coronel\\Documents\\NetBeansProjects\\WSFE-spring\\certs\\MiCertificado.pfx");
                ks.load(hiloEntrada, "test".toCharArray());
                hiloEntrada.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Se obtiene la clave y el certificado desde el almacen de Keys
            privKey = (PrivateKey) ks.getKey(ks.aliases().nextElement(), "test".toCharArray());
            pCertificate = (X509Certificate) ks.getCertificate(ks.aliases().nextElement());

            // Se crea la lista de certificados para agregar posteriormente
            ArrayList<X509Certificate> listaCertificados = new ArrayList<>();
            listaCertificados.add(pCertificate);

            // Se verifica si BouncyCastle esta dentro de los proveedores de seguridad, en caso de no serlo, lo agrega como proveedor
            if (Security.getProvider("BC") == null) {
                Security.addProvider(new BouncyCastleProvider());
            }

            // En el almacen de certificados se obtiene una instancia de tipo coleccion con los datos de la lista anterior, y se agrega como proveedor de seguridad a BouncyCastle
            cstore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(listaCertificados), "BC");

        } catch (InvalidAlgorithmParameterException | KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | UnrecoverableKeyException | CertificateException e) {
            System.out.println(e.getMessage());
        }

        // Crea el LoginTicketRequest (mensaje XML)
        LoginTicketRequest_xml = generarTRA();

        // Crea el mensaje CMS
        try {
            
            // Se utiliza un generador de datos firmados de tipo CMS para firmar y añadir el certificado
            CMSSignedDataGenerator generador = new CMSSignedDataGenerator();
            generador.addSigner(privKey, pCertificate, CMSSignedDataGenerator.DIGEST_SHA1);
            generador.addCertificatesAndCRLs(cstore);
            
            // Se agrega el LoginTicketRequest y se firma
            CMSProcessable datos = new CMSProcessableByteArray(LoginTicketRequest_xml.getBytes());
            CMSSignedData datosFirmados = generador.generate(datos, true, "BC");
            
            // Se obtiene la cadena de bytes correspondientes al mensaje CMS final
            mensajeCMS = datosFirmados.getEncoded();
        } catch (IOException | IllegalArgumentException | NoSuchAlgorithmException | NoSuchProviderException | CertStoreException | CMSException e) {
            System.out.println(e.getMessage());
        }

        return mensajeCMS;
    }

    /* - 3er Paso -
    Llamado para solicitar el WSAA
    Parametros de entrada: ****
    Parametro de salida: respuesta (String) = Respuesta del servidor */
    public String llamarWSAA() {

        // Objetos
        byte[] xml_cms = generarCMS();
        String respuesta = null;
        // Direccion a donde se solicita el WSAA (homologacion)
        String endPoint = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";

        // Crea un servicio y realiza una llamada a la direccion provista anteriormente
        try {
            Service servicio = new Service();
            Call llamada = (Call) servicio.createCall();

            llamada.setTargetEndpointAddress(new java.net.URL(endPoint));
            llamada.setOperationName("loginCms");
            llamada.addParameter("request", XMLType.XSD_STRING, ParameterMode.IN);
            llamada.setReturnType(XMLType.XSD_STRING);

            // Al realizar la llamada, encodea el mensaje CMS en Base64 para que sea aceptado por el servidor 
            respuesta = (String) llamada.invoke(new Object[]{Base64.encode(xml_cms)});

        } catch (Exception  e) {
            System.out.println(e.getMessage());
        }

        return respuesta;
    }
    
}

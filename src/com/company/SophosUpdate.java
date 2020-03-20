package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SophosUpdate {
    private static final String SOPHOS_URL="https://downloads.sophos.com/downloads/info/latest_IDE.xml";

    //private static final String SOPHOS_LOCAL_PATH = "C:\\Program Files (x86)\\Sophos\\Sophos Anti-Virus\\";
    private static final String SOPHOS_LOCAL_PATH = "C:\\Users\\utente\\Downloads\\temp\\Sophos\\Sophos Anti-Virus\\";

    // Proxy variables
    Proxy proxy = Proxy.NO_PROXY;
    private static final String USER_AGENT = "User-Agent";
    private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";

    //private String m_jsonData;

    private LatestUpdate latestUpdate;

    public SophosUpdate() {
        getProxy();
    }

    public static void main(String[] args) {
        SophosUpdate sophosUpdate = new SophosUpdate();
        //sophosUpdate.getUpdate();
        if (sophosUpdate.parsePage()) {
            sophosUpdate.checkFile();
        }
    }

    private boolean checkFile() {
        boolean retVal = false;
        File updateFile = new File(SOPHOS_LOCAL_PATH+latestUpdate.name);
        if (!updateFile.exists()){
            System.out.println("Il file "+updateFile.getAbsolutePath()+" non esiste");
            System.out.print ("Sophos NON aggiornato. Ultimo aggiornamento: ");
            File latestLocalUopdate =  getLatestLocalUpdate();
            if (latestLocalUopdate==null){
                System.out.println("Nessun aggiornamento trovato nella directory: "+SOPHOS_LOCAL_PATH);
                return false;
            } else {
                System.out.println(latestLocalUopdate.getAbsolutePath());
                return false;
            }

        } else {
            System.out.println("Sophos locale aggiornato");
            System.out.println("\t"+updateFile.getAbsolutePath());
            Date date = new Timestamp(updateFile.lastModified());
            //System.out.println("\t"+updateFile.lastModified());
            System.out.println("\t ultima modifica: "+date);
            retVal = true;
        }
        return retVal;
    }

    private File getLatestLocalUpdate(){
        File directory = new File(SOPHOS_LOCAL_PATH);
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".ide");
            }
        };

        File[] files = directory.listFiles(filenameFilter);

        if (files ==null) {
            //System.out.println("File di aggiornamento non trovati in:\n\t"+SOPHOS_LOCAL_PATH);
            return null;
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o1.lastModified()).compareTo(o2.lastModified());
            }
        });

//        System.out.println("Ultimo file di aggiornamento trovato: "+ files[0].getAbsolutePath());
//        System.out.println("File di aggiornamento completi: ");
//        for(File f : files){
//            System.out.println(f.getAbsolutePath());
//        }

        return files[0];
    }

//    private HttpStatusCodes getUpdate() throws IOException {
//        HttpStatusCodes respReturnCode = HttpStatusCodes.UNKNOWN_CODE;
//
//        HttpsURLConnection urlConnection;
//        URL url = new URL(SOPHOS_URL);
//        urlConnection = (HttpsURLConnection) url.openConnection(proxy);
//
//        // Indispensabile altrimenti si ottiene un errore 403
//        urlConnection.addRequestProperty(USER_AGENT, USER_AGENT_VALUE);
//
//        int respCode = urlConnection.getResponseCode();
//        respReturnCode = HttpStatusCodes.intToHttpStatusCode(respCode);
//        if (respCode == 200) {
//            m_jsonData = getData(urlConnection);
//        } else {
//            m_jsonData = null;
//        }
//        return respReturnCode;
//    }
//
//    /**
//     * Ritorna la pagina letta da un HttpURLConnection aperta
//     *
//     * @param urlConnection Url da aprire
//     * @return Pagina remota
//     * @throws IOException Eccezione lanciata in caso di errore nel recuperare la pagina
//     */
//    private String getData(HttpURLConnection urlConnection) throws IOException {
//        InputStream data;
//        BufferedReader reader;
//        data = urlConnection.getInputStream();
//        reader = new BufferedReader(new InputStreamReader(data, StandardCharsets.UTF_8));
//        String line;
//        StringBuilder sb = new StringBuilder();
//        while ((line = reader.readLine()) != null) {
//            sb.append(line);
//        }
//
//        // Per sicurezza
//        reader.close();
//        data.close();
//        return sb.toString();
//    }

    private boolean parsePage()  {
        boolean retVal = false;

        System.out.println("Recupero l'ultimo update dal sito Sophos");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = documentBuilder.parse(SOPHOS_URL);
        } catch (SAXException e) {
            System.out.println("Errore nel parsing della pagina");
            return false;
        } catch (IOException e) {
            System.out.println("Errore nel download della pagina");
            return false;
        }

        document.getDocumentElement().normalize();

        NodeList nodeList = document.getElementsByTagName("ide");

        for (int i=0;i< nodeList.getLength();i++){
            retVal = true;
            Node node = nodeList.item(i);
            Element elementNode = (Element) node;
            if (node.getNodeType() == Node.ELEMENT_NODE){
                latestUpdate = new LatestUpdate();
                latestUpdate.name = elementNode
                        .getElementsByTagName("name")
                        .item(0)
                        .getTextContent();
                latestUpdate.md5 = elementNode
                        .getElementsByTagName("md5")
                        .item(0)
                        .getTextContent();
                latestUpdate.size = elementNode
                        .getElementsByTagName("size")
                        .item(0)
                        .getTextContent();
                latestUpdate.timestamp = elementNode
                        .getElementsByTagName("timestamp")
                        .item(0)
                        .getTextContent();
                latestUpdate.published = elementNode
                        .getElementsByTagName("published")
                        .item(0)
                        .getTextContent();


                System.out.println("\tNome ultimo update: "+
                        latestUpdate.name);
                System.out.println("\tMd5: "+
                        latestUpdate.md5);
                System.out.println("\tDimensione: "+
                        latestUpdate.size);
                System.out.println("\tTimestamp: "+
                        latestUpdate.timestamp);
                System.out.println("\tPubblicato: "+
                        latestUpdate.published);
            }
        }

        return retVal;
    }

    private void getProxy() {
        System.setProperty("java.net.useSystemProxies", "true");
        List<Proxy> l = null;
        try {
            l = ProxySelector.getDefault().select(new URI(SOPHOS_URL));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Prendo il primo proxy disponibile tra quelli definiti a sistema
        if (l != null) {
            proxy = l.get(0);
        } else {
            proxy = Proxy.NO_PROXY;
        }
    }

    private class LatestUpdate{
        String name;
        String md5;
        String size;
        String timestamp;
        String published;
    }

}

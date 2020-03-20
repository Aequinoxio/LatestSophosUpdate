/*
 * Copyright (c) by GG - 2020.
 * This code follow the GPL v3 license scheme
 */

package org.gg;

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
    private static final String VERSION = "Latest Sophos update - v1.1 (20200321) - by GG";
    private static final String SOPHOS_URL = "https://downloads.sophos.com/downloads/info/latest_IDE.xml";
    private static final String UPDATE_FILE_EXTENSION = ".ide";

    private static final String SOPHOS_LOCAL_PATH = "C:\\Program Files (x86)\\Sophos\\Sophos Anti-Virus\\";
    //private static final String SOPHOS_LOCAL_PATH = "C:\\Users\\utente\\Downloads\\temp\\Sophos\\Sophos Anti-Virus\\";

    // Proxy variables
    Proxy proxy = Proxy.NO_PROXY;
    private static final String USER_AGENT = "User-Agent";
    private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";

    private LatestRemoteUpdate latestRemoteUpdate;

    public SophosUpdate() {
        getProxy();
    }

    public static void main(String[] args) {
        SophosUpdate sophosUpdate = new SophosUpdate();

        System.out.println(VERSION);

        if (sophosUpdate.parsePage()) {
            sophosUpdate.checkLocalUpdate();
        } else {
            System.out.println("Qualche problema nel download del file dal sito Sophos o nel parsing.");
        }
    }

    private boolean checkLocalUpdate() {
        boolean retVal = false;

        // Se qualcosa è andato storto nel recupero dell'ultimo update dal sito Sophos, esco e basta
        if (latestRemoteUpdate == null) {
            return retVal;
        }

        // Check se in locale ho l'ultimo file prelevabile dal sito Sophos
        File updateFile = new File(SOPHOS_LOCAL_PATH + latestRemoteUpdate.name);

        // Controllo l'esistenza del file e mostro gli esiti sinteticamente
        if (!updateFile.exists()) {
            // Houston, abbiamo un problema
            System.out.println("Il file " + updateFile.getAbsolutePath() + " non esiste");

            // L'ultimo aggiornamento potrebbe essere appena uscito -> controllo date necessario in caso negativo
            System.out.println("Sophos NON aggiornato. Verifica comunque le date dei file.");

            // Prendo l'ultimo file modificato nella directory locale
            File latestLocalFileUpdated = getLatestLocalFileUpdated();
            if (latestLocalFileUpdated == null) {
                System.out.println("\tNessun file di aggiornamento trovato nella directory: " + SOPHOS_LOCAL_PATH);
            } else {
                System.out.println("\tUltimo aggiornamento: " + latestLocalFileUpdated.getAbsolutePath() + " (ultima modifica: "+new Timestamp(latestLocalFileUpdated.lastModified())+")");
            }

            return false;

        } else {
            // Houston, the eagle has landed
            System.out.println("*******************************");
            System.out.println("* Sophos locale aggiornato :) *");
            System.out.println("*******************************");
            System.out.println("\t file locale: " + updateFile.getAbsolutePath());
            Date date = new Timestamp(updateFile.lastModified());
            //System.out.println("\t"+updateFile.lastModified());
            System.out.println("\t ultima modifica: " + date);
            retVal = true;
        }
        return retVal;
    }

    private File getLatestLocalFileUpdated() {
        File directory = new File(SOPHOS_LOCAL_PATH);
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(UPDATE_FILE_EXTENSION);
            }
        };

        File[] files = directory.listFiles(filenameFilter);

        if (files == null) {
            return null;
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.compare(o1.lastModified(), o2.lastModified());
            }
        });

        return files[0];
    }


    private boolean parsePage() {
        boolean retVal = false;

        System.out.println("Recupero l'ultimo update dal sito Sophos");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            //e.printStackTrace();
            return false;
        }

        Document document ;

        try {
            if (documentBuilder != null) {
                document = documentBuilder.parse(SOPHOS_URL);
            } else {
                System.out.println("Errore nella creazione del DOM XML per la pagina");
                return false;
            }
        } catch (SAXException e) {
            System.out.println("Errore nel parsing della pagina");
            return false;
        } catch (IOException e) {
            System.out.println("Errore nel download della pagina");
            return false;
        }

        document.getDocumentElement().normalize();

        NodeList nodeList = document.getElementsByTagName("latest");

        for (int i = 0; i < nodeList.getLength(); i++) {
            retVal = true;
            Node node = nodeList.item(i);
            Element elementNode = (Element) node;
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                latestRemoteUpdate = new LatestRemoteUpdate();
                try {
                    latestRemoteUpdate.name = elementNode
                            .getElementsByTagName("name")
                            .item(0)
                            .getTextContent();
                    latestRemoteUpdate.md5 = elementNode
                            .getElementsByTagName("md5")
                            .item(0)
                            .getTextContent();
                    latestRemoteUpdate.size = elementNode
                            .getElementsByTagName("size")
                            .item(0)
                            .getTextContent();
                    latestRemoteUpdate.timestamp = elementNode
                            .getElementsByTagName("timestamp")
                            .item(0)
                            .getTextContent();
                    latestRemoteUpdate.published = elementNode
                            .getElementsByTagName("published")
                            .item(0)
                            .getTextContent();


                    System.out.println("\tNome ultimo update: " +
                            latestRemoteUpdate.name);
                    System.out.println("\tMd5: " +
                            latestRemoteUpdate.md5);
                    System.out.println("\tDimensione: " +
                            latestRemoteUpdate.size);
                    System.out.println("\tTimestamp: " +
                            latestRemoteUpdate.timestamp);
                    System.out.println("\tPubblicato: " +
                            latestRemoteUpdate.published);
                } catch (NullPointerException ex) {
                    latestRemoteUpdate = null;
                    retVal = false;
                }
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

    private static class LatestRemoteUpdate {
        String name;
        String md5;
        String size;
        String timestamp;
        String published;
    }
}

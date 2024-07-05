package org.drishti.esign.service;


import jakarta.servlet.http.HttpServletRequest;
import org.drishti.esign.web.models.ESignXmlData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;


@Component
public class XmlGenerator {

    @Autowired
    private HttpServletRequest request;

    public String generateXml(ESignXmlData aspXmlDetais) {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element esign = doc.createElement("Esign");
            doc.appendChild(esign);

            // set attribute to esign Esign
            Attr attr = doc.createAttribute("ver");
            attr.setValue(aspXmlDetais.getVer());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("sc");
            attr.setValue(aspXmlDetais.getSc());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("ts");
            attr.setValue(aspXmlDetais.getTs());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("txn");
            attr.setValue(aspXmlDetais.getTxn());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("ekycId");
            attr.setValue(aspXmlDetais.getEkycId());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("ekycIdType");
            attr.setValue(aspXmlDetais.getEkycIdType());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("aspId");
            attr.setValue(aspXmlDetais.getAspId());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("AuthMode");
            attr.setValue(aspXmlDetais.getAuthMode());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("responseSigType");
            attr.setValue(aspXmlDetais.getResponseSigType());
            esign.setAttributeNode(attr);

            attr = doc.createAttribute("responseUrl");
            attr.setValue(aspXmlDetais.getResponseUrl());
            esign.setAttributeNode(attr);

            // Docs elements
            Element docs = doc.createElement("Docs");
            esign.appendChild(docs);

            // InputHash elements
            Element inputHash = doc.createElement("InputHash");

            // set attribute to staff element
            attr = doc.createAttribute("id");
            attr.setValue(aspXmlDetais.getId());
            inputHash.setAttributeNode(attr);

            attr = doc.createAttribute("hashAlgorithm");
            attr.setValue(aspXmlDetais.getHashAlgorithm());
            inputHash.setAttributeNode(attr);

            attr = doc.createAttribute("docInfo");
            attr.setValue(aspXmlDetais.getDocInfo());
            inputHash.setAttributeNode(attr);

            inputHash.appendChild(doc.createTextNode(aspXmlDetais.getDocHashHex()));
            docs.appendChild(inputHash);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            // Root Directory.
            String uploadRootPath = request.getServletContext().getRealPath("upload");
            System.out.println("uploadRootPath=" + uploadRootPath);

            File uploadRootDir = new File(uploadRootPath);
            // Create directory if it not exists.
            if (!uploadRootDir.exists()) {
                uploadRootDir.mkdirs();
            }

            try {
                // Create the file at server
                File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + "Testing.xml");


                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformerTemp = tf.newTransformer();
                transformerTemp.transform(source, result);


                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(writer.toString().getBytes());
                stream.close();
                return writer.toString();
            } catch (Exception e) {
                return "";
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            return "";
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            return "";
        }

    }

    public void writeToXmlFile(String xmlIn, String fileName) {
        try {
            Document doc;
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xmlIn)));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            // Create the file at server
            File serverFile = new File(fileName);


            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformerTemp = tf.newTransformer();
            transformerTemp.transform(source, result);


            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(writer.toString().getBytes());
            stream.close();

            //System.out.println("Write file: " + serverFile);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



//    public String generateXml(ESignXmlData aspXmlDetais) {
//        try {
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//            Document doc = docBuilder.newDocument();
//
//            Element esign = doc.createElement("Esign");
//            doc.appendChild(esign);
//
//            addAttributes(esign, aspXmlDetais,
//                    "ver", "sc", "ts", "txn", "ekycId", "ekycIdType", "aspId", "AuthMode", "responseSigType", "responseUrl");
//
//            Element docs = doc.createElement("Docs");
//            esign.appendChild(docs);
//
//            Element inputHash = doc.createElement("InputHash");
//            addAttributes(inputHash, aspXmlDetais, "id", "hashAlgorithm", "docInfo");
//            inputHash.appendChild(doc.createTextNode(aspXmlDetais.getDocHashHex()));
//            docs.appendChild(inputHash);
//
//            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            DOMSource source = new DOMSource(doc);
//
//            String uploadRootPath = request.getServletContext().getRealPath("upload");
//            File uploadRootDir = new File(uploadRootPath);
//            uploadRootDir.mkdirs();
//
//            File serverFile = new File(uploadRootDir, "Testing.xml");
//            StringWriter writer = new StringWriter();
//            transformer.transform(source, new StreamResult(writer));
//            writeToFile(serverFile, writer.toString());
//
//            return writer.toString();
//        } catch (ParserConfigurationException | TransformerException e) {
//            e.printStackTrace();
//            return "";
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void addAttributes(Element element, ESignXmlData aspXmlDetais, String... attributeNames) {
//        for (String attributeName : attributeNames) {
//            Attr attr = element.getOwnerDocument().createAttribute(attributeName);
//            attr.setValue(getAttributeValue(aspXmlDetais, attributeName));
//            element.setAttributeNode(attr);
//        }
//    }
//
//    private String getAttributeValue(ESignXmlData aspXmlDetais, String attributeName) {
//        return switch (attributeName) {
//            case "ver" -> aspXmlDetais.getVer();
//            case "sc" -> aspXmlDetais.getSc();
//            case "ts" -> aspXmlDetais.getTs();
//            case "txn" -> aspXmlDetais.getTxn();
//            case "ekycId" -> aspXmlDetais.getEkycId();
//            case "ekycIdType" -> aspXmlDetais.getEkycIdType();
//            case "aspId" -> aspXmlDetais.getAspId();
//            case "AuthMode" -> aspXmlDetais.getAuthMode();
//            case "responseSigType" -> aspXmlDetais.getResponseSigType();
//            case "responseUrl" -> aspXmlDetais.getResponseUrl();
//            case "id" -> aspXmlDetais.getId();
//            case "hashAlgorithm" -> aspXmlDetais.getHashAlgorithm();
//            case "docInfo" -> aspXmlDetais.getDocInfo();
//            default -> throw new IllegalArgumentException("Unknown attribute name: " + attributeName);
//        };
//    }
//
//    private void writeToFile(File file, String content) throws IOException {
//        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file))) {
//            stream.write(content.getBytes());
//        }
//    }


}


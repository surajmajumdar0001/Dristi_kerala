package org.drishti.esign.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.drishti.esign.cipher.Encryption;
import org.drishti.esign.util.FileStoreUtil;
import org.drishti.esign.util.XmlFormDataSetter;
import org.drishti.esign.web.models.ESignParameter;
import org.drishti.esign.web.models.ESignRequest;
import org.drishti.esign.web.models.ESignXmlData;
import org.drishti.esign.web.models.ESignXmlForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.PrivateKey;


@Service
@Slf4j
public class ESignService {

    @Autowired
    private PdfEmbedder pdfEmbedder;

    @Autowired
    private XmlSigning xmlSigning;


    @Autowired
    private Encryption encryption;
    @Autowired
    private XmlFormDataSetter formDataSetter;

    @Autowired
    private XmlGenerator xmlGenerator;

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private FileStoreUtil fileStoreUtil;

    public ESignXmlForm signDoc(ESignRequest request) {

        ESignParameter eSignParameter = request.getESignParameter();
        Resource resource = fileStoreUtil.fetchFileStoreObjectById(eSignParameter.getFileStoreId(), eSignParameter.getTenantId());

        String fileHash = pdfEmbedder.pdfSigner(resource);
        ESignXmlData eSignXmlData = formDataSetter.setFormXmlData(fileHash, new ESignXmlData());

        String strToEncrypt = xmlGenerator.generateXml(eSignXmlData);  // this method is writing in testing.xml
        String xmlData = "";

        try {
            PrivateKey rsaPrivateKey = encryption.getPrivateKey("privateKey.pem");
            xmlData = xmlSigning.signXmlStringNew(servletRequest.getServletContext().getRealPath("upload") + File.separator + "Testing.xml", rsaPrivateKey);

            xmlGenerator.writeToXmlFile(xmlData, servletRequest.getServletContext().getRealPath("upload") + File.separator + "Testing.xml");
        } catch (Exception e) {
//            System.out.println("Error in Encryption.");
//            e.printStackTrace();
//            return new RequestXmlForm();
        }

        ESignXmlForm myRequestXmlForm = new ESignXmlForm();
        myRequestXmlForm.setId("");
        myRequestXmlForm.setType("A");
        myRequestXmlForm.setDescription("Y");
        myRequestXmlForm.setESignRequest(xmlData);
        myRequestXmlForm.setAspTxnID(eSignXmlData.getTxn());
        myRequestXmlForm.setContentType("application/xml");
//        myUploadForm.setXml(xmlData);
        return myRequestXmlForm;


    }
}

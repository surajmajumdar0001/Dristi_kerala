package org.drishti.esign.service;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;


@Component
public class PdfEmbedder {


    PdfSignatureAppearance appearance;

    public String pdfSigner(Resource resource) {


        String hashDocument = null;

        try {
            InputStream inputStream = resource.getInputStream();
            PdfReader reader = new PdfReader(inputStream);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfStamper stamper = PdfStamper.createSignature(reader, bos, '\0', null, true);

            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);
            appearance.setAcro6Layers(false);

            Rectangle cropBox = reader.getCropBox(1);
            Rectangle rectangle = new Rectangle(cropBox.getLeft(), cropBox.getBottom(), cropBox.getLeft(100), cropBox.getBottom(90));
            appearance.setVisibleSignature(rectangle, reader.getNumberOfPages(), null);

            PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
            appearance.setCryptoDictionary(dic);

            HashMap<PdfName, Integer> exc = new HashMap<>();
            exc.put(PdfName.CONTENTS, 8192 * 2 + 2);
//            appearance.preClose(exc);

//            stamper.close();

            InputStream is = new ByteArrayInputStream(bos.toByteArray());
            hashDocument = DigestUtils.sha256Hex(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return hashDocument;

    }

    public String signPdfwithDS(String response, HttpServletRequest request, HttpSession session) {
        session = request.getSession(false);
        int contentEstimated = 8192;
        try {
            if (request.getSession() == null) {
                System.out.println("=================session===========");
            }

            String errorCode = response.substring(response.indexOf("errCode"), response.indexOf("errMsg"));
            errorCode = errorCode.trim();
            if (errorCode.contains("NA")) {
                String pkcsResponse = new XmlSigning().parseXml(response.trim());
                byte[] sigbytes = Base64.decodeBase64(pkcsResponse);
                byte[] paddedSig = new byte[contentEstimated];
                System.arraycopy(sigbytes, 0, paddedSig, 0, sigbytes.length);
                PdfDictionary dic2 = new PdfDictionary();
                dic2.put(PdfName.CONTENTS,
                        new PdfString(paddedSig).setHexWriting(true));
                appearance.close(dic2);
            } else {
//                destFile = "Error";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "destFile";
    }
}


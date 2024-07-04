package org.drishti.esign.service;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.drishti.esign.util.ByteArrayMultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


@Component
public class PdfEmbedder {

    PdfSignatureAppearance appearance;

    public MultipartFile signPdfWithDSAndReturnMultipartFile(Resource resource, String response) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            InputStream inputStream = resource.getInputStream();
            PdfReader reader = new PdfReader(inputStream);
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

            int contentEstimated = 8192;
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
                // handle error case
            }

            stamper.close();
            bos.close();

            return new ByteArrayMultipartFile("signedDoc", bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateHash(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            return DigestUtils.sha256Hex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

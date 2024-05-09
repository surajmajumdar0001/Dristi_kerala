package digit.util;

import digit.config.Configuration;
import digit.repository.ServiceRequestRepository;
import digit.web.models.CauseListResponse;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PdfServiceUtil {

    private final ServiceRequestRepository serviceRequestRepository;
    private final Configuration config;

    @Autowired
    public PdfServiceUtil(ServiceRequestRepository serviceRequestRepository, Configuration config) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.config = config;
    }

    public ByteArrayResource generatePdfFromPdfService(CauseListResponse causeListResponse, String tenantId,
                                                       String pdfTemplateKey, RequestInfo requestInfo) {
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(config.getPdfServiceHost())
                    .append(config.getPdfServiceEndpoint())
                    .append("?tenantId=").append(tenantId).append("&key=").append(pdfTemplateKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CauseListResponse> requestEntity = new HttpEntity<>(causeListResponse, headers);

            // Use ServiceRequestRepository to fetch the response from PDF service
            Object pdfResponse = serviceRequestRepository.fetchResult(uri, requestEntity);

            if (pdfResponse instanceof ByteArrayResource) {
                return (ByteArrayResource) pdfResponse;
            } else {
                throw new CustomException("", "Invalid response from PDF Service");
            }
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException("", "Error getting response from Pdf Service");
        }
    }
}

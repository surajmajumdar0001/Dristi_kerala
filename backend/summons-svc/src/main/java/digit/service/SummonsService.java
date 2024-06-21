package digit.service;

import digit.config.Configuration;
import digit.enrichment.SummonsDeliveryEnrichment;
import digit.kafka.Producer;
import digit.repository.SummonsRepository;
import digit.util.*;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
public class SummonsService {


    private final PdfServiceUtil pdfServiceUtil;

    private final Configuration config;

    private final FileStorageUtil fileStorageUtil;

    private final SummonsRepository summonsRepository;

    private final Producer producer;

    private final SummonsDeliveryEnrichment summonsDeliveryEnrichment;

    private final ExternalChannelUtil externalChannelUtil;

    private final TaskSummonsUtil taskSummonsUtil;

    @Autowired
    public SummonsService(PdfServiceUtil pdfServiceUtil, Configuration config, Producer producer,
                          FileStorageUtil fileStorageUtil, SummonsRepository summonsRepository,
                          SummonsDeliveryEnrichment summonsDeliveryEnrichment, ExternalChannelUtil externalChannelUtil,
                          TaskSummonsUtil taskSummonsUtil) {
        this.pdfServiceUtil = pdfServiceUtil;
        this.config = config;
        this.producer = producer;
        this.fileStorageUtil = fileStorageUtil;
        this.summonsRepository = summonsRepository;
        this.summonsDeliveryEnrichment = summonsDeliveryEnrichment;
        this.externalChannelUtil = externalChannelUtil;
        this.taskSummonsUtil = taskSummonsUtil;
    }

    public SummonsDocument generateSummonsDocument(GenerateSummonsRequest request) {
        String issueType = request.getTaskSummon().getSummonDetails().getDocType();
        String pdfTemplateKey;
        if (issueType.equalsIgnoreCase("summons")) {
            pdfTemplateKey = config.getSummonsPdfTemplateKey();
        } else if (issueType.equalsIgnoreCase("warrants")) {
            pdfTemplateKey = config.getWarrantPdfTemplateKey();
        } else {
            throw new CustomException("INVALID_ISSUE_TYPE", "Issued Summons Type must be Valid");
        }
        ByteArrayResource byteArrayResource = pdfServiceUtil.generatePdfFromPdfService(request, config.getEgovStateTenantId(),
                pdfTemplateKey);
        String fileStoreId = fileStorageUtil.saveDocumentToFileStore(byteArrayResource);
        return SummonsDocument
                .builder().fileStoreId(fileStoreId).docType("pdf").docName(issueType).build();
    }

    public SummonsDelivery sendSummonsViaChannels(SendSummonsRequest request) {
        SummonsDelivery summonsDelivery = generateSummonsDelivery(request.getTaskSummon());
        summonsDeliveryEnrichment.enrichSummonsDelivery(summonsDelivery, request.getRequestInfo());
        ChannelMessage channelMessage = externalChannelUtil.sendSummonsByDeliveryChannel(request, summonsDelivery);
        summonsDelivery.setIsAcceptedByChannel(Boolean.TRUE);
        if (channelMessage.getAcknowledgementStatus().equalsIgnoreCase("success")) {
            summonsDelivery.setDeliveryStatus("SUMMONS_IN_PROGRESS");
        }
        summonsDelivery.setChannelAcknowledgementId(channelMessage.getAcknowledgeUniqueNumber());
        SummonsRequest summonsRequest = SummonsRequest.builder()
                .summonsDelivery(summonsDelivery).requestInfo(request.getRequestInfo()).build();
        log.info("Summons Delivery: {}", summonsDelivery);
        producer.push("insert-summons", summonsRequest);
        return summonsDelivery;
    }

    private SummonsDelivery generateSummonsDelivery(TaskSummon taskSummon) {
        return SummonsDelivery.builder()
                .summonId(taskSummon.getSummonDetails().getSummonId())
                .caseId(taskSummon.getCaseDetails().getCaseId())
                .docType(taskSummon.getSummonDetails().getDocType())
                .docSubType(taskSummon.getSummonDetails().getDocSubType())
                .partyType(taskSummon.getSummonDetails().getPartyType())
                .paymentFees(taskSummon.getDeliveryChannel().getPaymentFees())
                .paymentStatus(taskSummon.getDeliveryChannel().getPaymentStatus())
                .paymentTransactionId(taskSummon.getDeliveryChannel().getPaymentTransactionId())
                .channelName(taskSummon.getDeliveryChannel().getChannelName())
                .deliveryRequestDate(LocalDate.now())
                .build();
    }

    public ChannelMessage updateSummonsDeliveryStatus(UpdateSummonsRequest request) {
        ChannelReport channelReport = request.getChannelReport();
        SummonsDeliverySearchCriteria searchCriteria = SummonsDeliverySearchCriteria
                .builder().summonsId(channelReport.getSummonId()).build();
        Optional<SummonsDelivery> optionalSummons = summonsRepository.getSummons(searchCriteria).stream().findFirst();
        if (optionalSummons.isEmpty()) {
            throw new CustomException("SUMMONS_UPDATE_STATUS_ERROR", "Update Summons api was provided with an invalid summons api");
        }
        SummonsDelivery summonsDelivery = optionalSummons.get();
        summonsDeliveryEnrichment.enrichForUpdate(summonsDelivery, request.getRequestInfo());
        summonsDelivery.setDeliveryStatus(channelReport.getDeliveryStatus());
        summonsDelivery.setAdditionalFields(channelReport.getAdditionalFields());
        SummonsRequest newRequest = SummonsRequest.builder()
                .requestInfo(request.getRequestInfo()).summonsDelivery(summonsDelivery).build();
        producer.push("update-summons", newRequest);
        ChannelMessage channelMessage = ChannelMessage.builder()
                .acknowledgeUniqueNumber(summonsDelivery.getSummonId().concat(LocalDate.now().toString()))
                .acknowledgementStatus("SUCCESS").build();
        return channelMessage;
    }

    public void processStatusAndUpdateSummonsTask(SummonsRequest request) {
        SummonsDelivery summonsDelivery = request.getSummonsDelivery();
        SummonsTaskStatus taskStatus = SummonsTaskStatus.builder()
                .summonsId(summonsDelivery.getSummonId())
                .statusTobeUpdated(summonsDelivery.getDeliveryStatus())
                .build();
        SummonsTaskUpdateRequest updateRequest = SummonsTaskUpdateRequest.builder()
                .summonsTaskStatus(taskStatus).requestInfo(request.getRequestInfo()).build();
        taskSummonsUtil.updateSummonsTaskStatus(updateRequest);
    }
}

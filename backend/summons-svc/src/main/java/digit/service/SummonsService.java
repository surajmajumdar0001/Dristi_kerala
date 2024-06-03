package digit.service;

import digit.config.Configuration;
import digit.enrichment.SummonsEnrichment;
import digit.kafka.Producer;
import digit.repository.SummonsRepository;
import digit.util.*;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SummonsService {


    private final PdfServiceUtil pdfServiceUtil;

    private final Configuration config;

    private final FileStorageUtil fileStorageUtil;

    private final SummonsRepository summonsRepository;

    private final Producer producer;

    private final SummonsEnrichment summonsEnrichment;

    private final ExternalChannelUtil externalChannelUtil;

    private final SummonsStatusLogicUtil logicUtil;

    private final OrdersServiceUtil ordersServiceUtil;

    @Autowired
    public SummonsService(PdfServiceUtil pdfServiceUtil, Configuration config, Producer producer,
                          FileStorageUtil fileStorageUtil, SummonsRepository summonsRepository,
                          SummonsEnrichment summonsEnrichment, ExternalChannelUtil externalChannelUtil, SummonsStatusLogicUtil logicUtil, OrdersServiceUtil ordersServiceUtil) {
        this.pdfServiceUtil = pdfServiceUtil;
        this.config = config;
        this.producer = producer;
        this.fileStorageUtil = fileStorageUtil;
        this.summonsRepository = summonsRepository;
        this.summonsEnrichment = summonsEnrichment;
        this.externalChannelUtil = externalChannelUtil;
        this.logicUtil = logicUtil;
        this.ordersServiceUtil = ordersServiceUtil;
    }


    public SummonsDocument generateSummonsDocument(GenerateSummonsRequest request) {
        ByteArrayResource byteArrayResource = pdfServiceUtil.generatePdfFromPdfService(request, config.getEgovStateTenantId(),
                config.getSummonsPdfTemplateKey());
        String fileStoreId = fileStorageUtil.saveDocumentToFileStore(byteArrayResource);
        return SummonsDocument
                .builder().fileStoreId(fileStoreId).docType("pdf").docName("summons").build();
    }

    public List<Summons> sendSummonsViaChannels(SendSummonsRequest request) {
        SummonsDetails summonsDetails = request.getSummonDetails();
        List<Summons> summonsList = new ArrayList<>();
        for (DeliveryChannel deliveryChannel : summonsDetails.getChannelsToDeliver()) {
            Summons summons = generateSummons(summonsDetails, deliveryChannel);
            summonsEnrichment.enrichSummons(summons, request.getRequestInfo());
            externalChannelUtil.sendSummonsByDeliveryChannel(summonsDetails, summons, deliveryChannel);
            SummonsRequest summonsRequest = SummonsRequest.builder()
                    .summon(summons).requestInfo(request.getRequestInfo()).build();
            producer.push("insert-summons", summonsRequest);
            summonsList.add(summons);
        }
        return summonsList;
    }

    private Summons generateSummons(SummonsDetails summonsDetails, DeliveryChannel deliveryChannel) {
        return Summons.builder()
                .orderId(summonsDetails.getOrderDetails().getOrderId())
                .channelName(deliveryChannel.getChannelName().name())
                .orderType(summonsDetails.getOrderDetails().getIssueType())
                .tenantId(config.getEgovStateTenantId())
                .requestDate(LocalDateTime.now())
                .build();
    }

    public Summons updateSummonsDeliveryStatus(UpdateSummonsRequest request) {
        ChannelMessage channelMessage = request.getChannelMessage();
        SummonsSearchCriteria searchCriteria = SummonsSearchCriteria
                .builder().summonsId(channelMessage.getSummonsId()).build();
        Optional<Summons> optionalSummons = summonsRepository.getSummons(searchCriteria).stream().findFirst();
        if (optionalSummons.isEmpty()) {
            throw new CustomException("SUMMONS_UPDATE_STATUS_ERROR", "Update Summons api was provided with an invalid summons api");
        }
        Summons summons = optionalSummons.get();
        summons.setStatusOfDelivery(channelMessage.getStatus());
        summons.setAdditionalFields(channelMessage.getAdditionalFields());
        SummonsRequest newRequest = SummonsRequest.builder()
                .requestInfo(request.getRequestInfo()).summon(summons).build();
        SummonsSearchCriteria newSearchCriteria = SummonsSearchCriteria
                .builder().orderId(summons.getOrderId()).build();
        List<Summons> summonsList = summonsRepository.getSummons(newSearchCriteria);
        String statusBeforeUpdate = logicUtil.evaluateSummonsStatus(summonsList);
        summonsList.removeIf(a -> a.getSummonsId().equals(summons.getSummonsId()));
        summonsList.add(summons);
        String statusAfterUpdate = logicUtil.evaluateSummonsStatus(summonsList);
        if (statusBeforeUpdate.equalsIgnoreCase("SUMMONS_IN_PROGRESS") &&
                (statusAfterUpdate.equalsIgnoreCase("SUMMONS_DELIVERED") || statusAfterUpdate.equalsIgnoreCase("SUMMONS_NOT_DELIVERED"))) {
            sendNotificationToUpdateOrders(summons, request.getRequestInfo(), statusAfterUpdate);
        }
        producer.push("update-summons", newRequest);
        return summons;
    }

    private void sendNotificationToUpdateOrders(Summons summons, RequestInfo requestInfo, String statusAfterUpdate) {
        OrderStatus orderStatus = OrderStatus.builder().orderId(summons.getOrderId()).statusTobeUpdated(statusAfterUpdate).build();
        OrderStatusUpdateRequest updateRequest = OrderStatusUpdateRequest.builder()
                .orderStatus(orderStatus).requestInfo(requestInfo).build();
        ordersServiceUtil.updateOrdersStatus(updateRequest);
    }
}

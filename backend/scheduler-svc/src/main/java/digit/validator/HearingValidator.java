package digit.validator;


import digit.repository.HearingRepository;
import digit.web.models.HearingSearchCriteria;
import digit.web.models.ScheduleHearing;
import digit.web.models.ScheduleHearingRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Component
public class HearingValidator {

    @Autowired
    private HearingRepository repository;

    public void validateHearing(List<ScheduleHearing> schedulingRequests) {

        schedulingRequests.forEach(application -> {
            if (ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("DK_SH_APP_ERR", "tenantId is mandatory for schedule a hearing");

            if (ObjectUtils.isEmpty(application.getDate())){
                throw new CustomException("DK_SH_APP_ERR", "date is mandatory for schedule a hearing");
            }else{
                LocalDate date = application.getDate();
                if (date.isBefore(LocalDate.now())){
                    throw new CustomException("DK_SH_APP_ERR", "cannot schedule a hearing for past date: " +date);

                }
            }

        });

        verifyHearingDates(schedulingRequests);
    }

    private void verifyHearingDates(List<ScheduleHearing> hearingRequest) {
        HashMap<String, Double> map = new HashMap<>(); // to avoid extra call to db

        //TODO: need to configure
        Double judgeBandwidth = 8.0;

        for (ScheduleHearing hearing : hearingRequest) {

            HearingSearchCriteria searchCriteria = HearingSearchCriteria.builder()
                    .toDate(hearing.getDate())
                    .fromDate(hearing.getDate())
                    .judgeId(hearing.getJudgeId()).build();

            Double requiredSlot = (hearing.getEndTime().getHour() - hearing.getStartTime().getHour()) +
                    (hearing.getEndTime().getMinute() - hearing.getStartTime().getMinute()) / 60.0 +
                    (hearing.getEndTime().getSecond() - hearing.getStartTime().getSecond()) / 3600.0;

            StringBuilder key = new StringBuilder();
            key.append(hearing.getJudgeId()).append("-").append(hearing.getDate());
            Double occupiedBandwidthOfJudgeForDay;

            if (map.containsKey(key.toString())) {
                occupiedBandwidthOfJudgeForDay = map.get(key.toString());
            } else {
                List<ScheduleHearing> judgeHearing = repository.getHearings(searchCriteria);
                occupiedBandwidthOfJudgeForDay = judgeHearing.stream().reduce(0.0, (total, element) ->
                                total + (element.getEndTime().getHour() - element.getStartTime().getHour()) +
                                        (element.getEndTime().getMinute() - element.getStartTime().getMinute()) / 60.0 +
                                        (element.getEndTime().getSecond() - element.getStartTime().getSecond()) / 3600.0,
                        Double::sum);
            }


            if ((judgeBandwidth - occupiedBandwidthOfJudgeForDay) > requiredSlot) {
                // temporary booked the slot
                map.put(key.toString(), occupiedBandwidthOfJudgeForDay + requiredSlot);

            } else {
                Error error = new Error();
                error.setCode("500");
                error.setMessage("No slots are available");
                error.setDescription("Judge: " + hearing.getJudgeId() + " is occupied for day " + hearing.getDate());
                hearing.setErrors(error);
            }


        }


    }

    public void validateHearingOnUpdate(ScheduleHearingRequest scheduleHearingRequest) {
    }
}


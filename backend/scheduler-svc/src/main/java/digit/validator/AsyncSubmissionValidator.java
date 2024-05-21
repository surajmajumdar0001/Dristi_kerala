package digit.validator;

import digit.config.Configuration;
import digit.kafka.Producer;
import digit.repository.HearingRepository;
import digit.web.models.AsyncSubmission;
import digit.web.models.HearingSearchCriteria;
import digit.web.models.ScheduleHearing;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class AsyncSubmissionValidator {

    private HearingRepository repository;

    private Producer producer;

    private Configuration config;

    @Autowired
    public AsyncSubmissionValidator(HearingRepository repository, Producer producer, Configuration config) {
        this.repository = repository;
        this.producer = producer;
        this.config = config;
    }

    public void validateHearing(AsyncSubmission asyncSubmission) {
        if (validateSubmissionAndResponseDates(asyncSubmission)) {
            HearingSearchCriteria searchCriteria = HearingSearchCriteria.builder()
                    .caseId(asyncSubmission.getCaseId()).build();
            List<ScheduleHearing> scheduleHearingList = repository.getHearings(searchCriteria, null, null);
            Optional<ScheduleHearing> latestHearing = findLatestHearingByStartTime(scheduleHearingList);
            if (latestHearing.isPresent()) {
                if (LocalDate.parse(asyncSubmission.getResponseDate()).isAfter(latestHearing.get().getDate())) {
                    producer.push(config.getAsyncSubmissionReScheduleHearing(), asyncSubmission);
                }
            }
        } else {
            throw new CustomException("DK_AS_APP_ERR", "async submission date must be before response date");
        }
    }

    private Boolean validateSubmissionAndResponseDates(AsyncSubmission asyncSubmission) {
        return LocalDate.parse(asyncSubmission.getSubmissionDate()).isBefore(LocalDate.parse(asyncSubmission.getResponseDate()));
    }

    public static Optional<ScheduleHearing> findLatestHearingByStartTime(List<ScheduleHearing> hearings) {
        return hearings.stream()
                .filter(hearing -> hearing.getStartTime() != null) // Filter out null start times
                .max(Comparator.comparing(ScheduleHearing::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo)));
    }

}

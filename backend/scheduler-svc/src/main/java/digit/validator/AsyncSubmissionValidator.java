package digit.validator;

import digit.config.Configuration;
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
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class AsyncSubmissionValidator {

    private HearingRepository repository;


    private Configuration config;

    @Autowired
    public AsyncSubmissionValidator(HearingRepository repository, Configuration config) {
        this.repository = repository;
        this.config = config;
    }

    public void validateHearing(AsyncSubmission asyncSubmission) {
        if (validateSubmissionAndResponseDates(asyncSubmission)) {
            HearingSearchCriteria searchCriteria = HearingSearchCriteria.builder()
                    .caseId(asyncSubmission.getCaseId()).build();
            List<ScheduleHearing> scheduleHearingList = repository.getHearings(searchCriteria);
            Optional<ScheduleHearing> latestHearing = findLatestHearingByStartTime(scheduleHearingList);
            if (latestHearing.isPresent()) {
                if (LocalDate.parse(asyncSubmission.getResponseDate()).isAfter(latestHearing.get().getDate())) {
                    throw new CustomException("DK_ASH_APP_ERR", "async submission and response dates must be before hearing date");
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
                .filter(hearing -> hearing.getDate() != null && hearing.getDate().isAfter(LocalDate.now()))
                .max(Comparator.comparing(ScheduleHearing::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo)));
    }

    public void validateDates(AsyncSubmission asyncSubmission) {
        HearingSearchCriteria searchCriteria = HearingSearchCriteria.builder()
                .caseId(asyncSubmission.getCaseId()).build();
        List<ScheduleHearing> scheduleHearingList = repository.getHearings(searchCriteria);
        Optional<ScheduleHearing> latestHearing = findLatestHearingByStartTime(scheduleHearingList);
        if (latestHearing.isPresent()) {
            if (LocalDate.parse(asyncSubmission.getResponseDate()).isAfter(latestHearing.get().getDate())) {
                asyncSubmission.setResponseDate(String.valueOf(latestHearing.get().getDate().minusDays(1)));
                if (ChronoUnit.DAYS.between(LocalDate.now(), latestHearing.get().getDate()) > 6) {
                    asyncSubmission.setSubmissionDate(String.valueOf(latestHearing.get().getDate().minusDays(6)));
                } else {
                    asyncSubmission.setSubmissionDate(String.valueOf(String.valueOf(LocalDate.now().plusDays(1))));
                }
            }
        }
    }
}

package digit.util;

import digit.web.models.Summons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
public class SummonsStatusLogicUtil {

    public String evaluateSummonsStatus(List<Summons> summons) {

        boolean anyServed = false;
        boolean allInProgress = true;
        boolean allExceededDays = true;
        boolean allFailed = true;

        for (Summons summon : summons) {
            String status = summon.getStatusOfDelivery();

            if (status.equalsIgnoreCase("SUMMONS_DELIVERED")) {
                anyServed = true;
            }

            if (status.equalsIgnoreCase("SUMMONS_IN_PROGRESS")) {
                allInProgress = false;
            }

            if (ChronoUnit.DAYS.between(summon.getRequestDate(), LocalDateTime.now()) <= 45) {
                allExceededDays = false;
            }

            if (status.equalsIgnoreCase("SUMMONS_NOT_SERVED")) {
                allFailed = false;
            }
        }

        if (anyServed) {
            return "SUMMONS_DELIVERED";
        }
        if (allInProgress) {
            return "SUMMONS_IN_PROGRESS";
        }
        if (allExceededDays || allFailed) {
            return "SUMMONS_NOT_SERVED";
        }

        return "SUMMONS_STATUS_UNKNOWN";
    }

}

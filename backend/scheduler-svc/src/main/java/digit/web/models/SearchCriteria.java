package digit.web.models;

import java.time.LocalDate;

public interface SearchCriteria {

    String getTenantId();

    String getJudgeId();

    String getCourtId();

    LocalDate getFromDate();

    LocalDate getToDate();
}

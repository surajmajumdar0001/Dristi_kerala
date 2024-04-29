package digit.web.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import digit.models.coremodels.AuditDetails;
import digit.web.models.enums.EventType;
import digit.web.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.model.Error;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleHearing {

    @JsonProperty("hearingBookingId")
    private String hearingBookingId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("courtId")
    private String courtId;

    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("date")
    private LocalDate date;

    //TODO: this should be enum
    @JsonProperty("eventType")
    private EventType eventType;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    //TODO: this should be enum
    @JsonProperty("status")
    private Status status;

    @JsonProperty("startTime")
    private LocalDateTime startTime;

    @JsonProperty("endTime")
    private LocalDateTime endTime;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("rowVersion")
    private Integer rowVersion = null;

    @JsonProperty("error")
    @JsonIgnore
    private Error errors = null;
}

package digit.web.models;


import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("rowVersion")
    private Integer rowVersion = null;

    @JsonProperty("error")
    @JsonIgnore
    private Error errors = null;

    @JsonProperty("rescheduleRequestId")
    private String rescheduleRequestId = null;

    @JsonProperty("hearingTimeInMinutes")
    private Integer hearingTimeInMinutes = null;

    //  copy constructor
    public ScheduleHearing(ScheduleHearing another) {
        this.hearingBookingId = another.hearingBookingId;
        this.tenantId = another.tenantId;
        this.courtId = another.courtId;
        this.judgeId = another.judgeId;
        this.caseId = another.caseId;
        this.date = another.date;
        this.eventType = another.eventType;
        this.title = another.title;
        this.description = another.description;
        this.status = another.status;
        this.startTime = another.startTime;
        this.endTime = another.endTime;
        this.auditDetails = another.auditDetails;
        this.rowVersion = another.rowVersion;
        this.errors = another.errors;
        this.rescheduleRequestId = another.rescheduleRequestId;
        this.hearingTimeInMinutes = another.hearingTimeInMinutes;
    }

    public boolean overlapsWith(ScheduleHearing other) {
        return !((!startTime.isBefore(other.endTime)) || (!endTime.isAfter(other.startTime)));
    }
}

package digit.web.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleHearing {

    private String hearingBookingId;

    private String courtId;
    private String judgeId;
    private String caseId;

    private String date;
    private String eventType;
    private String title;
    private String description;
    private String status;
    private String startTime;
    private String endTime;
}

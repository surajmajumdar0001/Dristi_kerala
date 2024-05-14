package digit.web.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {

    SCHEDULED("SCHEDULED"), RE_SCHEDULED("RE_SCHEDULED"), CANCELLED("CANCELLED"),APPROVED("APPROVED"),APPLIED("APPLIED"),REJECTED("REJECTED")
    ,BLOCKED("BLOCKED") ,AUTO_HEARING_SCHEDULE("AUTO_HEARING_SCHEDULE"),REVIEW("REVIEW");

    private String value;

    Status(String value){
        this.value=value;
    }

    @JsonCreator
    public static Status fromValue(String text) {
        for (Status b : Status.values()) {
            if (String.valueOf(b.value).equalsIgnoreCase(text) || String.valueOf(b.name()).equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}

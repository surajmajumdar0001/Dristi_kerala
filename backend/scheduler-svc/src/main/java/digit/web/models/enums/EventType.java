package digit.web.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum EventType {
    ADMISSION_HEARING("Admission Hearing");

    private String value;
    EventType(String value){
        this.value=value;
    }

    @JsonCreator
    public static EventType fromValue(String text) {
        for (EventType b : EventType.values()) {
            if (String.valueOf(b.value).equalsIgnoreCase(text) || String.valueOf(b.name()).equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }


}

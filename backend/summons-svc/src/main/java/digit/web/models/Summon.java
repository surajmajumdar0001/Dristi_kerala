package digit.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import digit.web.models.AuditDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * Summon
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-24T17:04:14.826760024+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Summon   {
        @JsonProperty("id")

          @Valid
                private UUID id = null;

        @JsonProperty("filingNumber")

                private String filingNumber = null;

        @JsonProperty("cnrNumber")

                private String cnrNumber = null;

        @JsonProperty("witnessIdentifier")

                private String witnessIdentifier = null;

        @JsonProperty("individualId")
          @NotNull

                private String individualId = null;

        @JsonProperty("remarks")

        @Size(min=10,max=5000)         private String remarks = null;

        @JsonProperty("isActive")

                private Boolean isActive = true;

        @JsonProperty("auditDetails")

          @Valid
                private AuditDetails auditDetails = null;

        @JsonProperty("additionalDetails")

                private Object additionalDetails = null;


}

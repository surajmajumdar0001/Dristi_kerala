package digit.repository.rowmapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.AuditDetails;
import digit.web.models.OptOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class OptOutRowMapper implements RowMapper<OptOut> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public OptOut mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return OptOut.builder()
                    .id(rs.getString("id"))
                    .judgeId(rs.getString("judgeId"))
                    .caseId(rs.getString("caseId"))
                    .rescheduleRequestId(rs.getString("reschedulingrequestid"))
                    .individualId(rs.getString(""))
                    .optoutDates(rs.getString("optoutDates") == null ? null : objectMapper.readValue(rs.getString(""), new TypeReference<List<LocalDate>>() {
                    }))
                    .rowVersion(rs.getInt("rowversion"))
                    .auditDetails(AuditDetails.builder()
                            .createdBy(rs.getString("createdby"))
                            .createdTime(rs.getLong("createdtime"))
                            .lastModifiedBy(rs.getString("lastmodifiedby"))
                            .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                            .build())
                    .tenantId(rs.getString(""))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}

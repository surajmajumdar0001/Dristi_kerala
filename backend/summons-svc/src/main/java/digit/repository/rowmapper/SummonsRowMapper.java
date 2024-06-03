package digit.repository.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.web.models.AdditionalFields;
import digit.web.models.Summons;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
@Slf4j
public class SummonsRowMapper implements RowMapper<Summons> {

    private final ObjectMapper objectMapper;

    @Autowired
    public SummonsRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public Summons mapRow(ResultSet rs, int rowNum) throws SQLException {
        Summons summons = new Summons();

        summons.setSummonsId(rs.getString("summons_id"));
        summons.setOrderId(rs.getString("order_id"));
        summons.setTenantId(rs.getString("tenant_id"));
        summons.setOrderType(rs.getString("order_type"));
        summons.setChannelName(rs.getString("channel_name"));
        summons.setIsAcceptedByChannel(rs.getBoolean("is_accepted_by_channel"));
        summons.setChannelAcknowledgementId(rs.getString("channel_acknowledgement_id"));

        Timestamp requestDateTimestamp = rs.getTimestamp("request_date");
        summons.setRequestDate(requestDateTimestamp != null ? requestDateTimestamp.toLocalDateTime() : null);

        summons.setStatusOfDelivery(rs.getString("status_of_delivery"));

        AdditionalFields additionalFields = new AdditionalFields();
        try {
            additionalFields = objectMapper.readValue(rs.getString("additionalfields"), AdditionalFields.class);
        } catch (JsonProcessingException e) {
            throw new SQLException(e);
        }
        summons.setAdditionalFields(additionalFields);

        AuditDetails auditdetails = AuditDetails.builder()
                .createdBy(rs.getString("created_by"))
                .createdTime(rs.getLong("created_time"))
                .lastModifiedBy(rs.getString("last_modified_by"))
                .lastModifiedTime(rs.getLong("last_modified_time"))
                .build();
        summons.setAuditDetails(auditdetails);

        summons.setRowVersion(rs.getInt("row_version"));
        return summons;
    }
}

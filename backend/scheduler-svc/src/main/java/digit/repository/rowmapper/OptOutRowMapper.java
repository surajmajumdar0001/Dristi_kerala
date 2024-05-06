package digit.repository.rowmapper;


import digit.web.models.AvailabilityDTO;
import digit.web.models.OptOut;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OptOutRowMapper implements RowMapper<OptOut> {
    @Override
    public OptOut mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }
}

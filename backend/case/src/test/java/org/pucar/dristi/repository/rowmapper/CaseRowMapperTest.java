package org.pucar.dristi.repository.rowmapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pucar.dristi.web.models.CourtCase;

@ExtendWith(MockitoExtension.class)
class CaseRowMapperTest {

    @Mock
    private ResultSet rs;

    private CaseRowMapper rowMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rowMapper = new CaseRowMapper();
    }

    @Test
    void testExtractData() throws Exception {
        when(rs.next()).thenReturn(true, false);  // Two rows
        when(rs.getString("id")).thenReturn(UUID.randomUUID().toString());
        when(rs.getString("tenantid")).thenReturn("tenant1");
        when(rs.getString("resolutionmechanism")).thenReturn("mechanism");
        when(rs.getString("casetitle")).thenReturn("title");
        when(rs.getString("casedescription")).thenReturn("description");
        when(rs.getString("filingnumber")).thenReturn("filing123");
        when(rs.getString("caseNumber")).thenReturn("case123");
        when(rs.getString("cnrnumber")).thenReturn("cnr123");
        when(rs.getString("courtcaseNumber")).thenReturn("court123");
        when(rs.getString("accesscode")).thenReturn("access1");
        when(rs.getString("courtid")).thenReturn("court1");
        when(rs.getString("benchid")).thenReturn("bench1");
        when(rs.getString("judgeid")).thenReturn("judge1");
        when(rs.getString("stage")).thenReturn("stage1");
        when(rs.getString("substage")).thenReturn("substage1");
        when(rs.getString("filingdate")).thenReturn("01-01-2024");
        when(rs.getString("judgementdate")).thenReturn("01-01-2024");
        when(rs.getString("registrationdate")).thenReturn("01-02-2024");
        when(rs.getString("casecategory")).thenReturn("category1");
        when(rs.getString("natureofpleading")).thenReturn("pleading");
        when(rs.getString("status")).thenReturn("status1");
        when(rs.getString("remarks")).thenReturn("remarks");

        when(rs.getLong("lastmodifiedtime")).thenReturn(123456789L);
        when(rs.getString("createdby")).thenReturn("user1");
        when(rs.getLong("createdtime")).thenReturn(123456789L);
        when(rs.getString("lastmodifiedby")).thenReturn("modifier");

        when(rs.getObject("additionalDetails")).thenReturn(null);
        when(rs.getObject("casedetails")).thenReturn(null);

        List<CourtCase> cases = rowMapper.extractData(rs);

        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("tenant1", cases.get(0).getTenantId());
        assertEquals("user1", cases.get(0).getAuditdetails().getCreatedBy());

        verify(rs, times(2)).getString("id");
    }

    @Test
    void testExtractData_Exception() throws Exception {
        when(rs.next()).thenThrow(new SQLException("Database error"));

        assertThrows(Exception.class, () -> rowMapper.extractData(rs));
    }

    @Test
    void testExtractData_CustomException() throws Exception {
        when(rs.next()).thenThrow(new CustomException());

        assertThrows(CustomException.class, () -> rowMapper.extractData(rs));
    }
}


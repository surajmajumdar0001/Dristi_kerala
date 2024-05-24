package digit.web.controllers;


import digit.web.models.ErrorRes;
import digit.web.models.SummonRequest;
import digit.web.models.SummonResponse;
    import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;
import java.util.*;

    import jakarta.validation.constraints.*;
    import jakarta.validation.Valid;
    import jakarta.servlet.http.HttpServletRequest;
        import java.util.Optional;
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-24T17:04:14.826760024+05:30[Asia/Kolkata]")
@Controller
    @RequestMapping("")
    public class SummonApiController{

        private final ObjectMapper objectMapper;

        private final HttpServletRequest request;

        @Autowired
        public SummonApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
        }

                @RequestMapping(value="/summon/v1/_generate", method = RequestMethod.POST)
                public ResponseEntity<SummonResponse> summonV1GeneratePost(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the new court case + RequestInfo meta data.", required=true, schema=@Schema()) @Valid @RequestBody SummonRequest body) {
                        String accept = request.getHeader("Accept");
                            if (accept != null && accept.contains("application/json")) {
                            try {
                            return new ResponseEntity<SummonResponse>(objectMapper.readValue("{  \"ResponseInfo\" : {    \"ver\" : \"ver\",    \"resMsgId\" : \"resMsgId\",    \"msgId\" : \"msgId\",    \"apiId\" : \"apiId\",    \"ts\" : 0,    \"status\" : \"SUCCESSFUL\"  },  \"summons\" : [ {    \"filingNumber\" : \"filingNumber\",    \"auditDetails\" : {      \"lastModifiedTime\" : 1,      \"createdBy\" : \"createdBy\",      \"lastModifiedBy\" : \"lastModifiedBy\",      \"createdTime\" : 6    },    \"witnessIdentifier\" : \"witnessIdentifier\",    \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\",    \"individualId\" : \"individualId\",    \"cnrNumber\" : \"cnrNumber\",    \"isActive\" : true,    \"additionalDetails\" : { },    \"remarks\" : \"remarks\"  }, {    \"filingNumber\" : \"filingNumber\",    \"auditDetails\" : {      \"lastModifiedTime\" : 1,      \"createdBy\" : \"createdBy\",      \"lastModifiedBy\" : \"lastModifiedBy\",      \"createdTime\" : 6    },    \"witnessIdentifier\" : \"witnessIdentifier\",    \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\",    \"individualId\" : \"individualId\",    \"cnrNumber\" : \"cnrNumber\",    \"isActive\" : true,    \"additionalDetails\" : { },    \"remarks\" : \"remarks\"  } ]}", SummonResponse.class), HttpStatus.NOT_IMPLEMENTED);
                            } catch (IOException e) {
                            return new ResponseEntity<SummonResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                            }

                        return new ResponseEntity<SummonResponse>(HttpStatus.NOT_IMPLEMENTED);
                }

        }

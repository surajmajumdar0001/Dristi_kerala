package drishti.payment.calculator.web.controllers;

import drishti.payment.calculator.service.PostalPinService;
import drishti.payment.calculator.web.models.PostalHubResponse;
import drishti.payment.calculator.web.models.PostalServiceRequest;
import drishti.payment.calculator.web.models.PostalServiceSearchRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-06-10T14:05:42.847785340+05:30[Asia/Kolkata]")
@Controller
@RequestMapping("")
@Slf4j
public class PostalServiceApiController {

    private final PostalPinService postalService;

    @Autowired
    public PostalServiceApiController(PostalPinService postalService) {
        this.postalService = postalService;
    }

    @RequestMapping(value = "/postal/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<PostalHubResponse> createHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody PostalServiceRequest body) {
        postalService.create(body);
        return null;
    }

    @RequestMapping(value = "/postal/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<PostalHubResponse> searchHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody PostalServiceSearchRequest body) {
        postalService.search(body);
        return null;
    }

    @RequestMapping(value = "/postal/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<PostalHubResponse> updateHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody PostalServiceRequest body) {
        postalService.update(body);
        return null;
    }
}

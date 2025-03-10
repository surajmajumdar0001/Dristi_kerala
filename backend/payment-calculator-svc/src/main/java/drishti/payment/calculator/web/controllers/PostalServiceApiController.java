package drishti.payment.calculator.web.controllers;

import drishti.payment.calculator.service.PostalPinService;
import drishti.payment.calculator.util.ResponseInfoFactory;
import drishti.payment.calculator.web.models.PostalService;
import drishti.payment.calculator.web.models.PostalServiceRequest;
import drishti.payment.calculator.web.models.PostalServiceResponse;
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

import java.util.List;

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
    public ResponseEntity<PostalServiceResponse> createHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody PostalServiceRequest request) {
        List<PostalService> postalServices = postalService.create(request);
        PostalServiceResponse response = PostalServiceResponse.builder().postal(postalServices).responseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true)).build();
        return ResponseEntity.accepted().body(response);

    }

    @RequestMapping(value = "/postal/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<PostalServiceResponse> searchHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody PostalServiceSearchRequest request) {
        List<PostalService> search = postalService.search(request);
        PostalServiceResponse response = PostalServiceResponse.builder().postal(search).responseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true)).build();
        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/postal/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<PostalServiceResponse> updateHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody PostalServiceRequest request) {
        List<PostalService> update = postalService.update(request);
        PostalServiceResponse response = PostalServiceResponse.builder().postal(update).responseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true)).build();
        return ResponseEntity.accepted().body(response);

    }
}

package org.pucar.dristi.controller;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.response.ResponseInfo;
import org.pucar.dristi.model.*;
import org.pucar.dristi.service.EPostService;
import org.pucar.dristi.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
public class EPostController {

    @Autowired
    private EPostService ePostService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @RequestMapping(value = "epost/v1/_sendEPost", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> sendEPost(@RequestBody TaskRequest body){
        ChannelMessage channelMessage = ePostService.sendEPost(body);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
        ChannelResponse channelResponse = ChannelResponse.builder().channelMessage(channelMessage).responseInfo(responseInfo).build();
        return new ResponseEntity<>(channelResponse, HttpStatus.OK);
    }
    @RequestMapping(value = "epost/v1/_getEPost", method = RequestMethod.POST)
    public ResponseEntity<EPostResponse> getEPost(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody EPostTrackerSearchRequest request, @NotNull @Min(0) @Max(1000) @ApiParam(value = "Pagination - limit records in response", required = true) @Valid @RequestParam(value = "limit", required = true) Integer limit, @NotNull @Min(1) @ApiParam(value = "Pagination - pageNo for which response is returned", required = true) @Valid @RequestParam(value = "offset", required = true) Integer offset) {
        List<EPostTracker> ePostTrackers = ePostService.getEPost(request, limit, offset);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        EPostResponse ePostResponse = EPostResponse.builder().responseInfo(responseInfo).ePostTrackers(ePostTrackers).build();
        return new ResponseEntity<>(ePostResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "epost/v1/_updateEPost", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> updateEPost(@RequestBody TaskRequest body){
        ChannelMessage channelMessage = ePostService.sendEPost(body);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
        ChannelResponse channelResponse = ChannelResponse.builder().channelMessage(channelMessage).responseInfo(responseInfo).build();
        return new ResponseEntity<>(channelResponse, HttpStatus.OK);
    }
}

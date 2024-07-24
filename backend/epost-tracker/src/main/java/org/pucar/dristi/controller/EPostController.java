package org.pucar.dristi.controller;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.response.ResponseInfo;
import org.pucar.dristi.model.ChannelMessage;
import org.pucar.dristi.model.EPostResponse;
import org.pucar.dristi.model.EPostTracker;
import org.pucar.dristi.model.TaskRequest;
import org.pucar.dristi.service.EPostService;
import org.pucar.dristi.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Slf4j
public class EPostController {
    @Autowired
    private EPostService ePostService;
    @Autowired
    private ResponseInfoFactory responseInfoFactory;
    @RequestMapping(value = "epost/v1/_sendEPost", method = RequestMethod.POST)
    public ResponseEntity<EPostResponse> sendEPost(@RequestBody TaskRequest body){
        ChannelMessage channelMessage = ePostService.sendEPost(body);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
        EPostResponse ePostResponse = EPostResponse.builder().channelMessage(channelMessage).responseInfo(responseInfo).build();
        return new ResponseEntity<>(ePostResponse, HttpStatus.OK);
    }
    @RequestMapping(value = "epost/v1/_getEPost", method = RequestMethod.POST)
    public ResponseEntity<EPostResponse> getEPost(@RequestBody TaskRequest body){
        ChannelMessage channelMessage = ePostService.sendEPost(body);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
        EPostResponse ePostResponse = EPostResponse.builder().channelMessage(channelMessage).responseInfo(responseInfo).build();
        return new ResponseEntity<>(ePostResponse, HttpStatus.OK);
    }
    @RequestMapping(value = "epost/v1/_updateEPost", method = RequestMethod.POST)
    public ResponseEntity<EPostResponse> updateEPost(@RequestBody TaskRequest body){
        ChannelMessage channelMessage = ePostService.sendEPost(body);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
        EPostResponse ePostResponse = EPostResponse.builder().channelMessage(channelMessage).responseInfo(responseInfo).build();
        return new ResponseEntity<>(ePostResponse, HttpStatus.OK);
    }
}

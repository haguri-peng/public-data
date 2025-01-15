package com.haguri.publicdata.bus.service;

import com.google.gson.JsonArray;
import com.haguri.publicdata.util.BusUtil;
import com.haguri.publicdata.util.MyInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SuburbsBusService {

    private final WebClient suburbsBusWebClient;
    private final MyInfoUtil myInfoUtil;

    @Autowired
    public SuburbsBusService(WebClient suburbsBusWebClient, MyInfoUtil myInfoUtil) {
        this.suburbsBusWebClient = suburbsBusWebClient;
        this.myInfoUtil = myInfoUtil;
    }

    public Mono<ResponseEntity<String>> getSuburbsBusCityCode() {
        log.info("====================[ getSuburbsBusCityCode ]====================");
        return suburbsBusWebClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/getCtyCodeList")
                                .queryParam("serviceKey", myInfoUtil.getPropertyValue("MyInfo.serviceKey"))
                                .queryParam("_type", "json")
                                .build()
                )
                .retrieve()
                .toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> getSuburbsBusGrad() {
        log.info("====================[ getSuburbsBusGrad ]====================");
        return suburbsBusWebClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/getSuberbsBusGradList")
                                .queryParam("serviceKey", myInfoUtil.getPropertyValue("MyInfo.serviceKey"))
                                .queryParam("_type", "json")
                                .build()
                )
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<JsonArray> getSuburbsBusTrminl() {
        log.info("====================[ getSuburbsBusTrminl ]====================");
        return BusUtil.getBusTrminl(suburbsBusWebClient, BusUtil.BusType.Suburbs, myInfoUtil.getPropertyValue("MyInfo.serviceKey"));
    }

    public ResponseEntity<JsonArray> getSuburbsBusInfo(
            int pageNo,
            String depTerminalId,
            String arrTerminalId,
            String depPlandTime,
            String busGradeId
    ) {
        log.info("====================[ getSuburbsBusInfo ]====================");
        return BusUtil.getBusInfo(
                suburbsBusWebClient,
                BusUtil.BusType.Suburbs,
                pageNo,
                depTerminalId,
                arrTerminalId,
                depPlandTime,
                busGradeId,
                myInfoUtil.getPropertyValue("MyInfo.serviceKey"));
    }

}

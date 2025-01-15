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
public class ExpBusService {

    private final WebClient expBusWebClient;
    private final MyInfoUtil myInfoUtil;

    @Autowired
    public ExpBusService(WebClient expBusWebClient, MyInfoUtil myInfoUtil) {
        this.expBusWebClient = expBusWebClient;
        this.myInfoUtil = myInfoUtil;
    }

    public Mono<ResponseEntity<String>> getExpBusCityCode() {
        log.info("====================[ getExpBusCityCode ]====================");
        return expBusWebClient
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

    public Mono<ResponseEntity<String>> getExpBusGrad() {
        log.info("====================[ getExpBusGrad ]====================");
        return expBusWebClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/getExpBusGradList")
                                .queryParam("serviceKey", myInfoUtil.getPropertyValue("MyInfo.serviceKey"))
                                .queryParam("_type", "json")
                                .build()
                )
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<JsonArray> getExpBusTrminl() {
        log.info("====================[ getExpBusTrminl ]====================");
        return BusUtil.getBusTrminl(
                expBusWebClient,
                BusUtil.BusType.Exp,
                myInfoUtil.getPropertyValue("MyInfo.serviceKey"));
    }

    public ResponseEntity<JsonArray> getExpBusInfo(
            int pageNo,
            String depTerminalId,
            String arrTerminalId,
            String depPlandTime,
            String busGradeId
    ) {
        log.info("====================[ getExpBusInfo ]====================");
        return BusUtil.getBusInfo(
                expBusWebClient,
                BusUtil.BusType.Exp,
                pageNo,
                depTerminalId,
                arrTerminalId,
                depPlandTime,
                busGradeId,
                myInfoUtil.getPropertyValue("MyInfo.serviceKey"));
    }

}

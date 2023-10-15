package com.haguri.publicdata.bus.controller;

import com.haguri.publicdata.bus.service.SuburbsBusService;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/suburbsBus")
public class SuburbsBusController {

    private final SuburbsBusService suburbsBusService;

    @Autowired
    public SuburbsBusController(SuburbsBusService suburbsBusService) {
        this.suburbsBusService = suburbsBusService;
    }

    // 시외버스 도시코드
    @GetMapping("/cityCode")
    public Mono<ResponseEntity<String>> searchSuburbsBusCityCode() {
        return suburbsBusService.getSuburbsBusCityCode();
    }

    // 시외버스 등급
    @GetMapping("/grad")
    public Mono<ResponseEntity<String>> searchSuburbsBusGrad() {
        return suburbsBusService.getSuburbsBusGrad();
    }

    // 시외버스 터미널
    @GetMapping("/trminl")
    public ResponseEntity<JSONArray> searchSuburbsBusTrminl() throws ParseException {
        return suburbsBusService.getSuburbsBusTrminl();
    }

    // 시외버스 출/도착지기반 버스 정보
    @GetMapping("/busInfo")
    public ResponseEntity<JSONArray> searchExpBusInfo(
            @RequestParam("pageNo") int pageNo,
            @RequestParam(value = "depTerminalId", required = false, defaultValue = "") String depTerminalId,
            @RequestParam(value = "arrTerminalId", required = false, defaultValue = "") String arrTerminalId,
            @RequestParam("depPlandTime") String depPlandTime,
            @RequestParam(value = "busGradeId", required = false, defaultValue = "") String busGradeId
    ) throws ParseException {
        return suburbsBusService.getExpBusInfo(
                pageNo,
                depTerminalId,
                arrTerminalId,
                depPlandTime,
                busGradeId
        );
    }

}

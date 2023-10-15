package com.haguri.publicdata.stanReginCd.controller;

import com.haguri.publicdata.stanReginCd.service.StanReginCdService;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/stanReginCd")
public class StanReginCdController {

    private final StanReginCdService stanReginCdService;

    @Autowired
    public StanReginCdController(StanReginCdService stanReginCdService) {
        this.stanReginCdService = stanReginCdService;
    }

    // 행정안전부 법정동코드
    // csv 파일 생성
    @GetMapping("/getStanReginCdList")
    public void searchStanReginCdList() throws ParseException, IOException {
        stanReginCdService.getStanReginCdList();
    }

    // 국토교통부 법정동코드
    @GetMapping("/getStanReginCd2List")
    public ResponseEntity<JSONArray> searchStanReginCd2List() throws ParseException {
        return stanReginCdService.getStanReginCd2List();
    }

}

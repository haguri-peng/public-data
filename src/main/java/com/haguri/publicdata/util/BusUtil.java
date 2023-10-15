package com.haguri.publicdata.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BusUtil {

    /**
     * Bus Type
     * 고속버스(Exp), 시외버스(Suburbs)
     */
    public enum BusType {
        Exp, // 고속버스
        Suburbs // 시외버스
    }

    /**
     * BusType에 따라 터미널 정보 전체를 조회한다.
     *
     * @param webClient WebClient
     * @param busType   BusType
     * @return ResponseEntity
     */
    @SuppressWarnings("rawtypes")
    public static ResponseEntity<JSONArray> getBusTrminl(WebClient webClient, BusType busType, String serviceKey) throws ParseException {
        // 먼저, HEADER 정보를 보고 진행 여부를 확인
        Mono<String> resMono = fetchBusTrminl(webClient, busType, 1, 10, serviceKey);
        String strRes = resMono.block();

        //System.out.println(strRes);

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(strRes);
        JSONObject jsonObj = (JSONObject) obj;

        JSONObject jsonRes = (JSONObject) jsonObj.get("response");
        JSONObject jsonHeader = (JSONObject) jsonRes.get("header"); // header
        JSONObject jsonBody = (JSONObject) jsonRes.get("body"); // body

        //System.out.println(jsonHeader);

        int totCnt; // 전체 결과 수
        int pageNo; // 페이지 번호
        int numOfRows; // 페이지 당 요청 숫자
        int totPageNo; // 총 페이지 수
        List<Map> itemLst = new ArrayList<>(); // 출력할 전체 데이터

        // resultCode 가 00 이면 정상
        if ("00".equals(jsonHeader.get("resultCode").toString())) {
            totCnt = Integer.parseInt(jsonBody.get("totalCount").toString());
            pageNo = Integer.parseInt(jsonBody.get("pageNo").toString());
            numOfRows = Integer.parseInt(jsonBody.get("numOfRows").toString());
            // 호출 횟수를 줄이기 위해 기본값(10)에서 100으로 설정
            if (numOfRows == 10) {
                numOfRows = 100;
            }
            totPageNo = (int) Math.ceil((double) totCnt / numOfRows);

            //System.out.println("totCnt >> " + totCnt);
            //System.out.println("pageNo >> " + pageNo);
            //System.out.println("numOfRows >> " + numOfRows);
            //System.out.println("totPageNo >> " + totPageNo);

            while (pageNo <= totPageNo) {
                String resFetch = fetchBusTrminl(webClient, busType, pageNo++, numOfRows, serviceKey).block();

                JSONParser parser = new JSONParser();
                Object objFetch = parser.parse(resFetch);
                JSONObject jsonFetch = (JSONObject) objFetch;

                JSONObject jsonResFetch = (JSONObject) jsonFetch.get("response");
                JSONArray jsonArrItem = (JSONArray)
                        ((JSONObject) ((JSONObject) jsonResFetch.get("body")).get("items")).get("item");
                List<Map> dataLst = JsonUtil.getListMapFromJSONArray(jsonArrItem);

                //System.out.println(dataLst);

                itemLst.addAll(dataLst);
            }
            return ResponseEntity.ok(JsonUtil.getJSONArrayFromList(itemLst));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * BusType에 따라 터미널 정보를 조회한다.
     *
     * @param webClient WebClient
     * @param busType   BusType
     * @param pageNo    페이지번호
     * @param numOfRows 페이지당건수
     * @return Mono
     */
    private static Mono<String> fetchBusTrminl(
            WebClient webClient,
            BusType busType,
            int pageNo,
            int numOfRows,
            String serviceKey
    ) {
        String path;
        if (busType == BusType.Exp) {
            path = "/getExpBusTrminlList";
        } else if (busType == BusType.Suburbs) {
            path = "/getSuberbsBusTrminlList";
        } else {
            path = "";
        }

        return webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(path)
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("pageNo", pageNo)
                                .queryParam("numOfRows", numOfRows)
                                .queryParam("_type", "json")
                                .build()
                )
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * BusType에 따라 출/도착지기반 버스 정보 전체를 조회한다.
     *
     * @param webClient     WebClient
     * @param busType       BusType
     * @param pageNo        페이지번호
     * @param depTerminalId 출발터미널ID
     * @param arrTerminalId 도착터미널ID
     * @param depPlandTime  출발일
     * @param busGradeId    버스등급
     * @return ResponseEntity
     */
    @SuppressWarnings("rawtypes")
    public static ResponseEntity<JSONArray> getBusInfo(
            WebClient webClient,
            BusType busType,
            int pageNo,
            String depTerminalId,
            String arrTerminalId,
            String depPlandTime,
            String busGradeId,
            String serviceKey
    ) throws ParseException {
        //System.out.println("pageNo >> " + pageNo);
        //System.out.println("depTerminalId >> " + depTerminalId);
        //System.out.println("arrTerminalId >> " + arrTerminalId);
        //System.out.println("depPlandTime >> " + depPlandTime);
        //System.out.println("busGradeId >> " + busGradeId);

        // 먼저, HEADER 정보를 보고 진행 여부를 확인
        Mono<String> resMono = fetchBusInfo(
                webClient,
                busType,
                pageNo,
                depTerminalId,
                arrTerminalId,
                depPlandTime,
                busGradeId,
                serviceKey
        );
        String strRes = resMono.block();

        //System.out.println(strRes);

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(strRes);
        JSONObject jsonObj = (JSONObject) obj;

        JSONObject jsonRes = (JSONObject) jsonObj.get("response");
        JSONObject jsonHeader = (JSONObject) jsonRes.get("header"); // header
        JSONObject jsonBody = (JSONObject) jsonRes.get("body"); // body

        //System.out.println(jsonHeader);

        int totCnt; // 전체 결과 수
        int totPageNo; // 총 페이지 수

        // resultCode 가 00 이면 정상
        if ("00".equals(jsonHeader.get("resultCode").toString())) {
            totCnt = Integer.parseInt(jsonBody.get("totalCount").toString());
            totPageNo = (int) Math.ceil((double) totCnt / 100);

            //System.out.println("totCnt >> " + totCnt);
            //System.out.println("totPageNo >> " + totPageNo);

            // 총 페이지 수보다 크면 fetch 하지 않음
            if (pageNo > totPageNo) {
                return ResponseEntity.noContent().build();
            }

            String resFetch = fetchBusInfo(
                    webClient,
                    busType,
                    pageNo,
                    depTerminalId,
                    arrTerminalId,
                    depPlandTime,
                    busGradeId,
                    serviceKey
            ).block();

            JSONParser parser = new JSONParser();
            Object objFetch = parser.parse(resFetch);
            JSONObject jsonFetch = (JSONObject) objFetch;

            JSONObject jsonResFetch = (JSONObject) jsonFetch.get("response");
            JSONArray jsonArrItem = (JSONArray)
                    ((JSONObject) ((JSONObject) jsonResFetch.get("body")).get("items")).get("item");
            List<Map> dataLst = JsonUtil.getListMapFromJSONArray(jsonArrItem);

            //System.out.println(dataLst);

            return ResponseEntity.ok(JsonUtil.getJSONArrayFromList(dataLst));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * BusType에 따라 출/도착지기반 버스 정보를 조회한다.
     *
     * @param webClient     WebClient
     * @param busType       BusType
     * @param pageNo        페이지번호
     * @param depTerminalId 출발터미널ID
     * @param arrTerminalId 도착터미널ID
     * @param depPlandTime  출발일
     * @param busGradeId    버스등급
     * @return Mono
     */
    private static Mono<String> fetchBusInfo(
            WebClient webClient,
            BusType busType,
            int pageNo,
            String depTerminalId,
            String arrTerminalId,
            String depPlandTime,
            String busGradeId,
            String serviceKey
    ) {
        String path;
        if (busType == BusType.Exp) {
            path = "/getStrtpntAlocFndExpbusInfo";
        } else if (busType == BusType.Suburbs) {
            path = "/getStrtpntAlocFndSuberbsBusInfo";
        } else {
            path = "";
        }

        return webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(path)
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("pageNo", pageNo)
                                .queryParam("numOfRows", 100)
                                .queryParam("_type", "json")
                                .queryParamIfPresent("depTerminalId", Optional.ofNullable(depTerminalId))
                                .queryParamIfPresent("arrTerminalId", Optional.ofNullable(arrTerminalId))
                                .queryParamIfPresent("depPlandTime", Optional.ofNullable(depPlandTime))
                                .queryParamIfPresent("busGradeId", Optional.ofNullable(busGradeId))
                                .build()
                )
                .retrieve()
                .bodyToMono(String.class);
    }

}

package com.haguri.publicdata.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
public class BusUtil {

    /**
     * BusType에 따라 터미널 정보 전체를 조회한다.
     *
     * @param webClient WebClient
     * @param busType   BusType
     * @return ResponseEntity
     */
    public static ResponseEntity<JsonArray> getBusTrminl(
            WebClient webClient,
            BusType busType,
            String serviceKey) {

        // 먼저, HEADER 정보를 보고 진행 여부를 확인
        Mono<String> resMono = fetchBusTrminl(webClient, busType, 1, 10, serviceKey);
        String strRes = resMono.block();

        log.info("====================[ BusUtil.getBusTrminl ]====================");
        log.info("busType : {}", busType);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(strRes, JsonObject.class);
        JsonObject jsonRes = jsonObject.get("response").getAsJsonObject();
        JsonObject jsonHeader = jsonRes.get("header").getAsJsonObject(); // header
        JsonObject jsonBody = jsonRes.get("body").getAsJsonObject(); // body

        int totCnt; // 전체 결과 수
        int pageNo; // 페이지 번호
        int numOfRows; // 페이지 당 요청 숫자
        int totPageNo; // 총 페이지 수
        JsonArray itemLst = new JsonArray(); // 출력할 데이터

        // resultCode 가 00 이면 정상
        if ("00".equals(jsonHeader.get("resultCode").getAsString())) {
            totCnt = jsonBody.get("totalCount").getAsInt();
            pageNo = jsonBody.get("pageNo").getAsInt();
            numOfRows = jsonBody.get("numOfRows").getAsInt();
            // 호출 횟수를 줄이기 위해 기본값(10)에서 100으로 설정
            if (numOfRows == 10) {
                numOfRows = 100;
            }
            totPageNo = (int) Math.ceil((double) totCnt / numOfRows);

            while (pageNo <= totPageNo) {
                String resFetch = fetchBusTrminl(webClient, busType, pageNo++, numOfRows, serviceKey).block();

                JsonObject jsonFetch = gson.fromJson(resFetch, JsonObject.class);
                JsonObject jsonResFetch = jsonFetch.get("response").getAsJsonObject();
                JsonArray jsonArray = jsonResFetch
                        .get("body").getAsJsonObject()
                        .get("items").getAsJsonObject()
                        .get("item").getAsJsonArray();

                itemLst.addAll(jsonArray);
            }
            return ResponseEntity.ok(itemLst);
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
    public static ResponseEntity<JsonArray> getBusInfo(
            WebClient webClient,
            BusType busType,
            int pageNo,
            String depTerminalId,
            String arrTerminalId,
            String depPlandTime,
            String busGradeId,
            String serviceKey
    ) {

        log.info("====================[ BusUtil.getBusInfo ]====================");
        log.info("pageNo : {}, busType : {}, busGradeId : {}", pageNo, busType, busGradeId);
        log.info("depPlandTime : {}, depTerminalId : {}, arrTerminalId : {}", depPlandTime, depTerminalId, arrTerminalId);

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

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(strRes, JsonObject.class);
        JsonObject jsonRes = jsonObject.get("response").getAsJsonObject();
        JsonObject jsonHeader = jsonRes.get("header").getAsJsonObject(); // header
        JsonObject jsonBody = jsonRes.get("body").getAsJsonObject(); // body

        int totCnt; // 전체 결과 수
        int totPageNo; // 총 페이지 수

        // resultCode 가 00 이면 정상
        if ("00".equals(jsonHeader.get("resultCode").getAsString())) {
            totCnt = jsonBody.get("totalCount").getAsInt();
            totPageNo = (int) Math.ceil((double) totCnt / 100);

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

            JsonObject jsonFetch = gson.fromJson(resFetch, JsonObject.class);
            JsonObject jsonResFetch = jsonFetch.get("response").getAsJsonObject();
            JsonArray jsonArray = jsonResFetch
                    .get("body").getAsJsonObject()
                    .get("items").getAsJsonObject()
                    .get("item").getAsJsonArray();

            return ResponseEntity.ok(jsonArray);
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

    /**
     * Bus Type
     * 고속버스(Exp), 시외버스(Suburbs)
     */
    public enum BusType {
        Exp, // 고속버스
        Suburbs // 시외버스
    }

}

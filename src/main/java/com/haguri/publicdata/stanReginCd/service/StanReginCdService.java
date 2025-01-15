package com.haguri.publicdata.stanReginCd.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.haguri.publicdata.util.MyInfoUtil;
import com.haguri.publicdata.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class StanReginCdService {

    private final WebClient stanReginCdWebClient;
    private final WebClient stanReginCd2ModelInfoWebClient;
    private final WebClient stanReginCd2WebClient;
    private final MyInfoUtil myInfoUtil;

    @Autowired
    public StanReginCdService(
            WebClient stanReginCdWebClient,
            WebClient stanReginCd2ModelInfoWebClient,
            WebClient stanReginCd2WebClient,
            MyInfoUtil myInfoUtil
    ) {
        this.stanReginCdWebClient = stanReginCdWebClient;
        this.stanReginCd2ModelInfoWebClient = stanReginCd2ModelInfoWebClient;
        this.stanReginCd2WebClient = stanReginCd2WebClient;
        this.myInfoUtil = myInfoUtil;
    }

    @SuppressWarnings("rawtypes")
    public void getStanReginCdList() throws IOException {
        log.info("====================[ getStanReginCdList ]====================");

        // 먼저, HEADER 정보를 보고 진행 여부를 확인
        String strRes = fetchStanReginCd(1, 10);

        Gson gson = new Gson();
        JsonObject jsonRes = gson.fromJson(strRes, JsonObject.class);
        JsonObject jsonHead = jsonRes.get("StanReginCd").getAsJsonArray().get(0).getAsJsonObject();
        JsonArray headLst = jsonHead.get("head").getAsJsonArray();

        int totCnt; // 전체 결과 수
        int pageNo; // 페이지 번호
        int numOfRows; // 페이지 당 요청 숫자
        int totPageNo; // 총 페이지 수
        JsonArray allDataLst = new JsonArray(); // 출력할 데이터

        if (!headLst.isEmpty()) {
            JsonObject jsonResult = headLst.get(2).getAsJsonObject().get("RESULT").getAsJsonObject();
            if ("INFO-0".equals(jsonResult.get("resultCode").getAsString())) {
                // INFO-0: 정상
                totCnt = headLst.get(0).getAsJsonObject().get("totalCount").getAsInt();
                pageNo = headLst.get(1).getAsJsonObject().get("pageNo").getAsInt();
                numOfRows = headLst.get(1).getAsJsonObject().get("numOfRows").getAsInt();
                // 호출 횟수를 줄이기 위해 기본값(10)에서 1000으로 설정
                if (numOfRows == 10) {
                    numOfRows = 1000;
                }
                totPageNo = (int) Math.ceil((double) totCnt / numOfRows);

                while (pageNo <= totPageNo) {
                    String resFetch = fetchStanReginCd(pageNo++, numOfRows);

                    JsonObject jsonResFetch = gson.fromJson(resFetch, JsonObject.class);
                    JsonObject jsonRow = jsonResFetch.get("StanReginCd").getAsJsonArray().get(1).getAsJsonObject();
                    JsonArray dataLst = jsonRow.get("row").getAsJsonArray();

                    allDataLst.addAll(dataLst);
                }

                try (FileOutputStream out = new FileOutputStream(".files/makeFiles/stanReginCd.csv")) {
                    for (int i = 0; i < allDataLst.size(); i++) {
                        Map map = gson.fromJson(allDataLst.get(i), Map.class);

                        // 첫번째 Row에 Header 정보를 세팅
                        if (i == 0) {
                            String strHeader = MyUtil.getMapToStringForKey(map) + "\n";
                            out.write(strHeader.getBytes());
                        }
                        out.write((MyUtil.getMapToStringForValue(map) + "\n").getBytes());
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 행정안전부 법정동코드 조회
     *
     * @param pageNo    페이지번호
     * @param numOfRows 페이지당건수
     * @return String
     */
    private String fetchStanReginCd(int pageNo, int numOfRows) {
        return stanReginCdWebClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/getStanReginCdList")
                                .queryParam("serviceKey", myInfoUtil.getPropertyValue("MyInfo.serviceKey"))
                                .queryParam("pageNo", pageNo)
                                .queryParam("numOfRows", numOfRows)
                                .queryParam("type", "json")
                                .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity<JsonArray> getStanReginCd2List() {
        log.info("====================[ getStanReginCd2List ]====================");

        String lstUpdDate = "";
        String lstPath = "";

        Map modelInfo = getStanReginCd2ModelInfo();
        for (Object key : modelInfo.keySet()) {
            Map modelDtlMap = (Map) ((Map) modelInfo.get(key)).get("get");
            String summary = modelDtlMap.get("summary").toString();
            String[] arrSummary = summary.split("_");
            String updDate = arrSummary[arrSummary.length - 1];

            Integer dateCmpVal = MyUtil.dateCompareTo(lstUpdDate, updDate);
            if (dateCmpVal != null && dateCmpVal < 0) {
                lstUpdDate = updDate;
                lstPath = key.toString();
            }
        }

        log.info("lstUpdDate : {}, lstPath : {}", lstUpdDate, lstPath);

        // 전체 건수 확인
        String strRes = fetchStanReginCd2(lstPath, 1, 10);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(strRes, JsonObject.class);
        JsonArray allDataLst = new JsonArray(); // 출력할 데이터

        if (!jsonObject.isEmpty()) {
            int page = 1; // 페이지 번호
            int perPage = 5000; // 페이지 당 요청 숫자
            int totCnt = jsonObject.get("totalCount").getAsInt(); // 전체 결과 수
            int totPage = (int) Math.ceil((double) totCnt / perPage); // 총 페이지 수

            log.info("totCnt: {}, totPage : {}", totCnt, totPage);

            while (page <= totPage) {
                String resFetch = fetchStanReginCd2(lstPath, page++, perPage);
                JsonObject jsonResFetch = gson.fromJson(resFetch, JsonObject.class);
                JsonArray dataLst = jsonResFetch.get("data").getAsJsonArray();

                allDataLst.addAll(dataLst);
            }

            return ResponseEntity.ok(allDataLst);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 국토교통부 법정동코드 기본 정보 조회
     *
     * @return Map
     */
    @SuppressWarnings("rawtypes")
    private Map getStanReginCd2ModelInfo() {
        // Model 및 정보를 확인
        String strRes = stanReginCd2ModelInfoWebClient
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(strRes, JsonObject.class);

        return gson.fromJson(jsonObject.get("paths"), Map.class);
    }

    /**
     * 국토교통부 법정동코드 조회
     *
     * @param path    URL path
     * @param page    페이지번호
     * @param perPage 페이지당건수
     * @return String
     */
    private String fetchStanReginCd2(String path, int page, int perPage) {
        return stanReginCd2WebClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(path)
                                .queryParam("serviceKey", myInfoUtil.getPropertyValue("MyInfo.serviceKey"))
                                .queryParam("page", page)
                                .queryParam("perPage", perPage)
                                .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

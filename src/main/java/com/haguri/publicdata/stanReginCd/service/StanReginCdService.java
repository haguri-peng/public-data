package com.haguri.publicdata.stanReginCd.service;

import com.haguri.publicdata.util.JsonUtil;
import com.haguri.publicdata.util.MyInfoUtil;
import com.haguri.publicdata.util.MyUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            MyInfoUtil myInfoUtil) {
        this.stanReginCdWebClient = stanReginCdWebClient;
        this.stanReginCd2ModelInfoWebClient = stanReginCd2ModelInfoWebClient;
        this.stanReginCd2WebClient = stanReginCd2WebClient;
        this.myInfoUtil = myInfoUtil;
    }

    @SuppressWarnings("rawtypes")
    public void getStanReginCdList() throws ParseException, IOException {
        // 먼저, HEADER 정보를 보고 진행 여부를 확인
        String strRes = fetchStanReginCd(1, 10);

        //System.out.println(strRes);

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(strRes);
        JSONObject jsonObj = (JSONObject) obj;

        JSONArray jsonArrStanReginCd = (JSONArray) jsonObj.get("StanReginCd");
        JSONObject jsonHead = (JSONObject) jsonArrStanReginCd.get(0); // head
        List<Map> headLst = JsonUtil.getListMapFromJSONArray((JSONArray) jsonHead.get("head"));

        //System.out.println(headLst);

        int totCnt; // 전체 결과 수
        int pageNo; // 페이지 번호
        int numOfRows; // 페이지 당 요청 숫자
        int totPageNo; // 총 페이지 수
        List<Map> allDataLst = new ArrayList<>(); // 출력할 전체 데이터

        if (!headLst.isEmpty()) {
            Map resultMap = (Map) headLst.get(2).get("RESULT");
            if ("INFO-0".equals(resultMap.get("resultCode").toString())) {
                // INFO-0: 정상
                totCnt = (int) headLst.get(0).get("totalCount");
                pageNo = Integer.parseInt(headLst.get(1).get("pageNo").toString());
                numOfRows = Integer.parseInt(headLst.get(1).get("numOfRows").toString());
                // 호출 횟수를 줄이기 위해 기본값(10)에서 1000으로 설정
                if (numOfRows == 10) {
                    numOfRows = 1000;
                }
                totPageNo = (int) Math.ceil((double) totCnt / numOfRows);

                //System.out.println("totCnt >> " + totCnt);
                //System.out.println("pageNo >> " + pageNo);
                //System.out.println("numOfRows >> " + numOfRows);
                //System.out.println("totPageNo >> " + totPageNo);

                while (pageNo <= totPageNo) {
                    String resFetch = fetchStanReginCd(pageNo++, numOfRows);

                    JSONParser parser = new JSONParser();
                    Object object = parser.parse(resFetch);
                    JSONObject jsonObject = (JSONObject) object;

                    JSONArray jsonArr = (JSONArray) jsonObject.get("StanReginCd");
                    JSONObject jsonRow = (JSONObject) jsonArr.get(1); // row
                    List<Map> dataLst = JsonUtil.getListMapFromJSONArray((JSONArray) jsonRow.get("row"));

                    //System.out.println(dataLst);

                    allDataLst.addAll(dataLst);
                }

                //System.out.println(allDataLst);

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(".files/makeFiles/stanReginCd.csv");

                    for (int i = 0; i < allDataLst.size(); i++) {
                        Map map = allDataLst.get(i);

                        // 첫번째 Row에 Header 정보를 세팅
                        if (i == 0) {
                            String strHeader = MyUtil.getMapToStringForKey(map) + "\n";
                            out.write(strHeader.getBytes());
                        }
                        out.write((MyUtil.getMapToStringForValue(map) + "\n").getBytes());
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } finally {
                    assert out != null;
                    out.close();
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
    public ResponseEntity<JSONArray> getStanReginCd2List() throws ParseException {
        String lstUpdDate = "";
        String lstPath = "";

        Map modelInfo = getStanReginCd2ModelInfo();
        for (Object key : modelInfo.keySet()) {
            Map modelDtlMap = (Map) ((Map) modelInfo.get(key)).get("get");
            String summary = modelDtlMap.get("summary").toString();
            String[] arrSummary = summary.split("_");
            String updDate = arrSummary[arrSummary.length - 1];

            //System.out.println("updDate >> " + updDate);

            Integer dateCmpVal = MyUtil.dateCompareTo(lstUpdDate, updDate);
            if (dateCmpVal != null && dateCmpVal < 0) {
                lstUpdDate = updDate;
                lstPath = key.toString();
            }
        }

        //System.out.println("lstUpdDate >> " + lstUpdDate);
        //System.out.println("lstPath >> " + lstPath);

        // 전체 건수 확인
        String strRes = fetchStanReginCd2(lstPath, 1, 10);

        //System.out.println(strRes);

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(strRes);
        JSONObject jsonObj = (JSONObject) obj;

        List<Map> allDataLst = new ArrayList<>(); // 출력할 전체 데이터

        //System.out.println("totalCount >> " + jsonObj.get("totalCount"));

        if (!jsonObj.isEmpty()) {
            int page = 1; // 페이지 번호
            int perPage = 5000; // 페이지 당 요청 숫자
            int totCnt = Integer.parseInt(jsonObj.get("totalCount").toString()); // 전체 결과 수
            int totPage = (int) Math.ceil((double) totCnt / perPage); // 총 페이지 수

            //System.out.println("totCnt >> " + totCnt);
            //System.out.println("totPage >> " + totPage);

            while (page <= totPage) {
                String resFetch = fetchStanReginCd2(lstPath, page++, perPage);

                JSONParser parser = new JSONParser();
                Object object = parser.parse(resFetch);
                JSONObject jsonObject = (JSONObject) object;

                List<Map> dataLst = JsonUtil.getListMapFromJSONArray((JSONArray) jsonObject.get("data"));

                allDataLst.addAll(dataLst);
            }

            //System.out.println(allDataLst);

            return ResponseEntity.ok(JsonUtil.getJSONArrayFromList(allDataLst));
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
    private Map getStanReginCd2ModelInfo() throws ParseException {
        // Model 및 정보를 확인
        String strRes = stanReginCd2ModelInfoWebClient
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //System.out.println(strRes);

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(strRes);
        JSONObject jsonObj = (JSONObject) obj;

        return JsonUtil.getMapFromJSONObject((JSONObject) jsonObj.get("paths"));
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

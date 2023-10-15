package com.haguri.publicdata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {

    /**
     * JSONObject를 Map 형식으로 변환한다.
     *
     * @param jsonObject Map으로 변환할 JSONObject
     * @return Map
     */
    @SuppressWarnings("rawtypes")
    public static Map getMapFromJSONObject(JSONObject jsonObject) {
        if (ObjectUtils.isEmpty(jsonObject)) {
            log.error("BAD REQUEST obj: {}", jsonObject);
            throw new IllegalArgumentException(String.format("BAD REQUEST OBJ %S", jsonObject));
        }

        try {
            return new ObjectMapper().readValue(jsonObject.toJSONString(), Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * JSONArray를 List 형식으로 변환한다.
     *
     * @param jsonArray List로 변환할 JSONArray
     * @return List
     */
    @SuppressWarnings("rawtypes")
    public static List<Map> getListMapFromJSONArray(JSONArray jsonArray) {
        if (ObjectUtils.isEmpty(jsonArray)) {
            log.error("jsonArray is null.");
            throw new IllegalArgumentException("jsonArray is null");
        }

        List<Map> list = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            list.add(getMapFromJSONObject((JSONObject) jsonObject));
        }
        return list;
    }

    /**
     * Map을 JSONObject로 변환한다.
     *
     * @param map JSONObject로 변환할 Map
     * @return JSONObject
     */
    @SuppressWarnings("unchecked")
    public static JSONObject getJSONObjectFromMap(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        for (String key : map.keySet()) {
            jsonObject.put(key, map.get(key));
        }
        return jsonObject;
    }

    /**
     * List를 JSONArray로 변환한다.
     *
     * @param list JSONArray로 변환할 List
     * @return JSONArray
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static JSONArray getJSONArrayFromList(List<Map> list) {
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> map : list) {
            jsonArray.add(getJSONObjectFromMap(map));
        }
        return jsonArray;
    }

}

package com.haguri.publicdata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {

    /**
     * JsonObject를 Map 형식으로 변환한다.
     *
     * @param jsonObject Map으로 변환할 JsonObject
     * @return Map
     */
    @SuppressWarnings("rawtypes")
    public static Map getMapFromJsonObject(JsonObject jsonObject) {
        if (ObjectUtils.isEmpty(jsonObject)) {
            log.error("BAD REQUEST obj: {}", jsonObject);
            throw new IllegalArgumentException(String.format("BAD REQUEST OBJ %S", jsonObject));
        }

        try {
            return new ObjectMapper().readValue(jsonObject.getAsString(), Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * JsonArray를 List 형식으로 변환한다.
     *
     * @param jsonArray List로 변환할 JsonArray
     * @return List
     */
    @SuppressWarnings("rawtypes")
    public static List<Map> getListMapFromJSONArray(JsonArray jsonArray) {
        if (ObjectUtils.isEmpty(jsonArray)) {
            log.error("jsonArray is null.");
            throw new IllegalArgumentException("jsonArray is null");
        }

        List<Map> list = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            list.add(getMapFromJsonObject((JsonObject) jsonObject));
        }
        return list;
    }

    /**
     * Map을 JsonObject로 변환한다.
     *
     * @param map JsonObject로 변환할 Map
     * @return JsonObject
     */
    public static JsonObject getJsonObjectFromMap(Map<String, Object> map) {
        JsonObject jsonObject = new JsonObject();
        for (String key : map.keySet()) {
            jsonObject.add(key, (JsonElement) map.get(key));
        }
        return jsonObject;
    }

    /**
     * List를 JsonArray로 변환한다.
     *
     * @param list JsonArray로 변환할 List
     * @return JsonArray
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static JsonArray getJsonArrayFromList(List<Map> list) {
        JsonArray jsonArray = new JsonArray();
        for (Map<String, Object> map : list) {
            jsonArray.add(getJsonObjectFromMap(map));
        }
        return jsonArray;
    }

}

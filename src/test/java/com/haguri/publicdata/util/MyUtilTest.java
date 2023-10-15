package com.haguri.publicdata.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyUtilTest {

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void getMapToStringForKey() {
        List<String> exptRslt = Arrays.asList("sido_cd", "ri_cd", "locat_order", "locat_rm");
        Map testMap = new HashMap<String, String>();
        testMap.put("ri_cd", "리코드");
        testMap.put("sido_cd", "시도코드");
        testMap.put("locat_rm", "비고");
        testMap.put("locat_order", "서열");
        assertEquals(String.join(",", exptRslt), MyUtil.getMapToStringForKey(testMap));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void getMapToStringForValue() {
        List<String> exptRslt = Arrays.asList("시도코드", "리코드", "서열", "비고");
        Map testMap = new HashMap<String, String>();
        testMap.put("ri_cd", "리코드");
        testMap.put("sido_cd", "시도코드");
        testMap.put("locat_rm", "비고");
        testMap.put("locat_order", "서열");
        assertEquals(String.join(",", exptRslt), MyUtil.getMapToStringForValue(testMap));
    }

    @Test
    void dateCompareTo() {
        String strtDt = "20231001";
        String endDt = "20231009";
        Integer result = MyUtil.dateCompareTo(strtDt, endDt);
        assert result != null;
        assertTrue(result < 0);

        String strtDt2 = "202310";
        String endDt2 = "202311";
        Integer result2 = MyUtil.dateCompareTo(strtDt2, endDt2);
        assertNull(result2);
    }

}
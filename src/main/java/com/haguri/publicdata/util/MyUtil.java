package com.haguri.publicdata.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MyUtil {

    /**
     * 선언된 Enum(StanReginCdRow)의 순서대로 Key 값을 sort 하고 구분자를 붙여서 출력한다.
     * (행정안전부 법정동코드 사용 시 적용)
     *
     * @param paramMap Map 형식의 데이터
     * @return String
     * <p>
     * e.g. region_cd,sido_cd,sgg_cd,umd_cd,ri_cd,...
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String getMapToStringForKey(Map paramMap) {
        Comparator<String> comp = (s1, s2) -> {
            StanReginCdRow enum1 = StanReginCdRow.valueOf(s1);
            StanReginCdRow enum2 = StanReginCdRow.valueOf(s2);
            return enum1.compareTo(enum2);
        };
        return paramMap
                .keySet()
                .stream()
                .map(Object::toString)
                .sorted(comp)
                .collect(Collectors.joining(",")).toString();
    }

    /**
     * 선언된 Enum(StanReginCdRow)의 순서대로 Key 값을 sort 하고 이 Key에 할당된 Value에 구분자를 붙여서 출력한다.
     * (행정안전부 법정동코드 사용 시 적용)
     *
     * @param paramMap Map 형식의 데이터
     * @return String
     * <p>
     * e.g. 717010900,27,170,109,00,...
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String getMapToStringForValue(Map paramMap) {
        Comparator<Map.Entry> comp = (s1, s2) -> {
            StanReginCdRow enum1 = StanReginCdRow.valueOf(s1.getKey().toString());
            StanReginCdRow enum2 = StanReginCdRow.valueOf(s2.getKey().toString());
            return enum1.compareTo(enum2);
        };

        Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
        return entries
                .stream()
                .sorted(comp)
                //.map(Map.Entry::getValue)
                .map(e -> {
                    Class<?> objectClass = e.getValue().getClass();
                    String className = objectClass.getSimpleName();
                    if (className.equals("Integer")) {
                        return String.valueOf(e.getValue());
                    } else {
                        return e.getValue().toString();
                    }
                })
                .collect(Collectors.joining(","));
    }

    /**
     * 행정안전부 법정동코드 Body
     */
    public enum StanReginCdRow {
        region_cd, // 지역코드
        sido_cd, // 시도코드
        sgg_cd, // 시군구코드
        umd_cd, // 읍면동코드
        ri_cd, // 리코드
        locatjumin_cd, // 지역코드_주민
        locatjijuk_cd, // 역코드_지적
        locatadd_nm, // 지역주소명
        locat_order, // 서열
        locat_rm, // 비고
        locathigh_cd, // 상위지여코드
        locallow_nm, // 최하위지역명
        adpt_de, // 생성일
    }

    /**
     * startDate, endDate 날짜 비교
     *
     * @param strtDt e.g. 20200915
     * @param endDt  e.g. 20200917
     * @return Integer
     * <p>
     * 양수: strtDt > endDt, 0: strtDt = endDt, 음수: strtDt < endDt
     */
    public static Integer dateCompareTo(String strtDt, String endDt) {
        if (strtDt.isEmpty()) strtDt = "19000101";
        if (endDt.isEmpty()) endDt = "99991231";
        if (strtDt.length() != 8 && endDt.length() != 8) return null;

        LocalDate strtDate = LocalDate.parse(strtDt, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate endDate = LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyyMMdd"));

        return strtDate.compareTo(endDate);
    }

}
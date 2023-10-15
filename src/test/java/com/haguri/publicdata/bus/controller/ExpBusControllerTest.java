package com.haguri.publicdata.bus.controller;

import com.haguri.publicdata.bus.service.ExpBusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ExpBusController.class)
class ExpBusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ExpBusService expBusService;

    @Test
    @DisplayName("고속버스 도시코드")
    void searchExpBusCityCode() throws Exception {
//        Mono<ResponseEntity<String>> res = null;
//        given(expBusService.getExpBusCityCode()).willReturn(res);

        mockMvc.perform(get("/api/expBus/cityCode"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andDo(print());
                //.andExpect(content().string(containsString("서울특별시")));
    }

    @Test
    void searchExpBusGrad() {
    }

    @Test
    void searchExpBusTrminl() {
    }

    @Test
    void searchExpBusInfo() {
    }

}
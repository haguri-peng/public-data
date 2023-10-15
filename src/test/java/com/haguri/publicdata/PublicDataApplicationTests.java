package com.haguri.publicdata;

import com.haguri.publicdata.bus.controller.ExpBusController;
import com.haguri.publicdata.bus.controller.SuburbsBusController;
import com.haguri.publicdata.stanReginCd.controller.StanReginCdController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = {"classpath:/myInfo.properties"})
class PublicDataApplicationTests {

    @Autowired
    private ExpBusController expBusController;

    @Autowired
    private SuburbsBusController suburbsBusController;

    @Autowired
    private StanReginCdController stanReginCdController;

    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
        assertThat(expBusController).isNotNull();
        assertThat(suburbsBusController).isNotNull();
        assertThat(stanReginCdController).isNotNull();

        System.out.println(environment.getProperty("MyInfo.serviceKey"));
        System.out.println(environment.getProperty("OpenAPI.baseUrl.stanReginCd2"));
    }

}

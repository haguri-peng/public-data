package com.haguri.publicdata.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:myInfo.properties")
public class MyInfoUtil {

    private final Environment environment;

    public String getPropertyValue(String key) {
        return environment.getProperty(key);
    }

}

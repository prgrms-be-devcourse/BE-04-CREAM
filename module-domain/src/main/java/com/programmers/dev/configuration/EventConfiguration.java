package com.programmers.dev.configuration;


import com.programmers.dev.event.EventManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class EventConfiguration {

    @Bean
    public InitializingBean eventInitializer(ApplicationContext applicationContext) {
        return () -> EventManager.setPublisher(applicationContext);
    }
}

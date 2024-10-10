package com.HNSSpring.HNS.configuration;

import com.HNSSpring.HNS.components.LocalizationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    @Bean
    public LocalizationUtils localizationUtils(MessageSource messageSource, LocaleResolver localeResolver) {
        return new LocalizationUtils(messageSource, localeResolver);
    }
}

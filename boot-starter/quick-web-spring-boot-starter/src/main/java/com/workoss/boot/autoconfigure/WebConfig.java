/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.autoconfigure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.workoss.boot.exception.ExceptionMapper;
import com.workoss.boot.util.DateUtil;
import com.workoss.boot.util.json.*;
import com.workoss.boot.web.advice.GlobalExceptionHandlerAdvice;
import com.workoss.boot.web.advice.GlobalResponseBodyAdvice;
import com.workoss.boot.web.advice.SecurityExceptionHandlerAdvice;
import com.workoss.boot.web.filter.RequestLogFilter;
import com.workoss.boot.web.interceptor.QuickHandlerInterceptor;
import com.workoss.boot.web.interceptor.QuickWebRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * web 统一配置
 *
 * @author workoss
 */
@Slf4j
@EnableConfigurationProperties(value = {QuickWebProjectProperties.class})
@AutoConfiguration
@AutoConfigureBefore(ValidationAutoConfiguration.class)
public class WebConfig {

    private final MessageSource messageSource;

    private final Environment environment;

    public WebConfig(MessageSource messageSource, Environment environment) {
        this.messageSource = messageSource;
        this.environment = environment;
    }

    @ConditionalOnClass({JsonMapper.class, ObjectMapper.class})
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(Environment environment) {
        String datetimePattern = environment.getProperty("spring.mvc.format.date-time",
                DateUtil.DEFAULT_DATE_TIME_PATTERN);
        String datePattern = environment.getProperty("spring.mvc.format.date", DateUtil.DEFAULT_DATE_PATTERN);
        String timePattern = environment.getProperty("spring.mvc.format.time", DateUtil.DEFAULT_TIME_PATTERN);
        String timeZone = environment.getProperty("spring.jackson.time-zone", DateUtil.DEFAULT_TIME_ZONE);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datetimePattern);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timePattern);

        return builder -> {
            builder.configure(JsonMapper.build().getMapper());
            // JSR 310日期时间处理
            JavaTimeModule javaTimeModule = new JavaTimeModule();

            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());

            javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
            javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());

            builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                    .dateFormat(new DateTimeFormat(datetimePattern))
                    .timeZone(timeZone)
                    .serializerByType(Long.class, BigNumberSerializer.INSTANCE)
                    .modules(javaTimeModule);
        };

    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.CHINA);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public WebMvcConfigurer webMvcConfigurer(LocaleChangeInterceptor interceptor,
                                             ObjectProvider<List<QuickHandlerInterceptor>> quickHandlerInterceptorsProvider,
                                             ObjectProvider<List<QuickWebRequestInterceptor>> quickWebRequestInterceptorsProvider) {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor);
                quickHandlerInterceptorsProvider.ifAvailable(list -> {
                    list.forEach(quickHandlerInterceptor -> {
                        registry.addInterceptor(quickHandlerInterceptor);
                        log.atDebug().log("[QUICK] add interceptor:{}", quickHandlerInterceptor);
                    });
                });

                quickWebRequestInterceptorsProvider.ifAvailable(list -> {
                    list.forEach(quickWebRequestInterceptor -> {
                        registry.addWebRequestInterceptor(quickWebRequestInterceptor);
                        log.atDebug().log("[QUICK] add web interceptor:{}", quickWebRequestInterceptor);
                    });
                });

            }
        };
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "quick.web.response.exception-advice", matchIfMissing = true)
    @Bean
    public GlobalExceptionHandlerAdvice globalExceptionHandlerAdvice(MessageSource messageSource) {
        return new GlobalExceptionHandlerAdvice(messageSource);
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "quick.web.response.body-advice", matchIfMissing = true)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public GlobalResponseBodyAdvice globalResponseBodyAdvice(ObjectMapper objectMapper) {
        return new GlobalResponseBodyAdvice(objectMapper);
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "quick.web.response.exception-advice", matchIfMissing = true)
    @ConditionalOnClass(name = "org.springframework.security.access.AccessDeniedException")
    @Bean
    public SecurityExceptionHandlerAdvice securityExceptionHandlerAdvice(MessageSource messageSource) {
        return new SecurityExceptionHandlerAdvice(messageSource);
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public CorsFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @ConditionalOnMissingBean
    @Bean
    public ExceptionMapper exceptionMapper(MessageSource messageSource) {
        return new ExceptionMapper(messageSource);
    }


    @ConditionalOnProperty(value = "quick.web.request.log.enabled", havingValue = "true")
    @Bean
    public RequestLogFilter requestLogFilter(QuickWebProjectProperties quickWebProjectProperties) {
        return new RequestLogFilter(quickWebProjectProperties);
    }

}

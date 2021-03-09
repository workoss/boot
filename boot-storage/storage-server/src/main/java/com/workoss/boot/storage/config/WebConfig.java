package com.workoss.boot.storage.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.workoss.boot.storage.web.advice.GlobalResponseHandler;
import com.workoss.boot.storage.web.filter.ApiSignWebFilter;
import com.workoss.boot.storage.web.filter.ReactiveRequestContextFilter;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.server.WebFilter;

import javax.validation.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

/**
 * web 相关配置
 *
 * @author workoss
 */
@Configuration
public class WebConfig {

	@Bean
	public GlobalResponseHandler globalResponseHandler(ServerCodecConfigurer serverCodecConfigurer,
                                                       RequestedContentTypeResolver requestedContentTypeResolver) {
		return new GlobalResponseHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(Environment environment) {
		String datetimePattern = environment.getProperty("spring.webflux.format.date-time", "yyyy-MM-dd HH:mm:ss");
		String datePattern = environment.getProperty("spring.webflux.format.date", "yyyy-MM-dd");
		String timePattern = environment.getProperty("spring.webflux.format.time", "HH:mm:ss");

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datetimePattern);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timePattern);

		return builder -> builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))
				.serializerByType(LocalDate.class, new LocalDateSerializer(dateFormatter))
				.serializerByType(LocalTime.class, new LocalTimeSerializer(timeFormatter))
				.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter))
				.deserializerByType(LocalDate.class, new LocalDateDeserializer(dateFormatter))
				.deserializerByType(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
	}

	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOriginPatterns(Arrays.asList("*"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowedMethods(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}

	@Order(Ordered.HIGHEST_PRECEDENCE + 1)
	@Bean
	public ReactiveRequestContextFilter reactiveRequestContextFilter() {
		return new ReactiveRequestContextFilter();
	}

	@Primary
	@Bean
	public Validator validator(MessageSource messageSource) {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setProviderClass(HibernateValidator.class);
		Map<String, String> propertyMap = validator.getValidationPropertyMap();
		propertyMap.put("hibernate.validator.fail_fast", "true");
		validator.setValidationPropertyMap(propertyMap);
		validator.setValidationMessageSource(messageSource);
		return validator;
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
		MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
		postProcessor.setValidator(validator);
		postProcessor.setExposeProxy(true);
		return postProcessor;
	}

	@Bean
	public WebFilter apiSignWebFilter() {
		return new ApiSignWebFilter();
	}

}

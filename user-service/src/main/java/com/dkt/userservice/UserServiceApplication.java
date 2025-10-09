package com.dkt.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	/**
	 * Bean này sẽ quyết định ngôn ngữ nào được sử dụng dựa trên
	 * header "Accept-Language" trong request.
	 */
	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		// Ngôn ngữ mặc định nếu không có header
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}

	/**
	 * Bean này chịu trách nhiệm tìm và quản lý các file "từ điển" (.properties).
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		// Tên gốc của các file (không bao gồm _vi, _en)
		messageSource.setBasename("messages");
		// Sử dụng encoding UTF-8 để hỗ trợ tiếng Việt có dấu
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
}
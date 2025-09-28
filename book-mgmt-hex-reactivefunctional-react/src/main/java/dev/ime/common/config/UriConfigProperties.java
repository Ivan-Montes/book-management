package dev.ime.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "properties.endpoint.uri")
@Getter
@Setter
public class UriConfigProperties {

	private String author;
	private String bookbookshop;
	private String book;
	private String bookshop;
	private String genre;
	private String publisher;

}

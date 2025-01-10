//package com.schall.jyyxbackendgateway.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//import org.springframework.web.util.pattern.PathPatternParser;
//
//import java.util.Arrays;
//
//@Configuration
//    public class CorsConfig {
//
//        @Bean
//        public CorsWebFilter corsFilter() {
//            CorsConfiguration config = new CorsConfiguration();
//            config.addAllowedMethod("*");
//            config.setAllowCredentials(true);
//            //TODO 实际改为线上真实域名，本地域名
//            config.setAllowedOriginPatterns(Arrays.asList("*"));
//            config.addAllowedHeader("*");
//            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
//            source.registerCorsConfiguration("/**", config);
//            return new CorsWebFilter(source);
//        }
//    }
package com.schall.jyyxbackendgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        configureCors(config); // 配置 CORS 规则
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config); // 应用到所有路径
        return new CorsWebFilter(source);
    }

    private void configureCors(CorsConfiguration config) {
        // 允许的 HTTP 方法
        config.addAllowedMethod("*");

        // 允许携带凭证（如 Cookie）
        config.setAllowCredentials(true);

        // 允许的域名（仅开发环境）
        config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:8080"));


        // 允许的请求头
        config.addAllowedHeader("*");

        // 日志记录
        System.out.println("CORS Configuration: " + config.getAllowedOriginPatterns());
    }
}


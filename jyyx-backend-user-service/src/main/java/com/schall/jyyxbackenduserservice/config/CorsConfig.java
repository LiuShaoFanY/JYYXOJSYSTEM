package com.schall.jyyxbackenduserservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    public CorsWebFilter corsFilter() {
        try {
            CorsConfiguration config = new CorsConfiguration();
            configureCors(config); // 配置 CORS 规则
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
            source.registerCorsConfiguration("/**", config); // 应用到所有路径

            logger.info("CORS 过滤器已初始化，配置详情：{}", config);
            return new CorsWebFilter(source);
        } catch (Exception e) {
            logger.error("CORS 过滤器初始化失败，请检查配置！错误信息：{}", e.getMessage(), e);
            throw new RuntimeException("CORS 过滤器初始化失败", e);
        }
    }

    private void configureCors(CorsConfiguration config) {
        try {
            // 允许的 HTTP 方法
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
            logger.debug("允许的 HTTP 方法：{}", config.getAllowedMethods());

            // 允许携带凭证（如 Cookie）
            config.setAllowCredentials(true);
            logger.debug("是否允许携带凭证：{}", config.getAllowCredentials());

            // 允许的域名（开发环境和生产环境）
            List<String> allowedOrigins = Arrays.asList(
                    "http://localhost:8080", // 开发环境
                    "http://www.jyyxojsystem.top:8077", // 生产环境
                    "https://www.jyyxojsystem.top:8077" // 支持 HTTPS
            );
            config.setAllowedOriginPatterns(allowedOrigins);
            logger.debug("允许的域名：{}", config.getAllowedOriginPatterns());

            // 允许的请求头
            config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With"));
            logger.debug("允许的请求头：{}", config.getAllowedHeaders());

            // 暴露的响应头
            config.setExposedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
            logger.debug("暴露的响应头：{}", config.getExposedHeaders());

            // 预检请求的缓存时间（单位：秒）
            config.setMaxAge(3600L);
            logger.debug("预检请求的缓存时间：{} 秒", config.getMaxAge());

            logger.info("CORS 配置已成功应用。");
        } catch (Exception e) {
            logger.error("CORS 配置过程中发生错误，请检查配置！错误信息：{}", e.getMessage(), e);
            throw new RuntimeException("CORS 配置失败", e);
        }
    }
}
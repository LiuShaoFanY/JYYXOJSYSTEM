//package com.schall.jyyxbackendgateway.filter;
//
//import cn.hutool.core.text.AntPathMatcher;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class GlobalAuthFilter implements GlobalFilter, Ordered {
//
//    private AntPathMatcher antPathMatcher = new AntPathMatcher();
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest serverHttpRequest = exchange.getRequest();
//        String path = serverHttpRequest.getURI().getPath();
//
//        // 判断路径是否包含inner，只允许内部调用
//        if (antPathMatcher.match("/**/inner/**", path)) {
//            ServerHttpResponse response = exchange.getResponse();
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            DataBufferFactory dataBufferFactory = response.bufferFactory();
//            DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
//            return response.writeWith(Mono.just(dataBuffer));
//        }
//
//        // todo for next step, get in return chain.filter(exchange);
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//
//}
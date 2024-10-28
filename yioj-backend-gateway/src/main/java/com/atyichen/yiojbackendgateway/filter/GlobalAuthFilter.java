package com.atyichen.yiojbackendgateway.filter;

import cn.hutool.core.text.AntPathMatcher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局请求拦截器
 */
public class GlobalAuthFilter implements GlobalFilter, Ordered {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    /**
     * /inner接口是给后端内部人员使用的， 不允许无关人员使用
     * 内部调用是通过openfeign调用的， 不经过网关， 因此经过网关的都不是内部调用
     * 经过网关: /8201 经过网关， 网关去nacos中找对应的服务
     * 不经过网关: 直接调用nacos中注册的服务
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 判断路径中是否包含 inner
        if (antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer buffer = dataBufferFactory.wrap("全局请求拦截: 无权限".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }

        // todo 使用jwt实现全局权限校验
        // todo 可以在网关实现接口的限流降级（sentinel or redisson）
        return chain.filter(exchange);
    }

    /**
     * 所有请求拦截器的优先级中 最高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

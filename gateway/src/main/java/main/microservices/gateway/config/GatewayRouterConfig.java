package main.microservices.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayRouterConfig {

    public GatewayRouterConfig() {
    }

    @Bean
    public RouterFunction<ServerResponse> gatewayRouter() {
        System.out.println("Hi iam here");
        return route("wallet-route")
                .route(path("/api/wallet/**"), http())
                .filter(lb("wallet-service"))
                .filter(circuitBreaker("myServiceCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }
}

package com.thomasvitale.demo.config;

import java.util.ArrayList;
import java.util.List;

import io.micrometer.observation.ObservationPredicate;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.endpoint.web.WebServerNamespace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration(proxyBeanMethods = false)
class ObservabilityConfig {

    private static final EndpointId HEALTH = EndpointId.of("health");

    /**
     * Disables all Micrometer Observations (metrics and traces) for the Kubernetes probes: the health
     * endpoint, its groups and their additional paths such as {@code /livez}. Other Actuator endpoints
     * (refresh, loggers, env, ...) are invoked deliberately and rarely, and stay observed.
     * <p>
     * Spring Boot has no property for this (spring-boot#34801), and disabling only the HTTP
     * observation is not enough: the health indicators query the database, and those JDBC
     * observations would become orphan traces. The HTTP observation carries the request in its
     * context; any other observation started while handling the request finds it bound to the
     * current thread by {@code RequestContextFilter}. Servlet, single server port and default
     * DispatcherServlet path only.
     * <p>
     * The paths come from {@link PathMappedEndpoints}, as in {@code EndpointRequest}, which can't be
     * used here because it's built on Spring Security. They're resolved lazily: the predicate is
     * needed to create the ObservationRegistry, which the health endpoint depends on via the DataSource.
     */
    @Bean
    ObservationPredicate noProbeObservations(ObjectProvider<PathMappedEndpoints> pathMappedEndpoints) {
        SingletonSupplier<List<String>> probePaths = SingletonSupplier.of(() -> probePaths(pathMappedEndpoints.getIfAvailable()));
        return (_, context) -> {
            // The HTTP observation: created before RequestContextFilter binds the request to the thread.
            if (context instanceof ServerRequestObservationContext http && http.getCarrier() != null) {
                return shouldObserve(probePaths.obtain(), http.getCarrier());
            }
            // Any other observation during the request (e.g. JDBC): no request in its context, and no parent to walk up
            // to since the HTTP observation is a no-op.
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                return shouldObserve(probePaths.obtain(), attributes.getRequest());
            }
            return true;
        };
    }

    private static List<String> probePaths(@Nullable PathMappedEndpoints endpoints) {
        List<String> paths = new ArrayList<>();
        String healthPath = endpoints != null ? endpoints.getPath(HEALTH) : null;
        if (endpoints != null && healthPath != null) {
            paths.add(healthPath);
            paths.addAll(endpoints.getAdditionalPaths(WebServerNamespace.SERVER, HEALTH));
        }
        return paths;
    }

    private static boolean shouldObserve(List<String> probePaths, HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return probePaths.stream().noneMatch(candidate -> path.equals(candidate) || path.startsWith(candidate + "/"));
    }

}

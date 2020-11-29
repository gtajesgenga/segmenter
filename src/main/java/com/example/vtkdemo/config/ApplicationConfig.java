package com.example.vtkdemo.config;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.example.vtkdemo.client.LoggingRequestInterceptor;
import com.example.vtkdemo.logging.AccessLogFilter;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@ConfigurationProperties(prefix = "application")
@EnableAutoConfiguration
@Getter
@Setter
@Slf4j
@EnableAspectJAutoProxy
public class ApplicationConfig {

    private static final String ACCESS_LOG_FILTER = "AccessLogFilter";
    String tempFolder;

    Boolean restTemplateLogin;

    @PostConstruct
    private void initialize() {
        try {
            File directory = new File(tempFolder);
            if (!directory.exists() && !directory.mkdir()) {
                    log.warn("eventType=error message=Error on creation of temp directory");
            }
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .build();
        if (restTemplateLogin) {
            restTemplate.setInterceptors(Collections.singletonList(new LoggingRequestInterceptor()));
        }
        return restTemplate;
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "vtkdemo");
    }

    @Bean
    public AccessLogFilter accessLogFilter() {
        return new AccessLogFilter();
    }

    @Bean
    public FilterRegistrationBean<Filter> accessLogFilterRegistration(AccessLogFilter filter) {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName(ACCESS_LOG_FILTER);
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public CountedAspect countedAspect(PrometheusMeterRegistry registry) {
        return new CountedAspect(registry);
    }

    @Bean
    public TimedAspect timedAspect(PrometheusMeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.initialize();
        return taskExecutor;
    }}

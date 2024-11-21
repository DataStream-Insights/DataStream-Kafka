package com.wnsud9771.config.async;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer{
	@Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // 기본 쓰레드 수
        executor.setMaxPoolSize(20);  // 최대 쓰레드 수
        executor.setQueueCapacity(30); // 대기 큐 크기
        executor.setThreadNamePrefix("format-async-"); // 쓰레드 이름 prefix
        executor.initialize();
        return executor;
    }
}

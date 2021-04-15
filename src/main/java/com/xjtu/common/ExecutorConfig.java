package com.xjtu.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@PropertySource(value = {"classpath:executor.properties"})
public class ExecutorConfig {
    private  final Logger logger=LoggerFactory.getLogger(this.getClass());
    /**
     * 核心线程数
     */
    @Value("${async.executor.thread.corePoolSize}")
    private int corePoolSize;
    /**
     * 最大线程数
     */
    @Value("${async.executor.thread.maxPoolSize}")
    private int maxPoolSize;
    /**
     * 队列大小
     */
    @Value("${async.executor.thread.queueCapacity}")
    private int queueCapacity;
    /**
     * 线程池中线程的名称前缀
     */
    @Value("${async.executor.thread.namePrefix}")
    private String namePrefix;


    /**
     * 配置一个线程池
     * @return
     */
    @Bean("AsyncThread")
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(namePrefix);
        /**
         *  rejection-policy：当pool已经达到max size的时候，如何处理新任务
         *  CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }




}

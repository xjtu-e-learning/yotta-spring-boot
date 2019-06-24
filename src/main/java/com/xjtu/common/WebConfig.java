package com.xjtu.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.Filter;

/**
 * 关于网络的配置
 *
 * @author yangkuan
 */
@Configuration
public class WebConfig {
    @Bean
    public Filter filter() {
        ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
        return filter;
    }
}

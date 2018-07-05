package com.xjtu.start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 服务启动执行的程序
 * 优先级：1>2
 */

@Component
@Order(value = 1)
public class StartOfCreateMysqlTable implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... args) throws Exception {
        logger.info(">>>>>>>>>>>>>>>服务启动执行，开始检查数据库操作<<<<<<<<<<<<<");

    }


}


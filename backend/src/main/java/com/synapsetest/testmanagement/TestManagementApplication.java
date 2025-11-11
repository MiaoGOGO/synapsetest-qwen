package com.synapsetest.testmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * AI驱动测试任务管理系统 - 主应用类
 *
 * @author SynapseTest Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
public class TestManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestManagementApplication.class, args);
    }
}

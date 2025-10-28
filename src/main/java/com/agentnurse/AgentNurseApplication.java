package com.agentnurse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AgentNurse 应用主入口
 * 非诊断/非医疗建议的照护助理
 */
@SpringBootApplication
public class AgentNurseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentNurseApplication.class, args);
        System.out.println("""
                
                ========================================
                  AgentNurse 照护助理启动成功！
                  API 文档: http://localhost:8080/api
                ========================================
                """);
    }
}

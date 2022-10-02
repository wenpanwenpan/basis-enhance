package org.enhance.groovy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 * 压测时jvm参数：-Xms40m -Xmx40m -XX:+HeapDumpOnOutOfMemoryError
 *
 * @author wenpan 2022/09/25 14:25
 */
@SpringBootApplication
public class DemoEnhanceGroovyEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoEnhanceGroovyEngineApplication.class, args);
    }
}

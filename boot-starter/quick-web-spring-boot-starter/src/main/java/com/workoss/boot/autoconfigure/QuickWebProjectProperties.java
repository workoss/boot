package com.workoss.boot.autoconfigure;

import jakarta.validation.Valid;
import lombok.Data;
import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author workoss
 */
@Data
@ConfigurationProperties(prefix = "quick.web")
public class QuickWebProjectProperties {


    private RequestProperties request = new RequestProperties();
    /**
     * 对response 的配置
     */
    @Valid
    private ResponseProperties response = new ResponseProperties();


    @Data
    public static class ResponseProperties {
        /**
         * 是否启用 统一返回 ResultInfo
         */
        private Boolean bodyAdvice;
        /**
         * 异常处理advice 返回 ResultInfo
         */
        private Boolean exceptionAdvice;

    }

    @Data
    public static class RequestProperties {

        @Valid
        private LogProperties log = new LogProperties();

        @Data
        public static class LogProperties {
            /**
             * 是否启用
             */
            private Boolean enabled = Boolean.FALSE;
            /**
             * 日志级别
             */
            private Level logLevel = Level.DEBUG;
            /**
             * 是否持久化
             */
            private Boolean persistence = Boolean.FALSE;
            /**
             * 持久化保存的表明
             */
            private String tableName = "request_log";
        }

    }


}

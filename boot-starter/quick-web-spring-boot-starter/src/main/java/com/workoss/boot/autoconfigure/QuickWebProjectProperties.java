/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

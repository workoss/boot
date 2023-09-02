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
package com.workoss.boot.storage.web.controller.param;

import com.workoss.boot.storage.model.ThirdPlatformType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公共参数，用于查询到db中ak/sk配置信息
 *
 * @author workoss
 */
@Data
public class StorageParam {

	private String tenentId = "default";

	@NotNull(message = "{security.storage.notnull}")
	private ThirdPlatformType storageType;

}

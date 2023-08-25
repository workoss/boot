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
package com.workoss.boot.storage.service.security.impl;

import com.workoss.boot.storage.BaseSpringTest;
import com.workoss.boot.storage.model.BaseStorageModel;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.service.security.SecurityService;
import com.workoss.boot.storage.web.controller.vo.STSTokenVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.ConfigurableConversionService;

class SecurityServiceImplTest extends BaseSpringTest {

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ConfigurableConversionService conversionService;

	@Test
	void generateUploadSign() {

		// securityService.generateUploadSign(null,"22").log().as(StepVerifier::create).expectNextCount(1).verifyComplete();
	}

	@Test
	void generateStsToken() {
		STSTokenVO vo = new STSTokenVO();
		vo.setStorageType(ThirdPlatformType.OBS);
		BaseStorageModel storageModel = conversionService.convert(vo, BaseStorageModel.class);
		System.out.println(storageModel.toString());
	}

}

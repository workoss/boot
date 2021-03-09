package com.workoss.boot.storage.service.security.impl;

import com.workoss.boot.storage.BaseSpringTest;
import com.workoss.boot.storage.model.BaseStorageModel;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.service.security.SecurityService;
import com.workoss.boot.storage.web.vo.STSTokenVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

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

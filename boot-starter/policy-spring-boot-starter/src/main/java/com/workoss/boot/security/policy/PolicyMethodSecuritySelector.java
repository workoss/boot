/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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
package com.workoss.boot.security.policy;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.method.PolicyMethodSecurityAdvisorRegistrar;
import org.springframework.security.authorization.method.PolicyMethodSecurityAspectJAutoProxyRegistrar;
import org.springframework.security.config.annotation.method.configuration.PolicyMethodSecurityConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author workoss
 */
final class PolicyMethodSecuritySelector implements ImportSelector {

	private final ImportSelector autoProxy = new AutoProxyRegistrarSelector();

	@Override
	public String[] selectImports(AnnotationMetadata importMetadata) {
		if (!importMetadata.hasAnnotation(EnablePolicyMethodSecurity.class.getName())) {
			return new String[0];
		}
		EnablePolicyMethodSecurity annotation = importMetadata.getAnnotations()
			.get(EnablePolicyMethodSecurity.class)
			.synthesize();
		List<String> imports = new ArrayList<>(Arrays.asList(this.autoProxy.selectImports(importMetadata)));
		if (annotation.cedarEnabled()) {
			imports.add(PolicyMethodSecurityConfiguration.class.getName());
		}

		return imports.toArray(new String[0]);
	}

	private static final class AutoProxyRegistrarSelector extends AdviceModeImportSelector<EnablePolicyMethodSecurity> {

		private static final String[] IMPORTS = new String[] { AutoProxyRegistrar.class.getName(),
				PolicyMethodSecurityAdvisorRegistrar.class.getName() };

		private static final String[] ASPECTJ_IMPORTS = new String[] {
				PolicyMethodSecurityAspectJAutoProxyRegistrar.class.getName() };

		@Override
		protected String[] selectImports(@NonNull AdviceMode adviceMode) {
			if (adviceMode == AdviceMode.PROXY) {
				return IMPORTS;
			}
			if (adviceMode == AdviceMode.ASPECTJ) {
				return ASPECTJ_IMPORTS;
			}
			throw new IllegalStateException("AdviceMode '" + adviceMode + "' is not supported");
		}

	}

}

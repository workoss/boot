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
package org.springframework.security.authorization.method;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author workoss
 */
public class PolicyMethodSecurityAspectJAutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		registerBeanDefinition("policyAuthorizeAuthorizationMethodInterceptor",
				"org.springframework.security.authorization.method.aspectj.PolicyAuthorizeAspect",
				"policyAuthorizeAspect$0", registry);

	}

	private void registerBeanDefinition(String beanName, String aspectClassName, String aspectBeanName,
			BeanDefinitionRegistry registry) {
		if (!registry.containsBeanDefinition(beanName)) {
			return;
		}
		BeanDefinition interceptor = registry.getBeanDefinition(beanName);
		BeanDefinitionBuilder aspect = BeanDefinitionBuilder.rootBeanDefinition(aspectClassName);
		// aspect.setFactoryMethod("aspectOf");
		aspect.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		aspect.addPropertyValue("securityInterceptor", interceptor);
		registry.registerBeanDefinition(aspectBeanName, aspect.getBeanDefinition());
	}

}

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

import com.workoss.boot.security.policy.PoliciesAuthorize;
import com.workoss.boot.security.policy.PolicyAuthorize;
import com.workoss.boot.security.policy.PolicyClient;
import com.workoss.boot.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 方法权限拦截
 *
 * @author workoss
 */
@Slf4j
public final class PolicyAuthorizeAuthorizationManager implements AuthorizationManager<MethodInvocation> {

	private final PolicyClient policyClient;

	private final ApplicationContext applicationContext;

	public PolicyAuthorizeAuthorizationManager(PolicyClient policyClient, ApplicationContext applicationContext) {
		this.policyClient = policyClient;
		this.applicationContext = applicationContext;
	}

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation object) {
		PolicyAuthorize policy = findPolicyAuthorizeAnnotation(object.getMethod());
		Authentication token = authentication.get();
		if (policy == null) {
			return new AuthorizationDecision(true);
		}
		if (token instanceof AnonymousAuthenticationToken) {
			return new AuthorizationDecision(false);
		}
		// 不需要校验policy
		boolean ignoreIgnore = StringUtils.isBlank(policy.principal()) && StringUtils.isBlank(policy.action())
				&& StringUtils.isBlank(policy.resource());
		log.debug("[POLICY]: policyIds:{} principal:{} action:{} resource:{}", policy.policyIds(), policy.principal(),
				policy.action(), policy.resource());
		return new AuthorizationDecision((ignoreIgnore || policyClient.check(token, policy)));
	}

	private PolicyAuthorize findPolicyAuthorizeAnnotation(Method method) {
		return AuthorizationAnnotationUtils.findUniqueAnnotation(method, PolicyAuthorize.class);
	}

	private Set<PolicyAuthorize> findPolicyAuthorizeAnnotations(Method method) {
		RepeatableContainers repeatableContainers = RepeatableContainers.of(PolicyAuthorize.class,
				PoliciesAuthorize.class);
		return MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, repeatableContainers)
			.stream(PolicyAuthorize.class)
			.filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex))
			.map(MergedAnnotation::withNonMergedAttributes)
			.collect(MergedAnnotationCollectors.toAnnotationSet());
	}

}

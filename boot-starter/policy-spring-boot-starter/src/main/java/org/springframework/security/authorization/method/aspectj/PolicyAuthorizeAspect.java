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
package org.springframework.security.authorization.method.aspectj;

import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author workoss
 */
@Setter
@Aspect
public class PolicyAuthorizeAspect {

	private MethodInterceptor securityInterceptor;

	@Pointcut("execution(* *(..)) && @annotation(com.workoss.shop.security.policy.PolicyAuthorize)")
	public void executionOfAnnotatedMethod() {
	}

	@Around("executionOfAnnotatedMethod()")
	public Object around(ProceedingJoinPoint joinPoint) {
		try {
			if (this.securityInterceptor == null) {
				return joinPoint.proceed();
			}
			MethodInvocation invocation = new JoinPointMethodInvocation(joinPoint, () -> {
				try {
					return joinPoint.proceed();
				}
				catch (Throwable e) {
					throw new IllegalStateException("Code unexpectedly reached", e);
				}
			});
			return this.securityInterceptor.invoke(invocation);
		}
		catch (Throwable t) {
			throw new IllegalStateException("Code unexpectedly reached", t);
		}

	}

}

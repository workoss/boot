/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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
package com.workoss.boot.annotation.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 唯一约束
 *
 * @author workoss
 */
@Target({})
@Retention(RUNTIME)
public @interface UniqueConstraint {

	/**
	 * (Optional) Constraint name. A provider-chosen name will be chosen if a name is not
	 * specified.
	 * @return 姓名
	 */
	String name() default "";

	/**
	 * (Required) An array of the column names that make up the constraint.
	 * @return 列名称
	 */
	String[] columnNames();

}

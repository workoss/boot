/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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
package com.workoss.boot.plugin.mybatis.provider;

import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;

/**
 * CrudSelectProvider
 *
 * @author workoss
 */
public class CrudSelectProvider extends BaseProvider {

	public CharSequence selectById(Map<String, Object> parameter, ProviderContext context) {
		return executeSql(context, parameter, (tableColumnInfo -> {

		}));
	}

	public CharSequence selectByIds(Map<String, Object> parameter, ProviderContext context) {
		return executeSql(context, parameter, (tableColumnInfo -> {

		}));
	}

	public CharSequence selectSelective(Map<String, Object> parameter, ProviderContext context) {
		return executeSql(context, parameter, (tableColumnInfo -> {

		}));
	}

	public CharSequence selectCountSelective(Map<String, Object> parameter, ProviderContext context) {
		return executeSql(context, parameter, (tableColumnInfo -> {

		}));
	}

}

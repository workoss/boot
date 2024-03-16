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
package com.workoss.boot.mapper;

import com.workoss.boot.model.IEnum;
import org.mapstruct.Named;
import org.mapstruct.TargetType;

/**
 * 枚举类工具方法
 */
@Named("enum")
public class EnumTranslator {

	@Named("getCode")
	public <M, N, E extends Enum<E>> M resolveCode(IEnum<M, N, E> iEnum) {
		return EnumUtil.getCode(iEnum);
	}

	@Named("getDesc")
	public <M, N, E extends Enum<E>> N resolveDesc(IEnum<M, N, E> iEnum) {
		return EnumUtil.getDesc(iEnum);
	}

	@Named("codeToEnum")
	public static <T extends IEnum<?, String, ?>> T resolve(Object code, @TargetType Class<T> tClass) {
		return EnumUtil.getByCode(tClass, code);
	}

}

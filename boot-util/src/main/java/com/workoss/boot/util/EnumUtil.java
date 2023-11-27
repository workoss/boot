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
package com.workoss.boot.util;

import com.workoss.boot.model.IEnum;

import java.util.Arrays;
import java.util.Objects;

/**
 * 枚举工具类
 *
 * @author workoss
 */
public class EnumUtil {

    private EnumUtil() {
    }

    public static <M, N, E extends Enum<E>> M getCode(IEnum<M, N, E> iEnum) {
        return iEnum != null ? iEnum.getCode() : null;
    }

    public static <M, N, E extends Enum<E>> N getDesc(IEnum<M, N, E> iEnum) {
        return iEnum != null ? iEnum.getDesc() : null;
    }

    public static <T extends IEnum<?, String, ?>> T getByCode(Class<T> enumClass, Object code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(s -> s.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name) {
        if (Objects.isNull(name)) {
            return null;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(s -> s.name().equals(name))
                .findFirst()
                .orElse(null);
    }

}

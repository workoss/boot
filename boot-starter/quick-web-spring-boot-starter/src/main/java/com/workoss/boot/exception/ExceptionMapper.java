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
package com.workoss.boot.exception;

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.util.exception.BootException;
import com.workoss.boot.util.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * 异常抛出类
 *
 * @author workoss
 */
public class ExceptionMapper {

    private MessageSource messageSource;

    public ExceptionMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void throwException(@NonNull String code, Object... argv) {
        throwException(QuickException.class, code, argv);
    }

    public void throwException(@NonNull Class<? extends BootException> clazz, @NonNull String code, Object... argv) {
        if (messageSource == null) {
            messageSource = new ResourceBundleMessageSource();
        }
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(code, argv, locale);
        throw ExceptionUtils.newInstance(clazz, code, message);
    }

}

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
package com.workoss.boot.util.exception;

/**
 * boot异常
 *
 * @author workoss
 */
public class BootException extends RuntimeException {

    private String code;

    private String msg;


    public BootException(String code) {
        super("code:" + code);
        this.code = code;
    }

    public BootException(String code, String msg) {
        super("code:" + code + ",msg:" + msg);
        this.code = code;
        this.msg = msg;
    }

    public BootException(Throwable throwable) {
        super("msg:" + throwable.getMessage(), throwable);
    }

    public BootException(String code, Throwable throwable) {
        super("code:" + code, throwable);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    @Override
    public String toString() {
        if (this.msg == null) {
            return "{\"code\":\"" + this.code + "\"}";
        }
        return "{\"code\":\"" + this.code + "\",\"msg\":\"" + this.msg + "\"}";
    }

}

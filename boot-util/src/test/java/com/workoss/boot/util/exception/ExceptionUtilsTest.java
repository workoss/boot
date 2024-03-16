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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author workoss
 */
class ExceptionUtilsTest {

    @Test
    void newInstance() {
        for (int i = 0; i < 10; i++) {
            System.out.println(ExceptionUtils.newInstance(BootException.class, "boot-"+i, "2"));
            System.out.println(ExceptionUtils.newInstance(TestException.class, "test-"+i, "2"));
        }

    }

    @Test
    void getConstructor() {
    }
}
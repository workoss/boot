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
package com.workoss.boot.engine;

/**
 * @author workoss
 */
public class ZenEngineLoader {

	/**
	 * zen 规则引擎执行
	 * @param decision json 规则集
	 * @param input 入参 json
	 * @param trace 是否trace
	 * @param maxDepth trace
	 * @return json 字符串
	 */
	static native byte[] evaluate(byte[] decision, byte[] input, boolean trace, int maxDepth);

	/**
	 * 校验 json规则
	 * @param decision json 规则集
	 * @return true/false
	 */
	static native boolean validate(byte[] decision);

	/**
	 * 执行表达式
	 * @param expression 表达式
	 * @param input 入参
	 * @return 表达式结果
	 */
	static native byte[] expression(byte[] expression, byte[] input);

}

/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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
package com.workoss.boot.util.event;

/**
 * @author workoss
 */
public interface Subscriber<E extends Event> {

	/**
	 * onEvent
	 * @param event 事件
	 */
	void onEvent(E event);

	/**
	 * onError
	 * @param throwable 异常
	 */
	void onError(Throwable throwable);

}

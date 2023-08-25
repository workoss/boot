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
package com.workoss.boot;

import java.util.ArrayList;
import java.util.List;

public class DemoMapperImpl implements DemoMapper {

	@Override
	public DemoModel toTarget(DemoEntity source) {
		return new DemoModel();
	}

	@Override
	public List<DemoModel> toTargetList(List<DemoEntity> sourceList) {
		return new ArrayList<>();
	}

}

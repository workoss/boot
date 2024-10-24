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
package com.workoss.boot.storage.mapper.web;

import com.workoss.boot.storage.mapper.BeanMapper;
import com.workoss.boot.storage.model.BaseStorageModel;
import com.workoss.boot.storage.web.controller.param.STSTokenParam;
import org.mapstruct.Mapper;

/**
 * sts token 参数 复制mapper
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
@Mapper(componentModel = "spring")
public interface STSTokenParamToStorageModelMapper extends BeanMapper<STSTokenParam, BaseStorageModel> {

}

package com.workoss.boot.storage.mapper.web;

import com.workoss.boot.storage.mapper.BeanMapper;
import com.workoss.boot.storage.model.BaseStorageModel;
import com.workoss.boot.storage.web.vo.STSTokenParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface STSTokenParamToStorageModelMapper extends BeanMapper<STSTokenParam, BaseStorageModel> {

}

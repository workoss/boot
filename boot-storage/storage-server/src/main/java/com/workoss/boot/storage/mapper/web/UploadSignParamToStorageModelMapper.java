package com.workoss.boot.storage.mapper.web;

import com.workoss.boot.storage.mapper.BeanMapper;
import com.workoss.boot.storage.model.BaseStorageModel;
import com.workoss.boot.storage.web.vo.UploadSignParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UploadSignParamToStorageModelMapper extends BeanMapper<UploadSignParam, BaseStorageModel> {

}

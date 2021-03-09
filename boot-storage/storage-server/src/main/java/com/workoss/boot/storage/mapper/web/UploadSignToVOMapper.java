package com.workoss.boot.storage.mapper.web;

import com.workoss.boot.storage.mapper.BeanMapper;
import com.workoss.boot.storage.model.UploadSign;
import com.workoss.boot.storage.web.vo.UploadSignVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UploadSignToVOMapper extends BeanMapper<UploadSign, UploadSignVO> {

}

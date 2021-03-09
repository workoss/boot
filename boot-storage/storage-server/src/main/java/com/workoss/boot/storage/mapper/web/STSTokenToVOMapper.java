package com.workoss.boot.storage.mapper.web;

import com.workoss.boot.storage.mapper.BeanMapper;
import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.web.vo.STSTokenVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface STSTokenToVOMapper extends BeanMapper<STSToken, STSTokenVO> {

}

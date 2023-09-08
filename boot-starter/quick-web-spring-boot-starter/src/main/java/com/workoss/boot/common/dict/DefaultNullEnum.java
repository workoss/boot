package com.workoss.boot.common.dict;

import com.workoss.boot.model.IEnum;

/**
 * @author workoss
 */
public enum DefaultNullEnum implements IEnum<Integer,String,DefaultNullEnum> {
    NO(0,"找不到");

    private final Integer code;

    private final String desc;

    DefaultNullEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}

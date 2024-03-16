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
package com.workoss.boot.common.typehandler;

import com.workoss.boot.util.EnumUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 通用枚举类处理器
 *
 * @author workoss
 */
public class IBaseEnumTypeHandler<E extends Enum<E> & com.workoss.boot.model.IEnum<Integer, String, E>>
		extends BaseTypeHandler<E> {

	private final Class<E> type;

	public IBaseEnumTypeHandler(Class<E> type) {
		if (type == null) {
			throw new IllegalArgumentException("Type argument cannot be null");
		}
		this.type = type;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter.getCode());
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		int code = rs.getInt(columnName);
		if (code == 0 && rs.wasNull()) {
			return null;
		}
		return EnumUtil.getByCode(type, code);
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		int code = rs.getInt(columnIndex);
		if (code == 0 && rs.wasNull()) {
			return null;
		}
		return EnumUtil.getByCode(type, code);
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		int code = cs.getInt(columnIndex);
		if (code == 0 && cs.wasNull()) {
			return null;
		}
		return EnumUtil.getByCode(type, code);
	}

}

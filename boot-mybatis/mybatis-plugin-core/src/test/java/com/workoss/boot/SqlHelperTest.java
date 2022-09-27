/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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

import com.workoss.boot.plugin.mybatis.SqlHelper;
import org.springframework.util.SerializationUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

public class SqlHelperTest {

	public static void main(String[] args)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		DemoDao demoDao = new DemoDaoImpl();
		DemoMapper demoMapper = new DemoMapperImpl();
		DemoEntity demoEntity = new DemoEntity();

		// SqlHelper.page(1, 10, false).start();
		// List<DemoEntity> demoEntities = demoDao.selectList(demoEntity);

		List<DemoEntity> entityList = SqlHelper.page(1, 10, false).execute(sqlParam -> demoDao.selectList(demoEntity));

//		Function3<String, String, Integer, List<DemoEntity>> selectSome = demoDao::selectSome;
//		Method write = selectSome.getClass().getDeclaredMethod("writeReplace");
//		write.setAccessible(true);
//		SerializedLambda serializedLambda = (SerializedLambda) write.invoke(selectSome);
//		System.out.println(serializedLambda.toString());
//		System.out.println(serializedLambda.getImplMethodName());
//		System.out.println(serializedLambda.getImplMethodKind());
//		System.out.println(serializedLambda.getImplMethodSignature());
//		System.out.println(serializedLambda.getImplClass());

		Function<DemoEntity, List<DemoEntity>> selectList = demoDao::selectList;

		Method write1 = selectList.getClass().getDeclaredMethod("writeReplace");
		write1.setAccessible(true);
		SerializedLambda serializedLambda1 = (SerializedLambda) write1.invoke(selectList);

		System.out.println(serializedLambda1.toString());
		System.out.println(serializedLambda1.getImplMethodName());
		System.out.println(serializedLambda1.getImplMethodKind());
		System.out.println(serializedLambda1.getImplMethodSignature());
		System.out.println(serializedLambda1.getImplClass());

		// Arrays.stream(selectSome.getClass().getDeclaredMethods()).forEach(method -> {
		// System.out.println(method.getName()+"-"+method.getParameterCount()+"-"+method.getReturnType());
		// });

		// List<DemoModel> modelList = SqlHelper.page(1, 10, false)
		// .executeAndMapper(sqlParam ->
		// demoDao.selectList(demoEntity)).mapper(demoMapper::toTargetList);

	}

}

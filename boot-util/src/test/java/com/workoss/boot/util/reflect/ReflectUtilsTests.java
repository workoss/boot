package com.workoss.boot.util.reflect;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectUtilsTests {

	@Test
	public void getFieldValue(){
		ReflectDemo demo = new ReflectDemo();
		demo.setName("Tom");
		String name = (String)ReflectUtils.getFieldValue(demo,"name");
		Assertions.assertEquals(name,"Tom");
		name = (String)ReflectUtils.getPropertyByInvokeMethod(demo,"name");
		Assertions.assertEquals(name,"Tom");
	}

}
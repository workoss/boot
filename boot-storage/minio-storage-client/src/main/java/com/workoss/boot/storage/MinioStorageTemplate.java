package com.workoss.boot.storage;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class MinioStorageTemplate extends BaseStorageTemplate implements InitializingBean, DisposableBean {

	@Override
	public void destroy() throws Exception {
		super.destroy();
	}

}

/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
package com.workoss.boot.ip;

import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * @author workoss
 */
@SuppressWarnings("unused")
public class Ip2RegionSearcher implements InitializingBean, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(Ip2RegionSearcher.class);

	private static final String FILE_PATH = "classpath:ip2region/ip2region.xdb";

	private Searcher searcher;

	private final ResourceLoader resourceLoader;

	public Ip2RegionSearcher(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public IpInfo search(String ip) {
		if (!StringUtils.hasLength(ip)) {
			return null;
		}
		try {
			String search = searcher.search(ip);
			if (search == null) {
				return null;
			}
			String[] split = search.split("\\|", 5);
			IpInfo ipInfo = new IpInfo();
			ipInfo.setCountry(split[0]);
			ipInfo.setRegion(split[1]);
			ipInfo.setProvince(split[2]);
			ipInfo.setCity(split[3]);
			ipInfo.setIsp(split[4]);
			return ipInfo;
		}
		catch (Exception e) {
			log.warn("[IP] search ip:{} error:{}", ip, e.getMessage());
		}
		return null;
	}

	@Override
	public void destroy() throws Exception {
		if (searcher != null) {
			searcher.close();
		}
	}

	@Override
	public void afterPropertiesSet() {
		Resource resource = this.resourceLoader.getResource(FILE_PATH);
		try (InputStream inputStream = resource.getInputStream()) {
			this.searcher = Searcher.newWithBuffer(StreamUtils.copyToByteArray(inputStream));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

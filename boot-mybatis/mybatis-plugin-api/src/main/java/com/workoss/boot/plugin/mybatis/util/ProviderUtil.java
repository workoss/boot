/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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
package com.workoss.boot.plugin.mybatis.util;

import com.workoss.boot.plugin.mybatis.provider.TableColumnInfo;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * mybatis Provider 工具类
 *
 * @author workoss
 */
public class ProviderUtil {

	private static final String JAR_PROTOCAL = "jar";

	private static final String TEMPLATE_BASE_ROOT = "providerTemplate/";

	private static final String TEMPLATE_SUFFIX = ".xml";

	private static final String DEFAULT_DBTYPE = "default";

	private static final ThreadLocal<String> DB_TYPE_LOCAL = new ThreadLocal<>();

	private static final Map<String, String> SCRIPT_TEMPLATE_CACHE = new ConcurrentHashMap<>();

	private static Lazy<XMLLanguageDriver> xmlLanguageDriverLazy = Lazy.of(() -> new XMLLanguageDriver());

	private static Lazy<Configuration> configurationLazy = Lazy.of(() -> new Configuration());

	private static Lazy<PathMatcher> subPatternLazy = Lazy
			.of(() -> FileSystems.getDefault().getPathMatcher("glob:**/*.xml"));

	public static String getDbType() {
		return DB_TYPE_LOCAL.get();
	}

	public static void setDbType(String dbType) {
		if (dbType == null) {
			DB_TYPE_LOCAL.remove();
			return;
		}
		DB_TYPE_LOCAL.set(dbType);
	}

	public static String getDbType(ProviderContext context, Map<String, Object> paramters) {
		// 优先参数中获取
		String dbType = (String) paramters.get("_dbType");
		// 其次threadlocal中获取
		if (dbType == null) {
			dbType = getDbType();
		}
		// _databaseId
		if (dbType == null) {
			dbType = context.getDatabaseId();
		}
		return dbType;
	}

	public static String getScript(String dbType, String methodName, TableColumnInfo tableColumnInfo) {
		String script = getScriptTemplate(dbType, methodName);
		if (ObjectUtil.isBlank(script)) {
			return null;
		}
		XMLLanguageDriver languageDriver = xmlLanguageDriverLazy.get();
		Configuration configuration = configurationLazy.get();
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, script, Map.class);
		Map<String, Object> paramter = Collections.singletonMap("tableColumnInfo", tableColumnInfo);
		return sqlSource.getBoundSql(paramter).getSql().replaceAll("@\\{", "{").replaceAll("\r\n", "")
				.replaceAll("\n", "").replaceAll("\\s+", " ");
	}

	public static String getScriptTemplate(String dbType, String methodName) {
		if (SCRIPT_TEMPLATE_CACHE.isEmpty()) {
			loadTemplateFile();
		}
		String value = SCRIPT_TEMPLATE_CACHE.get(getKey(dbType, methodName));
		if (DEFAULT_DBTYPE.equalsIgnoreCase(dbType)) {
			return value;
		}
		if (value == null) {
			value = SCRIPT_TEMPLATE_CACHE.get(getKey(DEFAULT_DBTYPE, methodName));
		}
		return value;
	}

	private static void loadTemplateFile() {
		URL url = ProviderUtil.class.getClassLoader().getResource(TEMPLATE_BASE_ROOT);
		String protocal = url.getProtocol();
		if (JAR_PROTOCAL.equalsIgnoreCase(protocal)) {
			readTemplateFromJar(url);
		}
		else {
			readTemplateFromResource(url);
		}
	}

	private static void readTemplateFromResource(URL url) {
		try {
			Files.find(Paths.get(url.toURI()), 2, (path, basicFileAttributes) -> basicFileAttributes.isRegularFile())
					.filter(path -> subPatternLazy.get().matches(path)).forEach(path -> {
						String dbType = path.getParent().getFileName().toString();
						String methodName = path.getFileName().toString().replaceAll(TEMPLATE_SUFFIX, "");
						try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
							readAndAddCache(dbType, methodName, fileInputStream);
						}
						catch (Exception e) {

						}
					});
		}
		catch (Exception e) {

		}
	}

	private static void readTemplateFromJar(URL url) {
		try {
			URLConnection con = url.openConnection();
			JarURLConnection jarCon = (JarURLConnection) con;
			JarFile jarFile = jarCon.getJarFile();
			Enumeration<JarEntry> enumeration = jarFile.entries();
			while (enumeration.hasMoreElements()) {
				JarEntry entry = enumeration.nextElement();
				String fileXmlPattern = entry.getName();
				if (!fileXmlPattern.startsWith(TEMPLATE_BASE_ROOT)) {
					continue;
				}
				Path path = Paths.get(fileXmlPattern);
				if (!subPatternLazy.get().matches(path)) {
					continue;
				}
				String dbType = path.getParent().getFileName().toString();
				String methodName = path.getFileName().toString().replaceAll(TEMPLATE_SUFFIX, "");
				InputStream inputStream = jarFile.getInputStream(entry);
				readAndAddCache(dbType, methodName, inputStream);
			}
		}
		catch (Exception e) {

		}

	}

	private static void readAndAddCache(String dbType, String methodName, InputStream inputStream) {
		StringBuffer stringBuffer = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				stringBuffer.append(line);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (stringBuffer.length() == 0) {
			return;
		}
		SCRIPT_TEMPLATE_CACHE.put(getKey(dbType, methodName), stringBuffer.toString());
	}

	private static String getKey(String dbType, String methodName) {
		return new StringJoiner("_").add(dbType).add(methodName).toString();
	}

}

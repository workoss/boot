package com.workoss.boot.plugin.mybatis.util;

import com.workoss.boot.plugin.mybatis.provider.TableColumnInfo;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public class ProviderUtil {

	private static final String TEMPLATE_SUFFIX = ".xml";

	private static final String TEMPLATE_PACKAGE = "providerTemplate";

	private static final String DEFAULT_DBTYPE = "default";

	private static final ThreadLocal<String> DB_TYPE_LOCAL = new ThreadLocal<>();

	private static final Map<String, String> SCRIPT_TEMPLATE_CACHE = new ConcurrentHashMap<>();

	private static Lazy<XMLLanguageDriver> xmlLanguageDriverLazy = Lazy.of(() -> new XMLLanguageDriver());

	private static Lazy<Configuration> configurationLazy = Lazy.of(() -> new Configuration());


	public static String getDbType(){
		return DB_TYPE_LOCAL.get();
	}

	public static void setDbType(String dbType){
		if (dbType == null){
			DB_TYPE_LOCAL.remove();
			return;
		}
		DB_TYPE_LOCAL.set(dbType);
	}


	public static String getDbType(ProviderContext context, Map<String,Object> paramters){
		//优先参数中获取
		String dbType = (String) paramters.get("_dbType");
		//其次threadlocal中获取
		if (dbType == null){
			dbType = getDbType();
		}
		//_databaseId
		if (dbType == null){
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
		return sqlSource.getBoundSql(paramter).getSql()
				.replaceAll("@\\{", "{")
				.replaceAll("\r\n", "")
				.replaceAll("\n", "")
				.replaceAll("\\s+"," ");
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
		try {
			InputStream inputStream = ProviderUtil.class.getClassLoader().getResourceAsStream(TEMPLATE_PACKAGE+"/default/selectSelective.xml");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line="";
			while((line=br.readLine())!=null){
				System.out.println("getClassLoader: "+line);
			}
			URL url = ProviderUtil.class.getClassLoader().getResource(TEMPLATE_PACKAGE);
			Files.find(Paths.get(url.toURI()), 2, (path, basicFileAttributes) -> basicFileAttributes.isRegularFile())
					.forEach(path -> readAndAddCache(path));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static void readAndAddCache(Path path) {
		try {
			String dbType = path.getParent().getFileName().toString();
			String methodName = path.getFileName().toString().replaceAll(TEMPLATE_SUFFIX, "");
			String xml = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			if (xml == null) {
				return;
			}
			SCRIPT_TEMPLATE_CACHE.put(getKey(dbType, methodName), xml);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getKey(String dbType, String methodName) {
		return new StringJoiner("_").add(dbType).add(methodName).toString();
	}




}

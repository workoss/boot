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
package com.workoss.boot.storage.web.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workoss.boot.storage.StorageTemplate;
import com.workoss.boot.storage.client.StorageClient;
import com.workoss.boot.storage.model.StorageSignature;
import com.workoss.boot.util.StringUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.io.Writer;

/**
 * 对象存储服务servlet
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class StorageServiceFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(StorageServiceFilter.class);

	// private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

	private StorageTemplate storageTemplate;

	private ObjectMapper objectMapper;

	public StorageServiceFilter(StorageTemplate storageTemplate, ObjectMapper objectMapper) {
		this.storageTemplate = storageTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("export storage service /storage/signservice/sign success");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String key = req.getParameter("key");
		if (StringUtils.isBlank(key)) {
			String res = "{\"errcode\":\"-1\",\"errmsg\":\"文件key不能为空\"}";
			writeResponse(resp, 500, res);
			return;
		}
		String clientKey = req.getParameter("clientKey");
		String mimeType = req.getParameter("mimeType");
		String successActionStatus = req.getParameter("successActionStatus");
		sign(clientKey, key, mimeType, successActionStatus, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MediaType mediaType = MediaType.parseMediaType(req.getContentType());
		if (mediaType == null || !mediaType.includes(MediaType.APPLICATION_JSON)) {
			String res = "{\"errcode\":\"-1\",\"errmsg\":\"post 请求只支持 application/json\"}";
			writeResponse(resp, 500, res);
			return;
		}
		int contentLength = req.getContentLength();
		if (contentLength < 0) {
			return;
		}
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(req.getInputStream());
		}
		catch (Exception e) {
			String err = "{\"errcode\":\"-1\",\"errmsg\":\""
					+ (e.getMessage() != null ? e.getMessage() : "Storage error") + "\"}";
			writeResponse(resp, 500, err);
			return;
		}
		if (jsonNode == null || jsonNode.isEmpty() || !jsonNode.has("key")) {
			String res = "{\"errcode\":\"-1\",\"errmsg\":\"入参校验失败\"}";
			writeResponse(resp, 500, res);
			return;
		}
		String clientKey = jsonNode.has("clientKey") ? jsonNode.get("clientKey").asText() : null;
		String mimeType = jsonNode.has("mimeType") ? jsonNode.get("mimeType").asText() : null;
		String successActionStatus = jsonNode.has("successActionStatus") ? jsonNode.get("successActionStatus").asText()
				: null;
		sign(clientKey, jsonNode.get("key").asText(), mimeType, successActionStatus, resp);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String method = req.getMethod();
		if ("get".equalsIgnoreCase(method)) {
			doGet(req, resp);
		}
		else if ("post".equalsIgnoreCase(method)) {
			doPost(req, resp);
		}
		else {
			chain.doFilter(request, response);
		}
	}

	private void sign(String clientKey, String key, String mimeType, String successActionStatus,
			HttpServletResponse response) {
		try {
			StorageClient storageClient = null;
			if (StringUtils.isBlank(clientKey)) {
				storageClient = storageTemplate.client();
			}
			else {
				storageClient = storageTemplate.client(clientKey);
			}
			StorageSignature storageSignature = storageClient.generateWebUploadSign(key, mimeType, successActionStatus);
			writeResponse(response, 200, objectMapper.writeValueAsString(storageSignature));
		}
		catch (Exception e) {
			String resp = "{\"errcode\":\"-1\",\"errmsg\":\""
					+ (e.getMessage() != null ? e.getMessage() : "Storage error") + "\"}";
			writeResponse(response, 500, resp);
		}
	}

	public static void writeResponse(HttpServletResponse resp, int httpStatus, String content) {
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setStatus(httpStatus);
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setHeader("Access-Control-Allow-Methods", "GET,POST");
			resp.setHeader("Access-Control-Allow-Headers", "Set-Cookie,X-Requested-With,Content-Type");
			resp.setHeader("Access-Control-Allow-Credentials", "true");
			resp.setContentType("application/json");
			resp.resetBuffer();
			Writer writer = resp.getWriter();
			writer.write(content);
			writer.flush();
			writer.close();
			log.debug("Storage: writeResponse httpStatus:{} body:{}", httpStatus, content);
		}
		catch (IOException e) {
			log.error("Stroage wrror", e);
		}
	}

}

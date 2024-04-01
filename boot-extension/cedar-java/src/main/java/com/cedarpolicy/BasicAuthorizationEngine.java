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
package com.cedarpolicy;

import static com.cedarpolicy.CedarJson.objectReader;
import static com.cedarpolicy.CedarJson.objectWriter;

import java.io.IOException;

import com.cedarpolicy.model.AuthorizationResponse;
import com.cedarpolicy.model.ValidationRequest;
import com.cedarpolicy.model.ValidationResponse;
import com.cedarpolicy.model.exception.AuthException;
import com.cedarpolicy.model.exception.BadRequestException;
import com.cedarpolicy.model.exception.InternalException;
import com.cedarpolicy.model.slice.Slice;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.workoss.boot.util.jni.NativeLibraryLoader;
import io.github.workoss.jni.JniLibLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An authorization engine that is compiled in process. Communicated with via JNI.
 */
public final class BasicAuthorizationEngine implements AuthorizationEngine {

	private static final Logger LOG = LoggerFactory.getLogger(BasicAuthorizationEngine.class);

	static {
		try {
			JniLibLoader.getInstance().loadLibrary(BasicAuthorizationEngine.class.getClassLoader(), "cedar-java",false);
		}
		catch (IOException e) {
			throw new RuntimeException("load cedar lib error");
		}
	}

	/**
	 * Construct a basic authorization engine.
	 */
	public BasicAuthorizationEngine() {
	}

	@Override
	public AuthorizationResponse isAuthorized(com.cedarpolicy.model.AuthorizationRequest q, Slice slice)
			throws AuthException {
		LOG.trace("Making an isAuthorized request:\n{}\nwith slice\n{}", q, slice);
		final AuthorizationRequest request = new AuthorizationRequest(q, slice);
		return call("AuthorizationOperation", AuthorizationResponse.class, request);
	}

	@Override
	public ValidationResponse validate(ValidationRequest q) throws AuthException {
		LOG.trace("Making a validate request:\n{}", q);
		return call("ValidateOperation", ValidationResponse.class, q);
	}

	private static <REQ, RESP> RESP call(String operation, Class<RESP> responseClass, REQ request)
			throws AuthException {
		try {
			final String cedarJNIVersion = getCedarJNIVersion();
			if (!cedarJNIVersion.equals(AuthorizationEngine.getCedarLangVersion())) {
				throw new AuthException(
						"Error, Java Cedar Language version is " + AuthorizationEngine.getCedarLangVersion()
								+ " but JNI Cedar Language version is " + cedarJNIVersion);
			}
			final String fullRequest = objectWriter().writeValueAsString(request);

			LOG.debug("Making a request ({}, {}) of length {} through the JNI interface:", operation,
					fullRequest.length());
			LOG.trace("The request:\n{}", fullRequest);

			final String response = callCedarJNI(operation, fullRequest);
			LOG.trace("Received response of length {}:\n{}", response.length(), response);

			final JsonNode responseNode = objectReader().readTree(response);
			boolean wasSuccessful = responseNode.path("success").asBoolean(false);
			if (wasSuccessful) {
				final String resultJson = responseNode.path("result").textValue();
				return objectReader().readValue(resultJson, responseClass);
			}
			else {
				final ErrorResponse error = objectReader().forType(ErrorResponse.class).readValue(responseNode);
				if (error.isInternal) {
					throw new InternalException(error.errors);
				}
				else {
					throw new BadRequestException(error.errors);
				}
			}
		}
		catch (JsonProcessingException e) {
			throw new AuthException("JSON Serialization Error", e);
		}
		catch (IllegalArgumentException e) {
			throw new AuthException("Authorization error caused by illegal argument exception.", e);
		}
		catch (IOException e) {
			throw new AuthException("JSON Deserialization Error", e);
		}
	}

	private static final class AuthorizationRequest extends com.cedarpolicy.model.AuthorizationRequest {

		@JsonProperty
		public final Slice slice;

		AuthorizationRequest(com.cedarpolicy.model.AuthorizationRequest request, Slice slice) {
			super(request.principalEUID, request.actionEUID, request.resourceEUID, request.context, request.schema);
			this.slice = slice;
		}

	}

	private static final class ErrorResponse {

		public final boolean success, isInternal;

		public final String[] errors;

		@JsonCreator
		ErrorResponse(@JsonProperty("success") boolean success, @JsonProperty("isInternal") boolean isInternal,
				@JsonProperty("errors") String[] errors) {
			this.success = success;
			this.isInternal = isInternal;
			this.errors = errors;
		}

	}

	/**
	 * Call out to the Rust implementation.
	 * @param call Call type ("AuthorizationOperation" or "ValidateOperation").
	 * @param input Request input in JSON format as a String
	 * @return The response (permit / deny for authorization, valid / invalid for
	 * validation)
	 */
	private static native String callCedarJNI(String call, String input);

	/**
	 * Get the Cedar language major version supported by the JNI (e.g., "1.2")
	 * @return The Cedar language version supported by the JNI
	 */
	private static native String getCedarJNIVersion();

}

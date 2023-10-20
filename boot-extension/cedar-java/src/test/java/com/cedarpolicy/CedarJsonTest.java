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
package com.cedarpolicy;

import com.cedarpolicy.model.AuthorizationResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author workoss
 */
class CedarJsonTest {

	@Test
	void test01() throws IOException {
		String json = "{\n" + "  \"decision\": \"Deny\",\n" + "  \"diagnostics\": {\n" + "    \"reason\": [],\n"
				+ "    \"errors\": [\n"
				+ "      \"JSON Schema file could not be parsed: invalid type: boolean `true`, expected struct NamespaceDefinition\"\n"
				+ "    ]\n" + "  }\n" + "}";

		AuthorizationResponse.Decision decision = AuthorizationResponse.Decision.Deny;
		AuthorizationResponse.Diagnostics diagnostics = new AuthorizationResponse.Diagnostics(Set.of(), List
			.of("JSON Schema file could not be parsed: invalid type: boolean `true`, expected struct NamespaceDefinition"));
		AuthorizationResponse.InterfaceResponse interfaceResponse = new AuthorizationResponse.InterfaceResponse(
				decision, diagnostics);

		AuthorizationResponse response = new AuthorizationResponse(interfaceResponse);
		String jsonString = CedarJson.objectWriter().writeValueAsString(response);
		System.out.println(jsonString);

		String diagnosticsString = "{\n" + "    \"reason\": [],\n" + "    \"errors\": [\n"
				+ "      \"JSON Schema file could not be parsed: invalid type: boolean `true`, expected struct NamespaceDefinition\"\n"
				+ "    ]\n" + "  }";

		AuthorizationResponse.Diagnostics diagnostics1 = CedarJson.objectReader()
			.readValue(diagnosticsString, AuthorizationResponse.Diagnostics.class);
		System.out.println(diagnostics1);
		String interfaceResponseString = jsonString;
		AuthorizationResponse.InterfaceResponse interfaceResponse1 = CedarJson.objectReader()
			.readValue(interfaceResponseString, AuthorizationResponse.InterfaceResponse.class);
		System.out.println(interfaceResponse1);

		AuthorizationResponse authorizationResponse = CedarJson.objectReader()
			.readValue(json, AuthorizationResponse.class);
		System.out.println(authorizationResponse);
	}

}
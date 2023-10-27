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

import com.cedarpolicy.model.AuthorizationRequest;
import com.cedarpolicy.model.AuthorizationResponse;
import com.cedarpolicy.model.ValidationRequest;
import com.cedarpolicy.model.ValidationResponse;
import com.cedarpolicy.model.exception.AuthException;
import com.cedarpolicy.model.schema.Schema;
import com.cedarpolicy.model.slice.BasicSlice;
import com.cedarpolicy.model.slice.Entity;
import com.cedarpolicy.model.slice.Policy;
import com.cedarpolicy.model.slice.Slice;
import com.cedarpolicy.serializer.JsonEUID;
import com.cedarpolicy.value.*;
import com.workoss.boot.util.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author workoss
 */
public class CedarTest {

	public static void main(String[] args) throws AuthException, IOException {
		CedarTest test = new CedarTest();
		test.testAuthorized();
	}

	@Test
	void testAuthorized() throws AuthException, IOException {
		AuthorizationEngine engine = new CustomBasicAuthorizationEngine();
		Map<String, Value> context = new HashMap<>();
		context.put("authenticated", new PrimBool(true));
		AuthorizationRequest request = new AuthorizationRequest("PhotoApp::User::\"alice\"",
				"PhotoApp::Action::\"viewPhoto\"", "PhotoApp::Photo::\"vacationPhoto.jpg\"", context);

		for (int i = 0; i < 1000; i++) {
			AuthorizationResponse authorized = engine.isAuthorized(request, buildSlice());
			System.out.println(authorized.getDecision());
			System.out.println(authorized.getErrors());
		}
	}

	@Test
	void testValidate() throws IOException, AuthException {
		String schemaJson = "{\"PhotoApp\":{\"commonTypes\":{\"PersonType\":{\"type\":\"Record\",\"attributes\":{\"age\":{\"type\":\"Long\"},\"name\":{\"type\":\"String\"}}},\"ContextType\":{\"type\":\"Record\",\"attributes\":{\"ip\":{\"type\":\"Extension\",\"name\":\"ipaddr\",\"required\":false},\"authenticated\":{\"type\":\"Boolean\",\"required\":true}}}},\"entityTypes\":{\"User\":{\"shape\":{\"type\":\"Record\",\"attributes\":{\"userId\":{\"type\":\"String\"},\"personInformation\":{\"type\":\"PersonType\"}}},\"memberOfTypes\":[\"UserGroup\"]},\"UserGroup\":{\"shape\":{\"type\":\"Record\",\"attributes\":{}}},\"Photo\":{\"shape\":{\"type\":\"Record\",\"attributes\":{\"account\":{\"type\":\"Entity\",\"name\":\"Account\",\"required\":true},\"private\":{\"type\":\"Boolean\",\"required\":true}}},\"memberOfTypes\":[\"Album\",\"Account\"]},\"Album\":{\"shape\":{\"type\":\"Record\",\"attributes\":{}}},\"Account\":{\"shape\":{\"type\":\"Record\",\"attributes\":{}}}},\"actions\":{\"viewPhoto\":{\"appliesTo\":{\"principalTypes\":[\"User\",\"UserGroup\"],\"resourceTypes\":[\"Photo\"],\"context\":{\"type\":\"ContextType\"}}},\"createPhoto\":{\"appliesTo\":{\"principalTypes\":[\"User\",\"UserGroup\"],\"resourceTypes\":[\"Photo\"],\"context\":{\"type\":\"ContextType\"}}},\"listPhotos\":{\"appliesTo\":{\"principalTypes\":[\"User\",\"UserGroup\"],\"resourceTypes\":[\"Photo\"],\"context\":{\"type\":\"ContextType\"}}}}}}";
		AuthorizationEngine engine = new CustomBasicAuthorizationEngine();
		Schema schema = new Schema(schemaJson);

		ValidationRequest validationRequest = new ValidationRequest(schema, buildPolicySlice());
		ValidationResponse response = engine.validate(validationRequest);
		System.out.println(response);
	}

	private Slice buildSlice() {
		Set<Policy> p = buildPolicySlice();
		Set<Entity> e = buildEntitySlice();
		return new BasicSlice(p, e);
	}

	private Set<Entity> buildEntitySlice() {

		Map<String, Value> attributes1 = new HashMap<>();
		attributes1.put("userId", new PrimString("897345789237492878"));
		CedarMap personInformation = new CedarMap();
		attributes1.put("personInformation", personInformation);
		JsonEUID parent1 = new JsonEUID("PhotoApp::UserGroup", "alice_friends");
		JsonEUID parent2 = new JsonEUID("PhotoApp::UserGroup", "AVTeam");
		Entity entity1 = new Entity(new JsonEUID("PhotoApp::User", "alice"), attributes1, Set.of(parent1, parent2));

		Map<String, Value> attributes11 = new HashMap<>();
		attributes11.put("private", new PrimBool(false));
		EntityUID entityUID = new EntityUID("PhotoApp::Account", "ahmad");
		attributes11.put("account", entityUID);
		Entity entity11 = new Entity(new JsonEUID("PhotoApp::Photo", "vacationPhoto.jpg"), attributes11, Set.of());

		Map<String, Value> attributes22 = new HashMap<>();
		Entity entity22 = new Entity(new JsonEUID("PhotoApp::UserGroup", "alice_friends"), attributes22, Set.of());

		Map<String, Value> attributes33 = new HashMap<>();
		Entity entity33 = new Entity(new JsonEUID("PhotoApp::UserGroup", "AVTeam"), attributes33, Set.of());

		return Set.of(entity1, entity11, entity22, entity33);
	}

	private Set<Policy> buildPolicySlice() {
		String policy1 = "permit (\n" + "    principal == PhotoApp::User::\"alice\",\n"
				+ "    action == PhotoApp::Action::\"viewPhoto\",\n"
				+ "    resource == PhotoApp::Photo::\"vacationPhoto.jpg\"\n" + ");";
		String policy2 = "permit (\n" + "    principal == PhotoApp::User::\"stacey\",\n"
				+ "    action == PhotoApp::Action::\"viewPhoto\",\n" + "    resource\n" + ")\n"
				+ "when { resource in PhotoApp::Account::\"stacey\" };";
		return Set.of(new Policy(policy1, "policy1"), new Policy(policy2, "policy2"));
	}

}

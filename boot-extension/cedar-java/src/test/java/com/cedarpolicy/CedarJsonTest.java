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
        String json = "{\n" +
                "  \"decision\": \"Deny\",\n" +
                "  \"diagnostics\": {\n" +
                "    \"reason\": [],\n" +
                "    \"errors\": [\n" +
                "      \"JSON Schema file could not be parsed: invalid type: boolean `true`, expected struct NamespaceDefinition\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        AuthorizationResponse.Decision decision = AuthorizationResponse.Decision.Deny;
        AuthorizationResponse.Diagnostics diagnostics = new AuthorizationResponse.Diagnostics(Set.of(),List.of("JSON Schema file could not be parsed: invalid type: boolean `true`, expected struct NamespaceDefinition"));
        AuthorizationResponse.InterfaceResponse interfaceResponse = new AuthorizationResponse.InterfaceResponse(decision,diagnostics);

        AuthorizationResponse response = new AuthorizationResponse(interfaceResponse);
        String jsonString = CedarJson.objectWriter().writeValueAsString(response);
        System.out.println(jsonString);

        String diagnosticsString = "{\n" +
                "    \"reason\": [],\n" +
                "    \"errors\": [\n" +
                "      \"JSON Schema file could not be parsed: invalid type: boolean `true`, expected struct NamespaceDefinition\"\n" +
                "    ]\n" +
                "  }";

        AuthorizationResponse.Diagnostics diagnostics1 = CedarJson.objectReader().readValue(diagnosticsString, AuthorizationResponse.Diagnostics.class);
        System.out.println(diagnostics1);
        String interfaceResponseString = jsonString;
        AuthorizationResponse.InterfaceResponse interfaceResponse1 = CedarJson.objectReader().readValue(interfaceResponseString, AuthorizationResponse.InterfaceResponse.class);
        System.out.println(interfaceResponse1);

        AuthorizationResponse authorizationResponse = CedarJson.objectReader().readValue(json, AuthorizationResponse.class);
        System.out.println(authorizationResponse);
    }

}
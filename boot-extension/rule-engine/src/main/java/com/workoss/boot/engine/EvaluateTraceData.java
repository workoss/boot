package com.workoss.boot.engine;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * @author workoss
 */
@Data
public class EvaluateTraceData {

    private Integer index;

    @JsonAlias({"reference_map"})
    private JsonNode referenceMap;

    private JsonNode rule;
}

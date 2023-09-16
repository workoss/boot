package com.workoss.boot.engine;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * @author workoss
 */
@Data
public class EvaluateTraceResult {

    private String id;

    private String name;

    private String performance;

    private JsonNode input;

    private JsonNode output;

    private EvaluateTraceData traceData;

}

package com.workoss.boot.engine;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.Map;

/**
 * @author workoss
 */
@Data
public class EvaluateResult {

    private String performance;

    private JsonNode result;

    private Map<String,EvaluateTraceResult> trace;

}

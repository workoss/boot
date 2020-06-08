package com.workoss.boot.autoconfigure.sapjco.client.handler;

import com.sap.conn.jco.JCoResponse;

@FunctionalInterface
public interface FunctionResponseHandler {
    /**
     * Handle response result
     * @param response response
     */
    void handle(JCoResponse response);

}

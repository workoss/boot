package com.workoss.boot.autoconfigure.sapjco.client.handler;

import com.sap.conn.jco.JCoParameterList;

@FunctionalInterface
public interface FunctionRequestHandler {
    /**
     * Handle before request.
     * @param importParameter One type of sap function input args.
     * @param tableParameter One type of sap function input args.
     * @param changingParameter One type of sap function input args.
     */
    void handle(JCoParameterList importParameter, JCoParameterList tableParameter, JCoParameterList changingParameter);
}

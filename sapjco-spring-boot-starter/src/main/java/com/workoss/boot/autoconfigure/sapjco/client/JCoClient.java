package com.workoss.boot.autoconfigure.sapjco.client;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.workoss.boot.autoconfigure.sapjco.client.handler.FunctionRequestHandler;
import com.workoss.boot.autoconfigure.sapjco.client.handler.FunctionResponseHandler;

import java.util.Map;

public interface JCoClient extends AutoCloseable{

    /**
     * Release client connection.
     */
    void release();


    /**
     * Get client configuration.
     * @return The configuration {@link JCoClientProperties}
     */
    JCoClientProperties getSettings();


    /**
     * Get jco destination.
     * @return The destination {@link JCoDestination}
     */
    JCoDestination getDestination();


    /**
     * Get sap function.
     * @param functionName The {@literal functionName}
     *                     to be used for viewing sap function information.
     * @return The sap function {@link JCoFunction}
     */
    JCoFunction getFunction(String functionName);


    /**
     * Invoke sap function.
     * @param functionName The {@literal functionName}
     *                     to be used for matching sap function.
     * @param requestHandler The {@link FunctionRequestHandler}
     *                       to be used for doing somethings before request.
     * @param responseHandler he {@link FunctionResponseHandler}
     *                       to be used for handling function's response result.
     */
    void invokeSapFunc(String functionName,
                       FunctionRequestHandler requestHandler,
                       FunctionResponseHandler responseHandler);


    /**
     * Invoke sap function.
     * @param functionName The {@literal functionName}
     *                     to be used for matching sap function.
     * @param requestHandler The {@link FunctionRequestHandler}
     *                       to be used for doing somethings before request.
     * @return The {@literal invoke result}.
     * @since 3.2.4
     */
    Map<String, Object> invokeSapFunc(String functionName,
                                      FunctionRequestHandler requestHandler);


    /**
     * Invoke sap function.
     * @param functionName The {@literal functionName}
     *                     to be used for matching sap function.
     * @param importParameterValue The {@literal importParameterValue}
     *                     to be used for setting value to {@literal IMPORT}
     * @param tablesParameterValue The {@literal tablesParameterValue}
     *                     to be used for setting value to {@literal TABLES}
     * @param changingParameterValue The {@literal changingParameterValue}
     *                     to be used for setting value to {@literal CHANGING}
     * @return The {@literal invoke result}.
     * @since 3.2.5
     */
    Map<String, Object> invokeSapFunc(String functionName, Object importParameterValue,
                                      Object tablesParameterValue, Object changingParameterValue);


    /**
     * Invoke sap function.
     * @param functionName The {@literal functionName}
     *                     to be used for matching sap function.
     * @param requestHandler The {@link FunctionRequestHandler}
     *                       to be used for doing somethings before request.
     * @param resultType The {@literal resultType}
     *                     to be used for formatting result.
     * @return The {@literal invoke result}.
     * @since 3.2.4
     */
    <T> T invokeSapFunc(String functionName,
                        FunctionRequestHandler requestHandler, Class<T> resultType);


    /**
     * Invoke sap function.
     * @param functionName The {@literal functionName}
     *                     to be used for matching sap function.
     * @param importParameterValue The {@literal importParameterValue}
     *                     to be used for setting value to {@literal IMPORT}
     * @param tablesParameterValue The {@literal tablesParameterValue}
     *                     to be used for setting value to {@literal TABLES}
     * @param changingParameterValue The {@literal changingParameterValue}
     *                     to be used for setting value to {@literal CHANGING}
     * @param resultType The {@literal resultType}
     *                     to be used for formatting result.
     * @return The {@literal invoke result}.
     * @since 3.2.5
     */
    <T> T invokeSapFunc(String functionName, Object importParameterValue,
                        Object tablesParameterValue, Object changingParameterValue, Class<T> resultType);
}

package com.workoss.boot.autoconfigure.sapjco.client;

import com.sap.conn.jco.*;
import com.workoss.boot.autoconfigure.sapjco.JCoDataProvider;
import com.workoss.boot.autoconfigure.sapjco.client.handler.FunctionRequestHandler;
import com.workoss.boot.autoconfigure.sapjco.client.handler.FunctionResponseHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
public class DefaultJCoClient implements JCoClient {


    private final JCoClientProperties settings;

    public DefaultJCoClient(JCoClientProperties settings) {
        this.settings = settings;
        initJCoConnection(this.settings);
    }


    @Override
    public void release() {
        JCoDataProvider.getSingleton().unRegisterClientSettings(this.settings.getUniqueKey());

        if (log.isDebugEnabled())
            log.debug("JCoClient: [" + this.settings.getUniqueKey() + "] released.");
    }

    @Override
    public void close() {
        this.release();
    }

    @Override
    public JCoClientProperties getSettings() {
        return this.settings;
    }

    @Override
    public JCoDestination getDestination() {
        try {
            return JCoDestinationManager
                    .getDestination(this.settings.getUniqueKey());
        } catch (JCoException ex) {
            throw new JCoClientException(ex);
        }
    }

    @Override
    public JCoFunction getFunction(String functionName) {
        try {
            return JCoDestinationManager
                    .getDestination(this.settings.getUniqueKey())
                    .getRepository()
                    .getFunction(functionName);
        } catch (JCoException ex) {
            throw new JCoClientException(ex);
        }
    }

    @Override
    public void invokeSapFunc(String functionName,
                              FunctionRequestHandler requestHandler,
                              FunctionResponseHandler responseHandler) {
        try {
            // get function
            JCoFunction function = this.getFunction(functionName);

            if (function == null)
                throw new JCoClientException("Could not find function: [" + functionName + "]");

            // request handle
            requestHandler.handle(
                    function.getImportParameterList(),
                    function.getTableParameterList(),
                    function.getChangingParameterList()
            );

            // invoke
            JCoResponse response = new DefaultRequest(function).execute(this.getDestination());

            // response handle
            responseHandler.handle(response);

        } catch (JCoException ex) {
            throw new JCoClientException("Fail to invoke sap function: [" + functionName + "]", ex);
        }
    }

    @Override
    public Map<String, Object> invokeSapFunc(String functionName, FunctionRequestHandler requestHandler) {
        Map<String, Object> invokeResult = new HashMap<>();
        FunctionResponseHandler responseHandler = response -> response
                .forEach(jCoField ->
                          System.out.println(jCoField.getName())
//                        invokeResult.put(jCoField.getName(), JCoDataUtils.getJCoFieldValue(jCoField))
                );
        this.invokeSapFunc(functionName, requestHandler, responseHandler);
        return invokeResult;
    }

    @Override
    public Map<String, Object> invokeSapFunc(String functionName,
                                             Object importParameterValue,
                                             Object tablesParameterValue, Object changingParameterValue) {
        FunctionRequestHandler requestHandler = (importParameter, tableParameter, changingParameter) -> {
          importParameter = (JCoParameterList) importParameterValue;
          tableParameter = (JCoParameterList) tablesParameterValue;
          changingParameter = (JCoParameterList) changingParameterValue;
        };
        return this.invokeSapFunc(functionName, requestHandler);
    }

    @Override
    public <T> T invokeSapFunc(String functionName, FunctionRequestHandler requestHandler, Class<T> resultType) {
        Map<String, Object> invokeResult = this.invokeSapFunc(functionName, requestHandler);
//        return TypeUtils.castToJavaBean(invokeResult, resultType);
        return null;
    }

    @Override
    public <T> T invokeSapFunc(String functionName,
                               Object importParameterValue,
                               Object tablesParameterValue,
                               Object changingParameterValue, Class<T> resultType) {
        FunctionRequestHandler requestHandler = setParameterRequestHandlerFunc.apply(InvokeParameter.builder()
                .importParameterValue(importParameterValue)
                .tablesParameterValue(tablesParameterValue)
                .changingParameterValue(changingParameterValue)
                .build());
        return this.invokeSapFunc(functionName, requestHandler, resultType);
    }

    private Function<InvokeParameter, FunctionRequestHandler> setParameterRequestHandlerFunc = invokeParameter ->
            (importParameter, tableParameter, changingParameter) -> {
                if (invokeParameter.getImportParameterValue() != null) {
//                    JCoDataUtils.setJCoParameterListValue(importParameter, invokeParameter.getImportParameterValue());
                }
                if (invokeParameter.getTablesParameterValue() != null) {
//                    JCoDataUtils.setJCoParameterListValue(tableParameter, invokeParameter.getTablesParameterValue());
                }
                if (invokeParameter.getChangingParameterValue() != null) {
//                    JCoDataUtils.setJCoParameterListValue(changingParameter, invokeParameter.getChangingParameterValue());
                }
            };


    @Builder
    @Getter
    private static class InvokeParameter {
        private Object importParameterValue;
        private Object tablesParameterValue;
        private Object changingParameterValue;
    }

    /* ============================================================================================================= */

    /**
     * Init connection
     *
     * @param settings The {@literal settings} to be used for initializing connection.
     */
    private static void initJCoConnection(JCoClientProperties settings) {
        try {
            // register client properties.
            JCoDataProvider.getSingleton().registerClientSettings(settings);
            // ping test
            JCoDestinationManager.getDestination(settings.getUniqueKey()).ping();
        } catch (JCoException ex) {
            // unregister client properties.
            JCoDataProvider.getSingleton().unRegisterClientSettings(settings.getUniqueKey());
            throw new JCoClientException("Unable to create the client: [" + settings.getUniqueKey() + "]", ex);
        }
    }

    static class DefaultRequest extends com.sap.conn.jco.rt.DefaultRequest {
        DefaultRequest(JCoFunction function) {
            super(function);
        }
    }


    public String getUniqueKey() {
        return UUID.randomUUID().toString();
    }
}

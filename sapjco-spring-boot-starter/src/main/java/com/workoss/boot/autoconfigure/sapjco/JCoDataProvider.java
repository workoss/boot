package com.workoss.boot.autoconfigure.sapjco;

import com.sap.conn.jco.ext.*;
import com.workoss.boot.autoconfigure.sapjco.client.JCoClientException;
import com.workoss.boot.autoconfigure.sapjco.client.JCoClientProperties;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCoDataProvider implements DestinationDataProvider {

    private final Map<String, Properties> clientSettingsProviders;

    private DestinationDataEventListener destinationDataEventListener;



    private JCoDataProvider() {
        this.clientSettingsProviders = new ConcurrentHashMap<>();
    }

    public static JCoDataProvider getSingleton() {  // singleton
        return JCoDataProviderInstance.INSTANCE;
    }

    private static class JCoDataProviderInstance {
        private static final JCoDataProvider INSTANCE = new JCoDataProvider();
    }

    @Override
    public Properties getDestinationProperties(String destinationName) throws DataProviderException {
        return clientSettingsProviders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .computeIfAbsent(destinationName, key -> {
                    throw new JCoClientException("Destination settings : [" + key + "] is not available.");
                });
    }

    @Override
    public boolean supportsEvents() {
        return true;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {
        this.destinationDataEventListener = destinationDataEventListener;
    }

    /* ============================================================================================================ */

    /**
     * Register provider in JCo runtime environment
     */
    public static void registerInEnvironment() {
        // register destination provider
        Environment.registerDestinationDataProvider(
                JCoDataProviderInstance.INSTANCE);
    }


    /**
     * Register settings in destination for JCo client
     * @param settings connection settings
     * @see DefaultDestinationManager#getDestination(String)
     */
    public void registerClientSettings(JCoClientProperties settings) {
        clientSettingsProviders.compute(settings.getUniqueKey(), (clientName, clientSettings) -> {
            // exist check
            if (clientSettings != null)
                throw new JCoClientException("Destination: [" + clientName + "] has been already registered.");
            // refer
            return referDestinationData(settings);
        });
    }




    /**
     * Un register destination for JCo client
     * @param settingsName name
     * @see DestinationDataEventListener#deleted(String)
     * @see DefaultDestinationManager#deleted(String)
     */
    public void unRegisterClientSettings(String settingsName) {
        clientSettingsProviders.remove(settingsName);
        destinationDataEventListener.deleted(settingsName);
    }



    private static Properties referDestinationData(JCoClientProperties settings) {
        Properties provider = new Properties();

        // connection settings
        provider.setProperty(DestinationDataProvider.JCO_ASHOST, settings.getAshost());
        provider.setProperty(DestinationDataProvider.JCO_SYSNR, settings.getSysnr());
        provider.setProperty(DestinationDataProvider.JCO_CLIENT, settings.getClient());
        provider.setProperty(DestinationDataProvider.JCO_USER, settings.getUser());
        provider.setProperty(DestinationDataProvider.JCO_PASSWD, settings.getPassword());
        provider.setProperty(DestinationDataProvider.JCO_LANG, settings.getLanguage());

        // pool settings
        provider.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, settings.getPoolCapacity());
        provider.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, settings.getPeakLimit());

        return provider;
    }


}

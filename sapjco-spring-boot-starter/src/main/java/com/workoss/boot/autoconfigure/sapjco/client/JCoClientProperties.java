package com.workoss.boot.autoconfigure.sapjco.client;

import lombok.Data;

import java.util.UUID;

@Data
public class JCoClientProperties {

    private String client = "800";

    private String ashost = "127.0.0.1";

    private String sysnr="00";

    private String user = "";

    private String password = "";

    private String language = "ZH";

    private String poolCapacity = "20";

    private String maxGetClientTime = "600";

    private String peakLimit = "50";

    private final String defaultKey = UUID.randomUUID().toString();

    public String getUniqueKey() {
       return defaultKey;
    }

}

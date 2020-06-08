package com.workoss.boot;

import com.sap.conn.jco.*;
import com.sap.conn.jco.rt.TableParameter;
import com.sun.org.apache.xml.internal.security.signature.ObjectContainer;
import com.workoss.boot.autoconfigure.sapjco.JCoDataProvider;
import com.workoss.boot.autoconfigure.sapjco.client.DefaultJCoClient;
import com.workoss.boot.autoconfigure.sapjco.client.JCoClient;
import com.workoss.boot.autoconfigure.sapjco.client.JCoClientProperties;
import com.workoss.boot.autoconfigure.sapjco.client.handler.FunctionRequestHandler;
import com.workoss.boot.autoconfigure.sapjco.client.handler.FunctionResponseHandler;
import com.workoss.boot.util.json.JsonMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class SapTest {

    private JCoClient jCoClient = null;

    @BeforeEach
    public void setUp() {
        JCoDataProvider.registerInEnvironment();

        JCoClientProperties client = new JCoClientProperties();
        client.setClient("800");
        client.setAshost("192.168.1.140");
        client.setUser("EHR_IF");
        client.setPassword("hr123456");
        client.setSysnr("00");
        this.jCoClient = new DefaultJCoClient(client);
    }

    @Test
    public void test01() throws JCoException {
        //        JCoFunction jCoFunction = jCoClient.getFunction("ZHR_RFC_SF_GET_EMPSALARY");
//        System.out.println(jCoFunction.getImportParameterList());
//        System.out.println(jCoFunction.getTableParameterList());
        String functionName = "ZHR_RFC_SF_GET_EMPSALARY";
        Date startDate = Date.from(LocalDate.parse("20190601", DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.parse("20190631", DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay(ZoneId.systemDefault()).toInstant());
        FunctionRequestHandler requestHandler = (importParameter, tableParameter, changingParameter) -> {
//            JCoParameterFieldIterator iterator = importParameter.getParameterFieldIterator();
//            while (iterator.hasNextField()) {
//                JCoParameterField jCoParameterField = iterator.nextParameterField();
//                System.out.println(jCoParameterField.getName());
//            }
            importParameter.setValue("PERNR_TEST", "131496");
            importParameter.setValue("PNBEGDA", startDate);
            importParameter.setValue("PNENDDA", endDate);
            System.out.println(tableParameter);
            System.out.println(changingParameter);
        };


        FunctionResponseHandler responseHandler = (jCoResponse) -> {
            jCoResponse.forEach((jCoField) -> {
                System.out.println(jCoField.getName() + "=" + jCoField.getValue());
            });
        };

        Map<String, Object> result = jCoClient.invokeSapFunc(functionName, requestHandler);
        System.out.println(JsonMapper.toJSONString(result));
    }

    @Test
    public void test02() {

    }


}

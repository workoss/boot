package com.workoss.boot.engine;

import com.workoss.boot.util.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author workoss
 */
class EvaluateResultTest {


    @Test
    void testJson(){
        Duration parse = Duration.parse("PT3.789304S");

        String json  = "{\"performance\":\"3.789304ms\",\"result\":{\"flag\":{\"companyType\":\"amber\",\"country\":\"green\",\"turnover\":\"amber\"},\"overall\":\"amber\"},\"trace\":{\"abaaa033-5516-440c-b211-35f1d616ad9f\":{\"input\":{\"company\":{\"country\":\"US\",\"turnover\":1000000}},\"output\":{\"flag\":{\"country\":\"green\"}},\"name\":\"Country\",\"id\":\"abaaa033-5516-440c-b211-35f1d616ad9f\",\"performance\":\"15.014µs\",\"traceData\":{\"index\":0,\"reference_map\":{},\"rule\":{\"_id\":\"TOi2qECISd\",\"company.country[z87lDk-Xar]\":\"\\\"US\\\",\\\"IE\\\",\\\"GB\\\",\\\"CA\\\"\",\"company.isEu[bGOSakKon0]\":\"\"}}},\"4e7e6bb9-f128-41e7-8cc5-b9d79670b96a\":{\"input\":null,\"output\":null,\"name\":\"Request\",\"id\":\"4e7e6bb9-f128-41e7-8cc5-b9d79670b96a\",\"performance\":null,\"traceData\":null},\"af6cdac4-2019-4a0f-9715-2ecfb27e0bfc\":{\"input\":{\"flag\":{\"companyType\":\"amber\",\"country\":\"green\",\"turnover\":\"amber\"}},\"output\":{\"amber\":2,\"critical\":0,\"green\":1,\"red\":0},\"name\":\"Overall Mapper\",\"id\":\"af6cdac4-2019-4a0f-9715-2ecfb27e0bfc\",\"performance\":\"3.629781ms\",\"traceData\":{\"log\":[]}},\"86ce04c9-b4dd-4513-ae2b-7f585ceb224a\":{\"input\":{\"amber\":2,\"critical\":0,\"green\":1,\"red\":0},\"output\":{\"overall\":\"amber\"},\"name\":\"Overall\",\"id\":\"86ce04c9-b4dd-4513-ae2b-7f585ceb224a\",\"performance\":\"70.754µs\",\"traceData\":{\"index\":2,\"reference_map\":{},\"rule\":{\"_id\":\"SBBO_aWujh\",\"amber[QJttqyV2FB]\":\"> 1\",\"critical[KsBwLhAedP]\":\"\",\"green[iFQl1CKB5S]\":\"\",\"red[AczIUwvClr]\":\"\"}}},\"95aa8f3c-f371-4e48-beb3-0b5775d2a814\":{\"input\":null,\"output\":null,\"name\":\"Response\",\"id\":\"95aa8f3c-f371-4e48-beb3-0b5775d2a814\",\"performance\":null,\"traceData\":null},\"46fbad36-4bbe-44ac-833f-d30e0d37d8d7\":{\"input\":{\"company\":{\"country\":\"US\",\"turnover\":1000000}},\"output\":{\"flag\":{\"turnover\":\"amber\"}},\"name\":\"Turnover\",\"id\":\"46fbad36-4bbe-44ac-833f-d30e0d37d8d7\",\"performance\":\"22.356µs\",\"traceData\":{\"index\":1,\"reference_map\":{},\"rule\":{\"_id\":\"lqBoqkvWHA\",\"company.turnover[6xj5CMIFv9]\":\"[200_000..1_000_000]\"}}},\"d6925cde-b3c9-4b7f-8652-7380dacea6a4\":{\"input\":{\"company\":{\"country\":\"US\",\"turnover\":1000000}},\"output\":{\"flag\":{\"companyType\":\"amber\"}},\"name\":\"Company Type\",\"id\":\"d6925cde-b3c9-4b7f-8652-7380dacea6a4\",\"performance\":\"33.368µs\",\"traceData\":{\"index\":1,\"reference_map\":{},\"rule\":{\"_id\":\"Ewgtm_21qr\",\"company.type[nd30YgUKve]\":\"\"}}}}}";
        EvaluateResult evaluateResult = JsonMapper.parseObject(json, EvaluateResult.class);
        System.out.println(JsonMapper.toJSONString(evaluateResult));
    }
}
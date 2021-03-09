INSERT INTO storage_account (id, account_type, access_key, config, policy_template, state, tenant_id,
                                        create_time, modify_time)
VALUES (1612768695130, 'OSS', 'LTAI4G3Apnzprof18S52qTs7', '{"access_key":"LTAI4G3Apnzprof18S52qTs7",
        "secret_key":"n7nLNV7MgmE7OxONClNfVb6qNb9F2I", "region":"cn-shenzhen",
        "role_arn":"acs:ram::1238831582674451:role/oss-token", "session_name":"popeye", "token_duration_seconds":"1200",
        "max_upload_size":"10485760000"}', '{"Version":"1", "Statement":[{"Effect":"Allow",
        "Action":["oss:{{action}}"], "Resource":["acs:oss:*:*:{{resource}}"]}]}', 'ON', 'default',
        '2021-02-19 14:41:09', '2021-02-19 14:41:11');
INSERT INTO storage_account (id, account_type, access_key, config, policy_template, state, tenant_id,
                                        create_time, modify_time)
VALUES (1612768695132, 'OBS', 'ZZXXUWJBPM5JS9DDM6ZR', '{"access_key":"ZZXXUWJBPM5JS9DDM6ZR",
        "secret_key":"pTCBOVf8NbdUCzOE2w06hQz6XkURz4648MCALAbM", "region":"cn-south-1", "agency_name":"obs_token",
        "domain_name":"workoss", "session_name":"popeye", "token_duration_seconds":"1200",
        "max_upload_size":"10485760000"}', '{"Version":"1.1", "Statement":[{"Action":["obs:{{action}}"],
        "Effect":"Allow", "Resource":["obs:*:*:*:{{resource}}"]}]}', 'ON', 'default', '2021-02-24 08:58:29',
        '2021-02-24 08:58:31');
-- auto-generated definition
create table if not exists  storage_account
(
    id          bigint(40)   primary key ,
    account_type varchar(10) null ,
    access_key  varchar(64)   null,
    config     varchar(1000)   null comment '账号信息',
    policy_template varchar(1000) null comment '授权模板',
    state       varchar(20)   null comment 'ON,OFF',
    tenant_id varchar(30) null comment '多租户ID',
    create_time datetime      null,
    modify_time datetime      null
)
    comment '存储账号管理';


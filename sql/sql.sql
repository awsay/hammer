-- auto-generated definition
create table wechat_listen_config
(
  id                  bigint auto_increment
    primary key,
  chatroom_nick_name  varchar(300)                       not null
  comment '监听的微信群',
  stop_call_key_words varchar(100)                       null
  comment '监听关键词，出现该关键词，就持续监听该人消息，该发言人只要言就不打电话，且10分钟后非该发言人发言才会打电话',
  on_call1_phone      varchar(20)                        not null
  comment '值班人电话',
  on_call1_name       varchar(20)                        not null
  comment '值班人姓名',
  on_duty_start_date  datetime default CURRENT_TIMESTAMP not null
  comment '值班开始日期',
  on_duty_end_date    datetime default CURRENT_TIMESTAMP not null
  comment '值班人结束日期',
  create_date         datetime default CURRENT_TIMESTAMP not null,
  client_ip           varchar(50)                        not null
  comment '客户端ip',
  update_time         datetime default CURRENT_TIMESTAMP null
  comment '更新时间',
  version             int(5) default '0'                 null
  comment '乐观锁'
)
  comment '微信群报警监控配置表';


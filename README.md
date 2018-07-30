# meepo-test
本例改自阿里gts的sample，增加若干测试用例


样例搭建方法
 
1) 准备数据库环境

安装MySQL，创建两个数据库db1和db2。在db1和db2中分别创建txc_undo_log表。在db1库中创建orders表，在db2库中创建stock表。

2) 下载样例

将样例文件sample-txc-dubbo下载到本地机器，样例中包含了meepo的依赖，需要本地打包。

3) 修改配置

打开sample-txc-dubbo/src/main/resources目录，将dubbo-order-service.xml、dubbo-stock-service.xml两个文件中数据源的url、username、password修改为实际值。

4) 运行样例


---------------建表sql语句-----------------

CREATE TABLE orders (

id bigint(20) NOT NULL AUTO_INCREMENT,

user_id varchar(255) NOT NULL,

product_id int(11) NOT NULL,

number int(11) NOT NULL,

gmt_create timestamp NOT NULL,

PRIMARY KEY (id)

) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8


CREATE TABLE stock (

product_id int(11) NOT NULL,

price float NOT NULL,

amount int(11) NOT NULL,

PRIMARY KEY (product_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8


create table txc_undo_log
(
  id            bigint auto_increment 
  comment '主键'
    primary key,
  gmt_create    datetime     not null
  comment '创建时间',
  gmt_modified  datetime     not null
  comment '修改时间',
  xid           varchar(100) not null
  comment '全局事务ID',
  branch_id     varchar(100) not null
  comment '分支事务ID',
  rollback_info longblob     not null
  comment 'LOG',
  status        int          not null
  comment '状态',
  server        varchar(32)  not null
  comment '分支所在DB IP'
)
  comment '事务日志表'
  charset = utf8;

create index unionkey
  on txc_undo_log (xid, branch_id);


create table txc_lock
(
  id         bigint auto_increment
    primary key,
  table_name varchar(32) not null,
  key_value  bigint      not null,
  xid        varchar(64) not null,
  branch_id  varchar(64) not null,
  xlock      varchar(64) not null,
  slock      int         not null,
  constraint txc_log_id_uindex
  unique (id),
  constraint txc_lock_table_name_key_value_xlock_uindex
  unique (table_name, key_value, xlock)
)
  charset = utf8mb4;


CREATE TABLE orders (
  id         bigint(20)   NOT NULL AUTO_INCREMENT,
  user_id    varchar(255) NOT NULL,
  product_id int(11)      NOT NULL,
  number     int(11)      NOT NULL,
  gmt_create timestamp    NOT NULL,
  PRIMARY KEY (id)
)
  ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARSET = utf8;

CREATE TABLE stock (
  product_id int(11) NOT NULL,
  price      float   NOT NULL,
  amount     int(11) NOT NULL,
  PRIMARY KEY (product_id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE TABLE txc_undo_log (
  id            bigint PRIMARY KEY AUTO_INCREMENT
  COMMENT '主键',
  gmt_create    datetime     NOT NULL
  COMMENT '创建时间',
  gmt_modified  datetime     NOT NULL
  COMMENT '修改时间',
  xid           varchar(100) NOT NULL
  COMMENT '全局事务ID',
  branch_id     varchar(100) NOT NULL
  COMMENT '分支事务ID',
  rollback_info longblob     NOT NULL
  COMMENT 'LOG',
  status        int          NOT NULL
  COMMENT '状态',
  server        varchar(32)  NOT NULL
  COMMENT '分支所在DB IP'
)
  CHARSET = utf8
  COMMENT '事务日志表';

CREATE INDEX unionkey
  ON txc_undo_log (xid, branch_id);

CREATE TABLE txc_lock (
  id         bigint PRIMARY KEY AUTO_INCREMENT,
  table_name varchar(32) NOT NULL,
  key_value  bigint      NOT NULL,
  xid        varchar(64) NOT NULL,
  branch_id  varchar(64) NOT NULL,
  xlock      varchar(64) NOT NULL,
  slock      int         NOT NULL,
  CONSTRAINT txc_log_id_uindex UNIQUE txc_log_id_uindex (id),
  CONSTRAINT txc_lock_table_name_key_value_xlock_uindex UNIQUE txc_lock_table_name_key_value_xlock_uindex (table_name, key_value, xlock)
)
  CHARSET = utf8mb4;
DROP DATABASE IF EXISTS yootk_authentication;
CREATE DATABASE yootk_authentication CHARACTER SET UTF8 ;
USE yootk_authentication ;
CREATE TABLE member(
   mid                  varchar(50) not null,
   name                 varchar(30),
   password             varchar(32),
   locked               int,
   CONSTRAINT pk_mid PRIMARY KEY (mid)
) engine='innodb';
-- 0表示活跃、1表示锁定
INSERT INTO member(mid,name,password,locked) VALUES ('admin','管理员','B1195D752E87643B4E54CD079A61E1AE',0) ;
INSERT INTO member(mid,name,password,locked) VALUES ('lee','普通人','B1195D752E87643B4E54CD079A61E1AE',0) ;
INSERT INTO member(mid,name,password,locked) VALUES ('mermaid','美人鱼','B1195D752E87643B4E54CD079A61E1AE',1) ;
COMMIT ;
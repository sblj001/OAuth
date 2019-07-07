DROP DATABASE IF EXISTS yootk;
CREATE DATABASE yootk CHARACTER SET UTF8 ;
USE yootk ;
CREATE TABLE member(
   mid                  varchar(50) not null,
   name                 varchar(30),
   password             varchar(32),
   locked               int,
   CONSTRAINT pk_mid PRIMARY KEY (mid)
) engine='innodb';

CREATE TABLE role(
	rid					varchar(50) ,
	title				varchar(200) ,
	CONSTRAINT pk_rid PRIMARY KEY(rid)
) engine='innodb' ;
CREATE TABLE action(
	actid					varchar(50) ,
	title				varchar(200) ,
	rid					varchar(50) ,
	CONSTRAINT pk_actid PRIMARY KEY(actid)
) engine='innodb' ;
CREATE TABLE member_role(
	mid				varchar(50) ,
	rid				varchar(50)
) engine='innodb' ;
-- 0表示活跃、1表示锁定
INSERT INTO member(mid,name,password,locked) VALUES ('admin','管理员','B1195D752E87643B4E54CD079A61E1AE',0) ;
INSERT INTO member(mid,name,password,locked) VALUES ('lee','普通人','B1195D752E87643B4E54CD079A61E1AE',0) ;
INSERT INTO member(mid,name,password,locked) VALUES ('mermaid','美人鱼','B1195D752E87643B4E54CD079A61E1AE',1) ;
-- 定义角色信息
INSERT INTO role(rid,title) VALUES ('member','用户管理') ;
INSERT INTO role(rid,title) VALUES ('dept','部门管理') ;
INSERT INTO role(rid,title) VALUES ('news','新闻管理') ;
-- 定义权限信息
INSERT INTO action(actid,title,rid) VALUES ('member:add','创建用户','member') ;
INSERT INTO action(actid,title,rid) VALUES ('member:edit','编辑用户','member') ;
INSERT INTO action(actid,title,rid) VALUES ('member:delete','删除用户','member') ;
INSERT INTO action(actid,title,rid) VALUES ('member:list','查看用户','member') ;
INSERT INTO action(actid,title,rid) VALUES ('dept:add','创建部门','dept') ;
INSERT INTO action(actid,title,rid) VALUES ('dept:edit','编辑部门','dept') ;
INSERT INTO action(actid,title,rid) VALUES ('dept:delete','删除部门','dept') ;
INSERT INTO action(actid,title,rid) VALUES ('dept:list','查看部门','dept') ;
INSERT INTO action(actid,title,rid) VALUES ('goods:item','商品分类','goods') ;
INSERT INTO action(actid,title,rid) VALUES ('goods:list','商品列表','goods') ;
INSERT INTO action(actid,title,rid) VALUES ('goods:add','商品添加','goods') ;
INSERT INTO action(actid,title,rid) VALUES ('goods:edit','商品修改','goods') ;
INSERT INTO action(actid,title,rid) VALUES ('goods:delete','商品删除','goods') ;

-- 定义用户与角色的关系
INSERT INTO member_role(mid,rid) VALUES ('admin','member') ;
INSERT INTO member_role(mid,rid) VALUES ('admin','dept') ;
INSERT INTO member_role(mid,rid) VALUES ('admin','goods') ;
INSERT INTO member_role(mid,rid) VALUES ('lee','goods') ;
INSERT INTO member_role(mid,rid) VALUES ('mermaid','dept') ;
COMMIT ;
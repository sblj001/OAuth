DROP DATABASE IF EXISTS yootk_client;
CREATE DATABASE yootk_client CHARACTER SET UTF8 ;
USE yootk_client ;
CREATE TABLE client (
	clid     BIGINT  AUTO_INCREMENT ,
	client_id  VARCHAR(200) ,
	client_secret VARCHAR(200) ,
	CONSTRAINT pk_clid PRIMARY KEY(clid)
) ;
-- 编写测试数据
INSERT INTO client(client_id,client_secret) VALUES ('hello_client','hellolee') ;

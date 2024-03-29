CREATE SEQUENCE HIBERNATE_SEQUENCE
  INCREMENT 1
  MINVALUE 100
  MAXVALUE 9223372036854775807
  START 100
  CACHE 1;

  CREATE TABLE OAUTH_CLIENT_DETAILS (
  CLIENT_ID VARCHAR(255) PRIMARY KEY,
  RESOURCE_IDS VARCHAR(255),
  CLIENT_SECRET VARCHAR(255),
  SCOPE VARCHAR(255),
  AUTHORIZED_GRANT_TYPES VARCHAR(255),
  WEB_SERVER_REDIRECT_URI VARCHAR(255),
  AUTHORITIES VARCHAR(255),
  ACCESS_TOKEN_VALIDITY INTEGER,
  REFRESH_TOKEN_VALIDITY INTEGER,
  ADDITIONAL_INFORMATION VARCHAR(4096),
  AUTOAPPROVE VARCHAR(255)
);

CREATE TABLE OAUTH_CLIENT_TOKEN (
  TOKEN_ID VARCHAR(255),
  TOKEN BYTEA,
  AUTHENTICATION_ID VARCHAR(255) PRIMARY KEY,
  USER_NAME VARCHAR(255),
  CLIENT_ID VARCHAR(255)
);

CREATE TABLE OAUTH_ACCESS_TOKEN (
  TOKEN_ID VARCHAR(255),
  TOKEN BYTEA,
  AUTHENTICATION_ID VARCHAR(255) PRIMARY KEY,
  USER_NAME VARCHAR(255),
  CLIENT_ID VARCHAR(255),
  AUTHENTICATION BYTEA,
  REFRESH_TOKEN VARCHAR(255)
);

CREATE TABLE OAUTH_REFRESH_TOKEN (
  TOKEN_ID VARCHAR(255),
  TOKEN BYTEA,
  AUTHENTICATION BYTEA
);

CREATE TABLE OAUTH_CODE (
  CODE VARCHAR(255),
  AUTHENTICATION BYTEA
);

CREATE TABLE OAUTH_APPROVALS (
  USERID VARCHAR(255),
  CLIENTID VARCHAR(255),
  SCOPE VARCHAR(255),
  STATUS VARCHAR(10),
  EXPIRESAT TIMESTAMP,
  LASTMODIFIEDAT TIMESTAMP
);


CREATE TABLE AUTHORITY (
   ID  BIGSERIAL NOT NULL,
   NAME VARCHAR(255),
   PRIMARY KEY (ID)
);

ALTER TABLE IF EXISTS AUTHORITY ADD CONSTRAINT AUTHORITY_NAME UNIQUE (NAME);

CREATE TABLE USER_ (
   ID  BIGSERIAL NOT NULL,

   ACCOUNT_EXPIRED BOOLEAN,
   ACCOUNT_LOCKED BOOLEAN,
   CREDENTIALS_EXPIRED BOOLEAN,
   ENABLED BOOLEAN,
   PASSWORD VARCHAR(255),
   USER_NAME VARCHAR(255),
   PRIMARY KEY (ID)
);

ALTER TABLE IF EXISTS USER_ ADD CONSTRAINT USER_USER_NAME UNIQUE (USER_NAME);

CREATE TABLE USERS_AUTHORITIES (
   USER_ID INT8 NOT NULL,
   AUTHORITY_ID INT8 NOT NULL,
   PRIMARY KEY (USER_ID, AUTHORITY_ID)
);

ALTER TABLE IF EXISTS USERS_AUTHORITIES ADD CONSTRAINT USERS_AUTHORITIES_AUTHORITY
  FOREIGN KEY (AUTHORITY_ID) REFERENCES AUTHORITY;

ALTER TABLE IF EXISTS USERS_AUTHORITIES ADD CONSTRAINT USERS_AUTHORITIES_USER_
  FOREIGN KEY (USER_ID) REFERENCES USER_;

INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)
	VALUES ('encourse-client', 'resource-server-rest-api', '$2a$04$apjHnf7/1zUdK.DEsllL5Ohn6xHgPT3zMHDGcVNyVUHpv7KodY4Aa', 'read,write',
	        'password,authorization_code,refresh_token,implicit', 'USER', 10800, 2592000);

INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)
	VALUES ('spring-config-oauth2-read-write-client', 'resource-server-rest-api', '$2a$04$soeOR.QFmClXeFIrhJVLWOQxfHjsJLSpWrU1iGxcMGdu.a5hvfY4W',	'read,write',
	        'password,authorization_code,refresh_token,implicit', 'USER', 10800, 2592000);

			INSERT INTO AUTHORITY(ID, NAME) VALUES (1, 'ADMIN');
INSERT INTO AUTHORITY(ID, NAME) VALUES (2, 'PROFESSOR');
INSERT INTO AUTHORITY(ID, NAME) VALUES (3, 'STUDENT');

ALTER TABLE public.OAUTH_CLIENT_DETAILS OWNER TO cs252;
ALTER TABLE public.OAUTH_CLIENT_TOKEN OWNER TO cs252;
ALTER TABLE public.OAUTH_ACCESS_TOKEN OWNER TO cs252;
ALTER TABLE public.OAUTH_REFRESH_TOKEN OWNER TO cs252;
ALTER TABLE public.OAUTH_CODE OWNER TO cs252;
ALTER TABLE public.OAUTH_APPROVALS OWNER TO cs252;
ALTER TABLE public.AUTHORITY OWNER TO cs252;
ALTER TABLE public.USER_ OWNER TO cs252;
ALTER TABLE public.USERS_AUTHORITIES OWNER TO cs252;

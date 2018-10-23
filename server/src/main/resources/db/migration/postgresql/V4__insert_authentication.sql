INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)
	VALUES ('encourse-client', 'resource-server-rest-api', '$2a$04$apjHnf7/1zUdK.DEsllL5Ohn6xHgPT3zMHDGcVNyVUHpv7KodY4Aa', 'read,write',
	        'password,authorization_code,refresh_token,implicit', 'USER', 10800, 2592000);

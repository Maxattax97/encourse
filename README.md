# encourse

## Backend

### Postgres
 Create files `pgdb_user.txt` and `pgdb_password.txt` that contain the admin credentials for the PostgreSQL database.

### Spring

 Add the following properties to your `application-secret.properties`:

    local.postgres.username=
    local.postgres.password=
    spring.security.user.name=
    spring.security.user.password=
 
 You must make the postgres credentials here the same as the files created for the Postgres setup. The credentials afterwards are for any REST call made until authentication is finished.

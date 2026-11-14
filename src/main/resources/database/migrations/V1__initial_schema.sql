CREATE TYPE role_type AS ENUM('ADMIN','MODERATOR','USER');

CREATE TABLE user_credentials(
    user_id           UUID        PRIMARY KEY,
    role              role_type   NOT NULL,
    email             VARCHAR     NOT NULL UNIQUE,
    password          VARCHAR     NOT NULL,
    is_verified       BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE jwt_refresh_token(
    jti        UUID      PRIMARY KEY,
    token      VARCHAR   NOT NULL UNIQUE,
    expiration TIMESTAMP NOT NULL,
    last_used  TIMESTAMP NOT NULL,
    user_agent VARCHAR   NOT NULL,
    ip_address VARCHAR   NOT NULL,
    user_id    UUID      NOT NULL REFERENCES user_credentials(user_id)
);

CREATE TABLE email_code(
    user_id    UUID       PRIMARY KEY REFERENCES user_credentials(user_id),
    code       VARCHAR(6) NOT NULL,
    expiration TIMESTAMP  NOT NULL
);

CREATE TABLE password_reset_code(
    user_id    UUID       PRIMARY KEY REFERENCES user_credentials(user_id),
    code       VARCHAR(6) NOT NULL,
    expiration TIMESTAMP  NOT NULL
);

CREATE TABLE ban(
    user_id    UUID      PRIMARY KEY REFERENCES user_credentials(user_id),
    expiration TIMESTAMP,
    reason     VARCHAR,
    banned_by  UUID      NOT NULL
);

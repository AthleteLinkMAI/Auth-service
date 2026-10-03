CREATE TABLE users(
    id       UUID        PRIMARY KEY,
    username VARCHAR(30) NOT NULL,
    email    VARCHAR     NOT NULL UNIQUE,
    password VARCHAR     NOT NULL,
    is_verified BOOLEAN  NOT NULL DEFAULT FALSE
);

CREATE TABLE jwt_refresh_tokens(
    jti         UUID      PRIMARY KEY,
    token      VARCHAR   NOT NULL UNIQUE,
    expiration TIMESTAMP NOT NULL,
    last_used  TIMESTAMP NOT NULL,
    user_agent VARCHAR   NOT NULL,
    ip_address VARCHAR   NOT NULL,
    user_id    UUID      NOT NULL REFERENCES users(id)
);

CREATE TABLE email_verification_codes(
    id         BIGSERIAL  PRIMARY KEY,
    code       VARCHAR(6) NOT NULL,
    expiration TIMESTAMP  NOT NULL,
    user_id    UUID       NOT NULL UNIQUE REFERENCES users(id)
);

CREATE TABLE password_reset_codes(
    id         BIGSERIAL  PRIMARY KEY,
    code       VARCHAR(6) NOT NULL,
    expiration TIMESTAMP  NOT NULL,
    user_id    UUID       NOT NULL UNIQUE REFERENCES users(id)
);

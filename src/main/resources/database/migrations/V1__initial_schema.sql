CREATE TABLE users(
    id       UUID        PRIMARY KEY,
    username VARCHAR(30) NOT NULL,
    email    VARCHAR     NOT NULL UNIQUE,
    password VARCHAR     NOT NULL
);

CREATE TABLE jwt_refresh_tokens(
    id         UUID      PRIMARY KEY,
    device_id  UUID      NOT NULL UNIQUE,
    token      VARCHAR   NOT NULL,
    expiration TIMESTAMP NOT NULL,
    last_used  TIMESTAMP NOT NULL,
    user_agent VARCHAR   NOT NULL,
    ip_address VARCHAR   NOT NULL,
    user_id    UUID      NOT NULL REFERENCES users(id)
);

CREATE TYPE code_type AS ENUM ('EMAIL', 'PASSWORD');

CREATE TABLE verification_codes(
    id         BIGSERIAL  PRIMARY KEY,
    code       VARCHAR(6) NOT NULL,
    type       code_type  NOT NULL,
    expiration TIMESTAMP  NOT NULL,
    user_id    UUID       NOT NULL UNIQUE REFERENCES users(id)
);
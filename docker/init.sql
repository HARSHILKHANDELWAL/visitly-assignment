-- USERS TABLE
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    last_login TIMESTAMP
);

INSERT INTO users (username, email, password)
VALUES ('Harshil', 'harshil@test.com', '$2a$10$HXPDbTUG5wybQZ7PmCRKW.GoCD9x97CpkZGup3RrK1oXNJ6colrBa');


-- ROLES TABLE
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');


-- USER_ROLES MANY-TO-MANY TABLE
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Assign ADMIN to user 1
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

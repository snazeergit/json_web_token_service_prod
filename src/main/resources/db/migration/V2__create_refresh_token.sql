CREATE TABLE refresh_tokens
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL,
    expiry_date DATETIME NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_refresh_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE TABLE blacklisted_tokens
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL,
    expiry_date DATETIME NOT NULL
);
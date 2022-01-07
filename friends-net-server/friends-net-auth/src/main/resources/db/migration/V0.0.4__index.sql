CREATE INDEX idx_auth_user_login ON auth_user (
    login
    );

CREATE INDEX idx_auth_user_login_name ON auth_user (
    login, name
    );
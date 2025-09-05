-- 2. Criação da sequência
CREATE SEQUENCE IF NOT EXISTS dating_adm.sq_user_id
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 50;

-- 3. Criação da tabela dating_adm.users
CREATE TABLE IF NOT EXISTS dating_adm.users (
    user_id         BIGINT        NOT NULL DEFAULT nextval('dating_adm.sq_user_id'),
    code            UUID          NOT NULL,
    email           VARCHAR(255)  NOT NULL,
    user_password   VARCHAR(255)  NOT NULL,
    name            VARCHAR(255)  NOT NULL,
    birth_date      DATE          NOT NULL,
    bio             TEXT,
    profession      TEXT,
    education       TEXT,
    height          INTEGER,
    body_type       VARCHAR(100),
    lifestyle       VARCHAR(100),
    looking_for     VARCHAR(100),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    city            VARCHAR(100),
    state           VARCHAR(100),
    country         VARCHAR(100),
    kyc_status      VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    is_premium      BOOLEAN       NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
    daily_likes     INTEGER       NOT NULL DEFAULT 0,
    daily_messages  INTEGER       NOT NULL DEFAULT 0,
    last_reset_date DATE,
    created_at      TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_pk PRIMARY KEY(user_id),
    CONSTRAINT users_code_uk UNIQUE(code),
    CONSTRAINT users_email_uk UNIQUE(email),
    CONSTRAINT users_kyc_status_ck CHECK (kyc_status IN ('PENDING', 'UNDER_REVIEW', 'APPROVED', 'REJECTED'))
);

-- 4. Trigger function
CREATE OR REPLACE FUNCTION dating_adm.users_updated_at_trigger()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

---- 5. Trigger
--DO $$
--BEGIN
--    IF NOT EXISTS (
--        SELECT 1
--        FROM pg_trigger t
--        JOIN pg_class c ON t.tgrelid = c.oid
--        JOIN pg_namespace n ON c.relnamespace = n.oid
--        WHERE t.tgname = 'users_updated_at_tr'
--          AND n.nspname = 'dating_adm'
--          AND c.relname = 'users'
--    ) THEN
--        EXECUTE '
--            CREATE TRIGGER users_updated_at_tr
--            BEFORE UPDATE ON dating_adm.users
--            FOR EACH ROW
--            EXECUTE FUNCTION dating_adm.users_updated_at_trigger()
--        ';
--    END IF;
--END;
--$$ LANGUAGE plpgsql;

-- 6. Índices adicionais
CREATE INDEX IF NOT EXISTS users_kyc_status_idx ON dating_adm.users (kyc_status);
CREATE INDEX IF NOT EXISTS users_country_state_idx ON dating_adm.users (country, state);

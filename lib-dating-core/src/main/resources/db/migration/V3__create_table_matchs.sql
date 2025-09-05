-- 2. Criação da sequência
CREATE SEQUENCE IF NOT EXISTS dating_adm.sq_match_id
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 50;

-- 3. Criação da tabela dating_adm.match
CREATE TABLE IF NOT EXISTS dating_adm.match (
    id              BIGINT        NOT NULL DEFAULT nextval('dating_adm.sq_match_id'),
    user_id         BIGINT        NOT NULL,
    target_user_id  BIGINT        NOT NULL,
    is_like         BOOLEAN       NOT NULL DEFAULT FALSE,
    is_mutual       BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT match_pk PRIMARY KEY(id),
    CONSTRAINT match_user_fk FOREIGN KEY (user_id) REFERENCES dating_adm.users(user_id),
    CONSTRAINT match_target_user_fk FOREIGN KEY (target_user_id) REFERENCES dating_adm.users(user_id)
);

-- 4. Comentários para documentação
COMMENT ON TABLE  dating_adm.match IS '[NOT_SECURITY_APPLY] Tabela que representa interações (match) entre usuários.';
COMMENT ON COLUMN dating_adm.match.id             IS '[NOT_SECURITY_APPLY] Identificador único do match.';
COMMENT ON COLUMN dating_adm.match.user_id        IS '[NOT_SECURITY_APPLY] ID do usuário que realizou a ação.';
COMMENT ON COLUMN dating_adm.match.target_user_id IS '[NOT_SECURITY_APPLY] ID do usuário alvo da interação.';
COMMENT ON COLUMN dating_adm.match.is_like        IS '[NOT_SECURITY_APPLY] Indica se houve like.';
COMMENT ON COLUMN dating_adm.match.is_mutual      IS '[NOT_SECURITY_APPLY] Indica se o like foi mútuo.';
COMMENT ON COLUMN dating_adm.match.created_at     IS '[NOT_SECURITY_APPLY] Data/hora da criação do registro.';

-- 5. Índices adicionais
CREATE INDEX IF NOT EXISTS match_user_target_idx ON dating_adm.match (user_id, target_user_id);

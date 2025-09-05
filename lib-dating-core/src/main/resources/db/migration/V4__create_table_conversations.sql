-- 2. Criação da sequência
CREATE SEQUENCE IF NOT EXISTS dating_adm.sq_conversation_id
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 50;

-- 3. Criação da tabela dating_adm.conversations
CREATE TABLE IF NOT EXISTS dating_adm.conversations (
    id             BIGINT       NOT NULL DEFAULT nextval('dating_adm.sq_conversation_id'),
    user1_id       BIGINT       NOT NULL,
    user2_id       BIGINT       NOT NULL,
    last_message   TEXT,
    last_message_at TIMESTAMP(6),
    unread_count1  INTEGER      NOT NULL DEFAULT 0,
    unread_count2  INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT conversations_pk PRIMARY KEY (id),
    CONSTRAINT conversations_user1_fk FOREIGN KEY (user1_id) REFERENCES dating_adm.users (user_id),
    CONSTRAINT conversations_user2_fk FOREIGN KEY (user2_id) REFERENCES dating_adm.users (user_id)
);

-- 4. Função trigger para atualizar updated_at
--CREATE OR REPLACE FUNCTION dating_adm.conversations_updated_at_trigger()
--RETURNS TRIGGER AS $$
--BEGIN
--    NEW.updated_at := CURRENT_TIMESTAMP;
--    RETURN NEW;
--END;
--$$ LANGUAGE plpgsql;
--
---- 5. Criação do trigger se não existir
--DO $$
--BEGIN
--    IF NOT EXISTS (
--        SELECT 1
--        FROM pg_trigger t
--        JOIN pg_class c ON t.tgrelid = c.oid
--        JOIN pg_namespace n ON c.relnamespace = n.oid
--        WHERE t.tgname = 'conversations_updated_at_tr'
--          AND n.nspname = 'dating_adm'
--          AND c.relname = 'conversations'
--    ) THEN
--        EXECUTE '
--            CREATE TRIGGER conversations_updated_at_tr
--            BEFORE UPDATE ON dating_adm.conversations
--            FOR EACH ROW
--            EXECUTE FUNCTION dating_adm.conversations_updated_at_trigger()
--        ';
--    END IF;
--END;
--$$ LANGUAGE plpgsql;

-- 6. Comentários para documentação
COMMENT ON TABLE  dating_adm.conversations IS '[NOT_SECURITY_APPLY] Tabela de conversas entre dois usuários.';
COMMENT ON COLUMN dating_adm.conversations.id              IS '[NOT_SECURITY_APPLY] Identificador único da conversa.';
COMMENT ON COLUMN dating_adm.conversations.user1_id        IS '[NOT_SECURITY_APPLY] Primeiro usuário da conversa.';
COMMENT ON COLUMN dating_adm.conversations.user2_id        IS '[NOT_SECURITY_APPLY] Segundo usuário da conversa.';
COMMENT ON COLUMN dating_adm.conversations.last_message    IS '[NOT_SECURITY_APPLY] Última mensagem enviada na conversa.';
COMMENT ON COLUMN dating_adm.conversations.last_message_at IS '[NOT_SECURITY_APPLY] Data/hora da última mensagem.';
COMMENT ON COLUMN dating_adm.conversations.unread_count1   IS '[NOT_SECURITY_APPLY] Contagem de mensagens não lidas para o usuário 1.';
COMMENT ON COLUMN dating_adm.conversations.unread_count2   IS '[NOT_SECURITY_APPLY] Contagem de mensagens não lidas para o usuário 2.';
COMMENT ON COLUMN dating_adm.conversations.created_at      IS '[NOT_SECURITY_APPLY] Data/hora de criação da conversa.';
COMMENT ON COLUMN dating_adm.conversations.updated_at      IS '[NOT_SECURITY_APPLY] Data/hora da última atualização da conversa.';

-- 7. Índices adicionais para performance
CREATE INDEX IF NOT EXISTS conversations_user1_idx ON dating_adm.conversations (user1_id);
CREATE INDEX IF NOT EXISTS conversations_user2_idx ON dating_adm.conversations (user2_id);
CREATE INDEX IF NOT EXISTS conversations_last_message_at_idx ON dating_adm.conversations (last_message_at);

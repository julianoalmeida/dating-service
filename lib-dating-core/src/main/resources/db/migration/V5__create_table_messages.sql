-- 2. Criação da sequência
CREATE SEQUENCE IF NOT EXISTS dating_adm.sq_message_id
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 50;

-- 3. Criação da tabela dating_adm.messages
CREATE TABLE IF NOT EXISTS dating_adm.messages (
    id              BIGINT       NOT NULL DEFAULT nextval('dating_adm.sq_message_id'),
    conversation_id BIGINT       NOT NULL,
    sender_id       BIGINT       NOT NULL,
    content         TEXT         NOT NULL,
    message_type    VARCHAR(50)  NOT NULL DEFAULT 'TEXT',
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT messages_pk PRIMARY KEY (id),
    CONSTRAINT messages_type_ck CHECK (message_type IN ('TEXT', 'IMAGE', 'GIFT', 'SYSTEM')),
    CONSTRAINT messages_sender_fk FOREIGN KEY (sender_id) REFERENCES dating_adm.users (user_id),
    CONSTRAINT messages_conversation_fk FOREIGN KEY (conversation_id) REFERENCES dating_adm.conversations (id)
);

-- 4. Comentários
COMMENT ON TABLE  dating_adm.messages IS '[NOT_SECURITY_APPLY] Tabela de mensagens trocadas entre usuários.';
COMMENT ON COLUMN dating_adm.messages.id              IS '[NOT_SECURITY_APPLY] Identificador único da mensagem.';
COMMENT ON COLUMN dating_adm.messages.conversation_id IS '[NOT_SECURITY_APPLY] ID da conversa à qual a mensagem pertence.';
COMMENT ON COLUMN dating_adm.messages.sender_id       IS '[NOT_SECURITY_APPLY] ID do usuário remetente da mensagem.';
COMMENT ON COLUMN dating_adm.messages.content         IS '[NOT_SECURITY_APPLY] Conteúdo da mensagem.';
COMMENT ON COLUMN dating_adm.messages.message_type    IS '[NOT_SECURITY_APPLY] Tipo da mensagem: TEXT, IMAGE, GIFT, SYSTEM.';
COMMENT ON COLUMN dating_adm.messages.is_read         IS '[NOT_SECURITY_APPLY] Indicador se a mensagem foi lida.';
COMMENT ON COLUMN dating_adm.messages.created_at      IS '[NOT_SECURITY_APPLY] Data/hora de criação da mensagem.';

-- 5. Índices adicionais
CREATE INDEX IF NOT EXISTS messages_conversation_idx ON dating_adm.messages (conversation_id);
CREATE INDEX IF NOT EXISTS messages_sender_idx       ON dating_adm.messages (sender_id);
CREATE INDEX IF NOT EXISTS messages_type_idx         ON dating_adm.messages (message_type);

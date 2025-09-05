-- 1. Ativa extensão para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 3. Criação da sequência
CREATE SEQUENCE IF NOT EXISTS dating_adm.sq_outbox_id
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 50;

-- 4. Criação da tabela outbox
CREATE TABLE IF NOT EXISTS dating_adm.outbox (
    id             BIGINT        NOT NULL DEFAULT nextval('dating_adm.sq_outbox_id'),
    event_id       UUID           NOT NULL,
    topic          VARCHAR(255)   NOT NULL,
    event_key      VARCHAR(255)   NOT NULL,
    event_type     VARCHAR(100)   NOT NULL,
    event_status   VARCHAR(50)    NOT NULL,
    event_payload  TEXT           NOT NULL,
    created_at     TIMESTAMP(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT outbox_pk PRIMARY KEY(id),
    CONSTRAINT outbox_eventid_uk UNIQUE(event_id),
    CONSTRAINT outbox_event_status_ck CHECK (event_status IN ('PENDING','SENT','ACK'))
);

COMMENT ON TABLE dating_adm.outbox IS '[NOT_SECURITY_APPLY] Tabela para padrão Outbox; controle de eventos para envio assíncrono.';
COMMENT ON COLUMN dating_adm.outbox.id             IS 'ID interno sequencial.';
COMMENT ON COLUMN dating_adm.outbox.event_id       IS 'UUID público do evento.';
COMMENT ON COLUMN dating_adm.outbox.topic          IS 'Tópico do evento.';
COMMENT ON COLUMN dating_adm.outbox.event_key      IS 'Chave única do evento.';
COMMENT ON COLUMN dating_adm.outbox.event_type     IS 'Tipo do evento.';
COMMENT ON COLUMN dating_adm.outbox.event_status   IS 'Status do envio do evento.';
COMMENT ON COLUMN dating_adm.outbox.event_payload  IS 'Payload do evento (CLOB).';
COMMENT ON COLUMN dating_adm.outbox.created_at     IS 'Data de criação (insert).';
COMMENT ON COLUMN dating_adm.outbox.updated_at     IS 'Data da última atualização.';

-- 5. Índices
DO $$
BEGIN
  CREATE INDEX IF NOT EXISTS outbox_idx01 ON dating_adm.outbox(topic);
  CREATE INDEX IF NOT EXISTS outbox_idx02 ON dating_adm.outbox(event_key);
  CREATE INDEX IF NOT EXISTS outbox_idx03 ON dating_adm.outbox(event_type);
  CREATE INDEX IF NOT EXISTS outbox_idx06 ON dating_adm.outbox(event_status);
  CREATE INDEX IF NOT EXISTS outbox_idx07 ON dating_adm.outbox(event_status, id);
END;
$$;

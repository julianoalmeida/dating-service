package br.com.dating.core.shared.outbox.enums;

public enum EventStatus {

    /**
     * Status pendente, evento apenas foi gravado na tabela de outbox, mas ainda não enviado
     */
    PENDING,

    /**
     * Status enviado, o JOB de envio rodou e realizou o envio da mensagem
     */
    SENT,

    /**
     * Status confirmado, após o consumidor de confirmação realizar a leitura do evento ele confirma
     * o envio na outbox
     */
    ACK
}

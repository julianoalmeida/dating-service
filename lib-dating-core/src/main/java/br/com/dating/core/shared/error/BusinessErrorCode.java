package br.com.dating.core.shared.error;

import java.io.Serializable;

/**
 * Interface padrão para erros de negócio.
 */
public interface BusinessErrorCode extends Serializable {

    /**
     * Deve retornar um código de erro que possibilita os clientes a identificarem o motivo do erro no sistema.
     *
     * @return um string contendo o código de erro.
     */
    String getCode();

    /**
     * Deve retornar um código de messagem que pode ser utilizado pelo MessageSource para tradução da mensagem de acordo com o Location.
     *
     * @return retorna um código que o messages.properties possa traduzir.
     */
    String getMessageCode();

}

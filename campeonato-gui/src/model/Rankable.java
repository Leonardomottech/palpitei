package model;

/**
 * Interface que define o contrato para entidades que podem ser classificadas
 * em um ranking baseado em pontuação.
 *
 * Implementada por: {@link Participant}
 */
public interface Rankable {

    /**
     * Retorna a pontuação total acumulada pela entidade.
     *
     * @return total de pontos
     */
    int getTotalPoints();

    /**
     * Retorna o nome de exibição da entidade no ranking.
     *
     * @return nome
     */
    String getName();

    /**
     * Retorna o identificador único (login) da entidade.
     *
     * @return login / identificador
     */
    String getLogin();
}

package model;

/**
 * Enum representando o resultado de uma partida.
 */
public enum MatchResult {
    HOME_WIN("Vitória Mandante"),
    AWAY_WIN("Vitória Visitante"),
    DRAW("Empate");

    private final String description;

    MatchResult(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }

    @Override
    public String toString() { return description; }
}

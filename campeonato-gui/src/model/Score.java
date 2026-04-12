package model;

/**
 * Representa o placar de uma partida.
 */
public class Score {
    private int homeGoals;
    private int awayGoals;

    public Score() {
        this.homeGoals = 0;
        this.awayGoals = 0;
    }

    public Score(int homeGoals, int awayGoals) {
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    public MatchResult getResult() {
        if (homeGoals > awayGoals) return MatchResult.HOME_WIN;
        if (awayGoals > homeGoals) return MatchResult.AWAY_WIN;
        return MatchResult.DRAW;
    }

    public boolean equals(Score other) {
        if (other == null) return false;
        return this.homeGoals == other.homeGoals && this.awayGoals == other.awayGoals;
    }

    public int getHomeGoals() { return homeGoals; }
    public void setHomeGoals(int homeGoals) { this.homeGoals = homeGoals; }
    public int getAwayGoals() { return awayGoals; }
    public void setAwayGoals(int awayGoals) { this.awayGoals = awayGoals; }

    @Override
    public String toString() {
        return homeGoals + " x " + awayGoals;
    }
}

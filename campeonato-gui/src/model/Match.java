package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma partida do campeonato.
 */
public class Match {
    private static int counter = 1;

    private int id;
    private Championship championship;
    private Club homeClub;
    private Club awayClub;
    private LocalDateTime dateTime;
    private Score finalScore;
    private boolean finished;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Construtor padrão
    public Match() {
        this.id = counter++;
        this.finished = false;
    }

    // Construtor sobrecarregado
    public Match(Championship championship, Club homeClub, Club awayClub, LocalDateTime dateTime) {
        this.id = counter++;
        this.championship = championship;
        this.homeClub = homeClub;
        this.awayClub = awayClub;
        this.dateTime = dateTime;
        this.finished = false;
    }

    public boolean canBet(LocalDateTime now) {
        return now.isBefore(dateTime.minusMinutes(20)) && !finished;
    }

    public void registerResult(Score score) {
        this.finalScore = score;
        this.finished = true;
    }

    public int getId() { return id; }
    public Championship getChampionship() { return championship; }
    public Club getHomeClub() { return homeClub; }
    public Club getAwayClub() { return awayClub; }
    public LocalDateTime getDateTime() { return dateTime; }
    public Score getFinalScore() { return finalScore; }
    public boolean isFinished() { return finished; }

    public void setChampionship(Championship championship) { this.championship = championship; }
    public void setHomeClub(Club homeClub) { this.homeClub = homeClub; }
    public void setAwayClub(Club awayClub) { this.awayClub = awayClub; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    @Override
    public String toString() {
        String status = finished
                ? "[ENCERRADA: " + finalScore + "]"
                : "[" + dateTime.format(FORMATTER) + "]";
        return String.format("#%d %s vs %s %s",
                id, homeClub.getAcronym(), awayClub.getAcronym(), status);
    }
}

package model;

/**
 * Representa a aposta de um participante em uma partida.
 */
public class Bet {
    public static final int POINTS_RESULT = 5;
    public static final int POINTS_EXACT = 10;

    private Participant participant;
    private Match match;
    private Score predictedScore;
    private int pointsEarned;
    private boolean evaluated;

    // Construtor padrão
    public Bet() {
        this.pointsEarned = 0;
        this.evaluated = false;
    }

    // Construtor sobrecarregado
    public Bet(Participant participant, Match match, Score predictedScore) {
        this.participant = participant;
        this.match = match;
        this.predictedScore = predictedScore;
        this.pointsEarned = 0;
        this.evaluated = false;
    }

    /**
     * Calcula e atribui pontuação ao participante.
     * Polimorfismo: calcula() é sobreposto aqui, poderia ser estendido em subclasses.
     */
    public void evaluate() {
        if (evaluated || !match.isFinished()) return;

        Score real = match.getFinalScore();
        MatchResult realResult = real.getResult();
        MatchResult predictedResult = predictedScore.getResult();

        if (predictedScore.equals(real)) {
            pointsEarned = POINTS_EXACT;
        } else if (predictedResult == realResult) {
            pointsEarned = POINTS_RESULT;
        } else {
            pointsEarned = 0;
        }

        participant.addPoints(pointsEarned);
        evaluated = true;
    }

    public Participant getParticipant() { return participant; }
    public Match getMatch() { return match; }
    public Score getPredictedScore() { return predictedScore; }
    public int getPointsEarned() { return pointsEarned; }
    public boolean isEvaluated() { return evaluated; }

    @Override
    public String toString() {
        String status = evaluated
                ? "(+" + pointsEarned + " pts)"
                : "(pendente)";
        return String.format("Partida %s | Palpite: %s %s",
                match.toString(), predictedScore.toString(), status);
    }
}

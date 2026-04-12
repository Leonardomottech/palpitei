package model;

/**
 * Participante de um grupo de apostas. Herda de Person e implementa Rankable.
 * Polimorfismo: sobrepõe getRole() de Person.
 * Interface: implementa Rankable para ser classificado em rankings por pontos.
 */
public class Participant extends Person implements Rankable {
    private Group group;
    private int totalPoints;

    // Construtor padrão
    public Participant() {
        super();
        this.totalPoints = 0;
    }

    // Construtor sobrecarregado (sem grupo)
    public Participant(String name, String login, String password) {
        super(name, login, password);
        this.totalPoints = 0;
    }

    // Construtor sobrecarregado (com grupo)
    public Participant(String name, String login, String password, Group group) {
        super(name, login, password);
        this.group = group;
        this.totalPoints = 0;
    }

    @Override
    public String getRole() {
        return "PARTICIPANTE";
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    @Override
    public String toString() {
        String groupName = (group != null) ? group.getName() : "Sem grupo";
        return String.format("%-20s | Grupo: %-15s | Pontos: %d",
                getName(), groupName, totalPoints);
    }
}

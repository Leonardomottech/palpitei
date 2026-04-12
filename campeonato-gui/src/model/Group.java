package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Grupo de apostas com no máximo 5 participantes.
 */
public class Group {
    public static final int MAX_PARTICIPANTS = 5;

    private String name;
    private List<Participant> participants;

    // Construtor padrão
    public Group() {
        this.participants = new ArrayList<>();
    }

    // Construtor sobrecarregado
    public Group(String name) {
        this.name = name;
        this.participants = new ArrayList<>();
    }

    public boolean addParticipant(Participant p) {
        if (participants.size() >= MAX_PARTICIPANTS) {
            return false;
        }
        participants.add(p);
        return true;
    }

    /**
     * Retorna os participantes do grupo ordenados por pontuação (maior primeiro).
     * Utiliza a interface {@link Rankable} para comparação, demonstrando
     * polimorfismo via interface.
     *
     * @return lista de Rankable ordenada
     */
    public List<Rankable> getRanking() {
        List<Rankable> sorted = new ArrayList<>(participants);
        sorted.sort((a, b) -> Integer.compare(b.getTotalPoints(), a.getTotalPoints()));
        return sorted;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Participant> getParticipants() { return participants; }

    @Override
    public String toString() {
        return String.format("Grupo: %s (%d participante(s))", name, participants.size());
    }
}

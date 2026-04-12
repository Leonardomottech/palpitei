package service;

import model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço central do sistema. Centraliza regras de negócio.
 */
public class BettingService {
    public static final int MAX_GROUPS = 5;
    public static final int MAX_PARTICIPANTS = 5;

    private Admin admin;
    private List<Championship> championships;
    private List<Club> clubs;
    private List<Match> matches;
    private List<Group> groups;
    private List<Participant> participants;
    private List<Bet> bets;

    public BettingService() {
        championships = new ArrayList<>();
        clubs = new ArrayList<>();
        matches = new ArrayList<>();
        groups = new ArrayList<>();
        participants = new ArrayList<>();
        bets = new ArrayList<>();
        admin = new Admin("Administrador", "admin", "admin123");
    }

    // ──────────── AUTENTICAÇÃO ────────────

    public Person login(String login, String password) {
        if (admin.getLogin().equals(login) && admin.getPassword().equals(password)) {
            return admin;
        }
        for (Participant p : participants) {
            if (p.getLogin().equals(login) && p.getPassword().equals(password)) {
                return p;
            }
        }
        return null;
    }

    // ──────────── CLUBES ────────────

    public boolean addClub(String name, String acronym) {
        clubs.add(new Club(name, acronym));
        return true;
    }

    public List<Club> getClubs() { return clubs; }

    public Club findClub(String acronym) {
        return clubs.stream()
                .filter(c -> c.getAcronym().equalsIgnoreCase(acronym))
                .findFirst().orElse(null);
    }

    // ──────────── CAMPEONATOS ────────────

    public String addChampionship(String name, int year) {
        championships.add(new Championship(name, year));
        return "Campeonato cadastrado com sucesso.";
    }

    public String addClubToChampionship(Championship c, Club club) {
        if (c.addClub(club)) return "Clube adicionado ao campeonato.";
        return "Limite de " + Championship.MAX_CLUBS + " clubes atingido.";
    }

    public List<Championship> getChampionships() { return championships; }

    // ──────────── PARTIDAS ────────────

    public String addMatch(Championship champ, Club home, Club away, LocalDateTime dt) {
        if (home.getAcronym().equals(away.getAcronym()))
            return "Um clube não pode jogar contra si mesmo.";
        matches.add(new Match(champ, home, away, dt));
        return "Partida registrada com sucesso.";
    }

    public List<Match> getMatches() { return matches; }

    public String registerResult(Match match, int homeGoals, int awayGoals) {
        if (match.isFinished()) return "Partida já encerrada.";
        Score score = new Score(homeGoals, awayGoals);
        match.registerResult(score);
        // Avalia todas as apostas desta partida
        bets.stream()
                .filter(b -> b.getMatch() == match && !b.isEvaluated())
                .forEach(Bet::evaluate);
        return "Resultado registrado e pontuações calculadas.";
    }

    // ──────────── GRUPOS ────────────

    public String addGroup(String name) {
        if (groups.size() >= MAX_GROUPS) return "Limite de grupos atingido.";
        if (groups.stream().anyMatch(g -> g.getName().equalsIgnoreCase(name)))
            return "Já existe um grupo com esse nome.";
        groups.add(new Group(name));
        return "Grupo criado com sucesso.";
    }

    public List<Group> getGroups() { return groups; }

    // ──────────── PARTICIPANTES ────────────

    public String addParticipant(String name, String login, String password) {
        if (participants.size() >= MAX_PARTICIPANTS) return "Limite de participantes atingido.";
        if (participants.stream().anyMatch(p -> p.getLogin().equals(login)))
            return "Login já em uso.";
        if (login.equals(admin.getLogin())) return "Login reservado.";
        participants.add(new Participant(name, login, password));
        return "Participante cadastrado com sucesso.";
    }

    public String joinGroup(Participant p, Group g) {
        if (p.getGroup() != null) return "Participante já está em um grupo.";
        if (g.addParticipant(p)) {
            p.setGroup(g);
            return "Ingressou no grupo '" + g.getName() + "' com sucesso.";
        }
        return "Grupo cheio (máx. " + Group.MAX_PARTICIPANTS + ").";
    }

    public List<Participant> getParticipants() { return participants; }

    // ──────────── APOSTAS ────────────

    public String placeBet(Participant p, Match match, int homeGoals, int awayGoals) {
        if (match.isFinished()) return "Partida já encerrada.";
        if (!match.canBet(LocalDateTime.now()))
            return "Apostas encerradas (menos de 20 min para a partida).";
        // Atualiza aposta existente
        for (Bet b : bets) {
            if (b.getParticipant() == p && b.getMatch() == match) {
                bets.remove(b);
                break;
            }
        }
        bets.add(new Bet(p, match, new Score(homeGoals, awayGoals)));
        return "Aposta registrada com sucesso!";
    }

    public List<Bet> getBetsOf(Participant p) {
        List<Bet> result = new ArrayList<>();
        for (Bet b : bets) {
            if (b.getParticipant() == p) result.add(b);
        }
        return result;
    }

    public List<Bet> getAllBets() { return bets; }

    public Admin getAdmin() { return admin; }

    // ──────────── REMOÇÕES ────────────

    public void removeClub(String acronym) {
        clubs.removeIf(c -> c.getAcronym().equalsIgnoreCase(acronym));
    }

    public void removeChampionship(String name) {
        championships.stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst().ifPresent(champ -> {
                // Remove partidas e apostas associadas
                matches.removeIf(m -> {
                    if (m.getChampionship() == champ) {
                        bets.removeIf(b -> b.getMatch() == m);
                        return true;
                    }
                    return false;
                });
                championships.remove(champ);
            });
    }

    public void removeMatch(int id) {
        matches.stream().filter(m -> m.getId() == id).findFirst().ifPresent(m -> {
            bets.removeIf(b -> b.getMatch() == m);
            matches.remove(m);
        });
    }

    public void removeGroup(String name) {
        groups.stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().ifPresent(g -> {
            // Desvincula participantes
            g.getParticipants().forEach(part -> part.setGroup(null));
            groups.remove(g);
        });
    }

    public void removeParticipant(String login) {
        participants.stream().filter(p -> p.getLogin().equals(login)).findFirst().ifPresent(p -> {
            // Remove do grupo
            if (p.getGroup() != null) p.getGroup().getParticipants().remove(p);
            // Remove apostas
            bets.removeIf(b -> b.getParticipant() == p);
            participants.remove(p);
        });
    }
}

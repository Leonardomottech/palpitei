package service;

import model.*;
import database.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço central do sistema com integração ao banco de dados SQLite.
 * Centraliza regras de negócio e gerencia persistência de dados.
 */
public class BettingService {
    public static final int MAX_GROUPS = 5;
    public static final int MAX_PARTICIPANTS = 5;

    // Banco de dados
    private DatabaseManager db;
    
    // Data Access Objects
    private PersonDAO personDAO;
    private ParticipantDAO participantDAO;
    private ClubDAO clubDAO;
    private ChampionshipDAO championshipDAO;
    private MatchDAO matchDAO;
    private BetDAO betDAO;

    // Cache em memória (para operações rápidas)
    private Admin admin;
    private List<Championship> championships;
    private List<Club> clubs;
    private List<Match> matches;
    private List<Group> groups;
    private List<Participant> participants;
    private List<Bet> bets;

    public BettingService() {
        // Inicializa banco de dados
        db = new DatabaseManager();
        
        // Inicializa DAOs
        personDAO = new PersonDAO(db);
        clubDAO = new ClubDAO(db);
        championshipDAO = new ChampionshipDAO(db);
        participantDAO = new ParticipantDAO(db);
        matchDAO = new MatchDAO(db, clubDAO, championshipDAO);
        betDAO = new BetDAO(db, participantDAO, matchDAO);
        
        // Inicializa estruturas em memória
        championships = new ArrayList<>();
        clubs = new ArrayList<>();
        matches = new ArrayList<>();
        groups = new ArrayList<>();
        participants = new ArrayList<>();
        bets = new ArrayList<>();
        
        // Admin padrão
        admin = new Admin("Administrador", "admin", "admin123");
        
        // Carrega dados do banco de dados
        loadDataFromDatabase();
    }

    /**
     * Carrega dados persistidos do banco de dados
     */
    private void loadDataFromDatabase() {
        try {
            System.out.println("✓ Carregando dados do banco de dados...");
            
            // Carrega clubes
            clubs.addAll(clubDAO.findAll());
            
            // Carrega campeonatos
            championships.addAll(championshipDAO.findAll());
            
            // Carrega participantes
            participants.addAll(participantDAO.findAll());
            
            // Carrega partidas
            matches.addAll(matchDAO.findAll());
            
            // Carrega apostas
            bets.addAll(betDAO.findAll());
            
            System.out.println("✓ Dados carregados com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados do banco de dados!");
            e.printStackTrace();
        }
    }

    // ──────────── AUTENTICAÇÃO ────────────

    public Person login(String login, String password) {
        // Tenta login do admin
        if (admin.getLogin().equals(login) && admin.getPassword().equals(password)) {
            return admin;
        }
        
        // Tenta login dos participantes
        for (Participant p : participants) {
            if (p.getLogin().equals(login) && p.getPassword().equals(password)) {
                return p;
            }
        }
        return null;
    }

    // ──────────── CLUBES ────────────

    public boolean addClub(String name, String country) {
        try {
            Club club = new Club(name, country);
            int id = clubDAO.save(club);
            
            if (id > 0) {
                club.setId(id);
                clubs.add(club);
                System.out.println("✓ Clube salvo no banco de dados!");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Club> getClubs() {
        return clubs;
    }

    public Club findClub(String name) {
        return clubs.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    // ──────────── CAMPEONATOS ────────────

    public String addChampionship(String name, int year) {
        try {
            // Verifica se já existe
            if (championshipDAO.findByName(name) != null) {
                return "Campeonato com esse nome já existe.";
            }
            
            Championship championship = new Championship(name, year);
            int id = championshipDAO.save(championship);
            
            if (id > 0) {
                championship.setId(id);
                championships.add(championship);
                System.out.println("✓ Campeonato salvo no banco de dados!");
                return "Campeonato cadastrado com sucesso.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao cadastrar campeonato.";
    }

    public String addClubToChampionship(Championship c, Club club) {
        if (c.addClub(club)) {
            System.out.println("✓ Clube adicionado ao campeonato!");
            return "Clube adicionado ao campeonato.";
        }
        return "Limite de " + Championship.MAX_CLUBS + " clubes atingido.";
    }

    public List<Championship> getChampionships() {
        return championships;
    }

    // ──────────── PARTIDAS ────────────

    public String addMatch(Championship champ, Club home, Club away, LocalDateTime dt) {
        if (home.getName().equals(away.getName()))
            return "Um clube não pode jogar contra si mesmo.";
        
        try {
            Match match = new Match(champ, home, away, dt);
            int id = matchDAO.save(match, champ.getId());
            
            if (id > 0) {
                match.setId(id);
                matches.add(match);
                System.out.println("✓ Partida salva no banco de dados!");
                return "Partida registrada com sucesso.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao registrar partida.";
    }

    public List<Match> getMatches() {
        return matches;
    }

    public String registerResult(Match match, int homeGoals, int awayGoals) {
        if (match.isFinished()) return "Partida já encerrada.";
        
        try {
            Score score = new Score(homeGoals, awayGoals);
            match.registerResult(score);
            
            // Atualiza status no banco
            matchDAO.updateStatus(match.getId(), "FINALIZADO");
            
            // Avalia todas as apostas desta partida
            bets.stream()
                    .filter(b -> b.getMatch() == match && !b.isEvaluated())
                    .forEach(Bet::evaluate);
            
            System.out.println("✓ Resultado registrado e pontuações calculadas!");
            return "Resultado registrado e pontuações calculadas.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao registrar resultado.";
        }
    }

    // ──────────── GRUPOS ────────────

    public String addGroup(String name) {
        if (groups.size() >= MAX_GROUPS) return "Limite de grupos atingido.";
        if (groups.stream().anyMatch(g -> g.getName().equalsIgnoreCase(name)))
            return "Já existe um grupo com esse nome.";
        
        groups.add(new Group(name));
        System.out.println("✓ Grupo criado com sucesso!");
        return "Grupo criado com sucesso.";
    }

    public List<Group> getGroups() {
        return groups;
    }

    // ──────────── PARTICIPANTES ────────────

    public String addParticipant(String name, String login, String password) {
        if (participants.size() >= MAX_PARTICIPANTS) 
            return "Limite de participantes atingido.";
        
        if (participants.stream().anyMatch(p -> p.getLogin().equals(login)))
            return "Login já em uso.";
        
        if (login.equals(admin.getLogin())) 
            return "Login reservado.";
        
        try {
            Participant participant = new Participant(name, login, password);
            
            // Salva como pessoa no BD
            if (!personDAO.save(participant)) {
                return "Erro ao salvar participante no banco de dados.";
            }
            
            // Salva como participante
            if (!participantDAO.save(participant)) {
                return "Erro ao salvar participante.";
            }
            
            participants.add(participant);
            System.out.println("✓ Participante salvo no banco de dados!");
            return "Participante cadastrado com sucesso.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao cadastrar participante.";
        }
    }

    public String joinGroup(Participant p, Group g) {
        if (p.getGroup() != null) return "Participante já está em um grupo.";
        
        if (g.addParticipant(p)) {
            p.setGroup(g);
            System.out.println("✓ Participante ingressou no grupo!");
            return "Ingressou no grupo '" + g.getName() + "' com sucesso.";
        }
        return "Grupo cheio (máx. " + Group.MAX_PARTICIPANTS + ").";
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    // ──────────── APOSTAS ────────────

    public String placeBet(Participant p, Match match, int homeGoals, int awayGoals) {
        if (match.isFinished()) return "Partida já encerrada.";
        
        if (!match.canBet(LocalDateTime.now()))
            return "Apostas encerradas (menos de 20 min para a partida).";
        
        try {
            // Remove aposta anterior se existir
            for (Bet b : bets) {
                if (b.getParticipant() == p && b.getMatch() == match) {
                    betDAO.delete(b.getId());
                    bets.remove(b);
                    break;
                }
            }
            
            // Cria e salva nova aposta
            Bet bet = new Bet(p, match, new Score(homeGoals, awayGoals));
            int id = betDAO.save(bet);
            
            if (id > 0) {
                bet.setId(id);
                bets.add(bet);
                System.out.println("✓ Aposta salva no banco de dados!");
                return "Aposta registrada com sucesso!";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao registrar aposta.";
    }

    public List<Bet> getBetsOf(Participant p) {
        List<Bet> result = new ArrayList<>();
        for (Bet b : bets) {
            if (b.getParticipant() == p) result.add(b);
        }
        return result;
    }

    public List<Bet> getAllBets() {
        return bets;
    }

    public Admin getAdmin() {
        return admin;
    }

    // ──────────── REMOÇÕES ────────────

    public void removeClub(String name) {
        clubs.stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst()
            .ifPresent(club -> {
                clubDAO.delete(club.getId());
                clubs.remove(club);
                System.out.println("✓ Clube removido do banco de dados!");
            });
    }

    public void removeChampionship(String name) {
        championships.stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst()
            .ifPresent(champ -> {
                // Remove partidas e apostas associadas
                matches.removeIf(m -> {
                    if (m.getChampionship() == champ) {
                        bets.removeIf(b -> {
                            betDAO.delete(b.getId());
                            return b.getMatch() == m;
                        });
                        matchDAO.delete(m.getId());
                        return true;
                    }
                    return false;
                });
                
                championshipDAO.delete(champ.getId());
                championships.remove(champ);
                System.out.println("✓ Campeonato removido do banco de dados!");
            });
    }

    public void removeMatch(int id) {
        matches.stream()
            .filter(m -> m.getId() == id)
            .findFirst()
            .ifPresent(m -> {
                bets.removeIf(b -> {
                    if (b.getMatch() == m) {
                        betDAO.delete(b.getId());
                        return true;
                    }
                    return false;
                });
                matchDAO.delete(m.getId());
                matches.remove(m);
                System.out.println("✓ Partida removida do banco de dados!");
            });
    }

    public void removeGroup(String name) {
        groups.stream()
            .filter(g -> g.getName().equalsIgnoreCase(name))
            .findFirst()
            .ifPresent(g -> {
                g.getParticipants().forEach(part -> part.setGroup(null));
                groups.remove(g);
                System.out.println("✓ Grupo removido!");
            });
    }

    public void removeParticipant(String login) {
        participants.stream()
            .filter(p -> p.getLogin().equals(login))
            .findFirst()
            .ifPresent(p -> {
                if (p.getGroup() != null) 
                    p.getGroup().getParticipants().remove(p);
                
                bets.removeIf(b -> {
                    if (b.getParticipant() == p) {
                        betDAO.delete(b.getId());
                        return true;
                    }
                    return false;
                });
                
                personDAO.delete(p.getId());
                participantDAO.delete(p.getId());
                participants.remove(p);
                System.out.println("✓ Participante removido do banco de dados!");
            });
    }

    /**
     * Fecha a conexão com o banco de dados
     */
    public void closeDatabase() {
        if (db != null) {
            db.close();
            System.out.println("✓ Conexão com banco de dados fechada!");
        }
    }
}

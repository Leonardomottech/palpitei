package database;

import model.Bet;
import model.Participant;
import model.Match;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Apostas
 */
public class BetDAO {
    private DatabaseManager db;
    private ParticipantDAO participantDAO;
    private MatchDAO matchDAO;

    public BetDAO(DatabaseManager db, ParticipantDAO participantDAO, MatchDAO matchDAO) {
        this.db = db;
        this.participantDAO = participantDAO;
        this.matchDAO = matchDAO;
    }

    /**
     * Insere uma nova aposta
     */
    public int save(Bet bet) {
        try {
            String sql = "INSERT INTO bet (participant_id, match_id, predicted_result, points_earned) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, bet.getParticipant().getId());
            stmt.setInt(2, bet.getMatch().getId());
            stmt.setString(3, getPredictedResult(bet));
            stmt.setInt(4, 0);
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    rs.close();
                    stmt.close();
                    return id;
                }
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Busca uma aposta pelo ID
     */
    public Bet findById(int id) {
        try {
            String sql = "SELECT * FROM bet WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int participantId = rs.getInt("participant_id");
                int matchId = rs.getInt("match_id");
                int pointsEarned = rs.getInt("points_earned");
                
                Participant participant = participantDAO.findById(participantId);
                Match match = matchDAO.findById(matchId);
                
                Bet bet = new Bet(participant, match, null);
                
                rs.close();
                stmt.close();
                return bet;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todas as apostas de um participante
     */
    public List<Bet> findByParticipant(int participantId) {
        List<Bet> bets = new ArrayList<>();
        try {
            String sql = "SELECT * FROM bet WHERE participant_id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int betId = rs.getInt("id");
                int matchId = rs.getInt("match_id");
                
                Participant participant = participantDAO.findById(participantId);
                Match match = matchDAO.findById(matchId);
                
                Bet bet = new Bet(participant, match, null);
                bets.add(bet);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bets;
    }

    /**
     * Lista todas as apostas
     */
    public List<Bet> findAll() {
        List<Bet> bets = new ArrayList<>();
        try {
            String sql = "SELECT * FROM bet";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int participantId = rs.getInt("participant_id");
                int matchId = rs.getInt("match_id");
                
                Participant participant = participantDAO.findById(participantId);
                Match match = matchDAO.findById(matchId);
                
                Bet bet = new Bet(participant, match, null);
                bets.add(bet);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bets;
    }

    /**
     * Atualiza pontos ganhos em uma aposta
     */
    public boolean updatePoints(int betId, int pointsEarned) {
        try {
            String sql = "UPDATE bet SET points_earned = ? WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, pointsEarned);
            stmt.setInt(2, betId);
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deleta uma aposta
     */
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM bet WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper: converte Score da aposta em texto
     */
    private String getPredictedResult(Bet bet) {
        if (bet.getScore() == null) return "UNKNOWN";
        int homeGoals = bet.getScore().getHomeGoals();
        int awayGoals = bet.getScore().getAwayGoals();
        
        if (homeGoals > awayGoals) return "WIN";
        if (homeGoals < awayGoals) return "LOSS";
        return "DRAW";
    }
}

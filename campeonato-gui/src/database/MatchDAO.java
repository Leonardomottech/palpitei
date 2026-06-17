package database;

import model.Match;
import model.Club;
import model.Championship;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Partidas
 */
public class MatchDAO {
    private DatabaseManager db;
    private ClubDAO clubDAO;
    private ChampionshipDAO championshipDAO;

    public MatchDAO(DatabaseManager db, ClubDAO clubDAO, ChampionshipDAO championshipDAO) {
        this.db = db;
        this.clubDAO = clubDAO;
        this.championshipDAO = championshipDAO;
    }

    /**
     * Insere uma nova partida
     */
    public int save(Match match, int championshipId) {
        try {
            String sql = "INSERT INTO match_table (group_id, home_club_id, away_club_id, status) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, 1); // grupo padrão por enquanto
            stmt.setInt(2, match.getHomeClub().getId());
            stmt.setInt(3, match.getAwayClub().getId());
            stmt.setString(4, match.isFinished() ? "FINALIZADO" : "PENDENTE");
            
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
     * Busca uma partida pelo ID
     */
    public Match findById(int id) {
        try {
            String sql = "SELECT * FROM match_table WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int homeClubId = rs.getInt("home_club_id");
                int awayClubId = rs.getInt("away_club_id");
                String status = rs.getString("status");
                
                Club homeClub = clubDAO.findById(homeClubId);
                Club awayClub = clubDAO.findById(awayClubId);
                
                Match match = new Match(null, homeClub, awayClub, LocalDateTime.now());
                match.setId(id);
                
                rs.close();
                stmt.close();
                return match;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todas as partidas
     */
    public List<Match> findAll() {
        List<Match> matches = new ArrayList<>();
        try {
            String sql = "SELECT * FROM match_table";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int homeClubId = rs.getInt("home_club_id");
                int awayClubId = rs.getInt("away_club_id");
                
                Club homeClub = clubDAO.findById(homeClubId);
                Club awayClub = clubDAO.findById(awayClubId);
                
                Match match = new Match(null, homeClub, awayClub, LocalDateTime.now());
                match.setId(id);
                matches.add(match);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    /**
     * Atualiza o status de uma partida
     */
    public boolean updateStatus(int matchId, String status) {
        try {
            String sql = "UPDATE match_table SET status = ? WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, matchId);
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deleta uma partida
     */
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM match_table WHERE id = ?";
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
}

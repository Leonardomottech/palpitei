package database;

import model.Person;
import model.Admin;
import model.Participant;
import java.sql.*;

/**
 * Data Access Object para Pessoas (usuários)
 */
public class PersonDAO {
    private DatabaseManager db;

    public PersonDAO(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Insere uma nova pessoa no banco de dados
     */
    public boolean save(Person person) {
        try {
            String sql = "INSERT INTO person (name, email, password, type) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, person.getName());
            stmt.setString(2, person.getEmail());
            stmt.setString(3, person.getPassword());
            stmt.setString(4, person.getClass().getSimpleName().toUpperCase());
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca uma pessoa pelo email
     */
    public Person findByEmail(String email) {
        try {
            String sql = "SELECT * FROM person WHERE email = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                String type = rs.getString("type");
                
                Person person = type.equals("ADMIN") ? 
                    new Admin(id, name, email, password) :
                    new Participant(id, name, email, password);
                
                rs.close();
                stmt.close();
                return person;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verifica se o email e senha estão corretos
     */
    public Person authenticate(String email, String password) {
        Person person = findByEmail(email);
        if (person != null && person.getPassword().equals(password)) {
            return person;
        }
        return null;
    }

    /**
     * Busca uma pessoa pelo ID
     */
    public Person findById(int id) {
        try {
            String sql = "SELECT * FROM person WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String type = rs.getString("type");
                
                Person person = type.equals("ADMIN") ? 
                    new Admin(id, name, email, password) :
                    new Participant(id, name, email, password);
                
                rs.close();
                stmt.close();
                return person;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Atualiza informações de uma pessoa
     */
    public boolean update(Person person) {
        try {
            String sql = "UPDATE person SET name = ?, email = ?, password = ? WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, person.getName());
            stmt.setString(2, person.getEmail());
            stmt.setString(3, person.getPassword());
            stmt.setInt(4, person.getId());
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deleta uma pessoa do banco de dados
     */
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM person WHERE id = ?";
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

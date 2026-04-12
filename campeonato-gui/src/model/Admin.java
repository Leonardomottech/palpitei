package model;

/**
 * Administrador do sistema. Herda de Person.
 * Responsável por registrar resultados das partidas.
 */
public class Admin extends Person {

    public Admin() {
        super();
    }

    public Admin(String name, String login, String password) {
        super(name, login, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}

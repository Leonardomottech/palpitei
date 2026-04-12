package model;

/**
 * Classe abstrata base representando uma pessoa no sistema.
 * Aplica encapsulamento e serve de base para herança.
 */
public abstract class Person {
    private String name;
    private String login;
    private String password;

    // Construtor padrão
    public Person() {
        this.name = "";
        this.login = "";
        this.password = "";
    }

    // Construtor sobrecarregado
    public Person(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    // Método abstrato para polimorfismo por sobreposição
    public abstract String getRole();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Sobrecarga de toString
    @Override
    public String toString() {
        return String.format("[%s] %s (login: %s)", getRole(), name, login);
    }
}

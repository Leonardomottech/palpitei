package model;

/**
 * Representa um clube (time) de futebol.
 */
public class Club {
    private String name;
    private String acronym;

    // Construtor padrão
    public Club() {
        this.name = "";
        this.acronym = "";
    }

    // Construtor sobrecarregado com sigla
    public Club(String name, String acronym) {
        this.name = name;
        this.acronym = acronym.toUpperCase();
    }

    // Construtor sobrecarregado apenas com nome
    public Club(String name) {
        this.name = name;
        this.acronym = name.length() >= 3 ? name.substring(0, 3).toUpperCase() : name.toUpperCase();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAcronym() { return acronym; }
    public void setAcronym(String acronym) { this.acronym = acronym; }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, acronym);
    }
}

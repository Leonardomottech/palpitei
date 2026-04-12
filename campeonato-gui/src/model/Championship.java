package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um campeonato de futebol com no máximo 8 clubes.
 */
public class Championship {
    public static final int MAX_CLUBS = 8;

    private String name;
    private int year;
    private List<Club> clubs;

    // Construtor padrão
    public Championship() {
        this.clubs = new ArrayList<>();
    }

    // Construtor sobrecarregado
    public Championship(String name, int year) {
        this.name = name;
        this.year = year;
        this.clubs = new ArrayList<>();
    }

    public boolean addClub(Club club) {
        if (clubs.size() >= MAX_CLUBS) {
            return false;
        }
        clubs.add(club);
        return true;
    }

    public List<Club> getClubs() { return clubs; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return String.format("%s (%d) - %d clube(s)", name, year, clubs.size());
    }
}

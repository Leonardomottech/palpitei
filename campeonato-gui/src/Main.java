import ui.LoginFrame;
import service.BettingService;
import javax.swing.*;

/**
 * Ponto de entrada da aplicação com interface gráfica (Swing).
 *
 * Compilação:
 *   javac -d out src/model/*.java src/service/*.java src/ui/*.java src/Main.java
 * Execução:
 *   java -cp out Main
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BettingService service = new BettingService();
            new LoginFrame(service).setVisible(true);
        });
    }
}

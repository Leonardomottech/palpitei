package ui;

import model.*;
import service.BettingService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final BettingService service;
    private JTextField fieldLogin;
    private JPasswordField fieldPass;

    public LoginFrame(BettingService service) {
        this.service = service;
        setTitle("Palpitei! — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);

        // ── Header ──
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Theme.BG_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(44, 30, 24, 30));

        JLabel emoji = Theme.makeLabel("🎯", new Font("Segoe UI Emoji", Font.PLAIN, 54), Theme.ACCENT_GREEN);
        emoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = Theme.makeLabel("Palpitei!", Theme.FONT_TITLE, Theme.TEXT_PRIMARY);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = Theme.makeLabel("Sistema de Apostas em Futebol", Theme.FONT_BODY, Theme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(emoji);
        header.add(Box.createVerticalStrut(10));
        header.add(brand);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        // ── Form ──
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.BG_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(28, 32, 28, 32)));

        fieldLogin = Theme.makeField();
        fieldLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        fieldPass  = Theme.makePasswordField();
        fieldPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        form.add(Theme.fieldLabel("Login"));
        form.add(Box.createVerticalStrut(6));
        form.add(fieldLogin);
        form.add(Box.createVerticalStrut(14));
        form.add(Theme.fieldLabel("Senha"));
        form.add(Box.createVerticalStrut(6));
        form.add(fieldPass);
        form.add(Box.createVerticalStrut(22));

        JButton btnLogin = Theme.makeButton("ENTRAR", Theme.ACCENT_GREEN);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> doLogin());
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(10));

        JButton btnReg = Theme.makeButton("CRIAR CONTA", Theme.ACCENT_BLUE);
        btnReg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReg.addActionListener(e -> showRegister());
        form.add(btnReg);
        form.add(Box.createVerticalStrut(16));

        JLabel hint = Theme.makeLabel("Admin padrão: admin / admin123", Theme.FONT_SMALL, Theme.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(hint);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(btnLogin);
    }

    private void doLogin() {
        String login = fieldLogin.getText().trim();
        String pass  = new String(fieldPass.getPassword());
        if (login.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha login e senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Person p = service.login(login, pass);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Login ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
        if (p instanceof Admin) {
            new AdminFrame(service).setVisible(true);
        } else {
            new ParticipantFrame(service, (Participant) p).setVisible(true);
        }
    }

    private void showRegister() {
        JDialog d = new JDialog(this, "Criar conta — Palpitei!", true);
        d.setSize(380, 320);
        d.setLocationRelativeTo(this);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        JLabel title = Theme.makeLabel("Nova conta", Theme.FONT_BRAND, Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(title); p.add(Box.createVerticalStrut(18));

        JTextField fName  = Theme.makeField(); fName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextField fLogin = Theme.makeField(); fLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JPasswordField fPass = Theme.makePasswordField(); fPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        p.add(Theme.fieldLabel("Nome completo")); p.add(Box.createVerticalStrut(5)); p.add(fName);
        p.add(Box.createVerticalStrut(10));
        p.add(Theme.fieldLabel("Login")); p.add(Box.createVerticalStrut(5)); p.add(fLogin);
        p.add(Box.createVerticalStrut(10));
        p.add(Theme.fieldLabel("Senha")); p.add(Box.createVerticalStrut(5)); p.add(fPass);
        p.add(Box.createVerticalStrut(18));

        JButton btn = Theme.makeButton("CADASTRAR", Theme.ACCENT_GREEN);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.addActionListener(e -> {
            String name  = fName.getText().trim();
            String login = fLogin.getText().trim();
            String pass  = new String(fPass.getPassword());
            if (name.isEmpty() || login.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Preencha todos os campos.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String msg = service.addParticipant(name, login, pass);
            JOptionPane.showMessageDialog(d, msg);
            if (msg.contains("sucesso")) d.dispose();
        });
        p.add(btn);
        d.setContentPane(p);
        d.setVisible(true);
    }
}

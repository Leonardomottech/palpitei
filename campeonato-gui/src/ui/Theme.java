package ui;

import java.awt.*;
import javax.swing.*;

public class Theme {
    // Paleta principal
    public static final Color BG_DARK      = new Color(13, 17, 27);
    public static final Color BG_PANEL     = new Color(22, 27, 42);
    public static final Color BG_CARD      = new Color(30, 37, 56);
    public static final Color BG_HOVER     = new Color(40, 50, 75);
    public static final Color ACCENT_GREEN = new Color(34, 197, 94);
    public static final Color ACCENT_BLUE  = new Color(59, 130, 246);
    public static final Color ACCENT_RED   = new Color(239, 68, 68);
    public static final Color ACCENT_GOLD  = new Color(251, 191, 36);
    public static final Color ACCENT_PURPLE= new Color(139, 92, 246);
    public static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    public static final Color TEXT_MUTED   = new Color(148, 163, 184);
    public static final Color BORDER       = new Color(51, 65, 85);

    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_BRAND  = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUB    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    public static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_SUB);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JButton makeIconButton(String text, Color bg) {
        JButton btn = makeButton(text, bg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return btn;
    }

    public static JTextField makeField() {
        JTextField f = new JTextField();
        f.setBackground(BG_DARK);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return f;
    }

    public static JPasswordField makePasswordField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG_DARK);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return f;
    }

    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JPanel makeCard() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        return p;
    }

    public static JScrollPane makeScrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getViewport().setBackground(BG_CARD);
        return sp;
    }

    public static JTable makeTable(javax.swing.table.DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(BG_CARD);
        t.setForeground(TEXT_PRIMARY);
        t.setGridColor(BORDER);
        t.setFont(FONT_BODY);
        t.setRowHeight(30);
        t.setSelectionBackground(ACCENT_BLUE);
        t.setSelectionForeground(Color.WHITE);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.getTableHeader().setBackground(BG_PANEL);
        t.getTableHeader().setForeground(ACCENT_GOLD);
        t.getTableHeader().setFont(FONT_SUB);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        return t;
    }

    public static JLabel sectionTitle(String text) {
        JLabel l = makeLabel(text, FONT_SUB, ACCENT_GOLD);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return l;
    }

    public static JLabel fieldLabel(String text) {
        return makeLabel(text, FONT_BODY, TEXT_MUTED);
    }

    // Stat card (para o perfil)
    public static JPanel makeStatCard(String value, String label, Color color) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0;
        JLabel val = makeLabel(value, new Font("Segoe UI", Font.BOLD, 28), color);
        val.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(val, g);
        g.gridy = 1; g.insets = new Insets(4, 0, 0, 0);
        JLabel lbl = makeLabel(label, FONT_SMALL, TEXT_MUTED);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lbl, g);
        return card;
    }
}

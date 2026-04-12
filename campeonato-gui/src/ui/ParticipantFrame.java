package ui;

import model.*;
import service.BettingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ParticipantFrame extends JFrame {

    private final BettingService service;
    private final Participant participant;

    public ParticipantFrame(BettingService service, Participant participant) {
        this.service = service;
        this.participant = participant;
        setTitle("Palpitei! — " + participant.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 650);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);
        root.add(buildSidebar(), BorderLayout.WEST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Theme.BG_PANEL);
        tabs.setForeground(Theme.TEXT_PRIMARY);
        tabs.setFont(Theme.FONT_SUB);
        tabs.addTab("👤  Perfil",        buildProfileTab());
        tabs.addTab("🎯  Meus Palpites", buildBetsTab());
        tabs.addTab("📅  Partidas",      buildMatchesTab());
        tabs.addTab("👥  Grupos",        buildGroupsTab());
        tabs.addTab("🏆  Ranking",       buildRankingTab());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ─── SIDEBAR ──────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.BG_PANEL);
        p.setPreferredSize(new Dimension(175, 0));
        p.setBorder(BorderFactory.createEmptyBorder(24, 14, 24, 14));

        JLabel emoji = Theme.makeLabel("🎯", new Font("Segoe UI Emoji", Font.PLAIN, 34), Theme.ACCENT_GREEN);
        emoji.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel brand = Theme.makeLabel("Palpitei!", Theme.FONT_BRAND, Theme.TEXT_PRIMARY);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Avatar com inicial do nome
        JPanel avatar = new JPanel(new GridBagLayout());
        avatar.setBackground(Theme.ACCENT_BLUE);
        avatar.setMaximumSize(new Dimension(56, 56));
        avatar.setPreferredSize(new Dimension(56, 56));
        avatar.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_GOLD, 2));
        String inicial = participant.getName().substring(0,1).toUpperCase();
        JLabel avatarLbl = Theme.makeLabel(inicial, new Font("Segoe UI", Font.BOLD, 22), Color.WHITE);
        avatar.add(avatarLbl);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name  = Theme.makeLabel(participant.getName(), Theme.FONT_SUB, Theme.TEXT_PRIMARY);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel role  = Theme.makeLabel("@" + participant.getLogin(), Theme.FONT_SMALL, Theme.TEXT_MUTED);
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel grpLbl = Theme.makeLabel(
            participant.getGroup() != null ? "📍 " + participant.getGroup().getName() : "Sem grupo",
            Theme.FONT_SMALL, Theme.ACCENT_BLUE);
        grpLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pontos em destaque
        JLabel ptsLabel = Theme.makeLabel(participant.getTotalPoints() + " pts", new Font("Segoe UI", Font.BOLD, 18), Theme.ACCENT_GOLD);
        ptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Timer t = new Timer(1000, e -> ptsLabel.setText(participant.getTotalPoints() + " pts"));
        t.start();

        p.add(emoji); p.add(Box.createVerticalStrut(6)); p.add(brand);
        p.add(Box.createVerticalStrut(16)); p.add(avatar);
        p.add(Box.createVerticalStrut(8)); p.add(name); p.add(role);
        p.add(Box.createVerticalStrut(8)); p.add(ptsLabel);
        p.add(Box.createVerticalStrut(4)); p.add(grpLbl);
        p.add(Box.createVerticalGlue());

        JButton logout = Theme.makeButton("Logout", Theme.ACCENT_RED);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logout.addActionListener(e -> { dispose(); new LoginFrame(service).setVisible(true); });
        p.add(logout);
        return p;
    }

    // ─── PERFIL ───────────────────────────────────────────────
    private JPanel buildProfileTab() {
        JPanel p = tab();

        // ── Cabeçalho do perfil ──
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Theme.BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        JPanel avatarBig = new JPanel(new GridBagLayout());
        avatarBig.setBackground(Theme.ACCENT_PURPLE);
        avatarBig.setPreferredSize(new Dimension(72, 72));
        avatarBig.setMaximumSize(new Dimension(72, 72));
        avatarBig.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_GOLD, 2));
        avatarBig.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel avatarTxt = Theme.makeLabel(participant.getName().substring(0,1).toUpperCase(),
            new Font("Segoe UI", Font.BOLD, 30), Color.WHITE);
        avatarBig.add(avatarTxt);

        JLabel nameL  = Theme.makeLabel(participant.getName(), Theme.FONT_TITLE, Theme.TEXT_PRIMARY);
        nameL.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel loginL = Theme.makeLabel("@" + participant.getLogin(), Theme.FONT_BODY, Theme.TEXT_MUTED);
        loginL.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel groupL = Theme.makeLabel(
            participant.getGroup() != null ? "Grupo: " + participant.getGroup().getName() : "Sem grupo",
            Theme.FONT_BODY, Theme.ACCENT_BLUE);
        groupL.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(avatarBig); header.add(Box.createVerticalStrut(10));
        header.add(nameL); header.add(Box.createVerticalStrut(4));
        header.add(loginL); header.add(Box.createVerticalStrut(4));
        header.add(groupL);

        // ── Stats cards ──
        List<Bet> bets = service.getBetsOf(participant);
        long totalBets = bets.size();
        long acertos   = bets.stream().filter(b -> b.isEvaluated() && b.getPointsEarned() > 0).count();
        long placarExato = bets.stream().filter(b -> b.isEvaluated() && b.getPointsEarned() == Bet.POINTS_EXACT).count();
        long pendentes   = bets.stream().filter(b -> !b.isEvaluated()).count();
        int  totalPts    = participant.getTotalPoints();
        String taxaAcerto = totalBets > 0 ? String.format("%.0f%%", (acertos * 100.0) / totalBets) : "—";

        JPanel stats = new JPanel(new GridLayout(2, 3, 10, 10));
        stats.setBackground(Theme.BG_DARK);
        stats.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        stats.add(Theme.makeStatCard(String.valueOf(totalPts),   "Pontos totais",    Theme.ACCENT_GOLD));
        stats.add(Theme.makeStatCard(String.valueOf(acertos),    "Apostas acertadas", Theme.ACCENT_GREEN));
        stats.add(Theme.makeStatCard(taxaAcerto,                 "Taxa de acerto",   Theme.ACCENT_BLUE));
        stats.add(Theme.makeStatCard(String.valueOf(placarExato),"Placares exatos",  Theme.ACCENT_PURPLE));
        stats.add(Theme.makeStatCard(String.valueOf(totalBets),  "Total de apostas", Theme.TEXT_MUTED));
        stats.add(Theme.makeStatCard(String.valueOf(pendentes),  "Aguardando result.", Theme.ACCENT_GOLD));

        // ── Botão excluir conta ──
        JButton btnDelete = Theme.makeButton("🗑 Excluir minha conta", Theme.ACCENT_RED);
        btnDelete.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir sua conta?\nEsta ação não pode ser desfeita.",
                "Excluir conta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                service.removeParticipant(participant.getLogin());
                JOptionPane.showMessageDialog(this, "Conta excluída. Até mais! 👋");
                dispose();
                new LoginFrame(service).setVisible(true);
            }
        });

        JPanel botRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botRow.setBackground(Theme.BG_DARK);
        botRow.add(btnDelete);

        p.add(header, BorderLayout.NORTH);
        p.add(stats, BorderLayout.CENTER);
        p.add(botRow, BorderLayout.SOUTH);
        return p;
    }

    // ─── MEUS PALPITES ────────────────────────────────────────
    private JPanel buildBetsTab() {
        JPanel p = tab();
        DefaultTableModel model = tm(new String[]{"Partida", "Meu Palpite", "Resultado Real", "Pontos"});
        JTable table = Theme.makeTable(model);

        Runnable refresh = () -> {
            model.setRowCount(0);
            service.getBetsOf(participant).forEach(b -> model.addRow(new Object[]{
                b.getMatch().getHomeClub().getAcronym() + " x " + b.getMatch().getAwayClub().getAcronym(),
                b.getPredictedScore(),
                b.getMatch().isFinished() ? b.getMatch().getFinalScore().toString() : "—",
                b.isEvaluated() ? "+" + b.getPointsEarned() + " pts" : "⏳ Pendente"
            }));
        };

        // Form nova aposta
        List<Match> open = service.getMatches().stream().filter(m -> !m.isFinished()).toList();
        JComboBox<String> cbMatch = new JComboBox<>();
        cbMatch.setBackground(Theme.BG_DARK); cbMatch.setForeground(Theme.TEXT_PRIMARY); cbMatch.setFont(Theme.FONT_BODY);
        open.forEach(m -> cbMatch.addItem("#" + m.getId() + " — " + m.getHomeClub().getAcronym() + " x " + m.getAwayClub().getAcronym()));

        JSpinner sHome = new JSpinner(new SpinnerNumberModel(0,0,99,1));
        JSpinner sAway = new JSpinner(new SpinnerNumberModel(0,0,99,1));
        JButton btn = Theme.makeButton("🎯 Registrar Palpite", Theme.ACCENT_GREEN);
        btn.addActionListener(e -> {
            int i = cbMatch.getSelectedIndex();
            if (i < 0 || i >= open.size()) { warn("Selecione uma partida."); return; }
            String msg = service.placeBet(participant, open.get(i), (int) sHome.getValue(), (int) sAway.getValue());
            JOptionPane.showMessageDialog(this, msg);
            refresh.run();
        });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints g = gbc();
        g.gridx=0; g.weightx=0; form.add(fl("Partida:"), g);
        g.gridx=1; g.weightx=1; form.add(cbMatch, g);
        g.gridx=2; g.weightx=0; form.add(fl("Mandante:"), g);
        g.gridx=3; form.add(sHome, g);
        g.gridx=4; form.add(fl("x"), g);
        g.gridx=5; form.add(sAway, g);
        g.gridx=6; form.add(btn, g);

        p.add(Theme.sectionTitle("Meus Palpites"), BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(table), BorderLayout.CENTER);
        p.add(form, BorderLayout.SOUTH);
        refresh.run();
        return p;
    }

    // ─── PARTIDAS ─────────────────────────────────────────────
    private JPanel buildMatchesTab() {
        JPanel p = tab();
        DefaultTableModel model = tm(new String[]{"#", "Campeonato", "Mandante", "Visitante", "Data/Hora", "Status"});
        JTable table = Theme.makeTable(model);
        service.getMatches().forEach(m -> model.addRow(new Object[]{
            m.getId(), m.getChampionship().getName(),
            m.getHomeClub().getAcronym(), m.getAwayClub().getAcronym(),
            m.getDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            m.isFinished() ? "✅ " + m.getFinalScore() : "🕐 Aguardando"
        }));
        p.add(Theme.sectionTitle("Todas as Partidas"), BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // ─── GRUPOS ───────────────────────────────────────────────
    private JPanel buildGroupsTab() {
        JPanel p = tab();
        JPanel top = new JPanel(new GridLayout(1, 2, 14, 0));
        top.setBackground(Theme.BG_DARK);

        // Criar grupo
        JPanel createCard = new JPanel(new GridBagLayout());
        createCard.setBackground(Theme.BG_CARD);
        createCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(16,16,16,16)));
        GridBagConstraints gc = gbc();
        JTextField fName = Theme.makeField();
        JButton btnCreate = Theme.makeButton("+ Criar Grupo", Theme.ACCENT_BLUE);
        btnCreate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnCreate.addActionListener(e -> {
            String msg = service.addGroup(fName.getText().trim());
            JOptionPane.showMessageDialog(this, msg);
            if (msg.contains("sucesso")) {
                Group g = service.getGroups().getLast();
                JOptionPane.showMessageDialog(this, service.joinGroup(participant, g));
            }
            fName.setText("");
        });
        JLabel t1 = Theme.makeLabel("Criar novo grupo", Theme.FONT_SUB, Theme.ACCENT_BLUE);
        gc.gridx=0; gc.gridy=0; gc.gridwidth=2; createCard.add(t1, gc);
        gc.gridy=1; gc.gridwidth=1; gc.weightx=0; createCard.add(fl("Nome:"), gc);
        gc.gridx=1; gc.weightx=1; createCard.add(fName, gc);
        gc.gridx=0; gc.gridy=2; gc.gridwidth=2; createCard.add(btnCreate, gc);

        // Entrar em grupo
        JPanel joinCard = new JPanel(new GridBagLayout());
        joinCard.setBackground(Theme.BG_CARD);
        joinCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(16,16,16,16)));
        GridBagConstraints gj = gbc();
        JComboBox<String> cbGroup = new JComboBox<>();
        cbGroup.setBackground(Theme.BG_DARK); cbGroup.setForeground(Theme.TEXT_PRIMARY); cbGroup.setFont(Theme.FONT_BODY);
        service.getGroups().forEach(g -> cbGroup.addItem(g.getName() + " (" + g.getParticipants().size() + "/" + Group.MAX_PARTICIPANTS + ")"));
        JButton btnJoin = Theme.makeButton("Entrar no Grupo", Theme.ACCENT_GREEN);
        btnJoin.addActionListener(e -> {
            int i = cbGroup.getSelectedIndex();
            if (i < 0) { warn("Selecione um grupo."); return; }
            JOptionPane.showMessageDialog(this, service.joinGroup(participant, service.getGroups().get(i)));
        });
        JLabel t2 = Theme.makeLabel("Entrar em grupo existente", Theme.FONT_SUB, Theme.ACCENT_GREEN);
        gj.gridx=0; gj.gridy=0; gj.gridwidth=2; joinCard.add(t2, gj);
        gj.gridy=1; gj.gridwidth=1; gj.weightx=0; joinCard.add(fl("Grupo:"), gj);
        gj.gridx=1; gj.weightx=1; joinCard.add(cbGroup, gj);
        gj.gridx=0; gj.gridy=2; gj.gridwidth=2; joinCard.add(btnJoin, gj);

        top.add(createCard); top.add(joinCard);
        p.add(Theme.sectionTitle("Grupos de Apostas"), BorderLayout.NORTH);
        p.add(top, BorderLayout.CENTER);
        return p;
    }

    // ─── RANKING ──────────────────────────────────────────────
    private JPanel buildRankingTab() {
        JPanel p = tab();
        if (participant.getGroup() == null) {
            JLabel msg = Theme.makeLabel("Você ainda não está em nenhum grupo. Entre em um grupo para ver o ranking! 👥",
                Theme.FONT_SUB, Theme.TEXT_MUTED);
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            p.add(msg, BorderLayout.CENTER);
            return p;
        }
        Group g = participant.getGroup();
        DefaultTableModel model = tm(new String[]{"Pos.", "Participante", "Login", "Pontos", "Acertos", "Placares Exatos"});
        JTable table = Theme.makeTable(model);
        table.setRowHeight(36);

        Runnable refresh = () -> {
            model.setRowCount(0);
            List<model.Rankable> ranking = g.getRanking();
            for (int i = 0; i < ranking.size(); i++) {
                model.Rankable part = ranking.get(i);
                String medal = switch(i){ case 0->"🥇"; case 1->"🥈"; case 2->"🥉"; default->(i+1)+"."; };
                List<Bet> bets = service.getBetsOf((Participant) part);
                long acertos = bets.stream().filter(b -> b.isEvaluated() && b.getPointsEarned() > 0).count();
                long exatos  = bets.stream().filter(b -> b.isEvaluated() && b.getPointsEarned() == Bet.POINTS_EXACT).count();
                model.addRow(new Object[]{medal, part.getName(), "@" + part.getLogin(), part.getTotalPoints() + " pts", acertos, exatos});
            }
        };

        JButton btnR = Theme.makeButton("🔄 Atualizar", Theme.ACCENT_BLUE);
        btnR.addActionListener(e -> refresh.run());

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setBackground(Theme.BG_DARK);
        titleRow.add(Theme.sectionTitle("🏆 Ranking — " + g.getName() + "   "));
        titleRow.add(btnR);

        p.add(titleRow, BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(table), BorderLayout.CENTER);
        refresh.run();
        return p;
    }

    // ─── HELPERS ──────────────────────────────────────────────
    private JPanel tab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return p;
    }
    private JLabel fl(String t) { return Theme.fieldLabel(t); }
    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }
    private DefaultTableModel tm(String[] cols) {
        return new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
    }
    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE); }
}

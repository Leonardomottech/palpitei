package ui;

import model.*;
import service.BettingService;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminFrame extends JFrame {

    private final BettingService service;
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Combos globais
    private JComboBox<String> cbChampForClub;
    private JComboBox<String> cbClubForChamp;
    private JComboBox<String> cbChampForMatch;
    private JComboBox<String> cbHomeForMatch;
    private JComboBox<String> cbAwayForMatch;

    // Tabelas globais
    private DefaultTableModel modelClubs;
    private DefaultTableModel modelChamp;
    private DefaultTableModel modelMatch;
    private DefaultTableModel modelUsers;
    private DefaultTableModel modelGroups;

    public AdminFrame(BettingService service) {
        this.service = service;
        setTitle("Palpitei! — Administrador");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 700);
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
        tabs.addTab("🏟  Clubes",      buildClubesTab());
        tabs.addTab("🏆  Campeonatos", buildChampionshipsTab());
        tabs.addTab("📅  Partidas",    buildMatchesTab());
        tabs.addTab("✅  Resultados",  buildResultsTab());
        tabs.addTab("👥  Grupos",      buildGroupsTab());
        tabs.addTab("👤  Usuários",    buildUsersTab());
        tabs.addTab("📊  Apostas",     buildBetsTab());

        tabs.addChangeListener((ChangeEvent e) -> refreshAll());
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ─── REFRESH GLOBAL ───────────────────────────────────────
    private void refreshAll() {
        refreshClubs();
        refreshChampCombos();
        refreshChampTable();
        refreshMatchCombos();
        refreshMatchTable();
        refreshUsers();
        refreshGroups();
    }

    private void refreshClubs() {
        if (modelClubs == null) return;
        modelClubs.setRowCount(0);
        service.getClubs().forEach(c -> modelClubs.addRow(new Object[]{c.getName(), c.getAcronym()}));
    }

    private void refreshChampCombos() {
        if (cbChampForClub == null) return;
        int s1 = cbChampForClub.getSelectedIndex();
        cbChampForClub.removeAllItems();
        service.getChampionships().forEach(c -> cbChampForClub.addItem(c.getName() + " (" + c.getYear() + ")"));
        if (s1 >= 0 && s1 < cbChampForClub.getItemCount()) cbChampForClub.setSelectedIndex(s1);
        if (cbClubForChamp == null) return;
        int s2 = cbClubForChamp.getSelectedIndex();
        cbClubForChamp.removeAllItems();
        service.getClubs().forEach(c -> cbClubForChamp.addItem(c.getAcronym() + " - " + c.getName()));
        if (s2 >= 0 && s2 < cbClubForChamp.getItemCount()) cbClubForChamp.setSelectedIndex(s2);
    }

    private void refreshChampTable() {
        if (modelChamp == null) return;
        modelChamp.setRowCount(0);
        service.getChampionships().forEach(c ->
            modelChamp.addRow(new Object[]{c.getName(), c.getYear(), c.getClubs().size() + "/" + Championship.MAX_CLUBS}));
    }

    private void refreshMatchCombos() {
        if (cbChampForMatch == null) return;
        int s = cbChampForMatch.getSelectedIndex();
        cbChampForMatch.removeAllItems();
        service.getChampionships().forEach(c -> cbChampForMatch.addItem(c.getName()));
        if (s >= 0 && s < cbChampForMatch.getItemCount()) cbChampForMatch.setSelectedIndex(s);
        else refreshMatchClubCombos();
    }

    private void refreshMatchTable() {
        if (modelMatch == null) return;
        modelMatch.setRowCount(0);
        service.getMatches().forEach(m -> modelMatch.addRow(new Object[]{
            m.getId(), m.getChampionship().getName(),
            m.getHomeClub().getAcronym(), m.getAwayClub().getAcronym(),
            m.getDateTime().format(DT),
            m.isFinished() ? "✅ " + m.getFinalScore() : "🕐 Aguardando"
        }));
    }

    private void refreshMatchClubCombos() {
        if (cbChampForMatch == null || cbHomeForMatch == null) return;
        int i = cbChampForMatch.getSelectedIndex();
        cbHomeForMatch.removeAllItems(); cbAwayForMatch.removeAllItems();
        if (i >= 0 && i < service.getChampionships().size()) {
            service.getChampionships().get(i).getClubs().forEach(c -> {
                cbHomeForMatch.addItem(c.getAcronym() + " - " + c.getName());
                cbAwayForMatch.addItem(c.getAcronym() + " - " + c.getName());
            });
        }
    }

    private void refreshUsers() {
        if (modelUsers == null) return;
        modelUsers.setRowCount(0);
        service.getParticipants().forEach(p ->
            modelUsers.addRow(new Object[]{
                p.getName(), p.getLogin(),
                p.getGroup() != null ? p.getGroup().getName() : "—",
                p.getTotalPoints() + " pts"
            }));
    }

    private void refreshGroups() {
        if (modelGroups == null) return;
        modelGroups.setRowCount(0);
        service.getGroups().forEach(g ->
            modelGroups.addRow(new Object[]{g.getName(), g.getParticipants().size() + "/" + Group.MAX_PARTICIPANTS}));
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
        JLabel role  = Theme.makeLabel("ADMINISTRADOR", Theme.FONT_SMALL, Theme.ACCENT_GOLD);
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(emoji); p.add(Box.createVerticalStrut(6));
        p.add(brand); p.add(Box.createVerticalStrut(2)); p.add(role);
        p.add(Box.createVerticalGlue());

        JButton logout = Theme.makeButton("Logout", Theme.ACCENT_RED);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logout.addActionListener(e -> { dispose(); new LoginFrame(service).setVisible(true); });
        p.add(logout);
        return p;
    }

    // ─── CLUBES ───────────────────────────────────────────────
    private JPanel buildClubesTab() {
        JPanel p = tab();
        modelClubs = tm(new String[]{"Nome", "Sigla"});
        JTable table = Theme.makeTable(modelClubs);

        // Formulário de adição
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints g = gbc();

        JTextField fName = Theme.makeField();
        JTextField fAcr  = Theme.makeField();
        JButton btnAdd = Theme.makeButton("+ Adicionar", Theme.ACCENT_GREEN);
        JButton btnDel = Theme.makeButton("🗑 Remover Selecionado", Theme.ACCENT_RED);

        btnAdd.addActionListener(e -> {
            String name = fName.getText().trim(), acr = fAcr.getText().trim();
            if (name.isEmpty() || acr.isEmpty()) { warn("Preencha nome e sigla."); return; }
            service.addClub(name, acr);
            fName.setText(""); fAcr.setText("");
            refreshClubs();
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Selecione um clube na tabela."); return; }
            String acr = modelClubs.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remover o clube " + acr + "? Esta ação não pode ser desfeita.", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.removeClub(acr);
                refreshClubs();
            }
        });

        g.gridx=0; g.weightx=0; form.add(fl("Nome:"), g);
        g.gridx=1; g.weightx=1; form.add(fName, g);
        g.gridx=2; g.weightx=0; form.add(fl("Sigla:"), g);
        g.gridx=3; g.weightx=0.3; form.add(fAcr, g);
        g.gridx=4; g.weightx=0; form.add(btnAdd, g);
        g.gridx=5; form.add(btnDel, g);

        p.add(Theme.sectionTitle("Clubes Cadastrados"), BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(table), BorderLayout.CENTER);
        p.add(form, BorderLayout.SOUTH);
        refreshClubs();
        return p;
    }

    // ─── CAMPEONATOS ──────────────────────────────────────────
    private JPanel buildChampionshipsTab() {
        JPanel p = tab();
        modelChamp = tm(new String[]{"Campeonato", "Ano", "Clubes"});
        JTable tableC = Theme.makeTable(modelChamp);

        cbChampForClub = combo(); cbClubForChamp = combo();

        JPanel bottom = new JPanel(new GridLayout(1, 2, 12, 0));
        bottom.setBackground(Theme.BG_DARK);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Card criar
        JPanel createCard = card();
        GridBagConstraints gc = gbc();
        JTextField fName = Theme.makeField(); JTextField fYear = Theme.makeField();
        JButton btnCreate = Theme.makeButton("Criar Campeonato", Theme.ACCENT_GREEN);
        JButton btnDel    = Theme.makeButton("🗑 Remover Selecionado", Theme.ACCENT_RED);

        btnCreate.addActionListener(e -> {
            try {
                String name = fName.getText().trim();
                if (name.isEmpty()) { warn("Digite o nome."); return; }
                service.addChampionship(name, Integer.parseInt(fYear.getText().trim()));
                fName.setText(""); fYear.setText("");
                refreshAll();
            } catch (NumberFormatException ex) { warn("Ano inválido."); }
        });

        btnDel.addActionListener(e -> {
            int row = tableC.getSelectedRow();
            if (row < 0) { warn("Selecione um campeonato na tabela."); return; }
            String nome = modelChamp.getValueAt(row, 0).toString();
            int c = JOptionPane.showConfirmDialog(this,
                "Remover o campeonato \"" + nome + "\" e todas as suas partidas?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { service.removeChampionship(nome); refreshAll(); }
        });

        gc.gridx=0; gc.gridy=0; gc.weightx=0; createCard.add(fl("Nome:"), gc);
        gc.gridx=1; gc.weightx=1; createCard.add(fName, gc);
        gc.gridx=0; gc.gridy=1; gc.weightx=0; createCard.add(fl("Ano:"), gc);
        gc.gridx=1; gc.weightx=1; createCard.add(fYear, gc);
        gc.gridx=0; gc.gridy=2; gc.gridwidth=2;
        JPanel btns = new JPanel(new GridLayout(1,2,6,0)); btns.setBackground(Theme.BG_CARD);
        btns.add(btnCreate); btns.add(btnDel);
        createCard.add(btns, gc);

        // Card adicionar clube
        JPanel addCard = card();
        GridBagConstraints ga = gbc();
        JButton btnAdd = Theme.makeButton("Adicionar Clube ao Campeonato", Theme.ACCENT_BLUE);
        btnAdd.addActionListener(e -> {
            int ci = cbChampForClub.getSelectedIndex(), li = cbClubForChamp.getSelectedIndex();
            if (ci < 0 || li < 0) { warn("Selecione campeonato e clube.\nCadastre clubes primeiro na aba 'Clubes'."); return; }
            String msg = service.addClubToChampionship(
                service.getChampionships().get(ci), service.getClubs().get(li));
            JOptionPane.showMessageDialog(this, msg);
            refreshAll();
        });
        ga.gridx=0; ga.gridy=0; ga.weightx=0; addCard.add(fl("Campeonato:"), ga);
        ga.gridx=1; ga.weightx=1; addCard.add(cbChampForClub, ga);
        ga.gridx=0; ga.gridy=1; ga.weightx=0; addCard.add(fl("Clube:"), ga);
        ga.gridx=1; ga.weightx=1; addCard.add(cbClubForChamp, ga);
        ga.gridx=0; ga.gridy=2; ga.gridwidth=2; addCard.add(btnAdd, ga);

        bottom.add(createCard); bottom.add(addCard);
        p.add(Theme.sectionTitle("Campeonatos"), BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(tableC), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        refreshAll();
        return p;
    }

    // ─── PARTIDAS ─────────────────────────────────────────────
    private JPanel buildMatchesTab() {
        JPanel p = tab();
        modelMatch = tm(new String[]{"#", "Campeonato", "Mandante", "Visitante", "Data/Hora", "Status"});
        JTable table = Theme.makeTable(modelMatch);

        cbChampForMatch = combo(); cbHomeForMatch = combo(); cbAwayForMatch = combo();
        cbChampForMatch.addActionListener(e -> refreshMatchClubCombos());

        JTextField fDt = Theme.makeField(); fDt.setToolTipText("Ex: 25/05/2025 20:00");
        JButton btnAdd = Theme.makeButton("+ Cadastrar", Theme.ACCENT_GREEN);
        JButton btnDel = Theme.makeButton("🗑 Remover Selecionada", Theme.ACCENT_RED);

        btnAdd.addActionListener(e -> {
            int ci = cbChampForMatch.getSelectedIndex();
            if (ci < 0) { warn("Selecione um campeonato."); return; }
            if (cbHomeForMatch.getItemCount() == 0) { warn("O campeonato não tem clubes."); return; }
            try {
                LocalDateTime dt = LocalDateTime.parse(fDt.getText().trim(), DT);
                Championship champ = service.getChampionships().get(ci);
                String homeAcr = cbHomeForMatch.getSelectedItem().toString().split(" - ")[0];
                String awayAcr = cbAwayForMatch.getSelectedItem().toString().split(" - ")[0];
                Club home = service.findClub(homeAcr), away = service.findClub(awayAcr);
                JOptionPane.showMessageDialog(this, service.addMatch(champ, home, away, dt));
                fDt.setText(""); refreshAll();
            } catch (DateTimeParseException ex) { warn("Data inválida! Use: dd/MM/yyyy HH:mm\nEx: 25/05/2025 20:00"); }
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Selecione uma partida na tabela."); return; }
            int id = (int) modelMatch.getValueAt(row, 0);
            int c = JOptionPane.showConfirmDialog(this, "Remover partida #" + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { service.removeMatch(id); refreshAll(); }
        });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints g = gbc();

        g.gridx=0; g.gridy=0; g.weightx=0; form.add(fl("Campeonato:"), g);
        g.gridx=1; g.weightx=1; form.add(cbChampForMatch, g);
        g.gridx=2; g.weightx=0; form.add(fl("Mandante:"), g);
        g.gridx=3; g.weightx=1; form.add(cbHomeForMatch, g);
        g.gridx=4; g.weightx=0; form.add(fl("Visitante:"), g);
        g.gridx=5; g.weightx=1; form.add(cbAwayForMatch, g);

        g.gridx=0; g.gridy=1; g.weightx=0; g.gridwidth=1; form.add(fl("Data/Hora (dd/MM/yyyy HH:mm):"), g);
        g.gridx=1; g.weightx=1; g.gridwidth=3; form.add(fDt, g);
        g.gridx=4; g.weightx=0; g.gridwidth=1; form.add(btnAdd, g);
        g.gridx=5; form.add(btnDel, g);

        p.add(Theme.sectionTitle("Partidas do Campeonato"), BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(table), BorderLayout.CENTER);
        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    // ─── RESULTADOS ───────────────────────────────────────────
    private JPanel buildResultsTab() {
        JPanel p = tab();
        DefaultTableModel model = tm(new String[]{"#", "Mandante", "Visitante", "Data/Hora"});
        JTable table = Theme.makeTable(model);
        Runnable refresh = () -> {
            model.setRowCount(0);
            service.getMatches().stream().filter(m -> !m.isFinished()).forEach(m ->
                model.addRow(new Object[]{m.getId(), m.getHomeClub().getName(), m.getAwayClub().getName(), m.getDateTime().format(DT)}));
        };
        JSpinner sHome = new JSpinner(new SpinnerNumberModel(0,0,99,1));
        JSpinner sAway = new JSpinner(new SpinnerNumberModel(0,0,99,1));
        JButton btn = Theme.makeButton("✅ Registrar Resultado", Theme.ACCENT_GREEN);
        btn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Selecione uma partida na tabela."); return; }
            int id = (int) model.getValueAt(row, 0);
            service.getMatches().stream().filter(m -> m.getId() == id).findFirst().ifPresent(m -> {
                JOptionPane.showMessageDialog(this, service.registerResult(m, (int)sHome.getValue(), (int)sAway.getValue()));
                refresh.run();
            });
        });
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints g = gbc();
        g.gridx=0; g.gridy=0; g.gridwidth=6; form.add(fl("← Selecione a partida na tabela, depois informe o placar:"), g);
        g.gridy=1; g.gridwidth=1;
        g.gridx=0; form.add(fl("Gols mandante:"), g);
        g.gridx=1; form.add(sHome, g);
        g.gridx=2; form.add(fl("Gols visitante:"), g);
        g.gridx=3; form.add(sAway, g);
        g.gridx=4; g.weightx=1; form.add(btn, g);
        p.add(Theme.sectionTitle("Registrar Resultados — Partidas em Aberto"), BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(table), BorderLayout.CENTER);
        p.add(form, BorderLayout.SOUTH);
        refresh.run();
        return p;
    }

    // ─── GRUPOS ───────────────────────────────────────────────
    private JPanel buildGroupsTab() {
        JPanel p = tab();
        modelGroups = tm(new String[]{"Grupo", "Participantes"});
        JTable tableG = Theme.makeTable(modelGroups);
        DefaultTableModel modelR = tm(new String[]{"Pos.", "Participante", "Pontos"});
        JTable tableR = Theme.makeTable(modelR);

        tableG.getSelectionModel().addListSelectionListener(e -> {
            int row = tableG.getSelectedRow();
            if (row < 0 || row >= service.getGroups().size()) return;
            Group g = service.getGroups().get(row);
            modelR.setRowCount(0);
            List<model.Rankable> ranking = g.getRanking();
            for (int i = 0; i < ranking.size(); i++) {
                String m = switch(i){case 0->"🥇";case 1->"🥈";case 2->"🥉";default->(i+1)+".";};
                modelR.addRow(new Object[]{m, ranking.get(i).getName(), ranking.get(i).getTotalPoints() + " pts"});
            }
        });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
        GridBagConstraints g = gbc();
        JTextField fName = Theme.makeField();
        JButton btnAdd = Theme.makeButton("+ Criar Grupo", Theme.ACCENT_BLUE);
        JButton btnDel = Theme.makeButton("🗑 Remover Grupo", Theme.ACCENT_RED);

        btnAdd.addActionListener(e -> {
            String msg = service.addGroup(fName.getText().trim());
            JOptionPane.showMessageDialog(this, msg);
            fName.setText(""); refreshGroups();
        });
        btnDel.addActionListener(e -> {
            int row = tableG.getSelectedRow();
            if (row < 0) { warn("Selecione um grupo na tabela."); return; }
            String nome = modelGroups.getValueAt(row, 0).toString();
            int c = JOptionPane.showConfirmDialog(this, "Remover o grupo \"" + nome + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { service.removeGroup(nome); refreshGroups(); }
        });

        g.gridx=0; form.add(fl("Nome do grupo:"), g);
        g.gridx=1; g.weightx=1; form.add(fName, g);
        g.gridx=2; g.weightx=0; form.add(btnAdd, g);
        g.gridx=3; form.add(btnDel, g);

        JPanel left  = panelWith(Theme.sectionTitle("Grupos"), Theme.makeScrollPane(tableG), form);
        JPanel right = panelWith(Theme.sectionTitle("Ranking do Grupo Selecionado"), Theme.makeScrollPane(tableR), null);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(460);
        p.add(split, BorderLayout.CENTER);
        refreshGroups();
        return p;
    }

    // ─── USUÁRIOS ─────────────────────────────────────────────
    private JPanel buildUsersTab() {
        JPanel p = tab();
        modelUsers = tm(new String[]{"Nome", "Login", "Grupo", "Pontos"});
        JTable table = Theme.makeTable(modelUsers);

        JButton btnDel = Theme.makeButton("🗑 Remover Conta Selecionada", Theme.ACCENT_RED);
        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Selecione um usuário."); return; }
            String login = modelUsers.getValueAt(row, 1).toString();
            String nome  = modelUsers.getValueAt(row, 0).toString();
            int c = JOptionPane.showConfirmDialog(this,
                "Remover a conta de \"" + nome + "\" (@" + login + ")?\nTodas as apostas do participante serão removidas.",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { service.removeParticipant(login); refreshUsers(); }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Theme.BG_DARK);
        top.add(Theme.sectionTitle("Contas de Participantes"));

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bot.setBackground(Theme.BG_DARK);
        bot.add(btnDel);

        p.add(top, BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(table), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        refreshUsers();
        return p;
    }

    // ─── APOSTAS ──────────────────────────────────────────────
    private JPanel buildBetsTab() {
        JPanel p = tab();
        DefaultTableModel model = tm(new String[]{"Participante","Partida","Palpite","Resultado Real","Pontos"});
        JTable table = Theme.makeTable(model);
        Runnable refresh = () -> {
            model.setRowCount(0);
            service.getAllBets().forEach(b -> model.addRow(new Object[]{
                b.getParticipant().getName(),
                b.getMatch().getHomeClub().getAcronym()+" x "+b.getMatch().getAwayClub().getAcronym(),
                b.getPredictedScore(),
                b.getMatch().isFinished() ? b.getMatch().getFinalScore().toString() : "—",
                b.isEvaluated() ? b.getPointsEarned()+" pts" : "—"
            }));
        };
        JButton btnR = Theme.makeButton("🔄 Atualizar", Theme.ACCENT_BLUE);
        btnR.addActionListener(e -> refresh.run());
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bot.setBackground(Theme.BG_DARK); bot.add(btnR);
        p.add(Theme.sectionTitle("Todas as Apostas"), BorderLayout.NORTH);
        p.add(Theme.makeScrollPane(table), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        refresh.run();
        return p;
    }

    // ─── HELPERS ──────────────────────────────────────────────
    private JPanel tab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return p;
    }
    private JPanel card() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(12,12,12,12)));
        return p;
    }
    private JPanel panelWith(JLabel title, JComponent center, JPanel south) {
        JPanel p = new JPanel(new BorderLayout(0,6));
        p.setBackground(Theme.BG_DARK);
        p.add(title, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        if (south != null) p.add(south, BorderLayout.SOUTH);
        return p;
    }
    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }
    private JLabel fl(String t) { return Theme.fieldLabel(t); }
    private JComboBox<String> combo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setBackground(Theme.BG_DARK); cb.setForeground(Theme.TEXT_PRIMARY); cb.setFont(Theme.FONT_BODY);
        return cb;
    }
    private DefaultTableModel tm(String[] cols) {
        return new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
    }
    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE); }
}

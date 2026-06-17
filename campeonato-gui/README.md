# Palpitei - Sistema de Apostas com Banco de Dados SQLite

Aplicação Java com interface gráfica (Swing) para gerenciamento de campeonatos e apostas desportivas, com **persistência de dados em SQLite**.

## 🎯 Funcionalidades

- ✅ Autenticação de usuários (Admin e Participantes)
- ✅ Gerenciamento de clubes e campeonatos
- ✅ Registro de partidas e resultados
- ✅ Sistema de apostas com pontuação automática
- ✅ Criação de grupos de participantes
- ✅ Persistência de dados em banco SQLite
- ✅ Interface gráfica intuitiva com Swing

## 📋 Pré-requisitos

- **Java JDK 8+** instalado e configurado no PATH
- **SQLite JDBC Driver** (sqlite-jdbc)

## 🚀 Instalação e Configuração

### 1. Baixar o Driver SQLite

O projeto utiliza SQLite JDBC para persistência de dados. Você precisa:

1. Acesse: https://github.com/xerial/sqlite-jdbc/releases
2. Baixe a versão mais recente (ex: `sqlite-jdbc-3.46.0.0.jar`)
3. Crie uma pasta `lib` na raiz do projeto:
   ```bash
   mkdir campeonato-gui/lib
   ```
4. Coloque o arquivo JAR dentro da pasta `lib`:
   ```
   campeonato-gui/
   ├── lib/
   │   └── sqlite-jdbc-3.46.0.0.jar  ← Coloque aqui
   ├── src/
   ├── out/
   └── executar.bat/executar.sh
   ```

### 2. Compilar o Projeto

**Windows:**
```bash
cd campeonato-gui
executar.bat
```

**Linux/Mac:**
```bash
cd campeonato-gui
chmod +x executar.sh
./executar.sh
```

O script fará automaticamente:
- ✓ Criar a pasta `out/` para arquivos compilados
- ✓ Compilar todos os arquivos Java (model, service, database, ui)
- ✓ Verificar a presença do driver SQLite
- ✓ Executar a aplicação

## 📁 Estrutura do Projeto

```
campeonato-gui/
├── src/
│   ├── model/              # Classes de modelo
│   │   ├── Person.java
│   │   ├── Admin.java
│   │   ├── Participant.java
│   │   ├── Club.java
│   │   ├── Championship.java
│   │   ├── Match.java
│   │   ├── Bet.java
│   │   ├── Group.java
│   │   ├── Score.java
│   │   └── Rankable.java
│   │
│   ├── service/            # Lógica de negócio
│   │   └── BettingService.java
│   │
│   ├── database/           # Camada de acesso a dados (DAOs)
│   │   ├── DatabaseManager.java
│   │   ├── PersonDAO.java
│   │   ├── ParticipantDAO.java
│   │   ├── ClubDAO.java
│   │   ├── ChampionshipDAO.java
│   │   ├── MatchDAO.java
│   │   └── BetDAO.java
│   │
│   ├── ui/                 # Interface gráfica
│   │   ├── Theme.java
│   │   ├── LoginFrame.java
│   │   ├── AdminFrame.java
│   │   └── ParticipantFrame.java
│   │
│   └── Main.java           # Ponto de entrada
│
├── lib/                    # Bibliotecas externas
│   └── sqlite-jdbc-3.46.0.0.jar
│
├── out/                    # Arquivos compilados (criado automaticamente)
│
├── palpitei.db            # Banco de dados SQLite (criado automaticamente)
│
├── executar.bat           # Script de execução (Windows)
├── executar.sh            # Script de execução (Linux/Mac)
└── README.md              # Este arquivo
```

## 🗄️ Banco de Dados

O banco de dados SQLite é criado automaticamente na primeira execução como `palpitei.db`.

### Tabelas criadas:

- **person** - Dados de usuários (Admin e Participantes)
- **participant** - Informações específicas de participantes
- **club** - Dados dos clubes
- **championship** - Campeonatos
- **group_table** - Grupos de participantes
- **match_table** - Partidas
- **match_result** - Resultados das partidas
- **bet** - Apostas dos participantes
- **score** - Pontuações dos participantes

## 👤 Credenciais Padrão

**Admin:**
- Login: `admin`
- Senha: `admin123`

## 🎮 Como Usar

### 1. Fazer Login
- Inicie a aplicação
- Faça login com admin ou crie um novo participante

### 2. Como Admin
- Adicionar clubes
- Criar campeonatos
- Registrar partidas e resultados
- Visualizar participantes e apostas

### 3. Como Participante
- Ingressar em grupos
- Fazer apostas em partidas
- Visualizar pontuação

## ⚙️ Compilação Manual

Se preferir compilar manualmente sem usar os scripts:

**Windows:**
```bash
cd campeonato-gui
javac -cp lib\sqlite-jdbc-3.46.0.0.jar -d out src\model\*.java src\service\*.java src\database\*.java src\ui\*.java src\Main.java
java -cp out;lib\sqlite-jdbc-3.46.0.0.jar Main
```

**Linux/Mac:**
```bash
cd campeonato-gui
javac -cp lib/sqlite-jdbc-3.46.0.0.jar -d out src/model/*.java src/service/*.java src/database/*.java src/ui/*.java src/Main.java
java -cp out:lib/sqlite-jdbc-3.46.0.0.jar Main
```

## 🔧 Solução de Problemas

### Erro: "Driver SQLite não encontrado"
- Certifique-se de que o arquivo `sqlite-jdbc-3.46.0.0.jar` está em `lib/`
- Verifique se o nome do arquivo está correto

### Erro: "Class not found"
- Confirme que todos os arquivos `.java` foram compilados
- Limpe a pasta `out/` e recompile

### Erro de compilação
- Verifique se você tem Java JDK instalado (não apenas JRE)
- Execute: `javac -version`

## 📝 Notas de Desenvolvimento

- O sistema carrega dados do banco de dados automaticamente na inicialização
- Dados são persistidos após cada operação
- As operações de remoção cascata são suportadas (ex: remover campeonato remove partidas e apostas)
- Mensagens com ✓ indicam sucesso das operações no banco de dados

## 📄 Licença

Projeto educacional para fins de aprendizado em Java.

## 👨‍💻 Autor

Leonardo Motta (Leonardomottech)

---

**Última atualização:** Junho 2026

#!/bin/bash
echo "Compilando o projeto..."
mkdir -p out
javac -d out src/model/*.java src/service/*.java src/ui/Theme.java src/ui/LoginFrame.java src/ui/AdminFrame.java src/ui/ParticipantFrame.java src/Main.java
echo "Iniciando..."
java -cp out Main

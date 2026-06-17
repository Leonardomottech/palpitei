#!/bin/bash

echo "Compilando o projeto..."
mkdir -p out
mkdir -p lib

# Verifica se o driver SQLite existe
if [ ! -f lib/sqlite-jdbc-3.46.0.0.jar ]; then
    echo ""
    echo "AVISO: Driver SQLite nao encontrado em lib/sqlite-jdbc-3.46.0.0.jar"
    echo "Por favor, baixe o driver em: https://github.com/xerial/sqlite-jdbc/releases"
    echo "E coloque o arquivo JAR na pasta 'lib'"
    echo ""
    read -p "Pressione enter para continuar..."
    exit 1
fi

# Compilacao com driver SQLite no classpath
javac -cp lib/sqlite-jdbc-3.46.0.0.jar -d out src/model/*.java src/service/*.java src/database/*.java src/ui/*.java src/Main.java

if [ $? -ne 0 ]; then
    echo ""
    echo "ERRO na compilacao! Verifique se o Java JDK esta instalado."
    read -p "Pressione enter para continuar..."
    exit 1
fi

echo "Iniciando o sistema..."
java -cp out:lib/sqlite-jdbc-3.46.0.0.jar Main

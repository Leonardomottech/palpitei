@echo off
echo Compilando o projeto...
if not exist out mkdir out
if not exist lib mkdir lib

REM Verifica se o driver SQLite existe
if not exist lib\sqlite-jdbc-3.46.0.0.jar (
    echo.
    echo AVISO: Driver SQLite nao encontrado em lib\sqlite-jdbc-3.46.0.0.jar
    echo Por favor, baixe o driver em: https://github.com/xerial/sqlite-jdbc/releases
    echo E coloque o arquivo JAR na pasta 'lib'
    echo.
    pause
    exit /b 1
)

REM Compilacao com driver SQLite no classpath
javac -cp lib\sqlite-jdbc-3.46.0.0.jar -d out src\model\*.java src\service\*.java src\database\*.java src\ui\*.java src\Main.java

if %errorlevel% neq 0 (
    echo.
    echo ERRO na compilacao! Verifique se o Java JDK esta instalado.
    pause
    exit /b 1
)

echo Iniciando o sistema...
java -cp out;lib\sqlite-jdbc-3.46.0.0.jar Main

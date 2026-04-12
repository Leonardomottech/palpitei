@echo off
echo Compilando o projeto...
if not exist out mkdir out

javac -d out src\model\*.java src\service\*.java src\ui\Theme.java src\ui\LoginFrame.java src\ui\AdminFrame.java src\ui\ParticipantFrame.java src\Main.java

if %errorlevel% neq 0 (
    echo.
    echo ERRO na compilacao! Verifique se o Java JDK esta instalado.
    pause
    exit /b 1
)

echo Iniciando o sistema...
java -cp out Main

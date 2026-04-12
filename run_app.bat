@echo off
cd /d "%~dp0"

REM Double-click this file or run it in the terminal to open the app.

set "JAVA_CMD=java"
set "JAVAC_CMD=javac"
set "JAVA_HOME_REG="

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    if exist "%JAVA_HOME%\bin\javac.exe" set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
)

if "%JAVA_CMD%"=="java" (
    for /f "tokens=2,*" %%A in ('reg query "HKCU\Environment" /v JAVA_HOME 2^>nul ^| find "JAVA_HOME"') do set "JAVA_HOME_REG=%%B"
    if not defined JAVA_HOME_REG (
        for /f "tokens=2,*" %%A in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v JAVA_HOME 2^>nul ^| find "JAVA_HOME"') do set "JAVA_HOME_REG=%%B"
    )
    if defined JAVA_HOME_REG (
        if exist "%JAVA_HOME_REG%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME_REG%\bin\java.exe"
        if exist "%JAVA_HOME_REG%\bin\javac.exe" set "JAVAC_CMD=%JAVA_HOME_REG%\bin\javac.exe"
    )
)

if "%JAVA_CMD%"=="java" (
    for /d %%D in ("C:\Program Files\Microsoft\jdk-*") do (
        if exist "%%~fD\bin\java.exe" set "JAVA_CMD=%%~fD\bin\java.exe"
        if exist "%%~fD\bin\javac.exe" set "JAVAC_CMD=%%~fD\bin\javac.exe"
    )
)

echo === COMPILING APP ===
"%JAVAC_CMD%" Product.java Inventory.java FileManager.java InventoryApp.java
if errorlevel 1 (
    echo.
    echo Compilation failed.
    exit /b 1
)

echo.
echo === RUNNING APP ===
"%JAVA_CMD%" InventoryApp

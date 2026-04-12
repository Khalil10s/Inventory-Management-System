@echo off
cd /d "%~dp0"

set "JAVA_CMD=java"
set "JAVAC_CMD=javac"

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    if exist "%JAVA_HOME%\bin\javac.exe" set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
)

echo === COMPILING TEST RUNNER ===
"%JAVAC_CMD%" InventoryAppRegressionTest.java
if errorlevel 1 (
    echo.
    echo Test compilation failed.
    exit /b 1
)

echo.
echo === RUNNING TESTS ===
"%JAVA_CMD%" InventoryAppRegressionTest
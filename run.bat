@echo off
REM ====================================================================
REM SMART INVENTORY MANAGEMENT SYSTEM
REM Runner script for Windows
REM Author: Payal (Roll No. 248036) | Cypher Analytica Internship
REM ====================================================================

title Smart Inventory Management System - Payal

echo ==========================================================
echo  Starting Smart Inventory Management System...
echo ==========================================================

REM Check Java availability
where javac >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] JDK (javac) is not found in your PATH.
    echo Please install Java JDK 11+ or 17+ and add it to your PATH.
    pause
    exit /b 1
)

if not exist "bin" mkdir bin

echo Compiling Java source files...
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)

echo Compilation successful!
echo Launching application...
echo.

java -cp bin com.cypheranalytica.inventory.Main %*

pause

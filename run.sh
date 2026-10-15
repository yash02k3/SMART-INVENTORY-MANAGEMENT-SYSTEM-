#!/usr/bin/env bash
# ====================================================================
# SMART INVENTORY MANAGEMENT SYSTEM
# Runner script for macOS and Linux
# Author: Payal (Roll No. 248036) | Cypher Analytica Internship
# ====================================================================

# Resolve Java & Javac
if [ -f "/opt/homebrew/opt/openjdk@17/bin/javac" ]; then
    JAVAC_CMD="/opt/homebrew/opt/openjdk@17/bin/javac"
    JAVA_CMD="/opt/homebrew/opt/openjdk@17/bin/java"
elif [ -f "/usr/local/opt/openjdk@17/bin/javac" ]; then
    JAVAC_CMD="/usr/local/opt/openjdk@17/bin/javac"
    JAVA_CMD="/usr/local/opt/openjdk@17/bin/java"
elif [ -n "$JAVA_HOME" ] && [ -f "$JAVA_HOME/bin/javac" ]; then
    JAVAC_CMD="$JAVA_HOME/bin/javac"
    JAVA_CMD="$JAVA_HOME/bin/java"
elif javac -version >/dev/null 2>&1; then
    JAVAC_CMD="javac"
    JAVA_CMD="java"
elif [ -x "/usr/libexec/java_home" ] && /usr/libexec/java_home >/dev/null 2>&1; then
    JH=$(/usr/libexec/java_home 2>/dev/null)
    JAVAC_CMD="$JH/bin/javac"
    JAVA_CMD="$JH/bin/java"
else
    echo "Error: Java Development Kit (JDK 11+ or 17+) is required."
    echo "Please install Java (e.g. brew install openjdk@17)."
    exit 1
fi

echo "=========================================================="
echo " Starting Smart Inventory Management System..."
echo " Using: $($JAVA_CMD -version 2>&1 | head -n 1)"
echo "=========================================================="

mkdir -p bin
SOURCES=$(find src -name "*.java")

# Compile
echo ">> Compiling Java source files..."
$JAVAC_CMD -d bin $SOURCES
if [ $? -ne 0 ]; then
    echo ">> [ERROR] Compilation failed."
    exit 1
fi

echo ">> Compilation successful."
echo ">> Launching application..."
echo ""

# Run Application
$JAVA_CMD -cp bin com.cypheranalytica.inventory.Main "$@"

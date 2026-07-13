#!/bin/bash
set -e

echo "Starting build process..."

# Define library paths (as defined in your .classpath file)
LIBS_DIR="/Users/nicolasbarticevic/eclipse-workspace/sim13/libraries"
MASON_JAR="$LIBS_DIR/mason.22.jar"
OPENCSV_JAR="$LIBS_DIR/opencsv-5.9.jar"
PORTFOLIO_JAR="$LIBS_DIR/portfolio.jar"
JMF_JAR="$LIBS_DIR/jmf.jar"
JFREECHART_JAR="$LIBS_DIR/jfreechart-1.0.17.jar"
JCOMMON_JAR="$LIBS_DIR/jcommon-1.0.21.jar"
ITEXT_JAR="$LIBS_DIR/itext-1.2.jar"
BSH_JAR="$LIBS_DIR/bsh-2.0b4.jar"
JSON_JAR="mason/json-java.jar"

# Build classpath
CLASSPATH="$MASON_JAR:$OPENCSV_JAR:$PORTFOLIO_JAR:$JMF_JAR:$JFREECHART_JAR:$JCOMMON_JAR:$ITEXT_JAR:$BSH_JAR:$JSON_JAR"

echo "1. Compiling source files..."
mkdir -p build/classes

# Find all non-test Java files and compile them
find mason -type f -name "*.java" \
    ! -name "*Test*.java" \
    ! -name "test*.java" \
    ! -name "RunAllTests.java" \
    ! -name "Tests.java" \
    -print0 | xargs -0 javac -d build/classes -classpath "$CLASSPATH"

echo "2. Packaging into a Fat JAR..."
mkdir -p build/fatjar
cd build/fatjar

# Extract necessary library classes to bundle them in the fat jar
echo "   Extracting dependencies..."
unzip -qo "$MASON_JAR"
unzip -qo "$OPENCSV_JAR"
unzip -qo "../../$JSON_JAR"

# Copy our compiled classes
cp -r ../classes/* .

# Create the executable JAR
echo "   Creating mason/ABMServer.jar..."
jar cfe ../../mason/ABMServer.jar runners.ABMServer .

# Cleanup
cd ../..
rm -rf build/fatjar

echo "Build complete! The executable JAR is located at: mason/ABMServer.jar"

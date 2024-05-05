#!/bin/bash
echo "Starting script..."
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "Directory: $DIR"
echo "Running Java application..."
java -verbose -jar "$DIR/../../../target/devInspector-1.0-jar-with-dependencies.jar"
echo "Script finished."
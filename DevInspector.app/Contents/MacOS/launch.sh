#!/bin/bash
echo "Starting script..."
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# shellcheck disable=SC2164
# Change to the parent directory of the .app bundle
cd "$DIR/../../../"
echo "Directory: $DIR"
# Print the new current working directory
echo "Current working directory: $(pwd)"
echo "Running Java application..."
java -verbose -jar "./target/devInspector-1.0-jar-with-dependencies.jar"
echo "Script finished."
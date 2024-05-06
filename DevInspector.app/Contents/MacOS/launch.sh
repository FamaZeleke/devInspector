#!/bin/bash

# The script file to convert
script_file="launch.sh"

# Check if we're on a Unix-based system (like macOS or Linux)
if [ "$(uname)" != "Windows_NT" ]; then
    # If we are, convert the line endings
    sed -i 's/\r$//' "$script_file"
fi

echo "Starting script..."
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# shellcheck disable=SC2164
# Change to the parent directory of the .app bundle
cd "$DIR/../"
echo "Directory: $DIR"
# Print the new current working directory
echo "Current working directory: $(pwd)"
echo "Running Java application..."
java -verbose -jar "./Java/devInspector-1.0-jar-with-dependencies.jar"
echo "Script finished."
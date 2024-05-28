#!/bin/bash

echo "Starting Server"

curl -L https://api.papermc.io/v2/projects/paper/versions/1.20.6/builds/115/downloads/paper-1.20.6-115.jar --output paper.jar

java -jar -Dcom.mojang.eula.agree=true paper.jar
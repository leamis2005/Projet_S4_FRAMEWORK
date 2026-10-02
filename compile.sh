#!/bin/bash

set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

find framework -name "*.java" > sources.txt

javac -cp "lib/servlet-api.jar" -d bin --release 8 @sources.txt

rm sources.txt

jar cf framework.jar -C bin .

if [ -d "../test/src/main/webapp/WEB-INF/lib" ]; then
    cp framework.jar "../test/src/main/webapp/WEB-INF/lib/framework.jar"
fi

# Also copy to deployed test webapp
if [ -d "/home/rakotoarivelo/apache-tomcat-10.0.16/webapps/test-framework/WEB-INF/lib" ]; then
    cp framework.jar "/home/rakotoarivelo/apache-tomcat-10.0.16/webapps/test-framework/WEB-INF/lib/framework.jar"
fi

#!/bin/bash
set -e
jarfile=/tmp/javaformatter-cli/target/javaformatter-cli-1.0-SNAPSHOT.jar

which git > /dev/null
which mvn > /dev/null
which java > /dev/null

if [ ! -f $jarfile]; then
    pushd /tmp >/dev/null
    git clone https://github.com/jrialland/javaformatter-cli
    pushd javaformatter-cli >/dev/null
    mvn dependency:copy-dependencies
    mvn clean package -DskipTests
    popd >/dev/null
    popd >/dev/null
fi

CLASSPATH=$jarfile
for j in `find /tmp/javaformatter-cli/target/dependency -name "*.jar"`; do
    export CLASSPATH=$CLASSPATH:$j
done

java -cp "$CLASSPATH" com.github.jrialland.javaformatter.FormatterCli $@

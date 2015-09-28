#!/bin/bash
set -e
tmpdir=$HOME/.javaformatter

jarfile=$tmpdir/javaformatter-cli/target/javaformatter-cli-1.0-SNAPSHOT.jar

which git > /dev/null
which mvn > /dev/null
which java > /dev/null

if [ ! -f $jarfile ]; then
    mkdir -p $tmpdir
    pushd $tmpdir >/dev/null
    git clone https://github.com/jrialland/javaformatter-cli
    pushd javaformatter-cli >/dev/null
    mvn dependency:copy-dependencies package -DskipTests > /dev/null
    popd >/dev/null
    popd >/dev/null
fi
CLASSPATH=$jarfile
for j in `find /tmp/javaformatter-cli/target/dependency -name "*.jar"`; do
    export CLASSPATH=$CLASSPATH:$j
done
java -cp "$CLASSPATH" com.github.jrialland.javaformatter.FormatterCli $@


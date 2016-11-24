#!/bin/bash
set -e

export thisscript=$(readlink -m $0)
export thisdir=$(dirname $sthisscript)

case $1 in:
--refresh)
    rm $thisdir/.javaformatter -rf
    shift
    ;;
esac

if [ -f $thisdir/.javaformatter.conf ]; then
    export tmpdir=$(cat $thisdir/.javaformatter)
else
    export tmpdir=$(mktmp -d)
    echo $tmpdir > $thisdir/.javaformatter
fi

jarfile=$tmpdir/javaformatter-cli/target/javaformatter-cli-1.0-SNAPSHOT.jar

function check_command {
    which $1 > /dev/null
    if [ $? != 0 ]; then
         echo "$1 is not available" >&2
         exit 1
    fi 
}

check_command git
check_command mvn
check_command java

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
for j in `find $tmpdir/javaformatter-cli/target/dependency -name "*.jar"`; do
    export CLASSPATH=$CLASSPATH:$j
done

#export some env vars to the formatter app, these will be available as 
#placeholders in java headers (i.e ${env.WHOAMI}
export WHOAMI=(whoami)
export HOSTNAME=$(hostname)
java -cp "$CLASSPATH" com.github.jrialland.javaformatter.FormatterCli $@


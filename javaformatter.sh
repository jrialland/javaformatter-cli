#!/bin/bash
set -e

export thisscript=$(readlink -m $0)
export thisdir=$(dirname $thisscript)

export installdir=$HOME/.javaformatter_install

case $1 in
"--refresh")
    rm $installdir -rf
    shift
    ;;
esac

if [ -f $thisdir/.javaformatter ]; then
    export tmpdir=$(cat $thisdir/.javaformatter)
else
    export tmpdir=$installdir
fi

jarfile=$tmpdir/javaformatter-cli/target/javaformatter-cli-1.0-SNAPSHOT.jar

function check_command {
    which $1 > /dev/null
    if [ $? != 0 ]; then
         echo "error : the $1 command is not available" >&2
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
#placeholders in java headers (i.e ${env.HOSTNAME}
export HOSTNAME=$(hostname)
java -cp "$CLASSPATH" com.github.jrialland.javaformatter.FormatterCli "$@"


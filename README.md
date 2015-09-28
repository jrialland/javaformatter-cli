# javaformatter-cli
Quick and dirty commandline that cleans up source code the way I like.
It uses the  Eclipse code java formatter.
- It can also add a text header at the beginning of java files
- It also formats xml files

[![Build Status](https://travis-ci.org/jrialland/javaformatter-cli.svg)](https://travis-ci.org/jrialland/javaformatter-cli)



```sh
#download the formatter script
curl https://cdn.rawgit.com/jrialland/javaformatter-cli/master/javaformatter.sh > javaformatter

#allow to run it
chmod +x javaformatter

#will cleanup the current dir
./javaformatter src/main/java
```

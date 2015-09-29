# javaformatter-cli

[![Build Status](https://travis-ci.org/jrialland/javaformatter-cli.svg)](https://travis-ci.org/jrialland/javaformatter-cli)

THis project evolved from a Quick and dirty commandline tool that formatted java source code to something more evoluted.

It scans source code directories and beautifies/formats the files it can, I add support for more file types when I can.

* java : Uses the  Eclipse code java formatter (and its default behavior by default).
* xml : beautifies xml using the javax.xml.transform api
* css : beautifies using js-beautify (https://github.com/beautify-web/js-beautify)
* html : beautifies using js-beautify
* js, json :  beautifies using js-beautify

- It can also add a commented text header at the beginning of java files (copyright infos, etc...)

It may in the future be appended more functionalities, like running wro4j, require.js, compass compiler, some minifier

Disclaimer : The tool is not very tuneable, as it already fits the settings I like.



```sh
#download the formatter script
curl https://cdn.rawgit.com/jrialland/javaformatter-cli/master/javaformatter.sh > javaformatter

#allow to run it
chmod +x javaformatter

#will cleanup the current dir
./javaformatter src/main/java
```

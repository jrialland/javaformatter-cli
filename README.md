# javaformatter-cli

[![Build Status](https://travis-ci.org/jrialland/javaformatter-cli.svg)](https://travis-ci.org/jrialland/javaformatter-cli)

This project evolved from a Quick and dirty commandline tool that formatted java source code to something more evoluted.

It scans source code directories and beautifies/formats/transforms the files it can, I add support for new tools when I them.

Disclaimer : The tool is made to fit the settings I like and the way I code. It works by convention, not by configuration.

Beautifier
----------

* java : Uses the  Eclipse code java formatter (and its default behavior by default) on all .java files
* xml : beautifies xml using the javax.xml.transform api on xml, xhtml, svg files
* css : beautifies using js-beautify (https://github.com/beautify-web/js-beautify) on every .css files
* html : beautifies using js-beautify (.html only)
* js, json :  beautifies using js-beautify (.js, .json files)

Header in java files
--------------------

- It can also add a commented text header at the beginning of java files (copyright infos, etc...) -> See -H option

Transpilers
-----------

It also features some extra tasks :
* coffescript 1.10 compilation (http://coffeescript.org/) (turning .coffe into .coffe.js)
* compass compiler (http://compass-style.org/) (detect files named compass.rb as configuration file and run them)
* freemarker : runs freemarker on all files with the .ftl extension
* css/js minifier : if the file has ".minify" in its name, the file is minified and into .min

An example :
 If you want to have some coffescript files that need to be minified :  
```
MyFirstFile.coffee
MySecondFile.coffe
```

Create a file named all.minify.coffe.ftl with the following content :
```
<#include "MyFirstFile.coffe" parse=false>
<#include "MySecondFile.coffe" parse=false>
```

The compilation mechanism will apply this way :

1) The .ftl extension means that freemarker will be executed, producing a file named all.minify.coffe
2) The .coffe extension will trigger the coffescript compiler, and produce the all.minify.js
3) The fact that there is .minify in the name will run jsmin, and produce all.min.js
    
By specifying the file names properly, the following chain will trigger each time :
    * merging several files into one
    * compiling coffescript into javascript
    * minifying the javascript
    

```sh
#download the formatter script
curl https://cdn.rawgit.com/jrialland/javaformatter-cli/master/javaformatter.sh > javaformatter

#allow to run it
chmod +x javaformatter

#will cleanup the current dir
./javaformatter src/main/java
```

var isIE = (navigator["appVersion"]["indexOf"]("MSIE") != -1) ? true : false;
var isWin = (navigator["appVersion"]["toLowerCase"]()["indexOf"]("win") != -1) ? true : false;
var isOpera = (navigator["userAgent"]["indexOf"]("Opera") != -1) ? true : false;

function ControlVersion()
{
    var a;
    var b;
    var c;
    try
    {
        b = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
        a = b.GetVariable("$version")
    }
    catch (c)
    {};
    if (!a)
    {
        try
        {
            b = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
            a = "WIN 6,0,21,0";
            b["AllowScriptAccess"] = "always";
            a = b.GetVariable("$version")
        }
        catch (c)
        {}
    };
    if (!a)
    {
        try
        {
            b = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
            a = b.GetVariable("$version")
        }
        catch (c)
        {}
    };
    if (!a)
    {
        try
        {
            b = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
            a = "WIN 3,0,18,0"
        }
        catch (c)
        {}
    };
    if (!a)
    {
        try
        {
            b = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
            a = "WIN 2,0,0,11"
        }
        catch (c)
        {
            a = -1
        }
    };
    return a
};

function GetSwfVer()
{
    var a = -1;
    if (navigator["plugins"] != null && navigator["plugins"]["length"] > 0)
    {
        if (navigator["plugins"]["Shockwave Flash 2.0"] || navigator["plugins"]["Shockwave Flash"])
        {
            var b = navigator["plugins"]["Shockwave Flash 2.0"] ? " 2.0" : "";
            var c = navigator["plugins"]["Shockwave Flash" + b]["description"];
            var d = c["split"](" ");
            var e = d[2]["split"](".");
            var f = e[0];
            var g = e[1];
            var h = d[3];
            if (h == "")
            {
                h = d[4]
            };
            if (h[0] == "d")
            {
                h = h["substring"](1)
            }
            else
            {
                if (h[0] == "r")
                {
                    h = h["substring"](1);
                    if (h["indexOf"]("d") > 0)
                    {
                        h = h["substring"](0, h["indexOf"]("d"))
                    }
                }
            };
            var a = f + "." + g + "." + h
        }
    }
    else
    {
        if (navigator["userAgent"]["toLowerCase"]()["indexOf"]("webtv/2.6") != -1)
        {
            a = 4
        }
        else
        {
            if (navigator["userAgent"]["toLowerCase"]()["indexOf"]("webtv/2.5") != -1)
            {
                a = 3
            }
            else
            {
                if (navigator["userAgent"]["toLowerCase"]()["indexOf"]("webtv") != -1)
                {
                    a = 2
                }
                else
                {
                    if (isIE && isWin && !isOpera)
                    {
                        a = ControlVersion()
                    }
                }
            }
        }
    };
    return a
};

function DetectFlashVer(a, b, c)
{
    versionStr = GetSwfVer();
    if (versionStr == -1)
    {
        return false
    }
    else
    {
        if (versionStr != 0)
        {
            if (isIE && isWin && !isOpera)
            {
                tempArray = versionStr["split"](" ");
                tempString = tempArray[1];
                versionArray = tempString["split"](",")
            }
            else
            {
                versionArray = versionStr["split"](".")
            };
            var d = versionArray[0];
            var e = versionArray[1];
            var f = versionArray[2];
            if (d > parseFloat(a))
            {
                return true
            }
            else
            {
                if (d == parseFloat(a))
                {
                    if (e > parseFloat(b))
                    {
                        return true
                    }
                    else
                    {
                        if (e == parseFloat(b))
                        {
                            if (f >= parseFloat(c))
                            {
                                return true
                            }
                        }
                    }
                }
            };
            return false
        }
    }
};

function AC_AddExtension(a, b)
{
    if (a["indexOf"]("?") != -1)
    {
        return a["replace"](/\?/, b + "?")
    }
    else
    {
        return a + b
    }
};

function AC_Generateobj(a, b, c)
{
    var d = "";
    if (isIE && isWin && !isOpera)
    {
        d += "<object ";
        for (var e in a)
        {
            d += e + "="
            "+a[e]+"
            " "
        };
        d += ">";
        for (var e in b)
        {
            d += "<param name="
            "+e+"
            " value="
            "+b[e]+"
            " /> "
        };
        d += "</object>"
    }
    else
    {
        d += "<embed ";
        for (var e in c)
        {
            d += e + "="
            "+c[e]+"
            " "
        };
        d += "> </embed>"
    };
    document["write"](d)
};

function AC_FL_RunContent()
{
    var a = AC_GetArgs(arguments, ".swf", "movie", "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000", "application/x-shockwave-flash");
    AC_Generateobj(a["objAttrs"], a["params"], a["embedAttrs"])
};

function AC_SW_RunContent()
{
    var a = AC_GetArgs(arguments, ".dcr", "src", "clsid:166B1BCA-3F9C-11CF-8075-444553540000", null);
    AC_Generateobj(a["objAttrs"], a["params"], a["embedAttrs"])
};

function AC_GetArgs(a, b, c, d, e)
{
    var f = new Object();
    f["embedAttrs"] = new Object();
    f["params"] = new Object();
    f["objAttrs"] = new Object();
    for (var g = 0; g < a["length"]; g = g + 2)
    {
        var h = a[g]["toLowerCase"]();
        switch (h)
        {
            case "classid":
                break;
            case "pluginspage":
                f["embedAttrs"][a[g]] = a[g + 1];
                break;
            case "src":
                ;
            case "movie":
                a[g + 1] = AC_AddExtension(a[g + 1], b);
                f["embedAttrs"]["src"] = a[g + 1];
                f["params"][c] = a[g + 1];
                break;
            case "onafterupdate":
                ;
            case "onbeforeupdate":
                ;
            case "onblur":
                ;
            case "oncellchange":
                ;
            case "onclick":
                ;
            case "ondblclick":
                ;
            case "ondrag":
                ;
            case "ondragend":
                ;
            case "ondragenter":
                ;
            case "ondragleave":
                ;
            case "ondragover":
                ;
            case "ondrop":
                ;
            case "onfinish":
                ;
            case "onfocus":
                ;
            case "onhelp":
                ;
            case "onmousedown":
                ;
            case "onmouseup":
                ;
            case "onmouseover":
                ;
            case "onmousemove":
                ;
            case "onmouseout":
                ;
            case "onkeypress":
                ;
            case "onkeydown":
                ;
            case "onkeyup":
                ;
            case "onload":
                ;
            case "onlosecapture":
                ;
            case "onpropertychange":
                ;
            case "onreadystatechange":
                ;
            case "onrowsdelete":
                ;
            case "onrowenter":
                ;
            case "onrowexit":
                ;
            case "onrowsinserted":
                ;
            case "onstart":
                ;
            case "onscroll":
                ;
            case "onbeforeeditfocus":
                ;
            case "onactivate":
                ;
            case "onbeforedeactivate":
                ;
            case "ondeactivate":
                ;
            case "type":
                ;
            case "codebase":
                ;
            case "id":
                f["objAttrs"][a[g]] = a[g + 1];
                break;
            case "width":
                ;
            case "height":
                ;
            case "align":
                ;
            case "vspace":
                ;
            case "hspace":
                ;
            case "class":
                ;
            case "title":
                ;
            case "accesskey":
                ;
            case "name":
                ;
            case "tabindex":
                f["embedAttrs"][a[g]] = f["objAttrs"][a[g]] = a[g + 1];
                break;
            default:
                f["embedAttrs"][a[g]] = f["params"][a[g]] = a[g + 1]
        }
    };
    f["objAttrs"]["classid"] = d;
    if (e)
    {
        f["embedAttrs"]["type"] = e
    };
    return f
};

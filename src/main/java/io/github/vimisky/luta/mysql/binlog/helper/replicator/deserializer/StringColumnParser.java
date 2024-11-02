package io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

//MySQL编码和Java编码对应关系：
//https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-charsets.html
public class StringColumnParser {
    public static String anyToString(String columnCharacterSetName, Serializable data) throws UnsupportedEncodingException {

        if (data == null)
            return null;

        String retData = null;

        if (data instanceof String){
            retData = (String)data;
        }

        //字节数组
        assert data instanceof byte[];
        byte[] bytes = (byte[]) data;

        if (null == columnCharacterSetName){
            retData = new String(bytes, "UTF-8");
        }else {
            switch (columnCharacterSetName){
                case "utf8":
                case "utf8mb4":
                    retData = new String(bytes, "UTF-8");
                    break;
                case "ascii":
                    retData = new String(bytes, "US-ASCII");
                    break;
                case "latin1":
                    retData = new String(bytes, "Cp1252");
                    break;
                case "latin2":
                    retData = new String(bytes, "ISO8859_2");
                    break;
                case "gb2312":
                    retData = new String(bytes, "EUC_CN");
                    break;
                case "greek":
                    retData = new String(bytes, "ISO8859_7");
                    break;
                case "hebrew":
                    retData = new String(bytes, "ISO8859_8");
                    break;
                case "macce":
                    retData = new String(bytes, "MacCentralEurope");
                    break;
                case "ucs2":
                    retData = new String(bytes, "UnicodeBig");
                    break;
                default:
                    retData = new String(bytes, columnCharacterSetName);
            }
        }


        return retData;
    }
}

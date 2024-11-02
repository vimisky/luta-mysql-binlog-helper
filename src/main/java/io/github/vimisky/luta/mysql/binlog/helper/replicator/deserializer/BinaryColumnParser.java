package io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BinaryColumnParser {

    public static String BINARY_PREFIX = "__binary__";
    public static String BINARY_SUFFIX = "__yranib__";

    public static String anyToBase64String(Serializable data){
        if (data == null)
            return null;
        byte[] bytes = (byte[]) data;
        return BINARY_PREFIX+Base64.encodeBase64String(bytes)+BINARY_SUFFIX;
    }
    public static String binaryToBase64String(Serializable data, String columnType){
        if (data == null)
            return null;
        byte[] bytes = (byte[]) data;
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(columnType);
        String subStr = null;
        if (m.find()) {
            subStr = m.group();
            int length = Integer.parseInt(subStr);
            if (bytes.length < length){
                byte[] newBytes = new byte[length];
                System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                for (int i = bytes.length; i<length; i++){
                    newBytes[i] = 0x00;
                }
                bytes = newBytes;
            }
        }
        return BINARY_PREFIX+Base64.encodeBase64String(bytes)+BINARY_SUFFIX;
    }
    public static byte[] anyToByteArray(Serializable data){
        if (data == null)
            return null;
        byte[] bytes = (byte[]) data;
        return bytes;
    }

}

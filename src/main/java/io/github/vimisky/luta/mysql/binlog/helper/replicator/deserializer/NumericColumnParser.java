package io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class NumericColumnParser {

    private static final BigInteger longlong_max = BigInteger.ONE.shiftLeft(64);

    public static Integer partIntToInteger(String dataType, String columnType, Serializable data){
        if (data == null)
            return null;
        //先转换为Integer
        Integer retData = (Integer)data;
        //无符号类型，但是解析出来是负数，那么需要计算来转换
        //tinyint,smallint,mediumint，移位不同，所以用bitLength来做变量
        if(columnType.matches(".* unsigned$")){
            if (retData < 0){
                int bitLength = bitLength(dataType);
                retData = (0x1 << bitLength) + retData;
            }
        }
        return retData;
    }

    public static Integer intSignedToInteger(String dataType, String columnType, Serializable data){
        if (data == null)
            return null;

        return (Integer) data;
    }

    public static Long intUnsignedToLong(String dataType, String columnType, Serializable data){
        if (data == null)
            return null;

        Integer retData = (Integer)data;

        if (retData < 0) {
            int bitLength = bitLength(dataType);
            return (1L << bitLength) + retData.longValue();
        }
        return retData.longValue();
    }

    public static Long bigintSignedToLong(String dataType, String columnType, Serializable data){
        if (data == null)
            return null;

        return (Long) data;
    }

    //统一转换为BigInteger
    public static BigInteger bigintUnsignedToBigInteger(String dataType, String columnType, Serializable data){
        if (data == null)
            return null;

        Long retData = (Long)data;

        if (retData < 0) {
            int bitLength = bitLength(dataType);
            return longlong_max.add(BigInteger.valueOf(retData));
        }
        return BigInteger.valueOf(retData);
    }

    public static Float floatToFloat(Serializable data){

        if (data == null)
            return null;

        return (Float)data;
    }

    public static String floatToString(Serializable data){

        if (data == null)
            return null;

        Float retData = (Float)data;
        return retData.toString();
    }

    public static Double doubleToDouble(Serializable data){

        if (data == null)
            return null;

        return (Double)data;
    }

    public static String doubleToString(Serializable data){

        if (data == null)
            return null;

        Double retData = (Double)data;
        return retData.toString();
    }

    public static String decimalToString(Serializable data){

        if (data == null)
            return null;

        BigDecimal bigDecimal = (BigDecimal)data;
        return bigDecimal.toEngineeringString();
    }

    private final static int bitLength(String dataType) {
        switch(dataType) {
            case "tinyint":
                return 8;
            case "smallint":
                return 16;
            case "mediumint":
                return 24;
            case "int":
                return 32;
            default:
                return 0;
        }
    }

}

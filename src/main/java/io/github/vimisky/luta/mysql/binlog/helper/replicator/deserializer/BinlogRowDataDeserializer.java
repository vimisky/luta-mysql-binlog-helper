package io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer;

import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.SchemaColumnDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Java数据类型和MySQL数据类型对应表
 * https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-type-conversions.html
 * https://dev.mysql.com/doc/connector-j/en/connector-j-reference-type-conversions.html
 * */
public class BinlogRowDataDeserializer {
    private static final Logger logger = LoggerFactory.getLogger(BinlogRowDataDeserializer.class);

    private String timezoneID= "Asia/Shanghai";

    public String getTimezoneID() {
        return timezoneID;
    }

    public void setTimezoneID(String timezoneID) {
        this.timezoneID = timezoneID;
    }

    public static void printColumnInstanceOf(Serializable columnValue){

        if (null == columnValue){
            logger.info("can't get column class, value is null");
        }else if (columnValue instanceof Long){
            logger.info("Long");
        }else if (columnValue instanceof BigInteger){
            logger.info("BigInteger");
        }else if (columnValue instanceof Integer){
            logger.info("Integer");
        }else if (columnValue instanceof String){
            logger.info("String");
        }else if (columnValue instanceof Byte){
            //从未命中
            logger.info("Byte");
        }else if (columnValue instanceof Byte[]){
            //从未命中
            logger.info("Byte[]");
        }else if (columnValue instanceof byte[]){
            logger.info("byte[]");
        }else if (columnValue instanceof java.sql.Date){
            logger.info("java.sql.Date");
        }else if (columnValue instanceof java.time.LocalDateTime){
            logger.info("java.time.LocalDateTime");
        }else if (columnValue instanceof java.time.ZonedDateTime){
            logger.info("java.time.ZonedDateTime");
        }else if (columnValue instanceof java.time.LocalDate){
            logger.info("java.time.LocalDate");
        }else if (columnValue instanceof java.time.LocalTime){
            logger.info("java.time.LocalTime");
        }else if (columnValue instanceof java.time.Duration){
            logger.info("java.time.Duration");
        }else if (columnValue instanceof java.sql.Timestamp){
            logger.info("java.sql.Timestamp");
        }else if (columnValue instanceof java.sql.Time){
            logger.info("java.sql.Time");
        }else if (columnValue instanceof Float){
            logger.info("float");
        }else if (columnValue instanceof Double){
            logger.info("double");
        }else if (columnValue instanceof BigDecimal){
            logger.info("BigDecimal");
        }else {
            logger.info("Not Long, Not BigInteger, Not Integer Not String Not Byte");
        }

    }

    public void debugColumnInstanceOf(Serializable columnValue){

        if (null == columnValue){
            logger.info("can't get column class, value is null");
        }else if (columnValue instanceof Long){
            logger.debug("Long");
        }else if (columnValue instanceof BigInteger){
            logger.debug("BigInteger");
        }else if (columnValue instanceof Integer){
            logger.debug("Integer");
        }else if (columnValue instanceof String){
            logger.debug("String");
        }else if (columnValue instanceof Byte){
            //从未命中
            logger.debug("Byte");
        }else if (columnValue instanceof Byte[]){
            //从未命中
            logger.debug("Byte[]");
        }else if (columnValue instanceof byte[]){
            logger.debug("byte[]");
        }else if (columnValue instanceof java.sql.Date){
            logger.info("java.sql.Date");
        }else if (columnValue instanceof java.time.LocalDateTime){
            logger.info("java.time.LocalDateTime");
        }else if (columnValue instanceof java.time.ZonedDateTime){
            logger.info("java.time.ZonedDateTime");
        }else if (columnValue instanceof java.time.LocalDate){
            logger.info("java.time.LocalDate");
        }else if (columnValue instanceof java.time.LocalTime){
            logger.info("java.time.LocalTime");
        }else if (columnValue instanceof java.time.Duration){
            logger.info("java.time.Duration");
        }else if (columnValue instanceof java.sql.Timestamp){
            logger.info("java.sql.Timestamp");
        }else if (columnValue instanceof java.sql.Time){
            logger.info("java.sql.Time");
        }else if (columnValue instanceof Float){
            logger.debug("float");
        }else if (columnValue instanceof Double){
            logger.debug("double");
        }else if (columnValue instanceof BigDecimal){
            logger.debug("BigDecimal");
        }else {
            logger.debug("Not Long, Not BigInteger, Not Integer Not String Not Byte");
        }

    }

    public List<Object> deserializeRowData(List<SchemaColumnDef> columnDefList, Serializable[] row) throws Exception {
        List<Object> deserialized = new ArrayList<>();

        int size = columnDefList.size();
        SchemaColumnDef columnDef = null;
        Serializable columnValue = null;
        Object deserializedValue = null;
        for(int i= 0; i<size; i++){
            columnDef = columnDefList.get(i);
            columnValue = row[i];
            String dataType = columnDef.getDataType();
            String columnType = columnDef.getColumnType();
            String columnCharacterSetName = columnDef.getCharacterSetName();
            logger.debug("deserialize column : " + columnDef.getColumnName());

            //类型转换参考Java Connector/J的对应关系，与其保持一致。但是从binlog中解析出来，不是那个对应关系，需要打印日志调试查看验证。
            switch (dataType){
                case "tinyint":
                case "smallint":
                case "mediumint":
                    //有符号和无符号区别处理，因为有符号对应的就是Integer，无符号对应的是Integer并且需要计算转换
                    deserialized.add(NumericColumnParser.partIntToInteger(dataType, columnType,columnValue));
                    break;
                case "int":
                    //有符号和无符号区别处理，因为有符号对应的就是Integer，无符号对应的是Long并且需要计算转换
                    if(columnType.matches(".* unsigned$")){
                        deserialized.add(NumericColumnParser.intUnsignedToLong(dataType, columnType,columnValue));
                    }else{
                        deserialized.add(NumericColumnParser.intSignedToInteger(dataType, columnType,columnValue));
                    }
                    break;
                case "bigint":
                    //有符号和无符号区别处理，因为有符号对应的就是Long，无符号对应的是BigInteger并且需要计算转换
                    if(columnType.matches(".* unsigned$")){
                        deserialized.add(NumericColumnParser.bigintUnsignedToBigInteger(dataType, columnType,columnValue));
                    }else{
                        deserialized.add(NumericColumnParser.bigintSignedToLong(dataType, columnType, columnValue));
                    }
                    break;
                case "float":
                    deserialized.add(NumericColumnParser.floatToFloat(columnValue));
                    break;
                case "double":
                    deserialized.add(NumericColumnParser.doubleToDouble(columnValue));
                    break;
                case "decimal":
                    deserialized.add(NumericColumnParser.decimalToString(columnValue));
                    break;
                case "tinytext":
                case "text":
                case "mediumtext":
                case "longtext":
                case "varchar":
                case "char":

                    String dColumnValue = StringColumnParser.anyToString(columnCharacterSetName, columnValue);
                    logger.debug("columnValue:"+dColumnValue);
                    deserialized.add(dColumnValue);
                    break;
                    //时间这块，没有按照mysql和java对应关系来做。
                //对于时间的处理，关键是mysql自身是什么样的值，这样在sql中where条件才能命中，不然会影响运行。格式会自动转换，譬如2024-05-22T11:22:33和2024-05-22 11:22:33是相等的
                case "datetime":
                    // 这里，是mysql binlog解析源头bug
                    // 不管时区，直接是将时间字符串按照UTC时间字符串转换为UNIX时间戳。这是bug，如果是其他时区，那么时间戳是错的。
                    // 所以如果非UTC时区，UNIX时间戳转换为字符串时，也要按照UTC时区转换为字符串，但是转换完之后我们当它是当地时区时间。
                    deserialized.add(DateTimeColumnParser.datetimeToString(columnValue, columnType));
                    break;
                case "date":
                    deserialized.add(DateTimeColumnParser.dateToString(columnValue));
                    break;
                case "time":
                    // 这里，是mysql binlog解析源头bug
                    // 不管时区，直接是将时间字符串按照UTC时间字符串转换为UNIX时间戳。这是bug，如果是其他时区，那么时间戳是错的。
                    // 所以如果非UTC时区，UNIX时间戳转换为字符串时，也要按照UTC时区转换为字符串，但是转换完之后我们当它是当地时区时间。
                    deserialized.add(DateTimeColumnParser.timeToString(columnValue));
                    break;
                case "timestamp":
                    // timestamp类型，是带时区的，时间字符串是当地时区的时间表达。所以转换后的时间戳是准确的。
//                    logger.info("timestamp timezoneID: " + timezoneID);
                    deserialized.add(DateTimeColumnParser.timestampToString(columnValue, columnType));
                    break;
                case "year":
                    deserialized.add(DateTimeColumnParser.yearToString(columnValue));
                    break;
                case "binary":
                    deserialized.add(BinaryColumnParser.binaryToBase64String(columnValue, columnType));
                    break;
                case "tinyblob":
                case "blob":
                case "mediumblob":
                case "longblob":
                case "varbinary":
//                    logger.info("命中解析binary类型: columnType: " + columnType + ", dataType: " + dataType);
                    deserialized.add(BinaryColumnParser.anyToBase64String(columnValue));
//                    deserialized.add(BinaryColumnParser.anyToByteArray(columnValue));
                    break;
                default:
                    //默认不处理，字段数量和值数量就会不匹配，就会抛出异常，人工处理，修改代码。
                    break;
            }
        }
        logger.debug("deserialized array:");
        for (Object ooo:deserialized) {
            logger.debug(ooo==null?"null":ooo.toString());
        }

        return deserialized;
    }

}

package io.github.vimisky.luta.mysql.binlog.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shyiko.mysql.binlog.event.deserialization.ColumnType;
import io.github.vimisky.luta.mysql.binlog.helper.controller.LutaBinlogViewController;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.DebeziumReplicator;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class LutaMysqlBinlogHelperApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(LutaMysqlBinlogHelperApplicationTests.class);

    @Test
    void contextLoads() {
         PasswordEncoder encoder =
         PasswordEncoderFactories.createDelegatingPasswordEncoder();
         String encodedPassword = encoder.encode("helper");
         logger.info(encodedPassword);
    }
    @Test
    void objectMapperTest() throws JsonProcessingException {
        byte[] uu = {0x9,0x8,0x8,0x2,0x3,0xE,0x1,0x0,0x0,0x0,0x9,0xD,0x4,0x4,0xA,0xA,0x9,0x1,0x2,0xB,0xC,0xD,0x7,0x5,0x7,0xD,0x3,0xD,0x4,0x3,0x3,0x4};

        String uuStr = new String(uu);
        logger.info("uuStr:"+uuStr);
        HashMap<String, Object> ff = new HashMap<>();
        ff.put("uu", uu);
        ObjectMapper om = new ObjectMapper();

        String ss = om.writeValueAsString(ff);
        logger.info("ss:" + ss);
        HashMap map = new ObjectMapper().readValue(ss, HashMap.class);
        logger.info("ff:" + map.get("uu"));
    }
    @Test
    void regex(){

//        byte[] oriBytes = {0xd, 0x7,0x2, 0x0,0xe, 0x1,0x0,0x0,0xb,0x1,0x4,0xc,0x5,0x6,0x8,0x9,0x2,0xf,0xa,0xf,0x7,0x1,0x0,0x6,0xf,0x3,0x2,0x4,0x9,0x9,0x0,0x0};

        byte[] oriBytes1 = {(byte)0xD7,(byte)0x20,(byte)0xE1,(byte)0x00,
                (byte)0xB1,(byte)0x4C,(byte)0x56,(byte)0x89,(byte)0x2F,
                (byte)0xAF,(byte)0x71,(byte)0x06,(byte)0xF3,(byte)0x24,
                (byte)0x99,(byte)0x00};
        byte[] oriBytes2 = {(byte)0xD7,(byte)0x20,(byte)0xE1,(byte)0x00,
                (byte)0xB1,(byte)0x4C,(byte)0x56,(byte)0x89,(byte)0x2F,
                (byte)0xAF,(byte)0x71,(byte)0x06,(byte)0xF3,(byte)0x24,
                (byte)0x99,(byte)0x00};

        byte[] oriBytes = {(byte)0x97,(byte)0x2F,(byte)0x9D,(byte)0x60,
                (byte)0x18,(byte)0x0F,(byte)0x11,(byte)0xEA,(byte)0x8B,
                (byte)0x48,(byte)0x02,(byte)0x42,(byte)0xAC,(byte)0x12,
                (byte)0x00,(byte)0x02};


        BigInteger bigInteger1 = new BigInteger(1, oriBytes1);

        logger.info(bigInteger1.toString(16));

        String encodedString = Base64.encodeBase64String(oriBytes1);
//        encodedString = "ANcg4QCxTFaJL69xBvMkmQ==";
//        encodedString = "1yDhALFMVokvr3EG8ySZ==";
//        encodedString = "ANcg4QCxTFaJL69xBvMkmQ==";
        logger.info(""+oriBytes[0]);
        logger.info(encodedString);
        Pattern p = Pattern.compile("^__binary__(?<substr>.*)__yranib__$");
//        String text = "__binary__" + "ANcg4QCxTFaJL69xBvMkmQ=="+"__yranib__";
        String text = "__binary__" + encodedString +"__yranib__";
        Matcher m = p.matcher(text);
        String subStr = null;
        if (m.find()){
            subStr = m.group("substr");
//            String newSubStr = new String(Base64.decodeBase64(subStr));
//            logger.info(newSubStr);
            byte[] decodedBytes = Base64.decodeBase64(subStr);
            for (byte b: decodedBytes) {
                logger.info(String.format("%02x", b));

            }

            String exactString = new String(Hex.encodeHex(decodedBytes));

            BigInteger bigInteger = new BigInteger(1, decodedBytes);
            logger.info(bigInteger.toString(16));
            logger.info(exactString);

        }
        logger.info("get subStr: " + subStr);
    }
    @Test
    void getsssss(){
        DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");

        long timestamp = System.nanoTime() / 1000;
        logger.info("sss:" + System.currentTimeMillis());
        logger.info("ssssss:" + timestamp);
        LocalDateTime lsss = LocalDateTime.now();
        Instant instant = lsss.toInstant(ZoneOffset.UTC);
        long lsssTimestamp = lsss.toEpochSecond(ZoneOffset.UTC);
        logger.info("date string: " + lsss.format(DAY_FORMATTER) + ", lsssTimestamp:" + lsssTimestamp);
        logger.info("instant: " + instant.toString() + "; timestamp: " + instant.toEpochMilli());
        //写啥type的返回啥type的。Z,UTC/+08:00/GMT+8,但是格式会标准一点，GMT+8->GMT+08:00
        logger.info("ZoneId:" + ZoneId.of("GMT+8").getId());
        ZonedDateTime zonedDateTime = lsss.atZone(ZoneId.of("UTC"));



    }

    @Test
    public void testColumnType(){
        ColumnType columnType = ColumnType.byCode(-4 & 0xFF);
        logger.info(""+columnType.getCode());

        int typeCode = (-2) & 0xFF, meta = 65040, length = 0;
        if (typeCode == ColumnType.STRING.getCode()) {
            if (meta >= 256) {
                int meta0 = meta >> 8, meta1 = meta & 0xFF;
                if ((meta0 & 0x30) != 0x30) {
                    typeCode = meta0 | 0x30;
                    length = meta1 | (((meta0 & 0x30) ^ 0x30) << 4);
                } else {
                    // mysql-5.6.24 sql/rpl_utility.h enum_field_types (line 278)
                    if (meta0 == ColumnType.ENUM.getCode() || meta0 == ColumnType.SET.getCode()) {
                        typeCode = meta0;
                    }
                    length = meta1;
                }
            } else {
                length = meta;
            }
        }
        logger.info("type code: "+typeCode+" length: " + length);

    }
    @Test
    public void testDede(){
        try {
            new DebeziumReplicator().startDebeziumEngine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

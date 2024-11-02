package io.github.vimisky.luta.mysql.binlog.helper;

import io.github.vimisky.luta.mysql.binlog.helper.replicator.DebeziumReplicator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class LutaMysqlBinlogHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(LutaMysqlBinlogHelperApplication.class, args);
    }

}

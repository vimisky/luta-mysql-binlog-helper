package io.github.vimisky.luta.mysql.binlog.helper.replicator;

public interface BinlogOutput {
    boolean output(String binlogTransactionJson) throws Exception;
}

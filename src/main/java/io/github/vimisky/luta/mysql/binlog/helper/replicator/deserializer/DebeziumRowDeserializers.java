/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.AbstractRowsEventDataDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.DeleteRowsEventDataDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.UpdateRowsEventDataDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.WriteRowsEventDataDeserializer;
import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;

//import io.debezium.DebeziumException;
//import io.debezium.config.CommonConnectorConfig.EventProcessingFailureHandlingMode;

/**
 * Custom deserializers for the binlog client library.<p></p>
 *
 * A few of the {@link AbstractRowsEventDataDeserializer} convert row data into {@link Date}, {@link Time},
 * and {@link Timestamp} values using {@link Calendar} instances (and thus implicitly uses the local timezone
 * to calculate the milliseconds past epoch. Rather than perform this conversion, we prefer to convert the
 * raw values directly into {@link LocalDate}, {@link LocalTime}, and {@link OffsetDateTime}.<p></p>
 *
 * Unfortunately, all the methods used to deserialize individuals values are defined on the {@link AbstractRowsEventDataDeserializer}
 * base class, and inherited by the {@link DeleteRowsEventDataDeserializer}, {@link UpdateRowsEventDataDeserializer},
 * and {@link WriteRowsEventDataDeserializer} subclasses. Since we cannot provide a new base class, the simplest
 * way to override is to subclass each of these 3 subclasses and override the methods on them all.<p></p>
 *
 * See <a href="https://dev.mysql.com/doc/refman/5.0/en/datetime.html">MySQL Date Time</a> documentation.
 *
 * @author Randall Hauch
 * @author Chris Cranford
 */
public class DebeziumRowDeserializers {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumRowDeserializers.class);

    /**
     * A specialization of {@link DeleteRowsEventDataDeserializer} that converts {@code DATE}, {@code TIME},
     * {@code DATETIME}, and {@code TIMESTAMP} values to {@link LocalDate}, {@link LocalTime}, {@link LocalDateTime},
     * and {@link OffsetDateTime} objects, respectively.
     */
    public static class DeleteRowsDeserializer extends DeleteRowsEventDataDeserializer {

        public DeleteRowsDeserializer(Map<Long, TableMapEventData> tableMapEventByTableId) {
            super(tableMapEventByTableId);
        }

        @Override
        protected Serializable deserializeString(int length, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeString(length, inputStream);
        }

        @Override
        protected Serializable deserializeVarString(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeVarString(meta, inputStream);
        }

        @Override
        protected Serializable deserializeDate(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDate(inputStream);
        }

        @Override
        protected Serializable deserializeDatetime(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDatetime(inputStream);
        }

        @Override
        protected Serializable deserializeDatetimeV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDatetimeV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeTimeV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimeV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeTime(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTime(inputStream);
        }

        @Override
        protected Serializable deserializeTimestamp(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimestamp(inputStream);
        }

        @Override
        protected Serializable deserializeTimestampV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimestampV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeYear(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeYear(inputStream);
        }
    }

    /**
     * A specialization of {@link UpdateRowsEventDataDeserializer} that converts {@code DATE}, {@code TIME},
     * {@code DATETIME}, and {@code TIMESTAMP} values to {@link LocalDate}, {@link LocalTime}, {@link LocalDateTime},
     * and {@link OffsetDateTime} objects, respectively.
     */
    public static class UpdateRowsDeserializer extends UpdateRowsEventDataDeserializer {

        public UpdateRowsDeserializer(Map<Long, TableMapEventData> tableMapEventByTableId) {
            super(tableMapEventByTableId);
        }

        @Override
        protected Serializable deserializeString(int length, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeString(length, inputStream);
        }

        @Override
        protected Serializable deserializeVarString(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeVarString(meta, inputStream);
        }

        @Override
        protected Serializable deserializeDate(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDate(inputStream);
        }

        @Override
        protected Serializable deserializeDatetime(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDatetime(inputStream);
        }

        @Override
        protected Serializable deserializeDatetimeV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDatetimeV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeTimeV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimeV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeTime(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTime(inputStream);
        }

        @Override
        protected Serializable deserializeTimestamp(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimestamp(inputStream);
        }

        @Override
        protected Serializable deserializeTimestampV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimestampV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeYear(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeYear(inputStream);
        }
    }

    /**
     * A specialization of {@link WriteRowsEventDataDeserializer} that converts {@code DATE}, {@code TIME},
     * {@code DATETIME}, and {@code TIMESTAMP} values to {@link LocalDate}, {@link LocalTime}, {@link LocalDateTime},
     * and {@link OffsetDateTime} objects, respectively.
     */
    public static class WriteRowsDeserializer extends WriteRowsEventDataDeserializer {

        public WriteRowsDeserializer(Map<Long, TableMapEventData> tableMapEventByTableId) {
            super(tableMapEventByTableId);
        }

        @Override
        protected Serializable deserializeString(int length, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeString(length, inputStream);
        }

        @Override
        protected Serializable deserializeVarString(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeVarString(meta, inputStream);
        }

        @Override
        protected Serializable deserializeDate(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDate(inputStream);
        }

        @Override
        protected Serializable deserializeDatetime(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDatetime(inputStream);
        }

        @Override
        protected Serializable deserializeDatetimeV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeDatetimeV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeTimeV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimeV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeTime(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTime(inputStream);
        }

        @Override
        protected Serializable deserializeTimestamp(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimestamp(inputStream);
        }

        @Override
        protected Serializable deserializeTimestampV2(int meta, ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeTimestampV2(meta, inputStream);
        }

        @Override
        protected Serializable deserializeYear(ByteArrayInputStream inputStream) throws IOException {
            return DebeziumRowDeserializers.deserializeYear(inputStream);
        }
    }

    private static final int MASK_10_BITS = (1 << 10) - 1;
    private static final int MASK_6_BITS = (1 << 6) - 1;

    /**
     * Converts a database string to a {@code byte[]}.
     *
     * @param length the number of bytes used to store the length of the string
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@code byte[]} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeString(int length, ByteArrayInputStream inputStream) throws IOException {
        // charset is not present in the binary log (meaning there is no way to distinguish between CHAR / BINARY)
        // as a result - return byte[] instead of an actual String
        int stringLength = length < 256 ? inputStream.readInteger(1) : inputStream.readInteger(2);
        return inputStream.read(stringLength);
    }

    /**
     * Converts a database string to a {@code byte[]}.
     *
     * @param meta the {@code meta} value containing the number of bytes in the length field
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@code byte[]} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeVarString(int meta, ByteArrayInputStream inputStream) throws IOException {
        int varcharLength = meta < 256 ? inputStream.readInteger(1) : inputStream.readInteger(2);
        return inputStream.read(varcharLength);
    }

    /**
     * Converts a {@code DATE} value to a {@link LocalDate}.
     * <p>
     * This method treats all <a href="http://dev.mysql.com/doc/refman/8.2/en/date-and-time-types.html">zero values</a>
     * for {@code DATE} columns as NULL, since they cannot be accurately represented as valid {@link LocalDate} objects.
     *
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@link LocalDate} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeDate(ByteArrayInputStream inputStream)
            throws IOException {
        int value = inputStream.readInteger(3);
        int day = value % 32; // 1-based day of the month
        value >>>= 5;
        int month = value % 16; // 1-based month number
        int year = value >> 4;
        if (year == 0 || month == 0 || day == 0) {
            return null;
        }
        try {
            return LocalDate.of(year, month, day);
        }
        catch (DateTimeException e) {
            return handleException( "date", e, LocalDate.EPOCH);
        }
    }

    /**
     * Converts a {@code TIME} value <em>without fractional seconds</em> to a {@link java.time.Duration}.
     *
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@link LocalTime} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeTime(ByteArrayInputStream inputStream) throws IOException {
        // Times are stored as an integer as `HHMMSS`, so we need to split out the digits ...
        int value = inputStream.readInteger(3);
        int[] split = split(value, 100, 3);
        int hours = split[2];
        int minutes = split[1];
        int seconds = split[0];
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    /**
     * Converts a {@code TIME} value <em>with fractional seconds</em> to a {@link java.time.Duration}.
     *
     * @param meta the {@code meta} value containing the fractional second precision, or {@code fsp}
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@link java.time.Duration} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeTimeV2(int meta, ByteArrayInputStream inputStream) throws IOException {
        /*
         * (in big endian)
         *
         * 1 bit sign (1= non-negative, 0= negative)
         * 1 bit unused (reserved for future extensions)
         * 10 bits hour (0-838)
         * 6 bits minute (0-59)
         * 6 bits second (0-59)
         * (3 bytes in total)
         *
         * + fractional-seconds storage (size depends on meta)
         * The fractional part:
         * read 1 byte, if meta is 1 or 2
         * read 2 bytes, if meta is 3 or 4
         * read 3 bytes, if meta is 5 or 6
         */
        int fractionBytes = (meta + 1) / 2;
        int payloadBytes = 3 + fractionBytes;
        int payloadBits = payloadBytes * 8;
        long time = bigEndianLong(inputStream.read(payloadBytes), 0, payloadBytes);
        boolean is_negative = bitSlice(time, 0, 1, payloadBits) == 0;

        if (is_negative) {
            /*
             * Negative numbers are stored in two's complement form.
             * To get the positive value of a negative number in two's complement form,
             * we should invert the bits of the number and add 1 to the result.
             * Then we can take the number from the corresponding bits on the final result.
             */
            time = ~time + 1;
        }

        int hours = bitSlice(time, 2, 10, payloadBits);
        int minutes = bitSlice(time, 12, 6, payloadBits);
        int seconds = bitSlice(time, 18, 6, payloadBits);
        int fraction = bitSlice(time, 24, fractionBytes * 8, payloadBits);
        long nanoSeconds = (long) (fraction / (0.0000001 * Math.pow(100, fractionBytes - 1)));
        final Duration duration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds).plusNanos(nanoSeconds);
        return is_negative && !duration.isNegative() ? duration.negated() : duration;
    }

    /**
     * Converts a {@code DATETIME} value <em>without fractional seconds</em> to a {@link LocalDateTime}.
     * <p>
     * This method treats all <a href="http://dev.mysql.com/doc/refman/8.2/en/date-and-time-types.html">zero values</a>
     * for {@code DATETIME} columns as NULL, since they cannot be accurately represented as valid {@link LocalDateTime} objects.
     *
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@link LocalDateTime} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeDatetime(ByteArrayInputStream inputStream)
            throws IOException {
        int[] split = split(inputStream.readLong(8), 100, 6);
        int year = split[5];
        int month = split[4]; // 1-based month number
        int day = split[3]; // 1-based day of the month
        int hours = split[2];
        int minutes = split[1];
        int seconds = split[0];
        int nanoOfSecond = 0; // This version does not support fractional seconds
        if (year == 0 || month == 0 || day == 0) {
            return null;
        }
        try {
            return LocalDateTime.of(year, month, day, hours, minutes, seconds, nanoOfSecond);
        }
        catch (DateTimeException e) {
            return handleException("datetime", e,
                    LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIDNIGHT));
        }
    }

    /**
     * Converts a {@code DATETIME} value <em>with fractional seconds</em> to a {@link LocalDateTime}.
     * <p>
     * This method treats all <a href="http://dev.mysql.com/doc/refman/8.2/en/date-and-time-types.html">zero values</a>
     * for {@code DATETIME} columns as NULL, since they cannot be accurately represented as valid {@link LocalDateTime} objects.
     *
     * @param meta the {@code meta} value containing the fractional second precision, or {@code fsp}
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@link LocalDateTime} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeDatetimeV2(int meta, ByteArrayInputStream inputStream)
            throws IOException {
        /*
         * (in big endian)
         *
         * 1 bit sign (1= non-negative, 0= negative)
         * 17 bits year*13+month (year 0-9999, month 0-12)
         * 5 bits day (0-31)
         * 5 bits hour (0-23)
         * 6 bits minute (0-59)
         * 6 bits second (0-59)
         *
         * (5 bytes in total)
         *
         * + fractional-seconds storage (size depends on meta)
         */
        long datetime = bigEndianLong(inputStream.read(5), 0, 5);
        int yearMonth = bitSlice(datetime, 1, 17, 40);
        int year = yearMonth / 13;
        int month = yearMonth % 13; // 1-based month number
        int day = bitSlice(datetime, 18, 5, 40); // 1-based day of the month
        int hours = bitSlice(datetime, 23, 5, 40);
        int minutes = bitSlice(datetime, 28, 6, 40);
        int seconds = bitSlice(datetime, 34, 6, 40);
        int nanoOfSecond = deserializeFractionalSecondsInNanos(meta, inputStream);
        if (year == 0 || month == 0 || day == 0) {
            return null;
        }
        try {
            return LocalDateTime.of(year, month, day, hours, minutes, seconds, nanoOfSecond);
        }
        catch (DateTimeException e) {
            return handleException("datetimeV2", e,
                    LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIDNIGHT));
        }
    }

    /**
     * Converts a {@code TIMESTAMP} value <em>without fractional seconds</em> to a {@link OffsetDateTime}.
     * MySQL stores the {@code TIMESTAMP} values as seconds past epoch in UTC, but the resulting {@link OffsetDateTime} will
     * be in the local timezone.
     *
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@link OffsetDateTime} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeTimestamp(ByteArrayInputStream inputStream) throws IOException {
        long epochSecond = inputStream.readLong(4);
        int nanoSeconds = 0; // no fractional seconds
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond, nanoSeconds), ZoneOffset.UTC);
    }

    /**
     * Converts a {@code TIMESTAMP} value <em>with fractional seconds</em> to a {@link OffsetDateTime}.
     * MySQL stores the {@code TIMESTAMP} values as seconds + fractional seconds past epoch in UTC, but the resulting
     * {@link OffsetDateTime} will be in the local timezone.
     *
     * @param meta the {@code meta} value containing the fractional second precision, or {@code fsp}
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@link OffsetDateTime} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeTimestampV2(int meta, ByteArrayInputStream inputStream) throws IOException {
        long epochSecond = bigEndianLong(inputStream.read(4), 0, 4);
        int nanoSeconds = deserializeFractionalSecondsInNanos(meta, inputStream);
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond, nanoSeconds), ZoneOffset.UTC);
    }

    /**
     * Converts a {@code YEAR} value to a {@link Year} object.
     *
     * @param inputStream the binary stream containing the raw binlog event data for the value
     * @return the {@link Year} object
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static Serializable deserializeYear(ByteArrayInputStream inputStream) throws IOException {
        return Year.of(1900 + inputStream.readInteger(1));
    }

    /**
     * Split the integer into multiple integers.<p></p>
     *
     * We can't use/access the private {@code split} method in the {@link AbstractRowsEventDataDeserializer} class, so we
     * replicate it here. Note the original is licensed under the same Apache Software License 2.0 as Debezium.
     *
     * @param value the long value
     * @param divider the value used to separate the individual values (e.g., 10 to separate each digit into a separate value,
     *            100 to separate each pair of digits into a separate value, 1000 to separate each 3 digits into a separate value,
     *            etc.)
     * @param length the expected length of the integer array
     * @return the integer values
     * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
     */
    protected static int[] split(long value, int divider, int length) {
        int[] result = new int[length];
        for (int i = 0; i < length - 1; i++) {
            result[i] = (int) (value % divider);
            value /= divider;
        }
        result[length - 1] = (int) value;
        return result;
    }

    /**
     * Read a big-endian long value.<p></p>
     *
     * We can't use/access the private {@code bigEndianLong} method in the {@link AbstractRowsEventDataDeserializer} class, so
     * we replicate it here. Note the original is licensed under the same Apache Software License 2.0 as Debezium.
     *
     * @param bytes the bytes containing the big-endian representation of the value
     * @param offset the offset within the {@code bytes} byte array where the value starts
     * @param length the length of the byte representation within the {@code bytes} byte array
     * @return the long value
     * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
     */
    protected static long bigEndianLong(byte[] bytes, int offset, int length) {
        long result = 0;
        for (int i = offset; i < (offset + length); i++) {
            byte b = bytes[i];
            result = (result << 8) | (b >= 0 ? (int) b : (b + 256));
        }
        return result;
    }

    /**
     * Slice an integer out of a portion of long value.<p></p>
     *
     * We can't use/access the private {@code bitSlice} method in the {@link AbstractRowsEventDataDeserializer} class, so
     * we replicate it here. Note the original is licensed under the same Apache Software License 2.0 as Debezium.
     *
     * @param value the long containing the integer encoded within it
     * @param bitOffset the number of bits where the integer value starts
     * @param numberOfBits the number of bits in the integer value
     * @param payloadSize the total number of bits used in the {@code value}
     * @return the integer value
     */
    protected static int bitSlice(long value, int bitOffset, int numberOfBits, int payloadSize) {
        long result = value >> payloadSize - (bitOffset + numberOfBits);
        return (int) (result & ((1 << numberOfBits) - 1));
    }

    /**
     * Read the binary input stream to obtain the number of nanoseconds given the <em>fractional seconds precision</em>, or
     * <em>fsp</em>.<p></p>
     *
     * We can't use/access the {@code deserializeFractionalSeconds} method in the {@link AbstractRowsEventDataDeserializer} class,
     * so we replicate it here with modifications to support nanoseconds rather than microseconds.
     * Note the original is licensed under the same Apache Software License 2.0 as Debezium.
     *
     * @param fsp the fractional seconds precision describing the number of digits precision used to store the fractional seconds
     *            (e.g., 1 for storing tenths of a second, 2 for storing hundredths, 3 for storing milliseconds, etc.)
     * @param inputStream the binary data stream
     * @return the number of nanoseconds
     * @throws IOException if there is an error reading from the binlog event data
     */
    protected static int deserializeFractionalSecondsInNanos(int fsp, ByteArrayInputStream inputStream) throws IOException {
        // Calculate the number of bytes to read, which is
        // '1' when fsp=(1,2) -- 7
        // '2' when fsp=(3,4) and -- 12
        // '3' when fsp=(5,6) -- 21
        int length = (fsp + 1) / 2;
        if (length > 0) {
            long fraction = bigEndianLong(inputStream.read(length), 0, length);
            // Convert the fractional value (which has extra trailing digit for fsp=1,3, and 5) to nanoseconds ...
            return (int) (fraction / (0.0000001 * Math.pow(100, length - 1)));
        }
        return 0;
    }

    private DebeziumRowDeserializers() {
    }

    private static Serializable handleException(String columnType, Exception e, Serializable defaultValue) throws IOException {
        LOGGER.error("Error while deserializing binlog data of {}: {}", columnType, e.getMessage());
        throw new IOException("Error while deserializing binlog data of " + columnType + ": " + e.getMessage());
    }
}

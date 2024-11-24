package io;

import dns.*;

import java.util.Map;

public final class WriterFactory {

    private static final Map<DnsRecordType, Writer<?>> writers = Map.of(
            DnsRecordType.HEADER, new DnsHeaderWriter(),
            DnsRecordType.QUESTION, new DnsQuestionWriter(),
            DnsRecordType.ANSWER, new DnsAnswerWriter(),
            DnsRecordType.MESSAGE, new DnsMessageWriter()
    );

    private WriterFactory() {
    }

    public static <T extends DnsRecord> byte[] write(T record) {
        return switch (record) {
            case DnsHeader header -> ((DnsHeaderWriter) writers.get(DnsRecordType.HEADER)).write(header);
            case DnsQuestion question -> ((DnsQuestionWriter) writers.get(DnsRecordType.QUESTION)).write(question);
            case DnsAnswer answer -> ((DnsAnswerWriter) writers.get(DnsRecordType.ANSWER)).write(answer);
            case DnsMessage message -> ((DnsMessageWriter) writers.get(DnsRecordType.MESSAGE)).write(message);
            case null -> new byte[0];
        };
    }

}

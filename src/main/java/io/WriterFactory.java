package io;

import dns.DnsAnswer;
import dns.DnsHeader;
import dns.DnsQuestion;
import dns.DnsRecord;

public final class WriterFactory {

    private static final DnsHeaderWriter headerWriter = new DnsHeaderWriter();

    private static final DnsQuestionWriter questionWriter = new DnsQuestionWriter();

    private static final DnsAnswerWriter answerWriter = new DnsAnswerWriter();

    public static <T extends DnsRecord> byte[] write(T record) {
        return switch (record) {
            case DnsHeader header -> headerWriter.write(header);
            case DnsQuestion question -> questionWriter.write(question);
            case DnsAnswer answer -> answerWriter.write(answer);
            case null, default -> new byte[0];
        };
    }

}

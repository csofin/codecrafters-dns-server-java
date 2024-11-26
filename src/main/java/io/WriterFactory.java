package io;

import dns.*;

import java.util.Optional;

public final class WriterFactory {

    public static <T extends DnsRecord> Optional<byte[]> write(T record) {
        return switch (record) {
            case DnsHeader header -> Optional.ofNullable(new DnsHeaderWriter().write(header));
            case DnsQuestion question -> Optional.ofNullable(new DnsQuestionWriter().write(question));
            case DnsAnswer answer -> Optional.ofNullable(new DnsAnswerWriter().write(answer));
            case DnsMessage message -> Optional.ofNullable(new DnsMessageWriter().write(message));
            case null -> Optional.empty();
        };
    }

}
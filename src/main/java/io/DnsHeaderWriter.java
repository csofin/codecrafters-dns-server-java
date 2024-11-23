package io;

import dns.DnsHeader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class DnsHeaderWriter implements Writer {

    private final DnsHeader header;

    public DnsHeaderWriter(DnsHeader header) {
        Objects.requireNonNull(header);

        this.header = header;
    }

    @Override
    public byte[] write() {
        return ByteBuffer
                .allocate(12)
                .order(ByteOrder.BIG_ENDIAN)
                .putShort(header.getIdentifier())
                .putShort(header.getFlags())
                .putShort(header.getQuestionCount())
                .putShort(header.getAnswerRecordsCount())
                .putShort(header.getAuthorityRecordsCount())
                .putShort(header.getAdditionalRecordsCount())
                .array();
    }

}

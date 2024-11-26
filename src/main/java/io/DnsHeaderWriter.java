package io;

import dns.DnsHeader;
import dns.DnsPacketIndicator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static dns.DnsHeader.*;

public class DnsHeaderWriter implements Writer<DnsHeader> {

    @Override
    public byte[] write(DnsHeader header) {
        return ByteBuffer
                .allocate(header.getSize())
                .order(ByteOrder.BIG_ENDIAN)
                .putShort(header.getIdentifier())
                .putShort(getFlags(header))
                .putShort(header.getQuestionCount())
                .putShort(header.getAnswerRecordsCount())
                .putShort(header.getAuthorityRecordsCount())
                .putShort(header.getAdditionalRecordsCount())
                .array();
    }

    private short getFlags(DnsHeader header) {
        short flags = 0;

        if (header.getQrIndicator() == DnsPacketIndicator.RESPONSE) {
            flags ^= FLAG_MASK_QR_INDICATOR;
        }

        flags ^= (short) ((header.getOperationCode() & FLAG_MASK_CODE) << 11);

        if (header.isAuthoritative()) {
            flags ^= FLAG_MASK_AUTHORITATIVE;
        }

        if (header.isTruncated()) {
            flags ^= FLAG_MASK_TRUNCATED;
        }

        if (header.isRecursionDesired()) {
            flags ^= FLAG_MASK_RECURSION_DESIRED;
        }

        if (header.isRecursionAvailable()) {
            flags ^= FLAG_MASK_RECURSION_AVAILABLE;
        }

        flags ^= (short) (header.getResponseCode() & FLAG_MASK_CODE);

        return flags;
    }

}

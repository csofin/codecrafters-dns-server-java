package io;

import dns.DnsHeader;
import dns.DnsPacketIndicator;

import java.nio.ByteBuffer;

import static dns.DnsHeader.*;

public class DnsHeaderReader implements Reader<DnsHeader> {

    @Override
    public DnsHeader read(ByteBuffer buffer) {
        DnsHeader.Builder header = DnsHeader.builder();
        header.withIdentifier(buffer.getShort(0));
        header.withQRIndicator(readPacketIndicator(buffer.getShort(2)));
        header.withOperationCode(readOperationCode(buffer.getShort(2)));
        header.isAuthoritative(readIsAuthoritative(buffer.getShort(2)));
        header.isTruncated(readIsTruncated(buffer.getShort(2)));
        header.isRecursionDesired(readIsRecursionDesired(buffer.getShort(2)));
        header.isRecursionAvailable(readIsRecursionAvailable(buffer.getShort(2)));
        header.withResponseCode(readResponseCode(buffer.getShort(2)));
        header.withQuestionCount(buffer.getShort(4));
        header.withAnswerRecordsCount(buffer.getShort(6));
        header.withAuthorityRecordsCount(buffer.getShort(8));
        header.withAdditionalRecordsCount(buffer.getShort(10));
        return header.build();
    }

    private DnsPacketIndicator readPacketIndicator(short flags) {
        return (flags & FLAG_MASK_QR_INDICATOR) != 0 ? DnsPacketIndicator.RESPONSE : DnsPacketIndicator.QUERY;
    }

    private byte readOperationCode(short flags) {
        return (byte) ((flags >> 11) & FLAG_MASK_CODE);
    }

    private boolean readIsAuthoritative(short flags) {
        return (flags & FLAG_MASK_AUTHORITATIVE) != 0;
    }

    private boolean readIsTruncated(short flags) {
        return (flags & FLAG_MASK_TRUNCATED) != 0;
    }

    private boolean readIsRecursionDesired(short flags) {
        return (flags & FLAG_MASK_RECURSION_DESIRED) != 0;
    }

    private boolean readIsRecursionAvailable(short flags) {
        return (flags & FLAG_MASK_RECURSION_AVAILABLE) != 0;
    }

    private byte readResponseCode(short flags) {
        return (byte) (flags & FLAG_MASK_CODE);
    }

}

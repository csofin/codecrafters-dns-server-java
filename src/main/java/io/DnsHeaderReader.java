package io;

import dns.DnsHeader;
import dns.DnsMessage;
import dns.DnsPacketIndicator;

import java.nio.ByteBuffer;

import static dns.DnsHeader.*;

public class DnsHeaderReader extends Reader {

    @Override
    public void readPart(ByteBuffer buffer, DnsMessage.Builder message) {
        ByteBuffer headerBuffer = buffer.slice(0, HEADER_SIZE_BYTES);

        DnsHeader.Builder header = DnsHeader.builder();
        header.withIdentifier(headerBuffer.getShort(0));
        short flags = headerBuffer.getShort(2);
        header.withQRIndicator(readPacketIndicator(flags));
        header.withOperationCode(readOperationCode(flags));
        header.isAuthoritative(readIsAuthoritative(flags));
        header.isTruncated(readIsTruncated(flags));
        header.isRecursionDesired(readIsRecursionDesired(flags));
        header.isRecursionAvailable(readIsRecursionAvailable(flags));
        header.withResponseCode(readResponseCode(flags));
        header.withQuestionCount(headerBuffer.getShort(4));
        header.withAnswerRecordsCount(headerBuffer.getShort(6));
        header.withAuthorityRecordsCount(headerBuffer.getShort(8));
        header.withAdditionalRecordsCount(headerBuffer.getShort(10));

        message.withHeader(header.build());
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

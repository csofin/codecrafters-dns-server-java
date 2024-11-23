package io;

import dns.DnsHeader;
import dns.DnsMessage;

import java.nio.ByteBuffer;

public class DnsMessageReader implements Reader<DnsMessage> {

    @Override
    public DnsMessage read(ByteBuffer buffer) {
        DnsMessage.Builder message = DnsMessage.builder();

        DnsHeaderReader headerReader = new DnsHeaderReader();
        DnsHeader header = headerReader.read(buffer.slice(0, DnsHeader.HEADER_SIZE_BYTES));
        message.withHeader(header);

        return message.build();
    }

}

package io;

import config.Environment;
import dns.DnsHeader;
import dns.DnsMessage;
import dns.DnsQuestion;

import java.nio.ByteBuffer;

public class DnsMessageReader implements Reader<DnsMessage> {

    @Override
    public DnsMessage read(ByteBuffer buffer) {
        DnsMessage.Builder message = DnsMessage.builder();

        DnsHeaderReader headerReader = new DnsHeaderReader();
        DnsHeader header = headerReader.read(buffer.slice(0, DnsHeader.HEADER_SIZE_BYTES));
        message.withHeader(header);

        DnsQuestionReader questionReader = new DnsQuestionReader();
        DnsQuestion question = questionReader.read(buffer.slice(DnsHeader.HEADER_SIZE_BYTES, Environment.BUFFER_SIZE - DnsHeader.HEADER_SIZE_BYTES));
        message.withQuestion(question);

        return message.build();
    }

}

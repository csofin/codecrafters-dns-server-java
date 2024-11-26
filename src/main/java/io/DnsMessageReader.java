package io;

import dns.DnsMessage;

import java.nio.ByteBuffer;

public class DnsMessageReader {

    public DnsMessage read(ByteBuffer buffer) {
        DnsMessage.Builder message = DnsMessage.builder();

        Reader headerReader = new DnsHeaderReader();
        Reader questionReader = new DnsQuestionReader();

        headerReader.setNextReader(questionReader);

        headerReader.read(buffer, message);

        return message.build();
    }

}

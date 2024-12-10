package io;

import dns.DnsMessage;

import java.nio.ByteBuffer;

public class DnsMessageReader {

    private final ByteBuffer buffer;

    public DnsMessageReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public DnsMessage read() {
        DnsMessage.Builder message = DnsMessage.builder();

        DnsHeaderReader headerReader = new DnsHeaderReader();

        DnsQuestionReader questionReader = new DnsQuestionReader();
        headerReader.setNextReader(questionReader);

        DnsAnswerReader answerReader = new DnsAnswerReader();
        questionReader.setNextReader(answerReader);

        headerReader.read(buffer, message);

        return message.build();
    }

}

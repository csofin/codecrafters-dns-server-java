package io;

import config.Environment;
import dns.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DnsMessageWriter implements Writer<DnsMessage> {

    @Override
    public byte[] write(DnsMessage message) {
        ByteBuffer buffer = ByteBuffer
                .allocate(Environment.BUFFER_SIZE)
                .order(ByteOrder.BIG_ENDIAN);

        WriterFactory.write(message.getHeader()).ifPresent(buffer::put);

        message.getQuestions().forEach(question -> WriterFactory.write(question).ifPresent(buffer::put));

        message.getAnswers().forEach(answer -> WriterFactory.write(answer).ifPresent(buffer::put));

        return buffer.array();
    }

}

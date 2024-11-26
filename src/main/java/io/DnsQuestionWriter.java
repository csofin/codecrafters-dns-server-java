package io;

import dns.DnsLabel;
import dns.DnsQuestion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class DnsQuestionWriter implements Writer<DnsQuestion> {

    @Override
    public byte[] write(DnsQuestion question) {
        ByteBuffer buffer = ByteBuffer
                .allocate(question.getSize())
                .order(ByteOrder.BIG_ENDIAN);

        List<byte[]> labels = question.getLabels()
                .stream()
                .map(DnsLabel::toByteArray)
                .toList();

        labels.forEach(buffer::put);

        return buffer
                .put((byte) 0)
                .putShort((short) question.getDnsType().getValue())
                .putShort((short) question.getDnsClass().getValue())
                .array();
    }

}

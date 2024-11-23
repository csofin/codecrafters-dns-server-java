package io;

import dns.DnsClass;
import dns.DnsLabel;
import dns.DnsQuestion;
import dns.DnsType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class DnsQuestionWriter implements Writer<DnsQuestion> {

    @Override
    public byte[] write(DnsQuestion question) {
        List<byte[]> labels = question.getLabels()
                .stream()
                .map(DnsLabel::toByteArray)
                .toList();

        int size = labels.stream().mapToInt(b -> b.length).sum()
                + 1 /* null byte */
                + DnsType.TYPE_SIZE_BYTES
                + DnsClass.CLASS_SIZE_BYTES;

        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
        labels.forEach(buffer::put);
        return buffer
                .put((byte) 0)
                .putShort((short) question.getDnsType().getValue())
                .putShort((short) question.getDnsClass().getValue())
                .array();
    }

}

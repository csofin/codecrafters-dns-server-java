package io;

import dns.DnsAnswer;
import dns.DnsLabel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class DnsAnswerWriter implements Writer<DnsAnswer> {

    @Override
    public byte[] write(DnsAnswer answer) {
        ByteBuffer buffer = ByteBuffer
                .allocate(answer.getSize())
                .order(ByteOrder.BIG_ENDIAN);

        List<byte[]> labels = answer.getLabels()
                .stream()
                .map(DnsLabel::toByteArray)
                .toList();

        labels.forEach(buffer::put);

        return buffer
                .put((byte) 0)
                .putShort((short) answer.getDnsType().getValue())
                .putShort((short) answer.getDnsClass().getValue())
                .putInt(answer.getTtl())
                .putShort((short) answer.getData().length)
                .put(answer.getData())
                .array();
    }

}

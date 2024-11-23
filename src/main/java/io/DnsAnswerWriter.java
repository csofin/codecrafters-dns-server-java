package io;

import dns.DnsAnswer;
import dns.DnsClass;
import dns.DnsLabel;
import dns.DnsType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class DnsAnswerWriter implements Writer<DnsAnswer> {

    @Override
    public byte[] write(DnsAnswer answer) {
        List<byte[]> labels = answer.getLabels()
                .stream()
                .map(DnsLabel::toByteArray)
                .toList();

        int size = labels.stream().mapToInt(b -> b.length).sum()
                + 1 /* null byte */
                + DnsType.TYPE_SIZE_BYTES
                + DnsClass.CLASS_SIZE_BYTES
                + 4 /* ttl */
                + 2 /* rdlength */
                + 4; /* rdata */

        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
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

package io;

import config.Environment;
import dns.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class DnsMessageWriter implements Writer<DnsMessage> {

    @Override
    public byte[] write(DnsMessage message) {
        return ByteBuffer
                .allocate(Environment.BUFFER_SIZE)
                .order(ByteOrder.BIG_ENDIAN)
                .put(WriterFactory.write(buildHeader(message)))
                .put(WriterFactory.write(buildQuestion(message)))
                .put(WriterFactory.write(buildAnswer(message)))
                .array();
    }

    private DnsHeader buildHeader(DnsMessage message) {
        return DnsHeader.builder()
                .withIdentifier(message.getHeader().getIdentifier())
                .withQRIndicator(DnsPacketIndicator.RESPONSE)
                .withOperationCode(message.getHeader().getOperationCode())
                .isAuthoritative(false)
                .isTruncated(false)
                .isRecursionDesired(message.getHeader().isRecursionDesired())
                .isRecursionAvailable(false)
                .withResponseCode((byte) (message.getHeader().getOperationCode() == 0 ? 0 : 4))
                .withQuestionCount((short) 1)
                .withAnswerRecordsCount((short) 1)
                .build();
    }

    private DnsQuestion buildQuestion(DnsMessage message) {
        return message.getQuestion();
    }

    private DnsAnswer buildAnswer(DnsMessage message) {
        byte[] rdata = new byte[4];
        Arrays.fill(rdata, (byte) 8);
        return DnsAnswer.builder()
                .forName(message.getQuestion().getName())
                .withDnsType(DnsType.A)
                .withDnsClass(DnsClass.IN)
                .withTTL(42)
                .withData(rdata)
                .build();
    }

}

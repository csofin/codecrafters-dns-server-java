package io;

import config.Environment;
import dns.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

public class DnsMessageWriter implements Writer<DnsMessage> {

    @Override
    public byte[] write(DnsMessage message) {
        ByteBuffer buffer = ByteBuffer.allocate(Environment.BUFFER_SIZE).order(ByteOrder.BIG_ENDIAN);

        DnsHeader header = buildHeader(message.getHeader(), message.getQuestions().size());
        WriterFactory.write(header).ifPresent(buffer::put);

        List<DnsQuestion> questions = List.copyOf(message.getQuestions());
        questions.forEach(question -> WriterFactory.write(question).ifPresent(buffer::put));

        List<DnsAnswer> answers = questions.stream().map(this::buildAnswer).toList();
        answers.forEach(answer -> WriterFactory.write(answer).ifPresent(buffer::put));

        return buffer.array();
    }

    private DnsHeader buildHeader(DnsHeader header, int recordCount) {
        return DnsHeader.builder()
                .withIdentifier(header.getIdentifier())
                .withQRIndicator(DnsPacketIndicator.RESPONSE)
                .withOperationCode(header.getOperationCode())
                .isAuthoritative(false)
                .isTruncated(false)
                .isRecursionDesired(header.isRecursionDesired())
                .isRecursionAvailable(false)
                .withResponseCode((byte) (header.getOperationCode() == 0 ? 0 : 4))
                .withQuestionCount((short) recordCount)
                .withAnswerRecordsCount((short) recordCount)
                .build();
    }

    private DnsAnswer buildAnswer(DnsQuestion question) {
        byte[] rdata = new byte[4];
        Arrays.fill(rdata, (byte) 8);
        return DnsAnswer.builder()
                .forName(question.getName())
                .withDnsType(DnsType.A)
                .withDnsClass(DnsClass.IN)
                .withTTL(42)
                .withData(rdata)
                .build();
    }

}

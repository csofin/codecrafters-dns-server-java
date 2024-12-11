package io;

import config.Environment;
import dns.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DnsAnswerReader extends Reader {

    @Override
    public void readPart(ByteBuffer buffer, DnsMessage.Builder message) {
        int usedSize = message.getHeader().getSize() + message.getQuestions().stream().mapToInt(DnsQuestion::getSize).sum();
        ByteBuffer answerBuffer = buffer.slice(usedSize, Environment.BUFFER_SIZE - usedSize);

        int answerCount = message.getHeader().getAnswerRecordsCount();
        if (answerCount == 0) {
            return;
        }

        for (int index = 0; index < answerCount; index++) {
            DnsAnswer.Builder answer = DnsAnswer.builder();

            byte value = answerBuffer.get();
            while (value > 0) {
                byte[] word = new byte[value];
                answerBuffer.get(word, 0, value);
                DnsLabel label = new DnsLabel(new String(word, StandardCharsets.UTF_8));
                answer = answer.addLabel(label);

                value = answerBuffer.get();
            }

            if (value < 0) {
                byte pointer = answerBuffer.get();
                List<DnsLabel> labels = message.getQuestions()
                        .stream()
                        .filter(dnsQuestion -> dnsQuestion.getLabels()
                                .stream()
                                .anyMatch(dnsLabel -> dnsLabel.getPosition() == pointer + 1))
                        .findFirst()
                        .map(DnsQuestion::getLabels)
                        .orElse(List.of())
                        .stream()
                        .toList();
                answer = answer.withLabels(labels);
            }

            DnsType dnsType = DnsType.findDnsType(answerBuffer.getShort()).orElse(null);
            answer = answer.withDnsType(dnsType);

            DnsClass dnsClass = DnsClass.findDnsClass(answerBuffer.getShort()).orElse(null);
            answer = answer.withDnsClass(dnsClass);

            answer = answer.withTTL(answerBuffer.getInt());

            short _ = answerBuffer.getShort();

            byte[] rdata = new byte[4];
            answerBuffer.get(rdata, 0, 4);
            answer = answer.withData(rdata);

            message = message.addAnswer(answer.build());
        }
    }

}

package io;

import config.Environment;
import dns.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DnsQuestionReader extends Reader {

    @Override
    public void readPart(ByteBuffer buffer, DnsMessage.Builder message) {
        int usedSize = message.getHeader().getSize();
        ByteBuffer questionBuffer = buffer.slice(usedSize, Environment.BUFFER_SIZE - usedSize);

        int questionCount = message.getHeader().getQuestionCount();
        if (questionCount == 0) {
            return;
        }

        for (int index = 0; index < questionCount; index++) {
            DnsQuestion.Builder question = DnsQuestion.builder();

            byte value = questionBuffer.get();
            int position = questionBuffer.arrayOffset() + questionBuffer.position();

            while (value > 0) {
                byte[] word = new byte[value];
                questionBuffer.get(word, 0, value);
                DnsLabel label = new DnsLabel(new String(word, StandardCharsets.UTF_8), position);
                question = question.addLabel(label);

                value = questionBuffer.get();
                position = questionBuffer.arrayOffset() + questionBuffer.position();
            }

            if (value < 0) {
                byte pointer = questionBuffer.get();
                List<DnsLabel> labels = message.getQuestions()
                        .stream()
                        .filter(dnsQuestion -> dnsQuestion.getLabels()
                                .stream()
                                .anyMatch(dnsLabel -> dnsLabel.getPosition() == pointer + 1))
                        .findFirst()
                        .map(dnsQuestion -> dnsQuestion
                                .getLabels()
                                .stream()
                                .filter(dnsLabel -> dnsLabel.getPosition() >= pointer)
                                .toList())
                        .orElse(List.of());
                question = question.withLabels(labels);
            }

            DnsType dnsType = DnsType.findDnsType(questionBuffer.getShort()).orElse(null);
            question = question.withDnsType(dnsType);

            DnsClass dnsClass = DnsClass.findDnsClass(questionBuffer.getShort()).orElse(null);
            question = question.withDnsClass(dnsClass);

            message = message.addQuestion(question.build());
        }
    }

}

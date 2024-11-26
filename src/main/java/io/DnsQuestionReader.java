package io;

import config.Environment;
import dns.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DnsQuestionReader extends Reader {

    @Override
    public void read(ByteBuffer buffer, DnsMessage.Builder message) {
        int usedSize = message.getHeader().getSize();
        ByteBuffer questionBuffer = buffer.slice(usedSize, Environment.BUFFER_SIZE - usedSize);

        int questionCount = message.getHeader().getQuestionCount();
        if (questionCount == 0) {
            return;
        }

        Map<Integer, DnsLabel> labelMap = new HashMap<>();

        for (int index = 0; index < questionCount; index++) {
            DnsQuestion.Builder question = DnsQuestion.builder();

            byte value = questionBuffer.get();
            int position = questionBuffer.arrayOffset() + questionBuffer.position();

            while (value > 0) {
                byte[] word = new byte[value];
                questionBuffer.get(word, 0, value);
                DnsLabel label = new DnsLabel(new String(word, StandardCharsets.UTF_8), position);
                labelMap.putIfAbsent(position, label);
                question = question.addLabel(label);

                value = questionBuffer.get();
                position = questionBuffer.arrayOffset() + questionBuffer.position();
            }

            if (value < 0) {
                byte pointer = questionBuffer.get();
                DnsLabel label = labelMap.get(pointer + 1);
                if (Objects.nonNull(label)) {
                    question = question.addLabel(label);
                }
            }

            DnsType dnsType = DnsType.findDnsType(questionBuffer.getShort()).orElse(null);
            question = question.withDnsType(dnsType);

            DnsClass dnsClass = DnsClass.findDnsClass(questionBuffer.getShort()).orElse(null);
            question = question.withDnsClass(dnsClass);

            message = message.addQuestion(question.build());
        }
    }

}

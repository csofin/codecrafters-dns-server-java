package io;

import dns.DnsClass;
import dns.DnsQuestion;
import dns.DnsType;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DnsQuestionReader implements Reader<DnsQuestion> {

    @Override
    public DnsQuestion read(ByteBuffer buffer) {
        DnsQuestion.Builder question = DnsQuestion.builder();

        List<byte[]> words = new ArrayList<>();
        byte value = buffer.get();
        while (value != 0) {
            byte[] word = new byte[value];
            buffer.get(word, 0, value);
            words.add(word);
            value = buffer.get();
        }

        String name = words.stream().map(String::new).collect(Collectors.joining("."));
        question = question.forName(name);

        DnsType dnsType = DnsType.findDnsType(buffer.getShort()).orElse(null);
        question = question.withDnsType(dnsType);

        DnsClass dnsClass = DnsClass.findDnsClass(buffer.getShort()).orElse(null);
        question = question.withDnsClass(dnsClass);

        return question.build();
    }

}

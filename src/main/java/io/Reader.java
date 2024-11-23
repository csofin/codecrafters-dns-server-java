package io;

import dns.DnsRecord;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface Reader<T extends DnsRecord> {
    T read(ByteBuffer buffer);
}

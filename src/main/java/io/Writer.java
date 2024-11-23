package io;

import dns.DnsRecord;

@FunctionalInterface
public interface Writer<T extends DnsRecord> {
    byte[] write(T record);
}

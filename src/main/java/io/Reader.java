package io;

import dns.DnsMessage;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class Reader {

    protected Reader nextReader;

    public void setNextReader(Reader nextReader) {
        this.nextReader = nextReader;
    }

    void read(ByteBuffer buffer, DnsMessage.Builder message) {
        readPart(buffer, message);
        if (Objects.nonNull(nextReader)) {
            nextReader.read(buffer, message);
        }
    }

    abstract void readPart(ByteBuffer buffer, DnsMessage.Builder message);

}

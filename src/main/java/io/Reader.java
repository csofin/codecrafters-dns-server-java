package io;

import dns.DnsMessage;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class Reader {

    protected Reader nextReader;

    public void setNextReader(Reader nextReader) {
        this.nextReader = nextReader;
    }

    abstract void read(ByteBuffer buffer, DnsMessage.Builder message);

    public void handOverToNextReader(ByteBuffer buffer, DnsMessage.Builder message) {
        if (Objects.nonNull(nextReader)) {
            nextReader.read(buffer, message);
        }
    }

}

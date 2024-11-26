package io;

import dns.DnsMessage;

import java.nio.ByteBuffer;

public abstract class Reader {

    protected Reader nextReader;

    public void setNextReader(Reader nextReader) {
        this.nextReader = nextReader;
    }

    abstract void read(ByteBuffer buffer, DnsMessage.Builder message);
}

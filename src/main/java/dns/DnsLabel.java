package dns;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class DnsLabel {

    private final String content;
    private int position;

    public DnsLabel(String content) {
        Objects.requireNonNull(content, "Content must not be null.");

        this.content = content;
    }

    public DnsLabel(String content, int position) {
        this(content);
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public int getPosition() {
        return position;
    }

    public byte[] toByteArray() {
        return ByteBuffer
                .allocate(1 + content.length())
                .order(ByteOrder.BIG_ENDIAN)
                .put((byte) content.length())
                .put(content.getBytes(StandardCharsets.UTF_8))
                .array();
    }

}

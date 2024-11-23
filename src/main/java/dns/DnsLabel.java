package dns;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record DnsLabel(String content) {

    public DnsLabel {
        Objects.requireNonNull(content, "Content must not be null.");
    }

    // <length><content>
    public byte[] toByteArray() {
        return ByteBuffer
                .allocate(1 + content.length())
                .order(ByteOrder.BIG_ENDIAN)
                .put((byte) content.length())
                .put(content.getBytes(StandardCharsets.UTF_8))
                .array();
    }

}

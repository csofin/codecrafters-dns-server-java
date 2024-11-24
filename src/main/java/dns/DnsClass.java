package dns;

import java.util.Arrays;
import java.util.Optional;

/**
 * https://www.rfc-editor.org/rfc/rfc1035#section-3.2.4
 */
public enum DnsClass {
    // the Internet
    IN(1),
    // the CSNET class
    @Deprecated CS(2),
    // the CHAOS class
    CH(3),
    // Hesiod [Dyer 87]
    HS(4);

    public static final int CLASS_SIZE_BYTES = 2;

    private final int value;

    DnsClass(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Optional<DnsClass> findDnsClass(short value) {
        return Arrays.stream(DnsClass.values())
                .filter(c -> c.value == value)
                .findFirst();
    }
}

package dns;

import util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * https://www.rfc-editor.org/rfc/rfc1035#section-3.2.1
 */
public final class DnsAnswer implements DnsRecord {

    private final List<DnsLabel> labels;
    private final DnsType dnsType;
    private final DnsClass dnsClass;
    private final int ttl;
    private final byte[] data;

    public DnsAnswer(DnsAnswer.Builder builder) {
        Objects.requireNonNull(builder.name, "Name must not be null.");
        Objects.requireNonNull(builder.dnsType, "Type must not be null.");
        if (builder.dnsType != DnsType.A) {
            throw new UnsupportedOperationException("Only type A is supported.");
        }
        Objects.requireNonNull(builder.dnsClass, "Class must not be null.");

        this.labels = builder.labels.isEmpty() ?
                Validator.validateDomain(builder.name).stream().map(DnsLabel::new).toList() :
                List.copyOf(builder.labels);
        this.dnsType = builder.dnsType;
        this.dnsClass = builder.dnsClass;
        this.ttl = builder.ttl;
        this.data = Objects.requireNonNullElse(builder.data, new byte[0]);
    }

    public List<DnsLabel> getLabels() {
        return labels;
    }

    public DnsType getDnsType() {
        return dnsType;
    }

    public DnsClass getDnsClass() {
        return dnsClass;
    }

    public int getTtl() {
        return ttl;
    }

    public byte[] getData() {
        return data;
    }

    public static Builder builder() {
        return new DnsAnswer.Builder();
    }

    public static class Builder {

        private String name;
        private List<DnsLabel> labels = new ArrayList<>();
        private DnsType dnsType;
        private DnsClass dnsClass;
        private int ttl;
        private byte[] data;

        public Builder forName(String name) {
            this.name = name;
            return this;
        }

        public Builder withLabels(DnsLabel... labels) {
            this.labels = List.of(labels);
            return this;
        }

        public Builder addLabel(DnsLabel label) {
            this.labels.add(label);
            return this;
        }

        public Builder withDnsType(DnsType dnsType) {
            this.dnsType = dnsType;
            return this;
        }

        public Builder withDnsClass(DnsClass dnsClass) {
            this.dnsClass = dnsClass;
            return this;
        }

        public Builder withTTL(int ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public DnsAnswer build() {
            return new DnsAnswer(this);
        }

    }

}

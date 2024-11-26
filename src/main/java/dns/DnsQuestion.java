package dns;

import util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * https://www.rfc-editor.org/rfc/rfc1035#section-4.1.2
 */
public final class DnsQuestion implements DnsRecord {

    private final List<DnsLabel> labels;
    private final DnsType dnsType;
    private final DnsClass dnsClass;

    public DnsQuestion(DnsQuestion.Builder builder) {
        Objects.requireNonNull(builder.dnsType, "Type must not be null.");
        Objects.requireNonNull(builder.dnsClass, "Class must not be null.");

        this.labels = Objects.nonNull(builder.name) ?
                Validator.validateDomain(builder.name).stream().map(DnsLabel::new).toList() :
                List.copyOf(builder.labels);
        this.dnsType = builder.dnsType;
        this.dnsClass = builder.dnsClass;
    }

    public String getName() {
        return labels.stream().map(DnsLabel::getContent).collect(Collectors.joining("."));
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

    @Override
    public int getSize() {
        return labels.stream().mapToInt(l -> l.toByteArray().length).sum()
                + 1 /* null byte */
                + DnsType.TYPE_SIZE_BYTES
                + DnsClass.CLASS_SIZE_BYTES;
    }

    public static Builder builder() {
        return new DnsQuestion.Builder();
    }

    public static class Builder {

        private String name;
        private List<DnsLabel> labels = new ArrayList<>();
        private DnsType dnsType;
        private DnsClass dnsClass;

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

        public DnsQuestion build() {
            return new DnsQuestion(this);
        }

    }

}

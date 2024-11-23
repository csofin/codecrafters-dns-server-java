package dns;

import util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * https://www.rfc-editor.org/rfc/rfc1035#section-4.1.2
 */
public final class DnsQuestion implements DnsRecord {

    private final List<DnsLabel> labels;
    private final DnsType dnsType;
    private final DnsClass dnsClass;

    public DnsQuestion(DnsQuestion.Builder builder) {
        Objects.requireNonNull(builder.name, "Name must not be null.");
        Objects.requireNonNull(builder.dnsType, "Type must not be null.");
        Objects.requireNonNull(builder.dnsClass, "Class must not be null.");

        this.labels = builder.labels.isEmpty() ?
                Validator.validateDomain(builder.name).stream().map(DnsLabel::new).toList() :
                List.copyOf(builder.labels);
        this.dnsType = builder.dnsType;
        this.dnsClass = builder.dnsClass;
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

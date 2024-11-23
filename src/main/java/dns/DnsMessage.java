package dns;

public record DnsMessage(DnsHeader header, DnsQuestion question) implements DnsRecord {
}

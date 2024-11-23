package dns;

public record DnsMessage(DnsHeader header, DnsQuestion question, DnsAnswer answer) implements DnsRecord {
}

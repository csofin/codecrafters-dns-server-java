package dns;

public sealed interface DnsRecord permits DnsHeader, DnsQuestion, DnsMessage {
}

package dns;

import java.net.SocketAddress;

public record DnsSocketMessage(DnsMessage dnsMessage, SocketAddress socketAddress) {
}

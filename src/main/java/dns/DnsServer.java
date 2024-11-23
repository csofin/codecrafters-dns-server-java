package dns;

import config.Environment;
import io.DnsHeaderWriter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public final class DnsServer {

    public DnsServer() {
    }

    public void start() {
        try (DatagramSocket serverSocket = new DatagramSocket(Environment.PORT)) {
            while (true) {
                final byte[] requestBuffer = new byte[512];
                final DatagramPacket request = new DatagramPacket(requestBuffer, 0, requestBuffer.length);
                serverSocket.receive(request);
                System.out.println("Received data");

                DnsHeader header = DnsHeader.builder()
                        .withIdentifier((short) 1234)
                        .withQRIndicator(DnsPacketIndicator.RESPONSE)
                        .build();

                DnsHeaderWriter writer = new DnsHeaderWriter(header);

                byte[] responseBuffer = ByteBuffer.allocate(512)
                        .put(writer.write())
                        .array();

                final DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length, request.getSocketAddress());
                serverSocket.send(response);
            }
        } catch (IOException ioe) {
            System.err.printf("IOException: %s%n", ioe.getMessage());
        }
    }

}

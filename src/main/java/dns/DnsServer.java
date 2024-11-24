package dns;

import config.Environment;
import io.*;

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
                final byte[] requestBuffer = new byte[Environment.BUFFER_SIZE];
                final DatagramPacket request = new DatagramPacket(requestBuffer, 0, requestBuffer.length);
                serverSocket.receive(request);
                DnsMessage message = new DnsMessageReader().read(ByteBuffer.wrap(requestBuffer));

                byte[] responseBuffer = WriterFactory.write(message);
                final DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length, request.getSocketAddress());
                serverSocket.send(response);
            }
        } catch (IOException ioe) {
            System.err.printf("IOException: %s%n", ioe.getMessage());
        }
    }

}

package dns;

import config.Environment;
import io.DnsAnswerWriter;
import io.DnsHeaderWriter;
import io.DnsQuestionWriter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
                        .withQuestionCount((short) 1)
                        .withAnswerRecordsCount((short) 1)
                        .build();

                DnsQuestion question = DnsQuestion.builder()
                        .forName("codecrafters.io")
                        .withDnsType(DnsType.A)
                        .withDnsClass(DnsClass.IN)
                        .build();

                byte[] rdata = new byte[4];
                Arrays.fill(rdata, (byte) 8);

                DnsAnswer answer = DnsAnswer.builder()
                        .forName("codecrafters.io")
                        .withDnsType(DnsType.A)
                        .withDnsClass(DnsClass.IN)
                        .withTTL(42)
                        .withData(rdata)
                        .build();

                DnsHeaderWriter headerWriter = new DnsHeaderWriter(header);
                DnsQuestionWriter questionWriter = new DnsQuestionWriter(question);
                DnsAnswerWriter answerWriter = new DnsAnswerWriter(answer);

                byte[] responseBuffer = ByteBuffer.allocate(512)
                        .put(headerWriter.write())
                        .put(questionWriter.write())
                        .put(answerWriter.write())
                        .array();

                final DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length, request.getSocketAddress());
                serverSocket.send(response);
            }
        } catch (IOException ioe) {
            System.err.printf("IOException: %s%n", ioe.getMessage());
        }
    }

}

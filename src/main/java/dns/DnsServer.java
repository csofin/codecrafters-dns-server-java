package dns;

import config.Environment;
import io.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public final class DnsServer {

    public DnsServer() {
    }

    public void start() {
        try (DatagramSocket serverSocket = new DatagramSocket(Environment.PORT)) {
            while (true) {
                DnsSocketMessage request = receive(serverSocket);
                DnsMessage message = Objects.isNull(Environment.getInstance().getFwdSocketAddress()) ? resolve(request.dnsMessage()) : forward(request.dnsMessage());
                DnsSocketMessage response = new DnsSocketMessage(message, request.socketAddress());
                send(serverSocket, response);
            }
        } catch (IOException ioe) {
            System.err.printf("IOException: %s%n", ioe.getMessage());
        }
    }

    private DnsSocketMessage receive(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[Environment.BUFFER_SIZE];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, Environment.BUFFER_SIZE);
        socket.receive(datagramPacket);
        DnsMessage message = new DnsMessageReader(ByteBuffer.wrap(buffer)).read();
        return new DnsSocketMessage(message, datagramPacket.getSocketAddress());
    }

    private void send(DatagramSocket socket, DnsSocketMessage message) throws IOException {
        byte[] buffer = WriterFactory.write(message.dnsMessage()).orElse(new byte[0]);
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, message.socketAddress());
        socket.send(datagramPacket);
    }

    private DnsMessage resolve(DnsMessage request) {
        DnsMessage.Builder message = DnsMessage.builder();

        int totalRecords = request.getQuestions().size();
        DnsHeader header = DnsHeader.builder()
                .withIdentifier(request.getHeader().getIdentifier())
                .withQRIndicator(DnsPacketIndicator.RESPONSE)
                .withOperationCode(request.getHeader().getOperationCode())
                .isAuthoritative(false)
                .isTruncated(false)
                .isRecursionDesired(request.getHeader().isRecursionDesired())
                .isRecursionAvailable(false)
                .withResponseCode((byte) (request.getHeader().getOperationCode() == 0 ? 0 : 4))
                .withQuestionCount((short) totalRecords)
                .withAnswerRecordsCount((short) totalRecords)
                .build();
        message = message.withHeader(header);

        message = message.withQuestions(request.getQuestions());

        final byte[] rdata = new byte[4];
        Arrays.fill(rdata, (byte) 8);
        List<DnsAnswer> answers = message
                .getQuestions()
                .stream()
                .map(question -> DnsAnswer.builder()
                        .forName(question.getName())
                        .withDnsType(DnsType.A)
                        .withDnsClass(DnsClass.IN)
                        .withTTL(42)
                        .withData(rdata)
                        .build())
                .toList();
        message = message.withAnswers(answers);

        return message.build();
    }

    private DnsMessage forward(DnsMessage request) {
        try (ExecutorService service = Executors.newVirtualThreadPerTaskExecutor()) {
            DnsHeader forwardHeader = DnsHeader.builder()
                    .withIdentifier(request.getHeader().getIdentifier())
                    .withQRIndicator(DnsPacketIndicator.QUERY)
                    .withOperationCode(request.getHeader().getOperationCode())
                    .withQuestionCount((short) 1)
                    .isRecursionDesired(request.getHeader().isRecursionDesired())
                    .build();

            List<CompletableFuture<DnsMessage>> forwardMessageTasks = request.getQuestions().stream()
                    .map(question -> DnsMessage.builder()
                            .withHeader(forwardHeader)
                            .addQuestion(question)
                            .build())
                    .map(this::messageForwarder)
                    .map(supplier -> CompletableFuture.supplyAsync(supplier, service))
                    .toList();

            List<DnsAnswer> answers = CompletableFuture.allOf(forwardMessageTasks.toArray(CompletableFuture[]::new))
                    .thenApply(_ ->
                            forwardMessageTasks.stream()
                                    .map(CompletableFuture::join)
                                    .map(DnsMessage::getAnswers)
                                    .flatMap(Collection::stream)
                                    .filter(Objects::nonNull)
                                    .toList())
                    .join();

            return DnsMessage.builder()
                    .withHeader(DnsHeader.builder()
                            .withIdentifier(request.getHeader().getIdentifier())
                            .withQRIndicator(DnsPacketIndicator.RESPONSE)
                            .withOperationCode(request.getHeader().getOperationCode())
                            .withQuestionCount((short) request.getQuestions().size())
                            .withAnswerRecordsCount((short) answers.size())
                            .isRecursionDesired(request.getHeader().isRecursionDesired())
                            .withResponseCode((byte) (request.getHeader().getOperationCode() == 0 ? 0 : 4))
                            .build())
                    .withQuestions(request.getQuestions())
                    .withAnswers(answers)
                    .build();
        }
    }

    private Supplier<DnsMessage> messageForwarder(DnsMessage message) {
        return () -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                DnsSocketMessage request = new DnsSocketMessage(message, Environment.getInstance().getFwdSocketAddress());
                send(socket, request);
                DnsSocketMessage response = receive(socket);
                return response.dnsMessage();
            } catch (IOException | NoSuchElementException e) {
                System.err.println(e.getMessage());
                return null;
            }
        };
    }

}

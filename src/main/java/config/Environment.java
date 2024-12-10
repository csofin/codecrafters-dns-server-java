package config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class Environment {

    private static class LazyHolder {
        public static Environment INSTANCE = new Environment();
    }

    public static int PORT = 2053;

    public static int BUFFER_SIZE = 512;

    private SocketAddress fwdSocketAddress;

    private Environment() {
    }

    public static Environment getInstance() {
        return LazyHolder.INSTANCE;
    }

    public SocketAddress getFwdSocketAddress() {
        return fwdSocketAddress;
    }

    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if ("--resolver".equals(args[i])) {
                String address = args[i + 1];
                String[] parts = address.split(":");
                try {
                    this.fwdSocketAddress = new InetSocketAddress(
                            InetAddress.getByName(parts[0]),
                            parts.length == 1 ? 53 : Integer.parseInt(parts[1])
                    );
                } catch (UnknownHostException uhe) {
                    System.err.println(uhe.getMessage());
                }
                break;
            }
        }
    }

}

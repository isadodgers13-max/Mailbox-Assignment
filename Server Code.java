import java.io.*;
import java.net.*;
import java.util.*;

public class MailboxServer {

    // Shared mailbox storage
    private static Map<String, List<String>> mailbox = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            // Continuous listening (Requirement ✔)
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                // Multi-threading (Requirement ✔)
                new ClientHandler(clientSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thread for each client
    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String message;

                // Continuous receive (Requirement ✔)
                while ((message = in.readLine()) != null) {

                    // Message format: SEND username message
                    String[] parts = message.split(" ", 3);

                    if (parts[0].equalsIgnoreCase("SEND")) {
                        String user = parts[1];
                        String msg = parts[2];

                        // Store message (Data Storage ✔)
                        mailbox.putIfAbsent(user, new ArrayList<>());
                        mailbox.get(user).add(msg);

                        out.println("Message stored for " + user);
                    }

                    // FETCH username
                    else if (parts[0].equalsIgnoreCase("FETCH")) {
                        String user = parts[1];

                        List<String> messages = mailbox.getOrDefault(user, new ArrayList<>());

                        for (String m : messages) {
                            out.println("MSG: " + m);
                        }

                        mailbox.remove(user); // clear after reading
                        out.println("END");
                    }

                    else {
                        out.println("Invalid command");
                    }
                }

            } catch (IOException e) {
                System.out.println("Client disconnected");
            }
        }
    }
}

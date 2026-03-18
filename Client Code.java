import java.io.*;
import java.net.*;

public class MailboxClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;

        try {
            Socket socket = new Socket(host, port);

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Thread for receiving messages (Client Responsiveness ✔)
            new Thread(() -> {
                try {
                    String response;
                    while ((response = serverIn.readLine()) != null) {
                        System.out.println("Server: " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Main thread handles sending
            while (true) {
                String userInput = input.readLine();
                out.println(userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import java.io.*; 
import java.net.*;
import java.util.*;


public class WordleServer {
    private static String IP;
    private static int PORT;
    private static ServerSocket server_socket;
    private List<WordleClientHandler> clients;

    public WordleServer(int PORT) throws IOException {
        // this.IP = IP;
        this.PORT = PORT;

        this.init_socket();
        clients = new ArrayList<WordleClientHandler>();
    }

    private void init_socket() throws IOException {
        try {
            server_socket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw e;
        }
    }

    private static void close_socket() throws IOException {
        try {
            if (server_socket != null && !server_socket.isClosed())
                server_socket.close();
        } catch (IOException e) {
            throw e;
        }
    }

    private void run() throws IOException {
        try {
            while (true) {
                Socket new_client = server_socket.accept();
                WordleClientHandler client_handler = new WordleClientHandler(new_client);
                add_list(client_handler);
                client_handler.run();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public void add_list(WordleClientHandler clh)
    {
        clients.add(clh);
    }

    public void remove_list(WordleClientHandler clh)
    {
        clients.remove(clh);
        System.out.println(clients.size());
    }

    public static void main(String[] args) {
        WordleServer server = null; 
        try {
            server = new WordleServer(2779);
            //System.out.println("Starting Server in port : " + PORT);
            server.run();
        } catch (IOException e) {
            try {
                close_socket();
            } catch (IOException e1) {
                System.out.println("Error: " + e1.getMessage());
            }
            System.out.println("Error: " + e.getMessage());
        }

    }
}
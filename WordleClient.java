import java.io.*; 
import java.net.*;
import java.util.*;


public class WordleClient {
    private String IP;
    private int PORT;
    private static int TIMEOUT = 1000 * 20;
    private Socket socket;
    private InputStream input_stream;
    private OutputStream output_stream;
    private BufferedReader input_stream_reader;
    private BufferedReader user_input;

    public WordleClient(String IP, int PORT) throws IOException {
        this.IP = IP;
        this.PORT = PORT;

        try {
            socket = new Socket(IP, PORT);
            socket.setSoTimeout(TIMEOUT);
            input_stream = socket.getInputStream();
            output_stream = socket.getOutputStream();
            input_stream_reader = new BufferedReader(new InputStreamReader(input_stream));
            user_input = new BufferedReader(new InputStreamReader(System.in));
        } catch (SocketException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }

    }

    private void close_socket() {
        try {
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String get_packet() {
        String packet = "";

        try {
            packet = input_stream_reader.readLine();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return packet;
    }

    private void send_packet(String packet) throws IOException {
        try {
            output_stream.write(packet.getBytes());
            output_stream.flush();
        } catch (IOException e) {
            throw e;
        }
    }

    private void process_packet(String packet) {
        WordleEnum packet_type = WordleProtocol.get_packet_type(packet);

        switch (packet_type) {
            case WORD:
                System.out.println(packet);
                break;
            case GAMEOVER:
                String hint = WordleProtocol.get_word(WordleEnum.GAMEOVER, packet);
                if (hint.equals("GGGGG")) {
                    System.out.println("You win");
                } else {
                    System.out.println("You lose");
                }
                close_socket();
                System.exit(0);
                break;
            case NONEXISTENT:
                System.out.println("try another word !");
                break;
            default:
                break;
        }
    }

    private String get_user_input() {
        String input = null;
        try {
            input = user_input.readLine();
        } catch (IOException e) {
            System.out.println("Error while reading user input : " + e);
        }

        return input;
    }

    public String get_user_choice() throws Exception {
        System.out.print("Your choice : ");
        String input = get_user_input();

        if (input == null) {
            throw new Exception("Error while reading user input");
        }

        switch (input) {
            case "1":
                return WordleProtocol.write_packet(WordleEnum.CHEAT);

            case "2":
                System.out.print("Enter a word : ");
                String word = get_user_input();
                if(word.length() != 5)
                {
                    return null;
                }
                return WordleProtocol.write_packet(WordleEnum.TRY, word);

            case "3":
                return WordleProtocol.write_packet(WordleEnum.QUIT);

            default:
                System.out.println("Input available are 1, 2, or 3");
                return null;
        }
    }

    public void run() throws IOException, Exception {
        System.out.print("1) Cheat\n");
        System.out.print("2) Propose a word\n");
        System.out.print("3) Quit\n");
        while (true) {

            String user_choice = get_user_choice();

            if(user_choice == null) { continue; }

            send_packet(user_choice);

            String packet_from_srvr = get_packet();
            if (packet_from_srvr == null) {
                close_socket();
                break;
            }
            if (packet_from_srvr == "") {
                continue;
            }
            process_packet(packet_from_srvr);

        }
    }

    public static void main(String[] args) {
        WordleClient client = null;
        try {
            client = new WordleClient("127.0.0.1", 2779);
            client.run();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}

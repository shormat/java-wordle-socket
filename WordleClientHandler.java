import java.io.*;
import java.net.*;
import java.util.*;

public class WordleClientHandler implements Runnable {

    private Socket socket;
    private int TIMEOUT = 1000 * 20; // 20 secs
    private InputStream input_stream;
    private OutputStream output_stream;
    private BufferedReader input_stream_reader;

    private int attempts = 5; // 5, 4, 3, 2, 1, 0 => 6 attempts
    private String word_to_guess;

    public WordleClientHandler(Socket socket) throws IOException {
        this.socket = socket;

        try {
            socket.setSoTimeout(TIMEOUT);
            input_stream = socket.getInputStream();
            output_stream = socket.getOutputStream();
            input_stream_reader = new BufferedReader(new InputStreamReader(input_stream));
        } catch (SocketException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }

        word_to_guess = WordleAlgorithm.choose_word();

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

    private String process_packet(String packet) {
        WordleEnum packet_type = WordleProtocol.get_packet_type(packet);

        switch (packet_type) {
            case TRY:
                String input = WordleProtocol.get_word(packet_type, packet);
                if (input == null) {
                    return WordleProtocol.write_packet(WordleEnum.WRONG);
                }

                String hints = WordleAlgorithm.verify_word(input, word_to_guess);
                String ret;
                if (!WordleAlgorithm.wordset_contains(input)) {
                    return WordleProtocol.write_packet(WordleEnum.NONEXISTENT);
                }
                else if (hints.equals("GGGGG") || attempts <= 0) {
                    ret = WordleProtocol.write_packet(WordleEnum.GAMEOVER, hints);
                } else {
                    ret = WordleProtocol.write_packet(WordleEnum.WORD, hints);
                }
                
                attempts--;
                
                return ret;

            case CHEAT:
                return WordleProtocol.write_packet(WordleEnum.WORD, word_to_guess.toUpperCase());
            case WRONG:
                return WordleProtocol.write_packet(WordleEnum.WRONG);
            case QUIT:
                return "QUIT";
            default:
                return WordleProtocol.write_packet(WordleEnum.WRONG);
        }

    }

    @Override
    public void run() {

        while (true) {
            try {
                String packet_from_client = get_packet();

                // client disconnected
                if (packet_from_client == null) {
                    break;
                }

                // empty packet, client maybe still connected
                if (packet_from_client == "") {
                    continue;
                }

                String packet_to_send = process_packet(packet_from_client);

                if (packet_to_send.equals("QUIT")) {
                    break;
                }

                send_packet(packet_to_send);
            } catch (IOException e) {
                close_socket();
                break;
            }
        }

        close_socket();
    }

}

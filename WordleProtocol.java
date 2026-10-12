public class WordleProtocol {

    private final static String TRY = "TRY";
    private final static String CHEAT = "CHEAT";
    private final static String QUIT = "QUIT";
    private final static String WRONG = "WRONG";
    private final static String GAMEOVER = "GAMEOVER";
    private final static String NONEXISTENT = "NONEXISTENT";
    private final static String END_PACKET = "\r\n";

    public static String get_word(WordleEnum type, String packet) {
        String[] tokens = packet.split(" ");

        if (tokens.length <= 0 || tokens.length > 2) {
            return null;
        }

        if ((type == WordleEnum.TRY) && tokens[1].length() == 5) {
            return tokens[1];
        }

        if ((type == WordleEnum.GAMEOVER) && (tokens[0].length() == 5) && tokens[0].matches("[BYG]+")) {
            return tokens[0];
        }

        return null;
    }

    public static WordleEnum get_packet_type(String packet) {
        if (packet == null) {
            return WordleEnum.WORD;
        }

        String[] tokens = packet.split(" ");

        if (tokens.length <= 0 || tokens.length > 2) {
            return WordleEnum.WRONG;
        }

        switch (tokens[0]) {
            case TRY:
                return WordleEnum.TRY;
            case CHEAT:
                return WordleEnum.CHEAT;
            case QUIT:
                return WordleEnum.QUIT;
            case GAMEOVER:
                return WordleEnum.GAMEOVER;
            case NONEXISTENT:
                return WordleEnum.NONEXISTENT;
            case WRONG:
                return WordleEnum.WRONG;
            default:
                // gameover case
                if (tokens.length == 2 && tokens[0].length() == 5 && tokens[0].matches("[BGY]+")) {
                    if (tokens[1].equals(GAMEOVER)) {
                        return WordleEnum.GAMEOVER;
                    }
                }
                // we have a word (return value from cheat packet)
                if (tokens.length == 1 && tokens[0].length() == 5) {
                    return WordleEnum.WORD;
                }

                return WordleEnum.WRONG;
        }

    }

    public static String write_packet(WordleEnum type, String word) {
        switch (type) {
            case TRY:
                return TRY + " " + word + END_PACKET;
            case CHEAT:
                return CHEAT + END_PACKET;
            case QUIT:
                return QUIT + END_PACKET;
            case GAMEOVER:
                return word + " " + GAMEOVER + END_PACKET;
            case NONEXISTENT:
                return NONEXISTENT + END_PACKET;
            case WRONG:
                return WRONG + END_PACKET;
            case WORD:
                return word + END_PACKET;
            default:
                return WRONG + END_PACKET;
        }
    }

    public static String write_packet(WordleEnum type) {
        switch (type) {
            case CHEAT:
                return CHEAT + END_PACKET;
            case QUIT:
                return QUIT + END_PACKET;
            case NONEXISTENT:
                return NONEXISTENT + END_PACKET;
            case WRONG:
                return WRONG + END_PACKET;
            default:
                return WRONG + END_PACKET;
        }
    }

}

public class Constants {
    public static final String CONFIG_FILE = "Common.cfg";
    public static final String PEER_FILE = "PeerInfo.cfg";
    public static final String FILENAME = "thefile";
    public static final String HEADER_HANDSHAKE = "P2PFILESHARINGPROJ";
    public static final String ZERO_HANDSHAKE = "0000000000";

    public static enum message {
        CHOKE(0),
        UNCHOKE(1),
        INTERESTED(2),
        NOT_INTERESTED(3),
        HAVE(4),
        BITFIELD(5),
        REQUEST(6),
        PIECE(7),
        COMPLETE(8);

        private final int message_type;
        private static message[] types = values();

        private message(int type) {
            this.message_type = type;
        }

        public int getType() {
            return this.message_type;
        }

        /**
         * Get the correct message type from list of message types
         * 
         * @param message_type - Integer value between 0 and 8 (inclusive) denoting type
         *                     of message
         * @return The type of message based on message_type
         */
        public static message valueOf(int message_type) {
            for (message m : types) {
                if (m.getType() == message_type)
                    return m;
            }
            return null;
        }
    }
}

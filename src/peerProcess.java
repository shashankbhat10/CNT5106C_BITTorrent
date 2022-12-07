import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class peerProcess {
    private static int sourcePeerID = -1;
    private static Config configObj = null;
    private static Util utilObj = null;
    private static int totalChunks = 0;
    private static int totalPeers = -1;
    private static int currentPeer = -1;
    private static int sourcePort = -1;
    private static boolean fileCompleted = false;
    private static Map<Integer, PeerNode> neighbours = new HashMap<>();
    private static LogUtility logFile = null;
    private static ConcurrentHashMap<Integer, Integer> bitFieldMap = new ConcurrentHashMap<>();
    private static AtomicInteger optimisticallyUnchokedPeer = new AtomicInteger(-1);
    private static FileUtility fileUtility = null;

    public class NeighbourUtility {
        int peerId = -1;
        PeerNode peerNode = null;
        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        boolean unchoked = false;
        byte[] message = null;

        class ConnectionThread implements Runnable {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                downloadRate.put(peerId, 0);
                sendBitField();
    
                try {
                    while(neighboursWhoHaveEntireFile < )
                }
            }

        }

        public NeighbourUtility(PeerNode peerNode, Socket socket) throws IOException {
            this.peerNode = peerNode;
            this.socket = socket;
            this.peerId = peerNode.getPeerId();
            message = new byte[5];
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }

        private void startConnection() {
            ConnectionThread tConnectionThread = new ConnectionThread();
            Thread neighbourThread = new Thread(tConnectionThread, "Thread_for_" + peerNode.getPeerId());
            neighbourThread.start();
            System.out.println("Thread process started for peerId: " + peerNode.getPeerId());
        }

        private byte[] getMessage(byte[] payload, int type) {
            int sizeOfPayload = 0;
            if (payload == null) {
                sizeOfPayload = 0;
            } else {
                sizeOfPayload = payload.length;
            }

            int totalLengthPayload = 1 + sizeOfPayload;
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            byte[] messageLength = byteBuffer.putInt(totalLengthPayload).array();

            byte messageType = (byte) ((char) type);

            byte[] message = new byte[messageLength.length + totalLengthPayload];

            int index = 0;
            for (int i = 0; i < messageLength.length; i++) {
                message[index++] = messageLength[i];
            }
            message[index++] = messageType;

            if (payload != null) {
                for (int i = 0; i < sizeOfPayload; i++) {
                    message[index++] = payload[i];
                }
            }

            return message;
        }

        private synchronized void sendBitField() {
            int[] bitField = new int[totalChunks];
            for (int i = 0; i < bitFieldMap.size(); i++) {
                bitField[i] = bitFieldMap.get(i);
            }

            byte[] payload = convertToByteArray(bitField);
            byte[] message = getMessage(payload, Constants.message.BITFIELD.getType());

            try {
                dataOutputStream.write(message);
                dataOutputStream.flush();
                logFile.sendBitfield(sourcePeerID, peerId);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        private synchronized void sendComplete() {
            byte[] message = getMessage(null, Constants.message.COMPLETE.getType());

            try {
                dataOutputStream.write(message);
                dataOutputStream.flush();
            } catch (Exception e) {
                // TODO: handle exception
                System.exit(0);
            }
        }

        private synchronized void sendHave(int pieceNumber) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            byte[] payload = byteBuffer.putInt(pieceNumber).array();
            byte[] message = getMessage(payload, Constants.message.HAVE.getType());

            try {
                dataOutputStream.write(message);
                dataOutputStream.flush();

                logFile.sendHave(sourcePeerID, peerId, pieceNumber);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        private synchronized void sendPiece(int pieceNumber) {
            try {
                if ((unchoked || (optimisticallyUnchokedPeer.get() == peerId)) && bitFieldMap.get(pieceNumber) == 1) {
                    byte[] piece = fileUtility.sendPiece(sourcePeerID, pieceNumber);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(4);

                    byte[] index = byteBuffer.putInt(pieceNumber).array();
                    byte[] payload = new byte[index.length + piece.length];

                    System.arraycopy(index, 0, payload, 0, index.length);
                    System.arraycopy(piece, 0, payload, 4, piece.length);

                    byte[] pieceMessage = getMessage(payload, Constants.message.PIECE.getType());

                    dataOutputStream.write(pieceMessage);
                    dataOutputStream.flush();

                    logFile.sendPiece(sourcePeerID, peerId, pieceNumber);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        private synchronized boolean checkPeerForInterestingPiece() {
            int[] peerBitField = peerNode.getBitField();
            int numberOfChunks = configObj.getNumberOfChunks();
            for (int i = 0; i < numberOfChunks; i++) {
                if (bitFieldMap.get(i) == 0 && peerBitField[i] == 1) {
                    return true;
                }
            }

            return false;
        }

        private synchronized void sendInterestMessage() {
            boolean isInterested = checkPeerForInterestingPiece();

            byte[] message;

            if (isInterested) {
                message = getMessage(null, Constants.message.INTERESTED.getType());
            } else {
                message = getMessage(null, Constants.message.NOT_INTERESTED.getType());
            }

            try {
                dataOutputStream.write(message);
                dataOutputStream.flush();

                if (isInterested) {
                    logFile.sendInterested(sourcePeerID, peerId);
                } else {
                    logFile.sendNotInterested(sourcePeerID, peerId);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        private synchronized void sendChoke() {
            if (optimisticallyUnchokedPeer.get() != peerId) {
                byte[] message = getMessage(null, Constants.message.CHOKE.getType());

                try {
                    dataOutputStream.write(message);
                    dataOutputStream.flush();
                } catch (Exception e) {
                    // TODO: handle exception
                    System.exit(0);
                    e.printStackTrace();
                }
            }
            if (unchoked) {
                unchoked = false;

                // int index =
            }
        }

        private synchronized byte[] convertToByteArray(int[] data) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(data);

            return byteBuffer.array();
        }

        private synchronized int[] convertToIntArray(byte[] data) {
            int[] message = new int[data.length / 4];
            int index = 0;
            // for (int i = 0; i < data.length; i += 4) {
            // byte[] bit = new byte[4];
            // System.arraycopy(data, i, bit, 0, 4);
            // message[index++] = ByteBuffer.wrap(bit).getInt();
            // }
            int i = 0;
            while (index < message.length) {
                byte[] bits = new byte[4];
                System.arraycopy(data, i, bits, 0, 4);
                message[index] = ByteBuffer.wrap(bits).getInt();

                i += 4;
                index++;
            }

            return message;
        }
    }

    /**
     * @param peerLines
     */
    private static void setPeers(List<String> peerLines) {
        totalPeers = 0;

        for (String line : peerLines) {
            int peerId = Integer.parseInt(line.split(" ")[0]);
            if (peerId == sourcePeerID) {
                currentPeer = totalPeers;
                sourcePort = Integer.parseInt(line.split(" ")[2]);
                fileCompleted = Integer.parseInt(line.split(" ")[3]) == 1 ? true : false;
            } else {
                PeerNode node = PeerNode.getPeerNode(line);
                neighbours.put(peerId, node);
            }
            totalPeers++;
        }
        totalPeers = peerLines.size();
    }

    /**
     * @param args[]
     */
    public static void main(String args[]) {
        sourcePeerID = Integer.parseInt(args[0]);

        utilObj = new Util();
        List<String> configLines = utilObj.parseFile("Common.cfg");
        configObj = Config.getConfig(configLines);
        totalChunks = configObj.getNumberOfChunks();

        List<String> peerLines = utilObj.parseFile("PeerInfo.cfg");
        setPeers(peerLines);
    }
}

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

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
        for (int i = 0; i < bitFieldHeaderMessage.length; i++) {
            bitField[i] = bitFieldHeaderMessage.get(i);
        }

        byte[] payload = convertToByteArray(bitField);
        byte[] message = getMessage(payload, Constants.message.BITFIELD.getType());

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

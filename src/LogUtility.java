import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtility {
    BufferedWriter writer = null;
    File logFile = null;

    public LogUtility(File logFile) {
        try {
            this.logFile = logFile;
            writer = new BufferedWriter(new FileWriter(logFile.getAbsolutePath(), true));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId
     * @param config
     */
    public synchronized void readCommonConfig(int peerId, Config config) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();
        entry.append(timestamp);
        entry.append(": Peer " + peerId + " ");
        entry.append("read Common.cfg file. Config used as follows: \n");
        entry.append("Number of Neighbours: " + config.getNumberOfNeighbours() + ".\n");
        entry.append("Unchoking Interval: " + config.getUnchokingInterval() + ".\n");
        entry.append("Optimistic Unchoking Interval: " + config.getOptimisedUnchokingInterval() + ".\n");
        entry.append("File Name: " + config.getFileName() + ".\n");
        entry.append("File Size: " + config.getFileSize() + ".\n");
        entry.append("Chunk Size: " + config.getChunkSize() + ".\n");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerIdFrom
     * @param peerIdTo
     */
    public synchronized void tcpConnectionTo(int peerIdFrom, int peerIdTo) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerIdFrom + " ]");
        entry.append(" makes connection to");
        entry.append(" Peer [ " + peerIdTo + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerIdFrom
     * @param peerIdTo
     */
    public synchronized void tcpConnectionFrom(int peerIdFrom, int peerIdTo) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerIdFrom + " ]");
        entry.append(" is connected from");
        entry.append(" Peer [ " + peerIdTo + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     */
    public synchronized void choking(int peerId1, int peerId2) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" is choked from ");
        entry.append("Peer [ " + peerId2 + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     */
    public synchronized void unchoking(int peerId1, int peerId2) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" is unchoked by ");
        entry.append("Peer [ " + peerId2 + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     * @param chunkNumber
     */
    public synchronized void sendHave(int peerId1, int peerId2, int chunkNumber) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" sent 'HAVE' message to ");
        entry.append("Peer [ " + peerId2 + " ]");
        entry.append(" for the piece: [ " + chunkNumber + " ].");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     * @param chunkNumber
     */
    public synchronized void receiveHave(int peerId1, int peerId2, int chunkNumber) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" received 'HAVE' message from ");
        entry.append("Peer [ " + peerId2 + " ]");
        entry.append(" for the piece: [ " + chunkNumber + " ].");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     * @param chunkNumber
     */
    public synchronized void sendRequest(int peerId1, int peerId2, int chunkNumber) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" sent 'REQUEST' message to ");
        entry.append("Peer [ " + peerId2 + " ]");
        entry.append(" for the piece: [ " + chunkNumber + " ].");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     * @param chunkNumber
     */
    public synchronized void receiveRequest(int peerId1, int peerId2, int chunkNumber) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" received 'REQUEST' message from ");
        entry.append("Peer [ " + peerId2 + " ]");
        entry.append(" for the piece: [ " + chunkNumber + " ].");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     */
    public synchronized void sendInterested(int peerId1, int peerId2) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" sent 'INTERESTED' message to ");
        entry.append("Peer [ " + peerId2 + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     */
    public synchronized void receiveInterested(int peerId1, int peerId2) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" received 'INTERESTED' message from ");
        entry.append("Peer [ " + peerId2 + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     */
    public synchronized void sendNotInterested(int peerId1, int peerId2) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" sent 'NOT INTERESTED' message to ");
        entry.append("Peer [ " + peerId2 + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     */
    public synchronized void receiveNotInterested(int peerId1, int peerId2) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" received 'NOT INTERESTED' message from ");
        entry.append("Peer [ " + peerId2 + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     * @param chunkNumber
     * @param numberOfChunks
     */
    public synchronized void downloadingPiece(int peerId1, int peerId2, int chunkNumber, int numberOfChunks) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" has downloaded the piece [ " + chunkNumber + " ] ");
        entry.append("from Peer [ " + peerId2 + " ].");
        entry.append(" Now the number of pieces it has is: " + numberOfChunks);

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId
     */
    public synchronized void downloadCompleted(int peerId) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId + " ]");
        entry.append(" has completed downloading the entire file");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public synchronized void processComplete() {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": All peers have completed downloading the file.");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     */
    public synchronized void sendBitfield(int peerId1, int peerId2) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" sent bitfield to ");
        entry.append("Peer [ " + peerId2 + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId1
     * @param peerId2
     */
    public synchronized void receiveBitfield(int peerId1, int peerId2) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(": Peer [ " + peerId1 + " ]");
        entry.append(" received bitfield from ");
        entry.append("Peer [ " + peerId2 + " ]");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId
     * @param peerIds
     */
    public synchronized void changePreferredNeighbours(int peerId, int[] peerIds) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(" Peer [ " + peerId + " ] has the following preferred neighbours [ ");

        StringBuilder sb = new StringBuilder();
        for (int id : peerIds) {
            String idString = Integer.toString(id);
            sb.append(idString).append(", ");
        }
        String idListString = sb.deleteCharAt(sb.length() - 1).toString();

        entry.append(idListString + " ].");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param peerId
     * @param neighbourId
     */
    public synchronized void changeOptimisticalUnchokedNeighbour(int peerId, int neighbourId) {
        String timestamp = new SimpleDateFormat("y-M-d at h:m:s a z").format(new Date().getTime());
        StringBuffer entry = new StringBuffer();

        entry.append(timestamp);
        entry.append(" Peer [ " + peerId + " ]");
        entry.append(" has the optimistically unchoked neighbor [ " + neighbourId + " ].");

        try {
            writer.write(entry.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

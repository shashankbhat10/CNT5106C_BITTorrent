import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * Utility function to create Directory and log file for the specified peer
     * 
     * @param peerId - ID of the peer for which log file needs to be created
     * @return logFile - Log File created for the peer
     */
    public File createPeerAndLogDirectory(int peerId) {
        File logFile = null;
        try {
            String peerDirName = System.getProperty("user.dir") + File.separator + "peer_" + peerId;
            File peerDir = new File(peerDirName);
            if (!peerDir.exists())
                peerDir.mkdir();

            String logFileNameWithDirectory = System.getProperty("user.dir") + File.separator + "log_peer_" + peerId
                    + ".log";
            logFile = new File(logFileNameWithDirectory);
            boolean logFileCreated = logFile.createNewFile();
            if (logFileCreated)
                System.out.println("Log file created for peer: " + peerId);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return logFile;
    }

    /**
     * Generate and return packet(byte[ ]) using peer ID, header for handshake
     * packet
     * and header for zeros
     * 
     * @param sourcePeerID
     * @return Handshake Packet - byte[ ]
     */
    public synchronized byte[] getHandshakePacket(int sourcePeerID) {
        String handshakeHeader = Constants.HEADER_HANDSHAKE;
        byte[] headerBytes = handshakeHeader.getBytes();

        String zeros = Constants.ZERO_HANDSHAKE;
        byte[] zeroBytes = zeros.getBytes();

        byte[] peerBytes = String.valueOf(sourcePeerID).getBytes();
        int handshakePacketSize = headerBytes.length + zeroBytes.length + peerBytes.length;
        byte[] packetHandshake = new byte[handshakePacketSize];

        for (int i = 0; i < handshakePacketSize; i++) {
            if (i < headerBytes.length)
                packetHandshake[i] = headerBytes[i];
            else if (i < headerBytes.length + zeroBytes.length)
                packetHandshake[i] = zeroBytes[i - headerBytes.length];
            else
                packetHandshake[i] = peerBytes[i - (headerBytes.length + zeroBytes.length)];
        }

        return packetHandshake;
    }

    /**
     * Utility function to split the given file to chunks of specified size
     * 
     * @param peerId - Peer which will read the file and send
     * @param config - Config object containing required fields
     */
    public void splitFileToChunks(String peerId, Config config) {
        int fileSize = config.getFileSize();
        int numberOfChunks = config.getNumberOfChunks();
        int chunkSize = config.getChunkSize();
        String fileName = Constants.FILENAME;
        try {
            String file = System.getProperty("user.dir") + File.separator + fileName;
            FileInputStream fis = new FileInputStream(file);
            File[] chunks = new File[chunkSize];
            int chunksToWrite = 0;
            for (int i = 0; i < numberOfChunks; i++) {
                chunksToWrite += chunkSize;
                int chunkLength = chunkSize;
                if (fileSize < chunksToWrite) {
                    chunksToWrite -= chunkSize;
                    chunkLength = fileSize - chunksToWrite;
                }
                byte[] chunk = new byte[chunkLength];
                String chunkName = System.getProperty("user.dir") + File.separator + "peer_" + peerId + File.separator
                        + fileName + "_" + i;
                chunks[i] = new File(chunkName);

                FileOutputStream fos = new FileOutputStream(chunkName);
                fis.read(chunk);
                fos.write(chunk);
                fos.close();
            }
            fis.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Utility function to merge chunks of the same file and regenerate the original
     * file and write to disk
     * 
     * @param peerId - ID of the Peer node which will receive and combine the chunks
     * @param config - Config object containing the required fields
     * @throws IOException
     */
    public synchronized void joinChunksToFile(int peerId, Config config) throws IOException {
        String fileName = Constants.FILENAME;
        int numberOfChunks = config.getNumberOfChunks();
        String fileNameWithDirectory = System.getProperty("user.dir") + File.separator + "peer_" + peerId
                + File.separator + fileName;
        File finalFile = new File(fileNameWithDirectory);
        File[] splitFiles = new File[numberOfChunks];

        for (int i = 0; i < numberOfChunks; i++) {
            String chunkName = System.getProperty("user.dir") + File.separator + "peer_" + peerId + File.separator
                    + fileName + "_" + i;
            splitFiles[i] = new File(chunkName);
        }

        FileOutputStream fos = new FileOutputStream(finalFile);
        for (int i = 0; i < numberOfChunks; i++) {
            FileInputStream fis = new FileInputStream(splitFiles[i]);
            int chunkFileSize = (int) splitFiles[i].length();
            byte[] chunkFile = new byte[chunkFileSize];
            fis.read(chunkFile);
            fos.write(chunkFile);
            fis.close();
        }
        fos.close();
    }

    /**
     * Utility function to write a chunk of the file to the respective directory of
     * the Peer
     * 
     * @param peerId      - ID of the peer which will receive the chunk
     * @param chunkNumber - ID of the chunk that is to be written
     * @param chunk       - Actual file chunk that will be written to the disk
     * @throws IOException
     */
    public synchronized void writeChunk(int peerId, int chunkNumber, byte[] chunk) throws IOException {
        String fileName = Constants.FILENAME;
        String fileNameWithDirectory = System.getProperty("user.dir") + File.separator + "peer_" + peerId
                + File.separator + fileName + "_" + chunkNumber;

        File file = new File(fileNameWithDirectory);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(chunk);
        fos.close();
    }

    /**
     * Utility function to get the specified chunk from the disk
     * 
     * @param peerId      - ID of the peer requesting the chunk
     * @param chunkNumber - ID of the chunk
     * @return
     * @throws IOException
     */
    public synchronized byte[] getChunk(int peerId, int chunkNumber) throws IOException {
        String fileName = Constants.FILENAME;
        String fileNameWithDirectory = System.getProperty("user.dir") + File.separator + "peer_" + peerId
                + File.separator + fileName + "_" + chunkNumber;

        File fileChunk = new File(fileNameWithDirectory);
        FileInputStream fis = new FileInputStream(fileChunk);
        int chunkSize = (int) fileChunk.length();
        byte[] chunk = new byte[chunkSize];

        fis.read(chunk);
        fis.close();

        return chunk;
    }

    public List<String> parseFile(String filePath) {
        List<String> fileContents = new ArrayList<String>();
        if (filePath != null) {
            String dirName = System.getProperty("user.dir") + File.separator + filePath;
            try {
                FileReader fr = new FileReader(dirName);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                while ((line = br.readLine()) != null) {
                    fileContents.add(line);
                }
                br.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return fileContents;
    }
}

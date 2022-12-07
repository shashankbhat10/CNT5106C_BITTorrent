import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {
    public static FileUtility getObj() {
        return new FileUtility();
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

    private void splitToChunks(Config config, String peerId) {
        String sourceFileName = Constants.FILENAME;
        String sourceFilePath = System.getProperty("user.dir") + File.separator + sourceFileName;

        int numberOfChunks = config.getNumberOfChunks();
        int sizeOfChunk = config.getChunkSize();
        int fileSize = config.getFileSize();

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFilePath);
            File[] chunks = new File[sizeOfChunk];
            int sizeOfCurrentChunk = 0;
            for (int i = 0; i < numberOfChunks; i++) {
                sizeOfCurrentChunk = sizeOfChunk;
                if (i == numberOfChunks - 1) {
                    sizeOfCurrentChunk = fileSize - (i - 1) * sizeOfChunk;
                }

                byte[] chunk = new byte[sizeOfCurrentChunk];
                String destinationFileChunk = System.getProperty("user.dir") + File.separator + "peer_" + peerId
                        + File.separator + sourceFileName + "_" + i;

                chunks[i] = new File(destinationFileChunk);
                FileOutputStream fileOutputStream = new FileOutputStream(destinationFileChunk);
                fileInputStream.read(chunk);
                fileOutputStream.write(chunk);

                fileOutputStream.close();
            }

            fileInputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void joinChunks(Config config, String peerId) {
        String sourceFileName = Constants.FILENAME;
        String finalFile = System.getProperty("user.dir") + File.separator + "peer_" + peerId + File.separator
                + sourceFileName;

        int numberOfChunks = config.getNumberOfChunks();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(finalFile);

            for (int i = 0; i < numberOfChunks; i++) {
                String chunkName = System.getProperty("user.dir") + File.separator + "peer_" + peerId + File.separator
                        + "_" + i;
                File chunkFile = new File(chunkName);

                FileInputStream fileInputStream = new FileInputStream(chunkName);
                int sizeOfChunk = (int) chunkFile.length();

                byte[] chunk = new byte[sizeOfChunk];

                fileInputStream.read(chunk);
                fileOutputStream.write(chunk);

                fileInputStream.close();
            }

            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private byte[] sendPiece(int peerId, int chunkNumber) {
        String sourceFileName = System.getProperty("user.dir") + File.separator + "peer_" + peerId + File.separator
                + Constants.FILENAME + "_" + chunkNumber;

        File chunkFile = new File(sourceFileName);
        byte[] chunk = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(chunkFile);
            int sizeOfChunk = (int) chunkFile.length();
            chunk = new byte[sizeOfChunk];

            fileInputStream.read(chunk);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return chunk;
    }

    private void storePiece(String peerId, int chunkNumber, byte[] piece) {
        String sourceFileName = System.getProperty("user.dir") + File.separator + "peer_" + peerId + File.separator
                + Constants.FILENAME + "_" + chunkNumber;

        File chunkFile = new File(sourceFileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(chunkFile);
            fileOutputStream.write(piece);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

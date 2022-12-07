import java.io.File;

public class PeerUtility {
    private boolean logFileExists(String peerId) {
        String logFileName = System.getProperty("user.dir") + File.separator + "log_peer_" + peerId + ".log";
        File logFile = new File(logFileName);
        if (logFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private File createLogFile(String peerId) {
        File logFile = null;

        String destinationDirectory = System.getProperty("user.dir") + File.separator + "peer" + peerId;

        try {
            File directory = new File(destinationDirectory);

            if (!directory.exists()) {
                boolean directoryCreated = directory.mkdir();
                if (!directoryCreated) {
                    String exceptionMessage = "Exception while creating directory for log file for peerId: " + peerId;
                    throw new Exception(exceptionMessage);
                }
            }

            String logFileName = System.getProperty("user.dir") + File.separator + "log_peer_" + peerId + ".log";
            logFile = new File(logFileName);
            boolean logFileCreated = logFile.createNewFile();
            if (!logFileCreated) {
                String exceptionMessage = "Exception while creating log file for peerId: " + peerId;
                throw new Exception(exceptionMessage);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return logFile;
    }

    // private byte[] getHandshakePacket(String peerId) {

    // }
}

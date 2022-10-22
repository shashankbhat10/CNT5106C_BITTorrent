import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

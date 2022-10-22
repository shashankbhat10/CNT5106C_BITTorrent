public class PeerNode {
    private int peerId = -1;
    private String hostName = "";
    private int port = -1;
    private int haveFile = 0;
    private int[] bitField = null;

    public PeerNode(int peerId, String hostName, int port, int haveFile) {
        this.peerId = peerId;
        this.hostName = hostName;
        this.port = port;
        this.haveFile = haveFile;
    }

    /**
     * @param line
     * @return PeerNode
     */
    public static PeerNode getPeerNode(String line) {
        String[] parameters = line.split(" ");
        int peerId = Integer.parseInt(parameters[0]);
        String hostName = parameters[1];
        int port = Integer.parseInt(parameters[2]);
        int haveFile = Integer.parseInt(parameters[3]);
        PeerNode node = new PeerNode(peerId, hostName, port, haveFile);

        return node;
    }

    /**
     * @return int
     */
    public int getPeerId() {
        return peerId;
    }

    /**
     * @param peerId
     */
    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    /**
     * @return String
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return int
     */
    public int getHaveFile() {
        return haveFile;
    }

    /**
     * @param haveFile
     */
    public void setHaveFile(int haveFile) {
        this.haveFile = haveFile;
    }

    /**
     * @return int[]
     */
    public int[] getBitField() {
        return bitField;
    }

    /**
     * @param bitField
     */
    public void setBitField(int[] bitField) {
        this.bitField = bitField;
    }

}

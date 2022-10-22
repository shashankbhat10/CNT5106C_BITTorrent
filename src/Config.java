import java.util.List;

public class Config {
    private int numberOfNeighbours;
    private String fileName;
    private int fileSize;
    private int chunkSize;
    private int numberOfChunks;
    private int unchokingInterval;
    private int optimisedUnchokingInterval;

    /**
     * 
     * @param numberOfNeighbours
     * @param fileName
     * @param fileSize
     * @param chunkSize
     * @param numberOfChunks
     * @param unchokingInterval
     * @param optimisedUnchokingInterval
     */
    public Config(int numberOfNeighbours, String fileName, int fileSize, int chunkSize, int unchokingInterval,
            int optimisedUnchokingInterval) {
        this.numberOfNeighbours = numberOfNeighbours;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.chunkSize = chunkSize;
        this.unchokingInterval = unchokingInterval;
        this.optimisedUnchokingInterval = optimisedUnchokingInterval;
    }

    /**
     * 
     * @param rows
     * @return The Config to be used
     */
    public static Config getConfig(List<String> rows) {
        if (rows != null && rows.size() == 6) {
            int numberOfNeighbours = Integer.parseInt(rows.get(0).split(" ")[0]);
            int unchokingInterval = Integer.parseInt(rows.get(1).split(" ")[0]);
            int optimisedUnchokingInterval = Integer.parseInt(rows.get(2).split(" ")[0]);
            String fileName = rows.get(3).split(" ")[0];
            int fileSize = Integer.parseInt(rows.get(4).split(" ")[0]);
            int chunkSize = Integer.parseInt(rows.get(4).split(" ")[0]);

            Config obj = new Config(numberOfNeighbours, fileName, fileSize, chunkSize, unchokingInterval,
                    optimisedUnchokingInterval);
            double fileSizeDouble = (double) fileSize;
            double chunkSizeDouble = (double) chunkSize;
            int numberOfChunks = (int) (Math.ceil(fileSizeDouble / chunkSizeDouble));
            obj.setNumberOfChunks(numberOfChunks);

            return obj;
        }

        return null;
    }

    /**
     * Get the total count of neighbours for current node
     * 
     * @return Number of neighbouring nodes
     */
    public int getNumberOfNeighbours() {
        return numberOfNeighbours;
    }

    /**
     * Sets the number of neighbours for the current node
     * 
     * @param numberOfNeighbours
     */
    public void setNumberOfNeighbours(int numberOfNeighbours) {
        this.numberOfNeighbours = numberOfNeighbours;
    }

    /**
     * 
     * @return fileName - Name of the file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the name of the file
     * 
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the size of the current file
     * 
     * @return fileSize
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * Set the the size of the current file
     * 
     * @param fileSize
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Returns the size of chunks the current file is split into
     * 
     * @return chunkSize
     */
    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * Set the size of each chunk the file is split into
     * 
     * @param chunkSize
     */
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * Returns the number of chunks the current file is split into
     * 
     * @return numberOfChunks
     */
    public int getNumberOfChunks() {
        return numberOfChunks;
    }

    /**
     * Set the number of chunks the current file is broken into
     * 
     * @param numberOfChunks
     */
    public void setNumberOfChunks(int numberOfChunks) {
        this.numberOfChunks = numberOfChunks;
    }

    /**
     * Returns the interval time after which unchoking should be done
     * 
     * @return unchokingInterval
     */
    public int getUnchokingInterval() {
        return unchokingInterval;
    }

    /**
     * Set the interval to check for unchoking neighbours
     * 
     * @param unchokingInterval
     */
    public void setUnchokingInterval(int unchokingInterval) {
        this.unchokingInterval = unchokingInterval;
    }

    /**
     * Returns the interval of the optimised unchoked neighbour
     * 
     * @return optimisedUnchokingInterval
     */
    public int getOptimisedUnchokingInterval() {
        return optimisedUnchokingInterval;
    }

    /**
     * Set the interval for the optimised unchoked neighbour
     * 
     * @param optimisedUnchokingInterval
     */
    public void setOptimisedUnchokingInterval(int optimisedUnchokingInterval) {
        this.optimisedUnchokingInterval = optimisedUnchokingInterval;
    }

}
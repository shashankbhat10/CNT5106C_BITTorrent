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

    class ChokeUnchoke implements Runnable
    {

        public List<Integer> getPeersAccordingToDownloadRate() 
        {
		// Create a list of peer IDs sorted by their download rates
		List<Integer> sortedPeers = new ArrayList<>(downloadRates.keySet());
		Collections.sort(sortedPeers, (peer1, peer2) -> downloadRates.get(peer1) - downloadRates.get(peer2));
		return sortedPeers;
        }


        public void run()
        {
            int unchokeInterval = configFileObj.getUnChokingInterval();
			try 
            {
                int preferredNeighbors = configFileObj.getNoOfNeighbors();
                while (peersWithEntireFile.get() < totalPeers) 
                {
                    int interestedPeersSize = interested_peers.size();
                    if (interestedPeersSize > 0) {
                        List<Integer> preferredPeers = new ArrayList<>();
                        List<Integer> chokedPeers = new ArrayList<>();

                        // Select preferred peers
                        if (interestedPeersSize < preferredNeighbors) {
                            preferredPeers.addAll(interested_peers);
                        } else {
                            List<Map.Entry<Integer, Integer>> sortedPeersMapList = getPeersAccordingToDownloadRate();
                            Random rand = new Random();
                            List<Integer> tempPeersList = new ArrayList<>(interested_peers);
                            for (int i = 0; i < preferredNeighbors; i++) {
                                int randomIdx = rand.nextInt(tempPeersList.size());
                                int peerId = tempPeersList.get(randomIdx);
                                preferredPeers.add(peerId);
                                tempPeersList.remove(randomIdx);
                            }

                            // Select choked peers
                            chokedPeers.addAll(tempPeersList);
                        }

                        // Send unchoke or choke messages to peers
                        for (Integer peerId : preferredPeers) {
                            NeighborPeerInteraction npiObj = neighborPeerConnections.get(peerId);
                            if (!npiObj.unchoked) {
                                npiObj.sendUnChokeMsg(false);
                            }
                        }
                        for (Integer peerId : chokedPeers) {
                            NeighborPeerInteraction npiObj = neighborPeerConnections.get(peerId);
                            npiObj.sendChokeMsg();
                        }

                        // Log change of preferred neighbors
                        logFileObj.log_change_of_preferred_neighbors(sourcePeerId, preferredPeers.toArray(new Integer[0]));
                    }
                    TimeUnit.SECONDS.sleep(unchokeInterval);
                }
    } 
			}catch(InterruptedException Ie) {
				//System.exit(0);
				//Ie.printStackTrace();
			}catch(Exception e) {
				//System.exit(0);
				//e.printStackTrace();
			}


        }
    }

    class OptimisticUnchoke implements Runnable
    {
        public void run()
        {
            int optimisticUnchokeInterval = configFileObj.getOptimisticUnchokingInterval();
            try 
            {
                while (peersWithEntireFile.get() < totalPeers) 
                {
                    int interestedPeersSize = interested_peers.size();
                    if (interestedPeersSize > 0) {
                        Random rand = new Random();
                        int randomIdx = rand.nextInt(interestedPeersSize);
                        int peerId = interested_peers.get(randomIdx);
                        optimisticallyUnchokedPeer.setPeerId(peerId);
                        NeighborPeerInteraction npiObj = neighborPeerConnections.get(peerId);

                        // Send optimistically unchoke message to selected peer
                        npiObj.sendUnChokeMsg(true);
                        logFileObj.log_optimistically_unchoked_peer(sourcePeerId, peerId);

                        // Wait for optimistic unchoke interval to elapse
                        TimeUnit.SECONDS.sleep(optimisticUnchokeInterval);

                        // Reset optimistically unchoked peer
                        optimisticallyUnchokedPeer.setPeerId(-1);

                        // Send choke message to selected peer if it is not unchoked
                        if (!npiObj.unchoked()) {
                            npiObj.sendChokeMsg();
                        }
                    }
                }
            } 

            catch(InterruptedException Ie) {
                //System.exit(0);
                //Ie.printStackTrace();
            }catch(Exception e) {
                //System.exit(0);
                //e.printStackTrace();
            }

    }

    class Client implements Runnable{
        @Override
        public void run() 
        {
            int i=0;
            Iterator<Entry<Integer, NeighbourPeerNode>> itr = neighborPeer.entrySet().iterator();

            while(index<currentPeerIndex){
                Entry<Integer, NeighbourPeerNode> entry = itr.next();
                int peerId = entry.getKey();
                NeighbourPeerNode node = entry.getValue();
                try {
                    Socket clientSocket = new Socket(node.getHostName(), node.getPortNumber());
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    byte[] handShakeHeader = utilObj.getHandshakePacket(sourcePeerId);
					outputStream.write(handShakeHeader);

					byte[] receivedHandshake = new byte[handShakeHeader.length];
					inputStream.readFully(receivedHandshake);
					int receivedPeerId = Integer.parseInt(new String(Arrays.copyOfRange(receivedHandshake,28,32)));

					if(receivedPeerId == peerId) {		
						//System.out.println(receivedPeerId);
						NeighborPeerInteraction npi = new NeighborPeerInteraction(socket,peerObj);
						npi.startInteraction();
						neighborPeerConnections.put(peerId, npi);
						logFileObj.log_tcp_connection_to(sourcePeerId, peerId);
					}		
					index++;
					outputStream.flush();

                } catch (IOException Ie) {
                    // TODO Auto-generated catch block
                    Ie.printStackTrace();
                }
                catch (UnknowHostException IIe) {
                    // TODO Auto-generated catch block
                     IIe.printStackTrace();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

class Server implements Runnable{

		@Override
		public void run() {
			int index = currentPeerIndex;
			try {
                    // Create the ServerSocket object
                    ServerSocket listener = new ServerSocket(sourcePortNumber);

                    while (currentPeerIndex < totalPeers - 1) 
                    {
                        // Accept an incoming connection
                        Socket socket = listener.accept();
                        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                        byte[] handshakePacket = new byte[32];
                        inputStream.readFully(handshakePacket);

                        // Parse the peer ID from the handshake packet
                        int peerId = Integer.parseInt(new String(Arrays.copyOfRange(handshakePacket, 28, 32)));
                        //send Handshake
					    
                        byte[] sendHandshake = utilObj.getHandshakePacket(sourcePeerId);
					    outputStream.write(sendHandshake);

                        // Use the peer ID to quickly look up the corresponding NeighborPeerNode object
                        NeighbourPeerNode peerObj = neighborPeers.get(peerId);

                        // Create a NeighborPeerInteraction object for the connecting peer
                        NeighborPeerInteraction npi = new NeighborPeerInteraction(socket, peerObj);
                        npi.startInteraction();

                        // Add the NeighborPeerInteraction object to the map
                        neighborPeerConnections.put(peerId, npi);

                        // Log the connection
                        logFileObj.log_tcp_connection_from(sourcePeerId, peerId);
                        index++;
                    }

				}
                
			catch(UnknownHostException Ie) {
				Ie.printStackTrace();
			}
			catch(IOException IIe) {
				IIe.printStackTrace();
			}
			catch(Exception ie) {
				e.printStackTrace();
			}

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

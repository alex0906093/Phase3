
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.*;
import java.io.*;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.lang.*;
public class RUBTClient{

    public static String file_destination;
    public static byte[] peerid = "alexchriskyung123456".getBytes();
    public static byte[] protocol_string = new byte[] { 'B', 'i', 't', 'T',
			'o', 'r', 'r', 'e', 'n', 't', ' ', 'p', 'r', 'o', 't', 'o', 'c',
			'o', 'l' };
    public static byte[] tResponse = null;
	public static TorrentInfo tInfo;
	public static byte[] info_hash = null;
    public static MemCheck globalMemory = null;
    public static TrackerResponse tResponseDecoded = null;
    public static final int PEER_LIMIT = 5;
    public static String DIPS;
	public static void main(String[] args) throws FileNotFoundException, IOException{
		/*Get variables ready*/
        String torrentFN;
		String saveFN;
		
		ArrayList<Peer> peers = null;
		
		byte[] b = null;
        /*check command line arguments*/
		if(args.length == 2){
			torrentFN = args[0];
			saveFN = args[1];
			file_destination = saveFN;
		}
		else{
			System.out.println("Invalid number of command line arguments");
			return;
		}
        
        RandomAccessFile fSave = null;
        
        try{
            fSave = new RandomAccessFile(new File(saveFN), "rw");
        }catch(FileNotFoundException e){}
        /*read torrent file*/
		
        BufferedReader reader = null;
		

        try{
			b = Files.readAllBytes(Paths.get(torrentFN));
		} catch(FileNotFoundException e){
			System.out.println("Caught Exception File not found");
			//e.printStackTrace();
		} catch(IOException e){
			System.out.println("Caught Exception IOException");
			//e.printStackTrace();
		} finally {
			try{
				if(reader != null){
					reader.close();
				}
			} catch(IOException e){
				System.out.println("Caught Exception IOException");
				//e.printStackTrace();
			}

		}

        /*send bytes to helper class*/
        try{
            tInfo = new TorrentInfo(b);
            try{
                fSave.setLength((long)tInfo.file_length);
                byte[] empty = new byte[tInfo.file_length];
                fSave.write(empty, 0,empty.length);
            }catch(IOException e){}
            
            
            globalMemory = new MemCheck(tInfo,fSave);
            System.out.println("File mem length is " + fSave.length());
            System.out.println(tInfo.file_name);
            
            
        }catch(BencodingException e){
            System.out.println("Bencoding Exception");
        }
        try{
        	tResponse = TrackerResponse.getTrackerResponseStart(tInfo, 0, 0,tInfo.file_length);
        }
        catch(Exception e){
        	System.out.println("Problem with GET Request, program exiting");
        	return;
        }
        

        /*decode the tracker response*/
        try{
        	tResponseDecoded = TrackerResponse.decodeTrackerResponse(tResponse);
        }catch(Exception e){
        	System.out.println("Problem decoding tracker response");
        	return;
        }
        DIPS = "";
        peers = tResponseDecoded.peers;
        int peerIndex;
        info_hash = tInfo.info_hash.array();
     	TrackerThread announceThread = new TrackerThread();
        seedHandler frontDoor = new seedHandler();
        new Thread(frontDoor).start();	
               
            //in actual production peerIndex < peers.size();
            for(peerIndex = 0; peerIndex < tResponseDecoded.peers.size(); peerIndex++){
            	if(!peers.get(peerIndex).ipAdd.equals("128.6.171.132")){
		              if(!DIPS.contains(peers.get(peerIndex).ipAdd)){
		            	Download peer = new Download(tResponseDecoded.peers.get(peerIndex).ipAdd, tResponseDecoded.peers.get(peerIndex).port);
                      	DIPS += " " + peer.ipAdd + " ";
		              	/*Lets schedule everything together*/
		              	new Thread(peer).start();
		              	//System.out.println("Peer index is " + peerIndex);
		              }
                }
	        }
            
         
            
            new Thread(announceThread).start();


    }
    




    
}
//package client;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/*
 *Class to grab repsonse from Bittorrent Tracker
 *
 */
public class TrackerResponse{
    /*
     *Fields to be received from tracker
     */
    public String TrackerID;
    public ArrayList<Peer> peers;
    public String failureReason;
    public String failureMessage;
    public int interval;
    public int minInterval;
    public int complete;
    public int incomplete;
    /*
     *Keys for fields
     */
    public final int NUM_PEERS = 33;
    public static final ByteBuffer KEY_FAILURE = ByteBuffer.wrap(new byte[]{'f', 'a', 'i','l','u', 'r', 'e', ' ', 'r', 'e', 'a', 's', 'o', 'n',});
    public static final ByteBuffer KEY_PEERS = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', 's' });
    private static final ByteBuffer KEY_IP = ByteBuffer.wrap(new byte[] { 'i','p' });
    private static final ByteBuffer KEY_PEER_ID = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', ' ', 'i', 'd' });
    public static final ByteBuffer KEY_PORT = ByteBuffer.wrap(new byte[] {'p', 'o', 'r', 't'});
    public static final ByteBuffer KEY_INTERVAL = ByteBuffer.wrap(new byte[] {'i', 'n', 't', 'e', 'r', 'v', 'a','l' });
    public static final ByteBuffer KEY_MIN_INTERVAL = ByteBuffer.wrap(new byte[] { 'm', 'i', 'n', ' ', 'i', 'n', 't', 'e', 'r','v', 'a', 'l' });
    public static final ByteBuffer KEY_COMPLETE = ByteBuffer.wrap(new byte[] {'c', 'o', 'm', 'p', 'l', 'e', 't', 'e' });
    public static final ByteBuffer KEY_INCOMPLETE = ByteBuffer.wrap(new byte[] {'i', 'n', 'c', 'o', 'm', 'p', 'l', 'e', 't', 'e' });
    
    public TrackerResponse(HashMap<ByteBuffer, Object> response) throws Exception{
        /*
         *get all of the keys from the hash map and set them accordingly
         *
         */
        if(response.containsKey(KEY_FAILURE)){
            throw new Exception("Tracker Failed Key Detected");
        }
        if(response.containsKey(KEY_INTERVAL)){
            this.interval = (Integer) response.get(KEY_INTERVAL);
        }else{
            System.out.println("No Intverval, set to 0");
            this.interval = 0;
        }
        if (response.containsKey(KEY_COMPLETE))
            this.complete = (Integer) response.get(KEY_COMPLETE);
        else {
            System.out.println("Warning: no complete, setting to zero");
            this.complete = 0;
        }
        if (response.containsKey(KEY_INCOMPLETE))
            this.incomplete = (Integer) response.get(KEY_INCOMPLETE);
        else {
            System.out.println("Warning: no incomplete, setting to zero");
            this.incomplete = 0;
        }
        if (response.containsKey(KEY_MIN_INTERVAL))
            this.minInterval = (Integer) response.get(KEY_MIN_INTERVAL);
        else {
            System.out.println("Warning: no min interval, setting to zero");
            this.minInterval = 0;
        }
        
        this.peers = new ArrayList<Peer>();
        //iterate through responses, get peers
        for (Object element : (ArrayList<?>) response.get(KEY_PEERS) ){
		//@SuppressWarning("unchecked");
            Map<ByteBuffer, Object> peerMap = (Map<ByteBuffer, Object>)element;
                
            if(!peerMap.containsKey(KEY_PORT) || !peerMap.containsKey(KEY_IP) || !peerMap.containsKey(KEY_PEER_ID)){
                System.out.println("Missing information about peer, skipping");
                continue;
            }
            int peerPort =((Integer) peerMap.get(KEY_PORT)).intValue();
            String peerIP = objectToStr(peerMap.get(KEY_IP));
	        String peerID = objectToStr(peerMap.get(KEY_PEER_ID));
            byte[] pid = ((ByteBuffer) peerMap.get(KEY_PEER_ID)).array(); 
		    this.peers.add(new Download(peerIP, peerPort));
        }
    

    }
    /*For the first tracker GET request*/
    public static byte[] getTrackerResponseStart(TorrentInfo ti, int down, int up, int left1) throws UnknownHostException, IOException{
    	String info_hash = toHexString(ti.info_hash.array());
    	String peer_id = toHexString("alexchriskyung123456".getBytes());
    	String port = "" + 6883;
    	String downloaded = "" + down;
    	String uploaded = "" + up;
    	String left = "" + left1;
    	String started = "started";
    	String announce = ti.announce_url.toString();
    	String aURL= announce.toString();
    	aURL += "?" + "info_hash" + "=" + info_hash + "&" + "peer_id" + "=" + peer_id + "&" + "port" + "=" + port + "&" + "uploaded" + "="
    	+ uploaded + "&" + "downloaded" + "=" + downloaded + "&" + "left" + "=" + left + "&" + "event" + "=" + started;
    	//System.out.println("URL is : " + aURL);
    	HttpURLConnection con = (HttpURLConnection)new URL(aURL).openConnection();
    	DataInputStream dInStream = new DataInputStream(con.getInputStream());
    	int dSize = con.getContentLength();
    	byte[] retBytes = new byte[dSize];
    	dInStream.readFully(retBytes);
    	dInStream.close();
    	return retBytes;
    }
    
    /*regular interval tracker response*/
    public static TrackerResponse getTrackerResponse(TorrentInfo ti, int down, int up) throws UnknownHostException, IOException{
    	String info_hash = toHexString(ti.info_hash.array());
    	String peer_id = toHexString("alexchriskyung123456".getBytes());
    	String port = "" + 6883;
    	String downloaded = "" + down;
    	String uploaded = "" + up;
    	String left = "" + (ti.file_length - down);
    	String announce = ti.announce_url.toString();
    	String aURL= announce.toString();
    	aURL += "?" + "info_hash" + "=" + info_hash + "&" + "peer_id" + "=" + peer_id + "&" + "port" + "=" + port + "&" + "uploaded" + "="
    	+ uploaded + "&" + "downloaded" + "=" + downloaded + "&" + "left" + "=" + left;
    	//System.out.println("URL is : " + aURL);
    	HttpURLConnection con = (HttpURLConnection)new URL(aURL).openConnection();
    	DataInputStream dInStream = new DataInputStream(con.getInputStream());
    	int dSize = con.getContentLength();
    	byte[] retBytes = new byte[dSize];
    	dInStream.readFully(retBytes);
    	dInStream.close();
    	Object o=null;
		try {
			o = Bencoder2.decode(retBytes);
		} catch (BencodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	HashMap<ByteBuffer, Object> response = (HashMap<ByteBuffer, Object>) o;
    	TrackerResponse tr2 = null;
        //call TrackerResponse.java decode the information
    	try{
    		tr2 = new TrackerResponse(response);
    	}catch(Exception e){
    		System.out.println("problem getting tracker response");
    		e.printStackTrace();
    	}
    	if(tr2 == null){
    		System.out.println("nothing being decoded");
    	}
    	return tr2;

    }
    
    
    /*Helper method for first GET request*/
    public static TrackerResponse decodeTrackerResponse(byte[] tr) throws BencodingException{
    	Object o = Bencoder2.decode(tr);
    	HashMap<ByteBuffer, Object> response = (HashMap<ByteBuffer, Object>) o;
    	TrackerResponse tr2 = null;
        //call TrackerResponse.java decode the information
    	try{
    		tr2 = new TrackerResponse(response);
    	}catch(Exception e){
    		System.out.println("problem getting tracker response");
    		e.printStackTrace();
    	}
    	if(tr2 == null){
    		System.out.println("nothing being decoded");
    	}
    	return tr2;
    }
    
    
    
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        String hex[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
            "B", "C", "D", "E", "F" };
        byte ch = 0x00;
        if(bytes == null || bytes.length <=0)
            return null;


        int i = 0;
        while(i < bytes.length){
                if ((bytes[i] >= '0' && bytes[i] <= '9')
                    || (bytes[i] >= 'a' && bytes[i] <= 'z')
                    || (bytes[i] >= 'A' && bytes[i] <= 'Z') || bytes[i] == '$'
                    || bytes[i] == '-' || bytes[i] == '_' || bytes[i] == '.'
                    || bytes[i] == '!') {
                sb.append((char) bytes[i]);
                i++;
            }else{
                sb.append('%');
                ch = (byte) (bytes[i] & 0xF0); // Strip off high 
                ch = (byte) (ch >>> 4); // shift the bits down
                ch = (byte) (ch & 0x0F); // must do this is high order bit is on
                sb.append(hex[(int) ch]); // convert the byte to a String Character
                ch = (byte) (bytes[i] & 0x0F); // Strip off low
                sb.append(hex[(int) ch]); // convert the byte to a String Character
                i++;

            }
        }

        String ret = new String(sb);
        return ret;
    }
    
    public static String objectToStr(Object o){
        
        if(o instanceof Integer){
            return String.valueOf(o);
        } else if(o instanceof ByteBuffer){
            try {
                return new String(((ByteBuffer) o).array(),"ASCII");
            } catch (UnsupportedEncodingException e) {
                return o.toString();
            }
        }else if(o instanceof Map<?,?>){
            
            String retStr = "";
            for (Object name: ((Map<?, ?>) o).keySet()){
                String value = objectToStr(((Map<?, ?>) o).get(name));  
                retStr += objectToStr(name) + ": " + value + "\n";  
            } 
            
            return retStr;
        }else if(o instanceof List){
            
            String retStr = "";
            for(Object elem: (List<?>)o){
                retStr += objectToStr(elem) + "\n";
            }
            return retStr;
        }
        return o.toString();
    }
    
    
}

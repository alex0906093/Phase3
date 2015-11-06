import java.io.IOException;
import java.util.Arrays;

public class Download extends Peer{

	public Download(String ip, int port){
		super(ip,port);

	}
	public boolean sendHandshake(byte[] info_hash, byte[] peerid){
		Message handshake = new Message(info_hash, peerid);
		boolean ret;
		try{
			dOutStream.write(handshake.mess);
			dOutStream.flush();
			socket.setSoTimeout(timeoutTime);
			byte[] receiveShake = new byte[68];
			dInStream.readFully(receiveShake);
			byte[] peerInfoHash = Arrays.copyOfRange(receiveShake, 28, 48);
			ret = Arrays.equals(peerInfoHash, info_hash) ? true : false;
			return ret;
		}catch(Exception e){
			System.out.println("Exception thrown for handshake");
		}
		
		return true;
	}
	
	public void run(){
		
		if(!(sendHandshake(tInfo.info_hash.array(), RUBTClient.peerid))){
			System.out.println("Handshake Failed");
			return;
		}
		try{
			download();
		}catch(Exception e){}
	}
	
	public void download() throws Exception{
		socket.setSoTimeout(3000);
		//Bitfield stuff
		try{
			int len = dInStream.readInt();
			if(len == 0){
				
			}else{
				byte id = dInStream.readByte();
				if(id == BITFEILD_ID){
					byte[] bitfield = new byte[len];
					for(int i = 0; i < len; i++){
						bitfield[i] = dInStream.readByte();
					}
					queueBitfield(bitfield);
				}
			}
			
		}catch(Exception e){}
		//send interested message
		dOutStream.writeInt(1);
		dOutStream.writeByte(INTERESTED_ID);
		
		//now wait for unchoke
		try{
			int len = dInStream.readInt();
			byte id = dInStream.readByte();
			if(id == UNCHOKE_ID){
				peer_choking = 0;
			}
		}catch(IOException e){}
		
		//cant do anything
		if(peer_choking == 1){
			return;
		}
		
		//lets start downloading
		int i;
		while(true){
			i = RUBTClient.globalMemory.nextNeededPiece();
			sendRequest(i);
		}
		
	}
	public void sendRequest(int index){
		
	}
	
	
	public void queueBitfield(byte[] bitfield){
		try{
		BitfieldIterable iter = new BitfieldIterable(bitfield);
		int i = 0;
		for(boolean val : iter){
			if(val){
				System.out.print(" " + i +" " );
				this.availablePieces.add(i);
			}
			i++;
		}}catch(UnsupportedOperationException e){

		}
	}
	
}

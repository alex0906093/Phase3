import java.io.IOException;
import java.net.Socket;
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
		try{
			socket = new Socket(ipAdd, port);
			connect_socket(socket);
			System.out.println("Socket Open");
		}catch(Exception e){
			System.out.println("Connection setup failed");
		}

		System.out.println("Running");
		if(!(sendHandshake(tInfo.info_hash.array(), RUBTClient.peerid))){
			System.out.println("Handshake Failed");
			return;
		}
		System.out.println("Handshake sucess");
		try{
			download();
		}catch(Exception e){}
	}
	
	public void download() throws Exception{
		socket.setSoTimeout(3000);
		//Bitfield stuff
		try{
			int len = dInStream.readInt();
			System.out.println("Len is " + len);
			if(len == 0){
				
			}else{
				byte id = dInStream.readByte();
				System.out.println("ID is " + id);
				if(id == BITFEILD_ID){
					byte[] bitfield = new byte[len];
					for(int i = 0; i < len; i++){
						dInStream.readFully(bitfield);
					}
					//queueBitfield(bitfield);
				}
			}
			
		}catch(Exception e){}
		//send interested message
		dOutStream.writeInt(1);
		dOutStream.writeByte(INTERESTED_ID);
		System.out.println("Wrote interest");
		//now wait for unchoke
		try{
			int len = dInStream.readInt();
			byte id = dInStream.readByte();
			if(id == UNCHOKE_ID){
				peer_choking = 0;
				System.out.println("Unchoked");
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
			get_piece();
		}
		
	}
	public void get_piece(){
		try {
			int len2 =dInStream.readInt();
			if(len2 == 0){
				return;
			}else{
				byte id = dInStream.readByte();
				if(id == PIECE_ID){
					int index = dInStream.readInt();
					int begin = dInStream.readInt();
					byte[] block = new byte[len2-9];
					for(int i = 0; i < len2-9;i++){
						block[i]=dInStream.readByte();
					}
					System.out.println("Read Block");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendRequest(int index){
		Message.RequestMessage reqMess1 = new Message.RequestMessage(index, 0, 16384);
		Message.RequestMessage reqMess2 = new Message.RequestMessage(index, 16384, 16384);
		//send first request
		try {
			dOutStream.writeInt(13);
			dOutStream.writeByte(6);
			dOutStream.writeInt(reqMess1.getPieceIndex());
			dOutStream.writeInt(reqMess1.getBegin());
			dOutStream.writeInt(reqMess1.getBlockLength());
			dOutStream.flush();
			dOutStream.writeInt(13);
			dOutStream.writeByte(6);
			dOutStream.writeInt(reqMess2.getPieceIndex());
			dOutStream.writeInt(reqMess2.getBegin());
			dOutStream.writeInt(reqMess2.getBlockLength());
			dOutStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void queueBitfield(byte[] bitfield){
		try{
		System.out.println("Queing bitfield");
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

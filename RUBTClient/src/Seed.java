//package client;

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;
//import GivenTools;

public class Seed extends Peer{
	/*GLOBALS*/
	public Seed(Socket connectionSocket) {
		super("-1", -1); // superclass not important
					
		try {
			connect_socket(connectionSocket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void run(){
		
	}
	
	public boolean peerToPeerHandshake( byte[] info_hash) throws Exception{
		
		byte[] incomingHandshake = new byte[68]; //all handshake message lengths are the same
		dInStream.readFully(incomingHandshake);
		
		Message outgoingHandshake = new Message( info_hash, RUBTClient.peerid);
		
		//check to see if the incoming message is a bittorent handshake by checking the length of the protocol
		if( incomingHandshake[0] != (byte) 19){
			return false;
		}else{
			//complete the handshake
			System.out.println("Receiving hanshake method from a peer");
			dOutStream.write(outgoingHandshake.mess);
			dOutStream.flush();
			
			//read the response and see if peer is interested
			if (read(dInStream) == 2){
				
				//peer is interested
				Message unchoke = new Message(1, (byte)1);
				dOutStream.write(unchoke.mess);
				dOutStream.flush();
				System.out.println("Handshake complete");
				return true;
			}else{
				//peer is uninterested
				return false;
			}
		}
	}
		
	public void upload() throws Exception{ 
		
		int index, begin, length;
		byte[] pieceBlock;
		Message piece;
		
		//keep looping until the peer is finished
		while(true){
			
			byte type = read(dInStream);
			if ( type == REQUEST_ID){
				
				index = dInStream.readInt();
				begin = dInStream.readInt();
				length = dInStream.readInt();
				
				if( )
			}
		}
	}
}


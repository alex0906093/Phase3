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
		
		System.out.println("Trying to upload in a new thread");
		
		try{
			peerToPeerHandshake( tInfo.info_hash.array());
			//if that succeds we can upload
			upload();
		}catch ( Exception e){
			e.printStackTrace();
		}
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
				
				if( MemCheck.finished_pieces[index] == true){
					 pieceBlock = new byte[length];
					 //copy the requested block into the pieceBlock
					 //throw that pieceBlock into a piece message
					 System.arraycopy(pieces.subList(index, pieces.size()).toArray(), begin, pieceBlock, 0, length );
					 piece = new Message(9+ length, (byte) 7);
					 
					 piece.setLoad(begin, index, pieceBlock, -1, -1, -1, -1);
					 
					 dOutStream.write(piece.mess);
					 dOutStream.flush();
					 MemCheck.uploaded += length;
	
				}else {
					//we dont have this piece
					//Message chokeMess = new Message(1, (byte) 1);
					//dOutStream.write(chokeMess.mess);
					//dOutStream.flush();
				}
			}else{
				//we received some other type of message and should ignore it
			}
		}
	}
}


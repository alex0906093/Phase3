//package client;

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.lang.*;
import java.util.LinkedList;
import java.util.PriorityQueue;

//import GivenTools;

public abstract class Peer implements Runnable{
	public TorrentInfo tInfo = null;
	ArrayList<byte[]> pieces = null;
	public int port;
	public String ipAdd;
	public Socket socket = null;
	public OutputStream output = null;
	public InputStream input = null;
	public DataOutputStream dOutStream = null;
	public DataInputStream dInStream = null;
	public final int timeoutTime = 130000;
	FileOutputStream fOutStream = null;
	public static final int KBLIM = 16384;
	private int am_choking = 1;
	private int am_interested = 0;
	protected int peer_choking = 1;
	private int peer_interested = 0;
	protected Queue<Integer> availablePieces;
	private LinkedList<Integer> gettingList;
	private Queue<Integer> sentList;
	private LinkedList aPieces;
	public static final byte KEEP_ALIVE_ID = -1;
    
    public static final byte CHOKE_ID = 0;
    
    public static final byte UNCHOKE_ID = 1;
    
    public static final byte INTERESTED_ID = 2;
    
    public static final byte NOT_INTERESTED_ID = 3;
    
    public static final byte HAVE_ID = 4;
    
    public static final byte BITFEILD_ID = 5;
    
    public static final byte REQUEST_ID = 6;
    
    public static final byte PIECE_ID = 7;
    
    public static final byte CANCEL_ID = 8;
    
    public static final byte HANDSHAKE_ID = 9;

	public int pSize;
	public Seed seed;



	/*
	 *Constructor for peer
	 */
	public Peer(String ipAdd, int port){
		//Initialize variables
		this.ipAdd = ipAdd;
		this.port = port;
		this.tInfo = tInfo;
		this.availablePieces = new LinkedList<Integer>();
		System.out.println("IP Address of peer is " + ipAdd + "opening Socket");
		//try to establish a connection and open a socket
		try{
			socket = new Socket(ipAdd, port);
			connect_socket(socket);
		}catch(Exception e){
			System.out.println("Connection setup failed");
		}



	}


	public byte read(DataInputStream IStream) throws IOException{
		int len = IStream.readInt();
		if(len ==0){
			return -1;
		}
		int id = IStream.readByte();
		switch(id){
			case(CHOKE_ID):{
				return CHOKE_ID;
			}
			case(UNCHOKE_ID):{
				return UNCHOKE_ID;
			}
			case(INTERESTED_ID):{
				return INTERESTED_ID;
			}
			case(NOT_INTERESTED_ID):{
				return NOT_INTERESTED_ID;
			}
			case(HAVE_ID):{
				return HAVE_ID;
			}
			case(BITFEILD_ID):{
				return BITFEILD_ID;
			}
			case(REQUEST_ID):{
				return REQUEST_ID;
			}
			case(PIECE_ID):{
				return PIECE_ID;
			}
			case(CANCEL_ID):{
				return CANCEL_ID;
			}
			case(HANDSHAKE_ID):{
				return HANDSHAKE_ID;
			}
		
		}
		
		return -1;
	}
	public void connect_socket(Socket socket) throws IOException{
		input = socket.getInputStream();
		output = socket.getOutputStream();
		this.dInStream = new DataInputStream(input);
		this.dOutStream = new DataOutputStream(output);
	}


	/*Method to take bitfield message and put it into a list so we know what this peer has*/
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
	public int sendMess(Message m){
		switch(m.id){
			case(REQUEST_ID):{
				
			}
		}
		return 1;
	}
	/*Close all connections*/
	public void closeCon() throws Exception{
		socket.close();
		dInStream.close();
		dOutStream.close();
		fOutStream.close();
	}
}


	
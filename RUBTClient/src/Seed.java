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
	
	
	

}


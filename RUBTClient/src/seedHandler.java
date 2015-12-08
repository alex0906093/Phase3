import java.io.*;
import java.net.*;


public class seedHandler implements Runnable {

	ServerSocket seederListen;
	
	int portNum = 69420;
	
	
	public seedHandler(){
		
		try{
			seederListen = new ServerSocket(portNum);
		} catch ( Exception e ){
			System.out.println("Server socket set up failure.");
		}
		
	}
	
	public void listener() throws Exception {
		try{
			while(true){
				Socket clientSocket = seederListen.accept();
				//now we have a peer connected to us on this socket
				//create a new seed for it
				
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
	}
	
	public void run(){
		try{
			listener();
		}catch ( Exception e ){
			e.printStackTrace();
		}
	}
}
import java.io.*;
import java.net.*;

/**
 * This class is the thread running on top of our
 * client that is listening to incoming leaches or 
 * peers that are looking to download some of our
 * file. Once the socket is establish a new Seed
 * object is created.
 * 
 * @author Chris McDonough
 */

public class seedHandler implements Runnable {

	ServerSocket seederListen;
	
	int portNum = 6884;	//may want to have this inputed as a argument to the constructor
	
	
	public seedHandler(){
		
		try{
			this.seederListen = new ServerSocket(this.portNum);
		} catch ( Exception e ){
			System.out.println("Server socket set up failure.");
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * The running thread that waits and listens for 
	 * incoming peers.
	 */
	
	public void run(){
		System.out.println("New seed handler thread running");
		try{
				while(true){
					Socket clientSocket = seederListen.accept();
					//now we have a peer connected to us on this socket
					//create a new seed for it
					Seed newSeeder =  new Seed( clientSocket);
					new Thread(newSeeder).start();
					
				}
		} catch ( Exception e ){
				e.printStackTrace();
		}
		
	}
}
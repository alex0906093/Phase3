import java.io.IOException;
import java.net.UnknownHostException;

public class TrackerThread extends RUBTClient implements Runnable {

	public boolean running = true;
	
	public void run(){

		while(running){
			
				
			try {
				tResponseDecoded = tResponseDecoded.getTrackerResponse(tInfo,globalMemory.downloaded,globalMemory.uploaded);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(tResponseDecoded.interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}
			
	}
	
	public void stop() {
		this.running = false;
	}
}

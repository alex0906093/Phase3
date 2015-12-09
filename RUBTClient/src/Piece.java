//package client;

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;
//import GivenTools;

public class Piece{
	public byte[] rawBytes;
	private Object lock1;
	public Verify verify;
	public boolean verified;
	public int pieceSize;
	public int numBlocks;
	public int blockSize;
	public int pieceIndex;
	public boolean[] wroteBlock;
	private boolean isLast;
	private int lastBlockSize;
	//constructor for last piece
	public Piece(int blockSize, int pieceSize, int pieceIndex, int lastBlockSize){
		//only one block
		this.isLast = true;
		//only one block
		if(lastBlockSize == 0){
			this.pieceIndex=pieceIndex;
			this.blockSize=blockSize;
			this.pieceSize = pieceSize;
			this.lastBlockSize = pieceSize;
			this.rawBytes = new byte[pieceSize];
			this.numBlocks = 1;
			this.wroteBlock = new boolean[numBlocks];
		}
		else{
			this.pieceIndex=pieceIndex;
			this.blockSize=blockSize;
			this.pieceSize = pieceSize;
			this.lastBlockSize = pieceSize%16384;
			this.numBlocks = ((pieceSize-lastBlockSize)/16384) + 1;
			this.wroteBlock = new boolean[numBlocks];
		}
	}
	
	//constructor for not last piece
	public Piece(int blockSize, int pieceSize, int pieceIndex){
		synchronized(this){
			this.pieceIndex = pieceIndex;
			this.blockSize = blockSize;
			this.pieceSize = pieceSize;
			this.numBlocks = pieceSize/16384;
			this.rawBytes = new byte[pieceSize];
			this.wroteBlock = new boolean[numBlocks];
			this.verify = new Verify(RUBTClient.tInfo);
		}
	}
	

	public void writeBlock(byte[] b, int begin){
		synchronized(this){
			if(rawBytes[begin] != 0){
				return;
			}
			int a;
			if(begin == 0){
				a = 0;
			}else{
				a = pieceSize/begin;
				a--;
			}
			wroteBlock[a] = true;
		//System.out.println("Piece size is " + pieceSize + " Writing Block " + a + " of " + numBlocks + " to piece " + pieceIndex);
		//System.out.println("byte array length is " + b.length);
		System.arraycopy(b,0,this.rawBytes,begin,b.length);
		}
	}

	/*
	 *Method to check if we have successfully downloaded all of the blocks
	 *return 0 if we have yet to receive all of the blocks
	 *return -1 if the bytes are corrupt
	 *return 1 if the hash checks out
	*/
	public int haveAllBlocks(){
		
			//System.out.println("trying to verify :piece :numBlocks" + this.pieceIndex + " " + this.numBlocks);
		for(int i = 0; i < numBlocks; i++){
			if(!this.wroteBlock[i]){
				//System.out.println("Block " + i + " of Piece " + pieceIndex + " is 0");
				return 0;
			}
		}	//if our bytes
			//System.out.println("Got here");
			if(!verify.checkHash(rawBytes, pieceIndex)){
				System.out.println("The data was corrupt, trying to redownload");
				reset();
				return -1;
			}else{
				verified = true;
				try {
					//System.out.println("Got to here");
					RUBTClient.globalMemory.updateDownload(pieceIndex,rawBytes);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Wrote piece " + pieceIndex);
				return 1;
			}
		
	}
	//corrupt data, get it again
	private void reset(){
		this.rawBytes = new byte[pieceSize];
	}
}
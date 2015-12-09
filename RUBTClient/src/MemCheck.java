//package client;
import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.io.RandomAccessFile;
//import GivenTools;
/*
 *This class will be used as the 
 *main memory so we can run threads 
 *and have the memory locked safely
 */

public class MemCheck{
	//Array List of all pieces in memory, maybe these shouldn't be held, keep track
	public ArrayList<Piece> pieces = new ArrayList<Piece>();
	public boolean[] finished_pieces;
	//file we're writing to
	public RandomAccessFile file;
	private TorrentInfo tInfo;
	
	//Lets keep a queue of all pieces we need, only accessable through a method in this class
	private Queue<Integer> neededPieces = new LinkedList<Integer>();
	
	//ints to see what we need to do
	public int uploaded=0;
	public int downloaded=0;
	public int left;
	private int piecesGotten=0;
	
	/*Constructor*/
	public MemCheck(TorrentInfo tInfo, RandomAccessFile file){
		this.tInfo = tInfo;
		this.file = file;
		this.left = tInfo.file_length;
		//INITIALIZE THE Queue, worry about everything else later
		for(int i =0; i < tInfo.piece_hashes.length; i++){
			neededPieces.add(i);
		}
		this.finished_pieces=new boolean[tInfo.piece_hashes.length];
		
		//INITIALIZE Pieces
		for(int i = 0; i < tInfo.piece_hashes.length; i++){
			if(i != tInfo.piece_hashes.length -1){
				Piece p = new Piece(16384,tInfo.piece_length,i);
				pieces.add(p);
			}//last piece
			else{
				int lastPieceSize = tInfo.file_length%tInfo.piece_length;
				int lastBlockSize = lastPieceSize % 16384;
				if(lastPieceSize == lastBlockSize){
					Piece p = new Piece(lastBlockSize,lastPieceSize,i,0);
					pieces.add(p);
				}else{
					Piece p = new Piece(16384, lastPieceSize, i, lastBlockSize);
					pieces.add(p);
				}
			}
		}
	}
	public synchronized void updateDownload(int index, byte[] fullPiece)
			throws Exception {
		// System.out.println("Finished Downloading Piece " + index);
		try{
			int r = index*tInfo.piece_length;
			this.file.seek(r);
			this.file.write(fullPiece);
		}catch(IOException e){}
		this.downloaded += fullPiece.length;
		this.left = left - fullPiece.length;
		this.piecesGotten++;
	}
	
	public boolean have_piece(int index){
		return this.finished_pieces[index];
	}
	/*Method that gives new piece for a thread to get*/
	public synchronized int nextNeededPiece(){
		synchronized(neededPieces){
			if(neededPieces.peek() == null)
				return -1;
			return neededPieces.remove();
		}
	}
	public synchronized void add_block(int pieceIndex, int offset,int size,byte[] b){
		Piece p = this.pieces.get(pieceIndex);
		p.writeBlock(b, offset);
		if(p.haveAllBlocks() == 1){
			this.finished_pieces[pieceIndex]=true;
			System.out.println("verified");
		}
		return;
	
	}
	public synchronized void putPieceBack(int i){
		synchronized(neededPieces){
			neededPieces.add(i);
		}
	}

	}


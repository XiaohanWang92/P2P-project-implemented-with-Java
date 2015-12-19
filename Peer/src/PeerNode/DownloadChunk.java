package PeerNode;

import java.net.*;
import java.util.Iterator;
import java.io.*;

public class DownloadChunk extends Thread {
	private int neighborPort;
	private Socket downloadSocket;
	int portNum;
	public DataInputStream dis;
	public DataOutputStream dos;
	public FileOutputStream outPutFile;
	
	public DownloadChunk(int np){
		neighborPort=np;
	}
	public Socket init() {
		Socket trySocket=null;
		System.out.println("Peer initializes connection to its neighbor, now is trying......");
		try{
			System.out.println("Connected to localhost in port "+neighborPort);
			trySocket=new Socket(InetAddress.getLocalHost(),neighborPort);	
			}
			catch (ConnectException e) {
				System.err.println("Connection refused. neighbor offline. Wait for 1 second to reconnect!");
				try {
					Thread.sleep(1000);
				} 
				catch (InterruptedException e1) {
					e1.printStackTrace();
				}
    			return null;
			} 
			catch(UnknownHostException unknownHost){
				System.err.println("You are trying to connect to an unknown host!");
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		return trySocket;//If connection is successfully done, then the return socket object will not be null
	}
	
	public void run(){
		Socket s = null;
		//if connection is established, the while-loop will be stopped
		while(s==null){
			s=this.init();		
		}
		downloadSocket=s;
		System.out.println("connect to my neighbor!");
		try {//basically the same with downloading from file_owner, just add more message to interact with neighbor
			dis=new DataInputStream(downloadSocket.getInputStream());
			dos=new DataOutputStream(downloadSocket.getOutputStream());
			boolean isEmpty=false;//if list is empty, means that we have receive all chunks
			while(isEmpty==PeerConnect.list.isEmpty()){//if not, ask neighbor to send more chunks that I don't have
				System.out.println("Missing peices number: "+PeerConnect.list.size());
				Iterator<Integer> iter = PeerConnect.list.iterator();
				//use iterator to remove list item in order to avoid out of bound exception
				while(iter.hasNext()){
					int get=iter.next();
					dos.writeInt(get);//tell neighbor chunk number that I don't have
					dos.flush();
					int doYouHave=dis.readInt();//receive answer from neighbor
					if(doYouHave==1111){//if code is 1111, means that neighbor has this chunk and start the transmission, 1112 means I don't have it
						int index=dis.readInt();
						String fileName=dis.readUTF();//receive chunk name
						System.out.println("receive chunk "+fileName+" from my neighbor!");
						long fileLength=dis.readLong();//receive chunk size
						outPutFile = new FileOutputStream(System.getProperty("user.dir")+File.separator+fileName);
						byte[] buffer=new byte[1024];
						long sizeCount=0;
						while(true){
							int read = 0;
			                read = dis.read(buffer);
			                sizeCount=sizeCount+read;
			                if(read == -1)
			                    break;
			                outPutFile.write(buffer,0,read);
			                outPutFile.flush();
			                if(sizeCount==fileLength)
			                	break;
			            }
						outPutFile.close();	
						System.out.println("remove "+index+" from missing chunk list, because I've got it!");
						iter.remove();//remove what I have get
					}
				}
			}
			if(PeerConnect.list.isEmpty()==true){
				FileCombiner fc =new FileCombiner();
				try {
					fc.combineFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			int rcvAll=8899;
			dos.write(rcvAll);
			dos.flush();
			System.out.println("I have receive all chunks! Sending "+rcvAll+" to my neighbor to tell him!");
		}catch (IOException e) {
			e.printStackTrace();
			}
	
	}
}
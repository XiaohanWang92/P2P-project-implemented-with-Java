package PeerNode;

import java.net.*;
import java.io.*;

public class ConnectToServer{
	public int portNum;
	Socket requestSocket;
	DataInputStream dis;
    FileOutputStream outPutFile;
    int fileNum;
    int fileSize;
    public int serverSendNum;
    public String originalName;
    
	//constructor
	public ConnectToServer(int pn){
		portNum=pn;
	}
	//try to connect, if refused, try again until Server accepts the connection.
	public Socket init() {
		Socket trySocket=null;
		System.out.println("Peer initializes connection to server node, now is trying......");
		try{
			System.out.println("Connected to localhost in port 8500");
			trySocket=new Socket(InetAddress.getLocalHost(),8500,InetAddress.getLocalHost(),portNum);	
			}
			catch (ConnectException e) {
				System.err.println("Connection refused. Server is offline. Wait for 1 second to reconnect!");
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
	//main method in this class
	public void run() throws Exception{
		Socket s = null;
		//if connection is established, the while-loop will be stopped
		while(s==null){
			s=this.init();		
		}
		requestSocket=s;
		dis = new DataInputStream(requestSocket.getInputStream());
		originalName=dis.readUTF();//file name
		PeerConnect.fileName=originalName;
		System.out.println("file name is "+PeerConnect.fileName);
		fileNum=dis.readInt();
		PeerConnect.fileNum=fileNum;
		System.out.println("the total chunk numbers are "+fileNum);
		for(int i=1;i<=fileNum;i++){
			PeerConnect.list.add((Integer)i);//initializing the list which contains the chunks that peer doesn't have right now
		}
		fileSize=dis.readInt();
		PeerConnect.fileSize=fileSize;
		System.out.println("the total file size is "+fileSize);
		serverSendNum=dis.readInt();
		System.out.println("now receiving chunks from server, I will recieve "+serverSendNum+" chunks");
		for(int i=1;i<=serverSendNum;i++){
			int get=dis.readInt();
			System.out.println("I get chunk number "+get);
			PeerConnect.list.remove((Integer)get);
			String fileName=dis.readUTF();//receive chunk name
			System.out.println("chunk name is "+fileName);
			long fileLength=dis.readLong();//receive chunk size
			System.out.println("chunk size is "+fileLength);
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
		}
	}	
}
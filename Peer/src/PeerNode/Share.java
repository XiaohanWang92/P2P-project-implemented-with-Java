package PeerNode;

import java.net.*;
import java.io.*;

public class Share extends Thread {
	private int myport;
	public DataInputStream dis;
	public DataOutputStream dos;
	public FileInputStream fis;
	
	public Share(int p){
		myport=p;
	}
	
	public void run(){
		try {		
			ServerSocket listen = new ServerSocket(myport);
			while(true){
				System.out.println("start to listen the neighbor's connection");
				Socket share=listen.accept();
				dis = new DataInputStream(share.getInputStream());
				dos = new DataOutputStream(share.getOutputStream());
				while(true){
					int chunkAsk=dis.readInt();
					if(chunkAsk==8899){//if receive 8899, means all chunks has been received by my neighbor
						//if not, what I receive is the chunk index.(for scalability, sending string and check string.isEqual() is better)
						System.out.println("All chunks has been received! Thread will close");
						listen.close();
					}
					System.out.println("you ask me do I have chunk: "+chunkAsk);
					boolean flag=false;
					if(flag==PeerConnect.list.contains(chunkAsk)){//if index is not in list, that means I have it!
						System.out.println("yes I have it! I'll send to you!");
						int iHave=1111;//tell neighbor that I have it!
						dos.writeInt(iHave);
						dos.flush();
						File file=new File(System.getProperty("user.dir")+File.separator+"part"+chunkAsk);
						fis = new FileInputStream(file);
						dos.writeInt(chunkAsk);//send chunk index again
						dos.flush();
						dos.writeUTF(file.getName());//send chunk name
						dos.flush();
						dos.writeLong(file.length());//send chunk length
						dos.flush();
						byte[] buffer=new byte[1024];
						int length = 0;
						while((length = fis.read(buffer, 0, buffer.length)) > 0){
							dos.write(buffer, 0, length);
							dos.flush();//send them into socket sending stream and go back to receive next chunkAsk
						}	
					}else{
						//if I don't have it
						int noHave=1112;
						dos.writeInt(noHave);
						dos.flush();
					}
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
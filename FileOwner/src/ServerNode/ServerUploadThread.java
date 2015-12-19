package ServerNode;
import java.net.*;
import java.io.*;

public class ServerUploadThread extends Thread{
	private Socket connection;//new socket to handle each peer
    private FileInputStream fis;//stream reads from the file
    private DataOutputStream dos;//stream writes to the socket and send to peer
    public DataInputStream receiveRes;//data input stream to receive message from peer
    String fileName;
    int fileNum;
    int lastChunkSize;
    String filePath;
    int fileSize;   
    //constructor
    public ServerUploadThread(Socket connectEstablish, String thisfileName,int fn, int lfs,String fp,int fs) {
        connection = connectEstablish;
        fileName=thisfileName;
        fileNum=fn;
        lastChunkSize=lfs;
        filePath=fp;
        fileSize=fs;
    }
    //main method of this thread
    public void run() {
    	int giveChunkIndex1=(int)Math.floor(fileNum/5);//if file number is 5a+b, (0<=b<5)
    	//send each 'a' number chunks to each peer, they will gets file part with a sequential index, for example
    	//peer1 will get file part1 to part(a), peer2 will get file part(a+1) to part(2a)......
    	int giveChunkIndex2=fileNum%5;//send the remainder number of chunks to peer1 (b chunks)
    	int sendLoop=giveChunkIndex1;
    	int sendLoop2=giveChunkIndex2;
    	//sending peer 1 (port number 9000) procedure
        if(connection.getPort()==ServerConnect.peerPorts[0]) {
        	System.out.println("sending chunks to peer with port 9000, chunks number is part1 to part"+giveChunkIndex1+
        			" and from part"+(5*giveChunkIndex1+1)+" to last chunk, that is part"+fileNum);
        	try {
				dos = new DataOutputStream(connection.getOutputStream());
				receiveRes=new DataInputStream(connection.getInputStream());
				//Socket.getOutputStream() will return the OutPutStream this socket is associated, we refer to it
				//Then we can implement data sending through this socket to lower-layer
				dos.writeUTF(fileName);//tell peer the name of the file
				dos.flush();
				dos.writeInt(fileNum);//send total chunk number
    			dos.flush();
    			dos.writeInt(fileSize);//send total file size(for combination part)
    			dos.flush();
    			dos.writeInt(giveChunkIndex1+giveChunkIndex2);//tell peer the chunk total number that sever node will send to
    			dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	for(int i=1;i<=sendLoop;i++){
        		File file=new File(filePath+File.separator+"part"+i);
				try {
					fis=new FileInputStream(file);//associate input stream to this chunk
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
        		try {
					dos.writeInt(i);//send chunk index
					dos.flush();
        			dos.writeUTF(file.getName());//send chunk name
	                dos.flush();
	                dos.writeLong(file.length());//send chunk length
	                dos.flush();
	                byte[] buffer=new byte[1024];
	                int length = 0;
	                while((length = fis.read(buffer, 0, buffer.length)) > 0){
	                	//read method will read up to length bytes into buffer from buffer[0] to buffer[length]
	                	//if read method read the end of the file, will return -1
	                	//input stream read data sequentially, that is from the beginning to end of the data chunk
	                	//so next read will start from the part that last read stopped and unread
	                    dos.write(buffer, 0, length);//write these data in buffer which read from file into output stream
	                    dos.flush();
	                }
	        		System.out.println("now sending chunk with name part"+i+" is done!");
				} catch (IOException e) {
					e.printStackTrace();
				} 
        	}
        	for(int j=1;j<=sendLoop2;j++){//send the last remainder part of chunks all to peer one
 
        		File file=new File(filePath+File.separator+"part"+(j+5*giveChunkIndex1));
        		try {
        			fis=new FileInputStream(file);//read only
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
        		try {
					dos.writeInt(j+5*giveChunkIndex1);//send chunk index
					dos.flush();
        			dos.writeUTF(file.getName());//send chunk name
	                dos.flush();
	                dos.writeLong(file.length());//send file length
	                dos.flush();
	                byte[] buffer=new byte[1024];
	                int length = 0;
	                while((length = fis.read(buffer, 0, buffer.length)) > 0){
	                    dos.write(buffer, 0, length);
	                    dos.flush();
	                }
	           		System.out.println("sending chunk with name part"+(j+5*giveChunkIndex1)+" is done!");
				} catch (IOException e) {
					e.printStackTrace();
				} 
        	}
        }
        //same as the if block above, send chunks to peer with port 9001
        if(connection.getPort()==ServerConnect.peerPorts[1]){
        	System.out.println("sending chunks to peer with port 9001, chunks number is "+(giveChunkIndex1+1)+
        			" to part"+(giveChunkIndex1*2));
        	try {
        		dos = new DataOutputStream(connection.getOutputStream());
				dos.writeUTF(fileName);//tell peer the name of the file
				dos.flush();
        		dos.writeInt(fileNum);//send total chunk number
        		dos.flush();
        		dos.writeInt(fileSize);//send total file size(for combination part)
        		dos.flush();
        		dos.writeInt(giveChunkIndex1);
        		dos.flush();
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}
        	for(int i=1;i<=sendLoop;i++){
        		File file=new File(filePath+File.separator+"part"+(i+giveChunkIndex1));
        		try {
        			fis=new FileInputStream(file);
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
        		try {
					dos.writeInt(i+giveChunkIndex1);//send chunk index
					dos.flush();
        			dos.writeUTF(file.getName());//send chunk name
        			dos.flush();
        			dos.writeLong(file.length());//send file length
        			dos.flush();
        			byte[] buffer=new byte[1024];
        			int length = 0;
        			while((length = fis.read(buffer, 0, buffer.length)) > 0){
	                    dos.write(buffer, 0, length);
	                    dos.flush();
	                }
            		System.out.println("sending chunk with name part"+(i+giveChunkIndex1)+" is done!");
        		} catch (IOException e) {
        			e.printStackTrace();
				}    		
        	}
        }
        if(connection.getPort()==ServerConnect.peerPorts[2]){
        	System.out.println("sending chunks to peer with port 9002, chunks number is "+(giveChunkIndex1*2+1)+
        			" to part"+(giveChunkIndex1*3));
        	try {
        		dos = new DataOutputStream(connection.getOutputStream());
				dos.writeUTF(fileName);//tell peer the name of the file
				dos.flush();
        		dos.writeInt(fileNum);//send total chunk number
        		dos.flush();
        		dos.writeInt(fileSize);//send total file size(for combination part)
        		dos.flush();
        		dos.writeInt(giveChunkIndex1);
        		dos.flush();
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}
        	for(int i=1;i<=sendLoop;i++){
        		File file=new File(filePath+File.separator+"part"+(i+giveChunkIndex1*2));
        		try {
        			fis=new FileInputStream(file);
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
        		try {
					dos.writeInt(i+giveChunkIndex1*2);//send chunk index
					dos.flush();
        			dos.writeUTF(file.getName());//send chunk name
        			dos.flush();
        			dos.writeLong(file.length());//send file length
        			dos.flush();
        			byte[] buffer=new byte[1024];
        			int length = 0;
        			while((length = fis.read(buffer, 0, buffer.length)) > 0){
	                    dos.write(buffer, 0, length);
	                    dos.flush();
	                }
            		System.out.println("sending chunk with name part"+(i+giveChunkIndex1*2)+" is done!");
        		} catch (IOException e) {
        			e.printStackTrace();
				}      		
        	}
        }
        if(connection.getPort()==ServerConnect.peerPorts[3]){
        	System.out.println("sending chunks to peer with port 9003, chunks number is "+(giveChunkIndex1*3+1)+
        			" to part"+(giveChunkIndex1*4));
        	try {
        		dos = new DataOutputStream(connection.getOutputStream());
				dos.writeUTF(fileName);//tell peer the name of the file
				dos.flush();
        		dos.writeInt(fileNum);//send total chunk number
        		dos.flush();
        		dos.writeInt(fileSize);//send total file size(for combination part)
        		dos.flush();
        		dos.writeInt(giveChunkIndex1);
        		dos.flush();
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}
        	for(int i=1;i<=sendLoop;i++){
        		File file=new File(filePath+File.separator+"part"+(i+giveChunkIndex1*3));
        		try {
        			fis=new FileInputStream(file);
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
        		try {
					dos.writeInt(i+giveChunkIndex1*3);//send chunk index
					dos.flush();
        			dos.writeUTF(file.getName());//send chunk name
        			dos.flush();
        			dos.writeLong(file.length());//send file length
        			dos.flush();
        			byte[] buffer=new byte[1024];
        			int length = 0;
        			while((length = fis.read(buffer, 0, buffer.length)) > 0){
	                    dos.write(buffer, 0, length);
	                    dos.flush();
	                }
            		System.out.println("sending chunk with name part"+(i+giveChunkIndex1*3)+" is done!");
        		} catch (IOException e) {
        			e.printStackTrace();
				}  
        	}
        }
        if(connection.getPort()==ServerConnect.peerPorts[4]){
        	System.out.println("sending chunks to peer with port 9004, chunks number is "+(giveChunkIndex1*4+1)+
        			" to part"+(giveChunkIndex1*5));
        	try {
        		dos = new DataOutputStream(connection.getOutputStream());
				dos.writeUTF(fileName);//tell peer the name of the file
				dos.flush();
        		dos.writeInt(fileNum);//send total chunk number
        		dos.flush();
        		dos.writeInt(fileSize);//send total file size(for combination part)
        		dos.flush();
        		dos.writeInt(giveChunkIndex1);
        		dos.flush();
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}
        	for(int i=1;i<=sendLoop;i++){
        		File file=new File(filePath+File.separator+"part"+(i+giveChunkIndex1*4));
        		try {
        			fis=new FileInputStream(file);
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
        		try {
					dos.writeInt(i+giveChunkIndex1*4);//send chunk index
					dos.flush();
        			dos.writeUTF(file.getName());//send chunk name
        			dos.flush();
        			dos.writeLong(file.length());//send file length
        			dos.flush();
        			byte[] buffer=new byte[1024];
        			int length = 0;
        			while((length = fis.read(buffer, 0, buffer.length)) > 0){
	                    dos.write(buffer, 0, length);
	                    dos.flush();
	                }
            		System.out.println("sending chunk with name part"+(i+giveChunkIndex1*4)+" is done!");
        		} catch (IOException e) {
        			e.printStackTrace();
				}  
        	}
        }
    }
}
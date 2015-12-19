package ServerNode;
import java.net.*;
import java.io.*;

public class ServerConnect {
	private static int serverport;
	static String partFilePath;
	public static String originalFileName;
	public static int lastPartSize;
	public static int fileNumber;
	public static int totalFileSize;
	public static int[] peerPorts;
	
	public static void main(String[] args) throws Exception {
		//find current path
		String sourceFilePath = System.getProperty("user.dir");
		System.out.println("Now reading config file...");
		File readConfig=new File(sourceFilePath+File.separator+"config.txt");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(readConfig));
		BufferedReader br = new BufferedReader(reader);
		String line;
		line = br.readLine();
		serverport=Integer.parseInt(line);
		System.out.println("My listen port is "+serverport);
		peerPorts=new int[5];
		for(int i=0;i<5;i++){
			line=br.readLine();
			peerPorts[i]=Integer.parseInt(line);
			System.out.println("My peers port is "+peerPorts[i]);
		}
		br.close();
		partFilePath=sourceFilePath;
		//scan current path, find upload file
		File scanHelper = new File(sourceFilePath);
		File[] array = scanHelper.listFiles();
		String[] fileName= new String[array.length];
		for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                // only takes file name, list files in current path
                fileName[i]=array[i].getName();   
                System.out.println("the current path has file: " + fileName[i]);
            }  
		}	
		//chunk size parameter is 100kb=100*1024 bytes
		//do cutting and return parameters which will need later
		FileSplit cut = new FileSplit();
		cut.splitFile(sourceFilePath, 100*1024);
		originalFileName=cut.originalFileName;//file name, used in reassemble
		lastPartSize=cut.lastPartSize;//last chunk size
		fileNumber=cut.fileNumber;//total file number
		totalFileSize=cut.fileLength;//total file length, used in reassemble
		
        ServerSocket listen = new ServerSocket(serverport);
		System.out.println("Now The file_owner is listening."); 
        try {
        	while(true) {
        		Socket s = listen.accept();
        		ServerUploadThread sut = new ServerUploadThread(s,originalFileName,fileNumber,
        				lastPartSize,partFilePath,totalFileSize);
            	sut.start();//the run method in this thread will be called by this method
                System.out.println("A peer is now being connected! The peer port number is "+s.getPort());
            }
        } 
        finally {
            listen.close();
        } 
    }
}
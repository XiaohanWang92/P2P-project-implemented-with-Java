package PeerNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class PeerConnect{
	private static int portNum;//=9003; //this is peer 4's port number
	private static int neighborPort;//=8703;
	private static int listenPort;//=8704;
	public static ArrayList<Integer> list=new ArrayList<Integer>();//what I don't have
	public static String fileName;
	public static int fileNum;
	public static int fileSize;
	
	public static void main(String[] args) throws Exception{
		System.out.println("reading the config file......");
		File readConfig=new File(System.getProperty("user.dir")+File.separator+"config.txt");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(readConfig));
		BufferedReader br = new BufferedReader(reader);
		String line;
		line = br.readLine();
		String[] split=line.split(" ",3);
		portNum=Integer.parseInt(split[0]);
		neighborPort=Integer.parseInt(split[1]);
		listenPort=Integer.parseInt(split[2]);
		br.close();
		System.out.println("Reading completes! my portNum is "+portNum+"\n"+"my neighborPort is "+neighborPort+"\n"+"And my listenPort is "+listenPort);
		ConnectToServer cts=new ConnectToServer(portNum);
		cts.run();// receive file from server
		Scanner sc = new Scanner(System.in);
		System.out.println("press any key to start sharing chunk...");
		String s=sc.nextLine();
		Share shareFile = new Share(listenPort);
		shareFile.start();
		System.out.println("you entered words: "+s+" ,press any key to start download chunk...");
		s=sc.nextLine();
		sc.close();
		DownloadChunk dlc=new DownloadChunk(neighborPort);
		dlc.start();
	}		
}
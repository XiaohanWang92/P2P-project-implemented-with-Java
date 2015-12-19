package PeerNode;

import java.io.File;
import java.io.RandomAccessFile;

public class FileCombiner
{
	static int fileNumber=PeerConnect.fileNum;
	static String combinePath=System.getProperty("user.dir");
	static int fileLength=PeerConnect.fileSize;
	static String originalFileName=PeerConnect.fileName;
	
	public void combineFile() throws Exception{
		File eachFileOutput = new File(System.getProperty("user.dir")+File.separator+originalFileName);
		RandomAccessFile combineFile=new RandomAccessFile(eachFileOutput, "rw");
		byte[] buffer = new byte[100*1024];
		int off=0;
		//same idea, do it reversely
		for(int i=1;i<fileNumber;i++){
			combineFile.seek(off);
			File partRead = new File(combinePath+File.separator+"part"+i);
			RandomAccessFile partialRead=new RandomAccessFile(partRead, "r");
			partialRead.readFully(buffer);
			combineFile.write(buffer);//write into combineFile, starting from offset
			partialRead.close();
			off=off+102400;
			System.out.println("chunk "+i+" has been combined into file!");
			}
		combineFile.seek(off);
		File lastPartRead = new File(combinePath+File.separator+"part"+fileNumber);
		byte[] buffer2 = new byte[(int)lastPartRead.length()];
		RandomAccessFile partialRead=new RandomAccessFile(lastPartRead, "r");
		partialRead.readFully(buffer2);
		combineFile.write(buffer2);
		System.out.println("last chunk has been combined into file!");
		partialRead.close();
		combineFile.close();
		System.out.println("We combine "+fileNumber+" chunks into "+originalFileName);
		System.out.println("Stored direction: "+System.getProperty("user.dir"));
		System.out.println("file size is "+(fileLength/1024)+"KB");
	}
}
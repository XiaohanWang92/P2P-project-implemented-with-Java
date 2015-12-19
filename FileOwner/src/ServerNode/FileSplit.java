package ServerNode;

import java.io.*;
import java.util.*;

public class FileSplit{
	
	public String originalFileName;
	public int fileLength;
	public int lastPartSize;
	public int fileNumber;
	
	public void splitFile(String sourceFilePath,int partFileLength) throws Exception {
		
		File sourceFile = null;//directory that stores the source file
		File targetFile = null;
		RandomAccessFile ips = null;//input stream
		RandomAccessFile ops = null;//output stream
		byte[] buffer = null;
		int partNumber = 0;
		int totalFileLength=0;
		
		System.out.println("please choose which one you want to split. "
				+"Current path is "+sourceFilePath);
		Scanner s = new Scanner(System.in);
        String splitFileName = s.nextLine(); //scan the input String from console
        s.close();
        originalFileName=splitFileName;
        System.out.println("file name: "+splitFileName+" will become chunks!");
		sourceFile = new File(sourceFilePath+File.separator+splitFileName);//pinpoint this file
		totalFileLength=(int)sourceFile.length();
		fileLength=totalFileLength;
		
		ips = new RandomAccessFile(sourceFile,"r");//open file stream to read this file	into byte buffer
		buffer = new byte[partFileLength];
		partNumber=(int)Math.ceil(((double)totalFileLength)/((double)partFileLength));//get total chunk number
		int modular=totalFileLength%partFileLength;//use modular arithmetic get last part size which may be less than 100KB
		lastPartSize=modular;
		System.out.println("file will be splitted into: "+(partNumber-1)+" chunks of "
		+(partFileLength/1024)+" KB and last chunk will be "+(modular/1024)+" KB");
		
		int off=0;//offset use to point the position where starts read the file
		for(int i=1;i<partNumber;i++){//the first (total chunks number-1) chunks with size of 100KB
			ips.seek((long)off);//set offset, next read/write of input stream will start here
			ips.readFully(buffer);//read buffer.length size data fully from position of offset
			String targetFilePath = sourceFilePath + File.separator + "part" + i;
			//split file name and directory: part1, part2,...and so on
			targetFile = new File(targetFilePath);
			ops = new RandomAccessFile(targetFile,"rw");//out put data stream into these chunks
			ops.write(buffer);//write buffer data into this file part
			ops.close();
			off=off+partFileLength;//set new offset
			System.out.println("chunks part"+i+" has been created! ");
		}
		//cut last part
		ips.seek((long)off);
		byte[] buffer2=new byte[modular];//modular value is last chunk's size
		ips.readFully(buffer2);
		String targetFilePath = sourceFilePath + File.separator + "part" + partNumber;//partNumber is the last part's name
		targetFile = new File(targetFilePath);
		ops = new RandomAccessFile(targetFile,"rw");
		ops.write(buffer2);
		ops.close();
		System.out.println("the last chunk part"+partNumber+" has been created!");
		fileNumber=partNumber;
		ips.close();
	}
	
}
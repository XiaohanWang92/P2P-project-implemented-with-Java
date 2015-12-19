This is my project for school course. First of all, I can't guarantee there is no bug when someone forked this and simply tried to run it because I only test on my own system and TA's demo computer and you need to read the code, understand some details and try to accommodate to your environment before try to run it.

Project description: This project creates a P2P network for file sharing. It resembles some features of Bit-torrent, but much simplified. There are two kinds of node: peer (I'm requested to create a five-peer system in this project) and file owner.
The file owner has a file, and it breaks the file into chunks of 100KB or less, each stored as a separate file. The file owner listens on a TCP port. It is designed as a server that can run multiple threads to serve multiple clients simultaneously.
Each peer is able to connect to the file owner to download some chunks. It then has two threads of control, one acting as a server that uploads the local chunks to another peer (referred to as upload neighbor), and the other acting as a client that downloads chunks from a third peer (referred to as download neighbor). So each peer has two neighbors, one of which will get chunks from this peer and the other will send chunks to this peer.
1. Start the file owner process, read config file which giving a listening port and scan the current path to choose the file that you wish to be split and shared by input the file name.
2. Start five peer processes, read config file which giving the file owner's listening port, the peer's listening port, and its download neighbor's listening port.
3. Each peer connects to the file owner's listening port. The latter creates a new thread to give one or several chunks to the peer, while its main thread goes back to listening for new peers. Each chunk will only be given to only one peer.
4. After receiving chunk(s) from the file owner, the peer stores them as separate file(s) and record the information recording what chunks that have been given and what have not, listing the IDs of the chunks it has and it has not.
5. The peer then proceeds with two new threads, with one thread listening to its upload neighbor to which it will upload file chunks, and the other thread connecting to its download neighbor.
6. The peer gets the chunk ID missing list from the download neighbor, compare with its own to find the missing ones, and gives those to the neighbor. At the mean time, it sends its own missing chunk ID list to its neighbor, and requests deloading chunks from the neighbor.
7. After a peer has all file chunks, it combines them for a single file.
8. A peer should output its activity to its console whenever it receives a chunk, sends a chunk, activity of the chunk list that it has or hasn't, requests for chunks, or receives such a request.

Additional detail: Basically I copied my Eclipse workspace folder into this repository. I have put a config text file or a sample shared pdf into all directory's bin folders if they are needed. if you'd like to run the program in CMD or Terminal, you may enter bin directory and enter:
"java package_name.main_class_name"
The package name is SeverNode and PeerNode, respectively.
The main class name is ServerConnect and PeerConnect, respectively.
Because the process is so quick, I use Scanner to wait some input from keyboard to separate the starting of peer download and upload threads, you can find more details by reading peer's main class method.

The server uses port 8500 to listen, and peer will connect server with port numbers from 9000 to 9004. The interaction between peers: Peer 1 listens to port 8701, connects to 8705. Peer 2 listens to port 8702, connects to 8701. Peer 3 listens to port 8703, connects to 8702. Peer 4 listens to port 8704, connects to 8703. Peer 5 listens to port 8705, connects to 8704.
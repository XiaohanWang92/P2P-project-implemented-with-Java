This is my project file for CNT5106c Computer Network.
Basically I copied my eclipse workspace folder into this file. For demonstration or grading purpose, I have put config text or a sample shared pdf into all directory's bin folders if they are needed. if you'd like to run the program in CMD or Terminal, you may enter bin directory and enter "java package_name.main_class_name"
The package name is SeverNode and PeerNode, respectively.
The main class name is ServerConnect and PeerConnect, respectively.

The server uses port 8500 to listen, and peer will connect server with port numbers from 9000 to 9004. The interaction between peers: Peer 1 listens to port 8701, connects to 8705. Peer 2 listens to port 8702, connects to 8701. Peer 3 listens to port 8703, connects to 8702. Peer 4 listens to port 8704, connects to 8703. Peer 5 listens to port 8705, connects to 8704.
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPDownload extends Thread {
	int server_port ;
	 Database d;
	
	 TCPDownload(int x,Database d) {
		  server_port= x;
		 this.d=d; 
		
		 this.start();
	 }
	 public void run () {
		try {
			ServerSocket listener = new ServerSocket(server_port);
			System.out.println("TCP Downloads Server running on port:"+server_port);
          while(true) {			
		    Socket s =listener.accept();
		    TCPDownloadHandler tcp=new TCPDownloadHandler(s,d);
		    System.out.println("got Download  request from "+s.getInetAddress());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
}

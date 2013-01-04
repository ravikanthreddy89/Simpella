import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;


public class TCPSimpella extends Thread {
Database d;
int server_port;
TCPSimpella(Database d,int server_port)
{
	this.d=d;
	this.server_port=server_port;
	this.start();
}
public  void run()
{
	try {
		ServerSocket listener = new ServerSocket(server_port);
		System.out.println("Simpella Server running on port:"+server_port);
      while(true) {			
	    Socket s =listener.accept();
	    BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
	    String str=br.readLine();
	    StringTokenizer st=new StringTokenizer(str);
	    if(st.hasMoreElements())
	    {
	     
	    if(st.nextElement().equals("SIMPELLA"))
	    {
	    	if(st.hasMoreElements()&&st.nextElement().equals("CONNECT/0.6"))
	    	{
	    	if(Database.no_incoming<3)
	    	{
	    		String welcome="Welcome!!!";
	    		String reply="SIMPELLA/0.6 200 "+welcome+"\r\n";
	    		DataOutputStream dos=new DataOutputStream(s.getOutputStream());
	    		dos.writeBytes(reply);
	    		System.out.println(welcome);
	    		
	    		SimpellaConnections sc=new SimpellaConnections();
				sc.s=s;
				sc.port=s.getPort();///////////////////////////////////////////////////////////////////////////
				
				sc.ip=s.getInetAddress();
	    		synchronized (d.SimpellaConnections_db) {
					d.SimpellaConnections_db.add(sc);
					
				}
	    		Database.no_incoming++;
	    		SimpellaSocketHandler tcp=new SimpellaSocketHandler(d,sc,true);
	    System.out.println("Got connection   request from "+s.getInetAddress());
	    	}
	    	else
	    	{
	    		String welcome="Maximum number of incoming connections reached. Sorry!";
	    		String reply="SIMPELLA/0.6 503 "+welcome+"\r\n";
	    		DataOutputStream dos=new DataOutputStream(s.getOutputStream());
	    		dos.writeBytes(reply);
	    		System.out.println(welcome);
	    		s.close();
	    	
	    	}
	    	}
	    	else
	    	{
	    		System.out.println("Invalid Simpella request!!!");
		    	s.close();
	    	}
	    }
	    else
	    {
	    	System.out.println("Invalid Simpella request!!!");
	    	s.close();
	    }
	    }
	    else
	    {
	    	System.out.println("Invalid Simpella request!!!");
	    	s.close();
	    }
      }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}

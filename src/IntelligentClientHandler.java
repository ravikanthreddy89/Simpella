import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class IntelligentClientHandler extends Thread {
Database d;
	public IntelligentClientHandler(Database d) {
		this.d=d;
		this.start();
		}
public void run()
{

	try
	{
		while(true)
	
{
	
	boolean flag=true;
	if(Database.no_outgoing<2)
{
	for(int i=d.HostInfo_db.size()-1; i >=0;i--)
	{
		
		
		for(int j=d.SimpellaConnections_db.size()-1;j>=0;j--)
		{
			

			if(d.SimpellaConnections_db.get(j).ip.equals(d.HostInfo_db.get(i).ip)&&d.SimpellaConnections_db.get(j).port==d.HostInfo_db.get(i).port_no)
			{
			flag=false;
			}
		}
			if(flag)
			{
				try
				{
							
					
					SimpellaConnections sc=new SimpellaConnections();
				sc.s=new Socket(d.HostInfo_db.get(i).ip,d.HostInfo_db.get(i).port_no);
				DataOutputStream dos=new DataOutputStream(sc.s.getOutputStream());
				String connect="SIMPELLA CONNECT/0.6\r\n";
				dos.writeBytes(connect);
				BufferedReader br=new BufferedReader(new InputStreamReader(sc.s.getInputStream()));
				String reply=br.readLine();
				StringTokenizer st1=new StringTokenizer(reply);
				String  s=st1.nextToken();
				
				
				 if(s.equals("SIMPELLA/0.6"))
				 {
					if(st1.hasMoreElements())
					{
					s=st1.nextToken();
					if(s.equals("200"))
					{
						s="";
						while(st1.hasMoreElements())
							s+=st1.nextToken();
						
						System.out.println(s);
						sc.ip=sc.s.getInetAddress();
						sc.port=sc.s.getPort();
						
						synchronized (d.SimpellaConnections_db) {
							d.SimpellaConnections_db.add(sc);
						}
						new SimpellaSocketHandler(d, sc,false);
						
						//update
						synchronized (d.HostInfo_db) {
							Database.total_no_of_hosts=1;
							Database.total_files_shared=Database.local_files_shared;
							Database.total_size_of_files_shared=Database.local_size_of_files_shared;
							d.HostInfo_db=new ArrayList<HostInfo>();
							Message m=new Message(d);
							m.PingCreator();
							byte[]ping=m.ping;
							SimpellaConnections sc1;
						for( i=0;i<d.SimpellaConnections_db.size();i++)//flooding ping
						{
							sc1=d.SimpellaConnections_db.get(i);
							Socket s1=d.SimpellaConnections_db.get(i).s;
							 dos=new DataOutputStream(s1.getOutputStream());
							dos.write(ping);
							d.packets_sent+=1;
							d.bytes_sent+=ping.length;
							sc1.packets_sent++;
							sc1.bytes_sent+=ping.length;
							//dos.close();
						}
						}
						
						//
						
						Database.no_outgoing++;
					break;
					}
					else
						if(s.equals("503"))
						{
							s="";
							while(st1.hasMoreElements())
								s+=st1.nextToken();
							
							System.out.println(s);
							sc.s.close();
						}
						else
						{
							System.out.println("Invalid Simpella reply");
							 sc.s.close();
						}
					}
					else
					{
						System.out.println("Invalid Simpella reply");
						 sc.s.close();
					}
				 }
				 else
				 {
					 System.out.println("Invalid Simpella reply");
					 sc.s.close();
				 }
			}
			
			catch(Exception e){
				
			}
		}
	}
}
try {
	Thread.sleep(5000);
} catch (InterruptedException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}	
}
	}
	catch(Exception e)
	{
		
	}
}
}

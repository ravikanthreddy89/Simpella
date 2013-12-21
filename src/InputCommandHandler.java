import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;


public class InputCommandHandler extends Thread 
{
Database d;
	public InputCommandHandler(Database d) {
		// TODO Auto-generated constructor stub
	this.d=d;
	this.start();
	}
	public void run(){
	BufferedReader inFromUser=new BufferedReader(new InputStreamReader(System.in));
	String cmd;
		while(true){
		System.out.print("\nSimpella>");
		try {
			cmd=inFromUser.readLine();
			cmd=cmd.trim();
			StringTokenizer st=new StringTokenizer(cmd);
			String s;
			if(st.hasMoreElements())
			{
				s=st.nextToken();
			
			// info command block
			//=======================================================================================================
			if(s.equals("info")){
				if(st.hasMoreElements()){
					s=st.nextToken();
					if(s.equals("c")){
						System.out.println("CONNECTION STATS:\n---------------");
						synchronized (d.SimpellaConnections_db) {
						for (int i=0;i<d.SimpellaConnections_db.size();i++){
							System.out.println(i+1+")"+d.SimpellaConnections_db.get(i).ip+":"+d.SimpellaConnections_db.get(i).port+"\t"+"Packs: "+d.SimpellaConnections_db.get(i).packets_sent+":"+d.SimpellaConnections_db.get(i).packets_rxd+"\t"+"Bytes: "+d.SimpellaConnections_db.get(i).bytes_sent+":"+d.SimpellaConnections_db.get(i).bytes_rxd);
						}
					}	
				}
				else if(s.equals("d")){
					synchronized(d.Downloads_db){
						Downloads download;
						Iterator i=d.Downloads_db.iterator();
						float total_size;
						float download_size;
						int count=1;
						System.out.println("DOWNLOAD STATS:\n---------------");
						while(i.hasNext())
						{
							String str_total;
							String str_download;
							download=(Downloads) i.next();
							total_size=download.total_size;
							download_size=download.downloaded_size;
							int m=1024*1024;
							int k=1024;
							if(total_size>m)
							{
								str_total=(total_size/m)+"M";
							}
							else if(total_size>k)
							{
								str_total=(total_size/k)+"K";
							}
							else
								str_total=(total_size)+"bytes";
							if(download_size>m)
							{
								str_download=(download_size/m)+"M";
							}
							else if(download_size>k)
							{
								str_download=(download_size/k)+"K";
							}
							else
								str_download=(download_size)+"bytes";	
							
							System.out.println(count+")"+download.ip+":"+download.port+"\t"+download.downloaded_percentage+"%\t"+str_download+"/"+str_total);
							count++;
						}
					}
				}
			else if(s.equals("h"))
			{
				long  size=Database.total_size_of_files_shared;
				String str_size;
				int m=1024;
				
				if(size>m)
				{
				str_size=size/m+"M";
				
				}
				else
					
					{
						str_size=size+"K";
					}
					
				System.out.println("HOST STATS:\n---------------");
				System.out.println("Hosts: "+d.total_no_of_hosts+"\tNum Shared: "+d.total_files_shared+"\t"+"Size Shared: "+str_size);	
			}
			else if(s.equals("n"))
			{
			    	    
			    
				System.out.println("Messages rxd: "+d.formater(d.packets_rcvd)+"\tMessages sent "+d.formater(d.packets_sent));
			    System.out.println("Unique GUIDs in memory : "+d.formater((d.RoutingTable_db.size()+d.MyUUID_db.size())));
			    System.out.println("Bytes rxd: "+d.formater(d.bytes_rcvd)+"\tBytes sent "+d.formater(d.bytes_sent));
			}
			else if(s.equals("q"))
			{
				
				System.out.println("QUERY STATS:\n---------------");
				System.out.println("Queries: "+d.queries_rcvd+"\t"+"Responses Sent: "+d.responses_sent);
				
			}
			else if(s.equals("s"))
			{
			long  size=d.local_size_of_files_shared;
				String str_size;
				int m=1024;
			
				if(size>m)
				{
				str_size=size/m+"M";
				
				}
				
					else
					{
						str_size=size+"K";
					}
				
				System.out.println("SHARE STATS:\n---------------");
				System.out.println("Num Shared: "+d.local_files_shared+"\t"+"Size Shared: "+str_size);
			}
			else
			{
				System.out.println("\nEnter a valid option : [cdhnqs]");	
			}
			
			}
			else
			{
				System.out.println("\nEnter a valid option : [cdhnqs]");
			}
		}


		//auto connect block
		//==============================================================================
		else if(s.equals("autoconnect")){
			IntelligentClientHandler tch=new IntelligentClientHandler(d);
		}
		
		
		//share command block
		//=================================================================================
		else if(s.equals("share"))
		{
			if(st.hasMoreElements())
			{
				s=st.nextToken();
				if(s.endsWith("-i"))
				{
				if(Database.file_directory==null)
				System.out.println("No  directory currently shared!!!");
				else
					System.out.println("sharing  "+Database.file_directory);
				}
				else
				{
					File f=new File(s);
					if(f.isDirectory())
					{
					Database.file_directory	=s;
					
					System.out.println("\nsharing "+f.getAbsolutePath());
					}
					else
					{
						System.out.println("\nEnter a valid Directory to be shared!!!");
					}
					}
				
				
			}
			else
				System.out.println("\nEnter a Directory name to be shared or -i option to share current directory!!!");
		}
		
		
		// scan command block
		//===================================================================================
		else if(s.equals("scan"))
		{
		     String path=Database.file_directory;
		    if(path!=null)
		    {
		     File f=new File(path);
		     int i=0;
		     long local_size=0;
		     if(f.isDirectory())
		     {
		    	
		    	 synchronized(d.SharedLibrary_db)
			    	{
		    		 d.SharedLibrary_db.clear();
		    System.out.println("Scanning "+f.getAbsolutePath()+" for files...");
		    		 for (File child:f.listFiles())
		    {
		    	SharedLibrary sl=new SharedLibrary();
		    	sl.file_index=i+1;
		    	sl.file_name=child.getName();
		    	
		    	
		    	
		    	sl.file_size=(int)child.length();
		    	local_size=local_size+sl.file_size;
		    	d.SharedLibrary_db.add(sl);
		    	i++;
		    	}
		    		
		    		 //info h
		    		Database.total_files_shared-=Database.local_files_shared;
		    		Database.total_size_of_files_shared-=Database.local_size_of_files_shared;
		    		
		    		 //
		    		 
		    		 
		    		 Database.local_files_shared=i;
		    		Database.local_size_of_files_shared=local_size/1024;;
		    		System.out.println("Scanned "+Database.local_files_shared+"files and "+local_size+" bytes ");
		    		 
		    //info h
		    		Database.total_files_shared+=Database.local_files_shared;
		    		Database.total_size_of_files_shared+=Database.local_size_of_files_shared;
		    		//
			    	}
		     }
		     else
		     {
		    	 System.out.println("No such Directory!!!");
		     }
		}
		else
		{
		System.out.println("Enter a valid directory to be shared First!!!");	
		}
		}
		
		
		
		//open command block
		//====================================================================================
		else if(s.equals("open"))
		{
			
			if(Database.no_outgoing<3)
			{
				
			if(st.hasMoreElements())
		{
			s=st.nextToken();
			StringTokenizer st1=new StringTokenizer(s,":");
			SimpellaConnections sc=new SimpellaConnections();
			try
			{
			sc.ip=InetAddress.getByName((String) st1.nextElement());
			if(st1.hasMoreElements())
		    {  
	        sc.port=Integer.parseInt((String) st1.nextElement());
			if(sc.port<1||sc.port>65535)
				throw new NumberFormatException();
			
			sc.s=new Socket(sc.ip,sc.port);
		DataOutputStream dos=new DataOutputStream(sc.s.getOutputStream());
		String connect="SIMPELLA CONNECT/0.6\r\n";
		dos.writeBytes(connect);
		BufferedReader br=new BufferedReader(new InputStreamReader(sc.s.getInputStream()));
		String reply=br.readLine();
		 st1=new StringTokenizer(reply);
		 s=st1.nextToken();
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
				synchronized (d.SimpellaConnections_db) {
					d.SimpellaConnections_db.add(sc);
				}
				new SimpellaSocketHandler(d, sc,false);
				Database.no_outgoing++;
			//sending ping to all
				{
					synchronized (d.HostInfo_db) {
						Database.total_no_of_hosts=1;
						Database.total_files_shared=Database.local_files_shared;
						Database.total_size_of_files_shared=Database.local_size_of_files_shared;
						d.HostInfo_db=new ArrayList<HostInfo>();
						Message m=new Message(d);
						m.PingCreator();
						byte[]ping=m.ping;
						SimpellaConnections sc1;
					for(int i=0;i<d.SimpellaConnections_db.size();i++)//flooding ping
					{
						sc1=d.SimpellaConnections_db.get(i);
						Socket s1=d.SimpellaConnections_db.get(i).s;
						 dos=new DataOutputStream(s1.getOutputStream());
						dos.write(ping);
						d.packets_sent+=1;
						d.bytes_sent+=ping.length;
						sc1.packets_sent++;
						sc1.bytes_sent+=ping.length;
				}
				
					}
				}///end of sending ping to all
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
			else
				System.out.println("Invalid syntax:open <hostname>:<port>");
			}
			catch(NumberFormatException n)
			{
				System.out.println("Enter a valid port number");
			}
			catch(UnknownHostException e)
			{
				System.out.println("Not reachable IP");
			}
		}

			else
				System.out.println("Enter Ip address and por number!!!");	
		}

			else
			{
				System.out.println("Reached maximum outgoing connections!!!");
			}
		}
		
		
		//update command block
		//=============================================================================
		else if(s.equals("update"))//update
		{
		synchronized (d.HostInfo_db) {
			Database.total_no_of_hosts=1;
			Database.total_files_shared=Database.local_files_shared;
			Database.total_size_of_files_shared=Database.local_size_of_files_shared;
			d.HostInfo_db=new ArrayList<HostInfo>();
			Message m=new Message(d);
			m.PingCreator();
			byte[]ping=m.ping;
			SimpellaConnections sc1;
		for(int i=0;i<d.SimpellaConnections_db.size();i++)//flooding ping
		{
			sc1=d.SimpellaConnections_db.get(i);
			Socket s1=d.SimpellaConnections_db.get(i).s;
			DataOutputStream dos=new DataOutputStream(s1.getOutputStream());
			dos.write(ping);
			d.packets_sent+=1;
			d.bytes_sent+=ping.length;
			sc1.packets_sent++;
			sc1.bytes_sent+=ping.length;
			//dos.close();
		}
		}
			
		}
		
	
		//find command block
		//==================================================================================
		else if(s.equals("find"))
		{
			
				  //clear the find result array list to store new result set
					d.FindResult_db.clear();
				
				  // creating query message
				     String q="";
				
				    while(st.hasMoreElements()){
				    	q+=" "+st.nextToken();
				    }
				    
				    Message m= new Message(d,q);
				    m.QueryCreator();
				    byte[] query=m.query;
				    //for (int p=0; p<16;p++) System.out.println(" "+p+")"+query[p]);
				    synchronized(d.SimpellaConnections_db) {
				     for(int i=0; i< d.SimpellaConnections_db.size(); i++){
				    	DataOutputStream dos=new DataOutputStream( d.SimpellaConnections_db.get(i).s.getOutputStream());
				    	dos.write(query);
				    	//dos.close();
				    	d.SimpellaConnections_db.get(i).bytes_sent+=query.length;
				    	d.SimpellaConnections_db.get(i).packets_sent++;
				    	d.packets_sent++;
				    	d.bytes_sent+=query.length;
				     }// end of for loop
				     
				    }// end of synchronized block, this block floods query
				      
				      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				      System.out.println("Searching Simpella network for "+"'"+q+"'");
				      System.out.println("press enter to continue");
				      br.readLine();
				      System.out.println("Please wait. Retrieving responses......");
				      Thread.sleep(3000);
				      System.out.println("---------------------------------------");
				     
				      System.out.println(d.FindResult_db.size()+" responses received");
				     
				      
				      for(int i=0 ; i<d.FindResult_db.size();i++){
				    	   System.out.println((i+1)+") "+d.FindResult_db.get(i).ip.toString()+":"+d.FindResult_db.get(i).port_no+"\tSize:"+d.formater(d.FindResult_db.get(i).file_size));
				    	   System.out.println("Name : "+d.FindResult_db.get(i).file_name);
				    	   
				      }
				      
					
		}
		
		// end of find command block
		
		
		// list command block
		//==================================================================
   		else if(s.equals("list"))
		{
			
				for(int i=0;i<d.List_db.size();i++){
					System.out.println((i+1)+") "+d.List_db.get(i).ip.toString()+":"+d.List_db.get(i).port_no+"\tSize:"+d.List_db.get(i).file_size);
			    	   System.out.println("Name : "+d.List_db.get(i).file_name);
				}
			
			
	
		}
	
   		
		// clear command block
		//========================================================================

   		else if(s.equals("clear"))
		{
			
				// check if any file number is provided
				
				if(st.hasMoreElements()){
					int f_no= Integer.parseInt( st.nextToken());
					f_no--;
					// check if file number is beyond the size of list_db
					
					if(f_no >=d.List_db.size()){
						 System.out.println("File number is out of range");
						 
					}
					else{
						d.List_db.remove(f_no);
					}
				}
		
				// if no file number is provided clear all the files
				else{
					d.List_db.clear();
				}
	
		}


		// download command block
		//===================================================
   		
   		else if(s.equals("download"))
		{
	
   			try
   			{
			int file_num=Integer.parseInt(st.nextToken());
			if(file_num<=d.FindResult_db.size())
			{
							
				Downloads download=new Downloads();
				int file_index=d.FindResult_db.get(file_num-1).file_index;
				int port=d.FindResult_db.get(file_num-1).port_no;
				String file_name=d.FindResult_db.get(file_num-1).file_name;
				Socket downloadclient=new Socket(d.FindResult_db.get(file_num-1).ip, port);			
				DataOutputStream dos=new DataOutputStream(downloadclient.getOutputStream());
				download.ip=d.FindResult_db.get(file_num-1).ip;
				download.port=port;
				download.total_size=d.FindResult_db.get(file_num-1).file_size;
				String request="GET /get/"+file_index+"/"+file_name+" HTTP/1.1\r\nUser-Agent: Simpella\r\nHost: "+d.FindResult_db.get(file_num-1).ip.getHostAddress()+":"+port+"\r\nConnection: Keep-Alive\r\nRange: bytes=0-\r\n\r\n";
				dos.writeBytes(request);
				String reply="";
				String c;
				BufferedReader br=new BufferedReader(new InputStreamReader(downloadclient.getInputStream()));
			
			while (!(c= br.readLine()).isEmpty()) {
				reply= reply + "\r\n" + c;
				
			}
			reply+="\r\n\r\n";
			if (reply.endsWith("\r\n\r\n"))
				;
			else
				throw new InvalidHttpException();
			
			
			StringTokenizer str = new StringTokenizer(reply, "\r\n");
			if(str.hasMoreElements())
			{
				StringTokenizer str1 = new StringTokenizer(str.nextToken());
			
				
					if (str1.hasMoreElements()) {
						s = str1.nextToken();
						if (s.equals("HTTP/1.1")) {
							
							if (str1.hasMoreElements())
							{
								s=str1.nextToken();
								if(s.equals("200"))
								{
									d.Downloads_db.add(download);
									download.downloaded_size=0;
									download.downloaded_percentage=0;
									//download.total_size=file_size;
									
									DataInputStream dis=new DataInputStream(downloadclient.getInputStream());
									int r=0;
									File f=new File(file_name);
									if(!f.exists())
										f.createNewFile();
									FileOutputStream fos=new FileOutputStream(f);
									System.out.println("Download started!!!");
									new DownloadThread(br,fos,download,d);
								}
								else
									if(s.equals("503"))
									{
										System.out.println("File not Found!!!");
										downloadclient.close();
									}
							}
						} else
							throw new InvalidHttpException();
			}
				
			}
			else
				throw new InvalidHttpException();
			
			
			}
			else
			{
				System.out.println("No such file index!!!");
			}

}
catch(InvalidHttpException e)
{
	System.out.println("Invalid Http response");
}
catch(Exception e)
{
	e.printStackTrace();
}
			//////////
		}
		
		
   		else if(s.equals("monitor"))
   		{
   			Database.monitor=true;
   			System.out.println("\nMONITORING SIMPELLA NETWORK:");
   		System.out.println("Press enter to continue");
   		inFromUser.readLine();
   			System.out.println("-----------------------------------------------");
   		inFromUser.readLine();
   		Database.monitor=false;
   		}
		
   		else if(s.equals("quit"))
   		{
   			System.out.println("Bye Bye!!!");
   			for(int i=0;i<d.SimpellaConnections_db.size();i++)
   			{
   				d.SimpellaConnections_db.get(i).s.close();
   			}
   		System.exit(0);
   		}
   		else
   		{
   			System.out.println("Invalid Simpella command!!!");
   		}
		
			}
		
		}
		
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			}
	
	}
}

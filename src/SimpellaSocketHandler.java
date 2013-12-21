import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class SimpellaSocketHandler extends Thread
{
Database d;
SimpellaConnections sc;
boolean incoming;
public SimpellaSocketHandler(Database d,SimpellaConnections sc,boolean incoming) {
this.d=d;
this.sc=sc;
this.incoming=incoming;
this.start();
}
public void run()
{
Socket s=sc.s;
try
{
	
	
	DataInputStream dis=new DataInputStream(s.getInputStream());
	byte b[]=new byte[4118];
	byte UID[]=new byte[16];
	while(true)
	{
		int anushka=0;
		anushka=dis.read(b);
		if(anushka==-1) throw new SocketException();
		for (int i=0;i<16;i++)
		{
			UID[i]=b[i];
			
		}
		
		//ping message handler block
		//===========================================================================
		
		if(b[16]==0)//PING
		{
	       boolean found=false;
	
	       //checking My ID's
	       for(int i=0;i<d.MyUUID_db.size();i++)
	       {
	    	   if(Arrays.equals(UID, d.MyUUID_db.get(i).GUID))
	    	   {
	    		  found=true;
	    	   }
	       }
	       
	       if(found)
	       continue;  //drop the packet and continue reading from socket
	       found=false;
	      // System.out.println("Ping received!!!");
	       
		for (int i=0;i<d.RoutingTable_db.size();i++)
		{
			if(Arrays.equals(UID,d.RoutingTable_db.get(i).GUID ))
					{
				found=true;
				break;
					}
		}
		
		
		if(found)
		{
			//System.out.println("entered if found");
			d.packets_rcvd+=1;
			d.bytes_rcvd+=23;
			sc.packets_rxd+=1;
			sc.bytes_rxd+=23;
		}
		else
		{
//			System.out.println("entered else block of if found");
			d.packets_rcvd+=1;
			d.bytes_rcvd+=23;
			sc.packets_rxd+=1;
			sc.bytes_rxd+=23;
			RoutingTable rt=new RoutingTable();
			
			System.arraycopy(b, 0, rt.GUID,0,16);
		rt.s=s;
			synchronized (d.RoutingTable_db) {
			if(d.RoutingTable_db.size()<d.ROUTING_SIZE)
			d.RoutingTable_db.add(rt);
			else
			{
				d.RoutingTable_db.add(d.routing_position,rt);
				d.routing_position++;
				
			}
			
		}
		
		Message m=new Message(d);
		m.PongCreator(b);
		byte pong[]=m.pong;
		if(b[17]+b[18]>15||b[17]<=0)
		{
			   
				Socket sock=s;
				DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
				dos.write(pong);
				//dos.close();
				d.packets_sent+=1;
				d.bytes_sent+=37;
				sc.packets_sent+=1;
				sc.bytes_sent+=37;
				//System.out.println("pong written 1");
		}
		else
		{
			
			if(b[17]+b[18]>7)
				b[17]=(byte)(7-b[18]);
			
			//debug statement
			//System.out.println("pong written 2");
		
			b[17]--;
			b[18]++;
			
			Socket sock=s;
			DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
			//debug statement
			//System.out.println("pong written  3");
			dos.write(pong);
			//debug statement
			//System.out.println("pong written 4");
			//dos.close();
			d.packets_sent+=1;
			d.bytes_sent+=37;
			sc.packets_sent+=1;
			sc.bytes_sent+=37;
			SimpellaConnections sc1;
			for(int i=0;i<d.SimpellaConnections_db.size();i++)
		{
			  // System.out.println("entered forwarding block");
				sc1=d.SimpellaConnections_db.get(i);
				sock=d.SimpellaConnections_db.get(i).s;
			if(!s.equals(sock))
			{
				//debug statement
				//System.out.println("pong written 5");
				dos=new DataOutputStream(sock.getOutputStream());
			
			
			dos.write(b,0, 37);
			
			//dos.close();
			d.packets_sent+=1;
			d.bytes_sent+=23;
			sc1.packets_sent+=1;
			sc1.bytes_sent+=23;
		}
		}
		}
		
		
		}
		}
		
		// pong message handler block
		//=================================================================================
		else if(b[16]==0x01)//PONG
		{
			int port=0;
			
			if(b[24]<0)
				 port=256*b[23]+b[24]+256;
			else
				port=256*b[23]+b[24];
			//String str=b[25]+"."+b[26]+"."+b[27]+"."+b[28];
			byte [] str = new byte[4];
			System.arraycopy(b, 25, str, 0, 4);
			InetAddress ip=InetAddress.getByAddress(str);
			
			d.packets_rcvd+=1;
			d.bytes_rcvd+=37;
			sc.packets_rxd+=1;
			sc.bytes_rxd+=37;
			boolean present=false;
			
		       System.out.println("Pong received!!!");

			for(int i=0;i<d.MyUUID_db.size();i++)
		       {
		    	   if(Arrays.equals(UID, d.MyUUID_db.get(i).GUID))
		    	   {
		    		  present=true;
		    	
		    	   }
		       }
			if(present)//my own pong
			{
			
				
				HostInfo hi=new HostInfo();
				hi.ip=ip;
				hi.port_no=port;
				int k29,k30,k31,k32,k33,k34,k35,k36;
				if(b[29]<0)
					 k29=b[29]+256;
				else
					k29=b[29];
				
				if(b[30]<0)
					 k30=b[30]+256;
				else
					k30=b[30];
				if(b[30]<0)
					 k30=b[30]+256;
				else
					k30=b[30];
				if(b[31]<0)
					 k31=b[31]+256;
				else
					k31=b[31];
				if(b[32]<0)
					 k32=b[32]+256;
				else
					k32=b[32];
				if(b[33]<0)
					 k33=b[33]+256;
				else
					k33=b[33];
				if(b[34]<0)
					 k34=b[34]+256;
				else
					k34=b[34];
				if(b[35]<0)
					 k35=b[35]+256;
				else
					k35=b[35];
				if(b[36]<0)
					 k36=b[36]+256;
				else
					k36=b[36];
				
				hi.files_shared=256*256*256*k29+256*256*k30+256*k31+k32;
				hi.shared_size=256*256*256*k33+256*256*k34+256*k35+k36;
				//addeded by ravikanth for updating info h bug
				d.total_files_shared+=hi.files_shared;
				d.total_size_of_files_shared+=hi.shared_size;
				d.total_no_of_hosts++;
				synchronized (d.HostInfo_db) {
				d.HostInfo_db.add(hi);
			}
			
				continue;
		}
			
			
			
			
			boolean found=false;
			int position=0;
		for (int i=0;i<d.RoutingTable_db.size();i++)
		{
			if(Arrays.equals(UID,d.RoutingTable_db.get(i).GUID ))
					{
				found=true;position=i;
				break;
					}
		}
		if(found)
		{
			
			if(b[17]+b[18]>15||b[17]<=0)
			{
				//dropping pong
			}
			else
			{
				if(b[17]+b[18]>7)
					b[17]=(byte)(7-b[18]);
				b[17]--;
				b[18]++;
				
					Socket sock=d.RoutingTable_db.get(position).s;
					SimpellaConnections sc1=null;
					for(int i=0;i<d.SimpellaConnections_db.size();i++)
					{
						if(d.SimpellaConnections_db.get(i).s.equals(sock))
						{
							sc1=d.SimpellaConnections_db.get(i);
						}
					}
					
					DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
					dos.write(b);
				//	dos.close();
					d.packets_sent+=1;
					d.bytes_sent+=37;
					sc1.bytes_sent+=37;
					sc1.packets_sent+=1;
				
			}
		}
		
		
		
		// query message handler
		//============================================================================
		
		}
		else if(b[16]==-128)//QUERY
		{
		
			int length=0;
		    
		    
			byte temp[]=new byte[4];
			System.arraycopy(b, 19, temp,0,4);
			
			String str=String.valueOf(ByteBuffer.wrap(temp).getInt());
			// note : query length contains  payload length not length of query
			
			int query_length=Integer.parseInt(str);
			d.packets_rcvd+=1;
			d.bytes_rcvd=d.bytes_rcvd+23+query_length;
			d.queries_rcvd++;
			sc.packets_rxd+=1;
			sc.bytes_rxd=sc.bytes_rxd+23+query_length;
			
            		boolean present=false;
			
			
			for(int i=0;i<d.MyUUID_db.size();i++)
		       {
		    	   if(Arrays.equals(UID, d.MyUUID_db.get(i).GUID))
		    	   {
		    		  present=true;
		    	
		    	   }
		       }
			
			if(present)
				continue;
			
		       System.out.println("Query received!!!");

		//monitor
			
			int i=25;
			while(b[i]!=0)
			{
				i++;
			}
			
			byte h[]=new byte[i-25+1];
			System.arraycopy(b,25, h, 0, i-25+1);
			String sq=new String(h);
		
			
			//monitor	
			// debug statement
			//System.out.println("creating query hit");
			int payload_length=0;
			Message m=new Message(d);
			m.QueryHitCreator(b);
			byte[]query_hit=m.query_hit;
			
			
			if(b[17]+b[18]>15||b[17]<=0)//dropping query and sending queryhit only
			{
				//send queryhit only if there is a hit in your shared library
				// above condition is not handled and query hit is forwarded irrespective of hits
				if(m.hits.size()>0){
					
					DataOutputStream dos=new DataOutputStream(s.getOutputStream());
					
					/*System.arraycopy(query_hit,19, temp,0, 4);		
					str=String.valueOf(ByteBuffer.wrap(temp).getInt());
					 payload_length=Integer.parseInt(str);
						*/
					//no need to do the above operation becoz u already calculated payload length above and store in query_length
							dos.write(query_hit);
							d.packets_sent+=1;
							d.bytes_sent=d.bytes_sent+23+query_length;
							d.responses_sent++;
							
				}
			continue;		
			}
			
			//before flooding you should iterate thru routing table to see if it was already seen 
			// if it is not there in routing table it is a new one and an entry should be added in routing table
			boolean brand_new = true;
			 for( i=0;i<d.RoutingTable_db.size();i++){
				  if(Arrays.equals(UID, d.RoutingTable_db.get(i).GUID)){
					  brand_new= false;
					  
				  }
			 }
			// if it is a new query  update routing table
		    if(brand_new){
		    	if(Database.monitor)
					System.out.println("Search: "+"'"+sq+"'");
		    	RoutingTable new_entry= new RoutingTable();
		    	System.arraycopy(UID,0, new_entry.GUID,0,16);
		    	new_entry.s= s;
		    	d.RoutingTable_db.add(new_entry);
		    	
		    }
		    
		    // if it is a new query flood it after updating routing table 
			if(brand_new)//query flooding
			{
				if(b[17]+b[18]>7)
					b[17]=(byte)(7-b[18]);
				b[17]--;
				b[18]++;
				
					for (int j=0;j<d.SimpellaConnections_db.size();j++)
					{
						
						SimpellaConnections sc1=d.SimpellaConnections_db.get(j);
						Socket sock=d.SimpellaConnections_db.get(j).s;
						//System.out.println("flooding to same socket"+s.equals(sock));
						if(!s.equals(sock))
						{
						
						//debug statement
						//System.out.println("flooding the query for second time");
						DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
						
						dos.write(b);
						//dos.close();
						d.packets_sent+=1;
						d.bytes_sent+=23+query_length;
						sc1.bytes_sent+=23+query_length;
						// bug 1
						sc1.packets_sent+=1;
					}
					}
				
					
			if(m.hits.size()>0)
			{
					DataOutputStream dos=new DataOutputStream(s.getOutputStream());//sending queryhit 
					        dos.write(query_hit);
						d.packets_sent+=1;
						d.bytes_sent=d.bytes_sent+23+payload_length;
						d.responses_sent++;
						sc.bytes_sent=d.bytes_sent+23+payload_length;
						sc.packets_sent+=1;
			}
			}
			
		}
		 
		
		// query hit handler block
		//===================================================================================
		else if(b[16]==-127)//QUERYHIT
		{
		    System.out.println("QueryHit received!!!");
 
		  //updating rcvd packets , these are global values
		    byte temp[]=new byte[4];
			System.arraycopy(b, 19, temp,0,4);
			
			String str=String.valueOf(ByteBuffer.wrap(temp).getInt());
			int payload_length=Integer.parseInt(str);
			d.packets_rcvd+=1;
			d.bytes_rcvd=d.bytes_rcvd+23+payload_length;
            
			sc.bytes_rxd=d.bytes_rcvd+23+payload_length;
			sc.packets_rxd+=1;
 			boolean to_me=false; // variable to check if message is destined to me
       		        boolean cool= true;
		    
			for(int i=0; i<d.MyUUID_db.size();i++){
				
				if(Arrays.equals(UID, d.MyUUID_db.get(i).GUID))
				{
					to_me= true;
					break;
				}
		    }// end of iteration
		    
			if(!cool){
				to_me = true;
				break;
			}
		// if message is destined to me
		    if(to_me){
		       
				int hits= b[23];
				
				// retrieving port number
				int byte1;
				int byte2;
				byte1= b[24];
				byte2=b[25];
				if(byte1<0 ) byte1= byte1+255+1;
				if(byte2<0) byte2= byte2+255+1;
				
				
				// retreiving ip address
				InetAddress ip;
				System.arraycopy(b, 26, temp, 0, 4);
				ip=InetAddress.getByAddress(temp);
				
				// retreiving supported speed value
				int speed;
				System.arraycopy(b, 30, temp , 0, 4);
				speed=Integer.parseInt(String.valueOf(ByteBuffer.wrap(temp).getInt()));
				
				
			    int	l=23+1+2+4+4;
			    
			    // iteration thru result set
				for (int i=0; i<hits; i++){
                    
					 if(d.List_db.size() > 160 ){
					    d.List_db.remove(0);
					 }// if block that handles list size >160
						 
					 d.FindResult_db.add(new FindResult());
					 d.List_db.add(new List());
							
					int j=d.FindResult_db.size();
					int k=d.List_db.size();
							
							
					// updating ip address					
					d.FindResult_db.get(j-1).ip= ip;
					d.List_db.get(k-1).ip=ip;
						
					//updating port number for downloading the file not simpella port number
					d.FindResult_db.get(j-1).port_no= byte2 + (byte1*256);
					d.List_db.get(k-1).port_no=byte2 + (byte1*256);	
							
							
					// updating file index
					System.arraycopy(b, l, temp, 0, 4);
					d.FindResult_db.get(j-1).file_index= Integer.parseInt(String.valueOf(ByteBuffer.wrap(temp).getInt()));
					d.List_db.get(k-1).file_index=Integer.parseInt(String.valueOf(ByteBuffer.wrap(temp).getInt()));
					l+=4;
		                        
					//updating file size
					System.arraycopy(b, l, temp, 0, 4);
					d.FindResult_db.get(j-1).file_size= Integer.parseInt(String.valueOf(ByteBuffer.wrap(temp).getInt()));
					d.List_db.get(k-1).file_size=Integer.parseInt(String.valueOf(ByteBuffer.wrap(temp).getInt()));
					l+=4;
								
					int count=0;
					while(b[l+count]!=0) count++;
								
					byte[] name= new byte[count];
					System.arraycopy(b, l, name, 0, count);
								
					d.FindResult_db.get(j-1).file_name=(new String(name));
					d.List_db.get(k-1).file_name=(new String(name));
								
					l+=count+1;	
					 
												
					}// end of for loop which updates file names and sizes and indices
						

		    }// end of if block that handles messages destined to me
		    
		    else {
		   	// iterate thru routing table to forward it back to the querier
		   	for (int i=0; i< d.RoutingTable_db.size(); i++){
		  		// if guid is in the routing table forward it back to the socket from where it came
		    		if(Arrays.equals(UID, d.RoutingTable_db.get(i).GUID)){
		    			
		    			DataOutputStream dos= new DataOutputStream(d.RoutingTable_db.get(i).s.getOutputStream());
		    			dos.write(b);
		    		}
		    }
		    }
		
		}// end of query hit block 
	}
}
catch(SocketException se)
{
	
synchronized (d.SimpellaConnections_db) {
	d.SimpellaConnections_db.remove(sc);
if(incoming)
	Database.no_incoming--;
else
	Database.no_outgoing--;
}
	
	System.out.println("Socket closed !!!");
}
catch(IOException io)
{
	io.printStackTrace();
}
catch(Exception e)
{
	e.printStackTrace();
}
}
}

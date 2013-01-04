                                                                     
                                                                     
                                                                     
                                             
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.UUID;

// class to store the result set of query hit search
class result_set {	
	int index;
	int size;
	String name;
}

class Message {
	// various fields used in message header
	byte[] message_id= new byte[16];
	byte message_type;
	byte ttl=(byte)7;
	byte hops=(byte)0;
	byte[] payload_length = new byte[4];

	// different headers and messages
	byte[] header = new byte[23];
	byte[] ping= new byte[23];
	byte[] pong= new byte[23+14];// 23 headerlength + 14 payload length
	byte[] query;
	byte[] query_hit;
	Database db;
	String query_string;
	String query_string2;
	int query_length;
	
	// arraylist to store query hit result set
	ArrayList<result_set> hits= new  ArrayList<result_set> ();
  // constructor for ping and pong messages
	Message(Database d){
		db= d;
	}
	

	
	// constructor for query creator
	Message(Database d, String query_string){
		db=d;
		
		query_string2= query_string+"\0";
		query_length=query_string2.getBytes().length;
		query= new byte[23+2+query_length];	
	     }
	
	
	
	
	void PingCreator() {
		// message id generation
				UUID guid = UUID.randomUUID();
				long  most_sig_bits=guid.getMostSignificantBits();
				long least_sig_bits=guid.getLeastSignificantBits();
				byte[] msb= ByteBuffer.allocate(8).putLong(most_sig_bits).array();
				byte[] lsb= ByteBuffer.allocate(8).putLong(least_sig_bits).array();
			 	System.arraycopy(msb, 0, message_id , 0, 8);
			 	System.arraycopy(lsb, 0, message_id, 8, 8);
			 	message_id[8]=(byte) 0xFF;
			 	message_id[15]=(byte) 0x00;
			 	System.arraycopy(message_id, 0, header, 0, 16);
			 	// end of message id generation block
	
			 	MY_UUID m=new MY_UUID();
				System.arraycopy(message_id, 0, m.GUID, 0 , 16);
				db.MyUUID_db.add(m);
			
			 	// message type
				message_type=0;   
				header[16]= message_type;
				
				// ttl
				header[17]=ttl;
				
				//hops
				header[18]=hops;
				
				// payload length=0 for ping message  
			 	payload_length= ByteBuffer.allocate(4).putInt(0).array();
			 	System.arraycopy(payload_length, 0, header, 19, 4);
			 	
			 	System.arraycopy(header, 0 , ping,0,23);
	}// ping creator block
	
	
	void PongCreator(byte [] guid) {
		
		//dumping ping message id into  Message Id
		System.arraycopy(guid , 0, header, 0, 16);
		 
		//Message type
		message_type=1;
		header[16]=message_type;
		
		// ttl
		header[17]= ttl;
		
		//hops
		header[18]=hops;
		
		// payload length
		payload_length =  ByteBuffer.allocate(4).putInt(14).array();// payload length
		System.arraycopy(payload_length, 0, header, 19, 4);
		
		// payload
		byte [] payload = new byte[14];
		
		byte[] portNo= new byte[2];
		portNo[0]= (byte)((Database.simpella_port>>8)&255);
		portNo[1]=(byte)((Database.simpella_port)&255);

		System.arraycopy(portNo, 0, payload, 0, 2);
		
		// copying ip address of localhost into Ip address field of payload
		System.arraycopy((Database.local_host).getAddress(), 0, payload, 2, 4);
		
		
		
		
	   if(Database.file_directory!=null){
		   File f= new File(Database.file_directory);
			
			
			
			// copying number of files shared by local host into no of files field of payload
			
			byte [] temp = new byte[4];
		    temp =  ByteBuffer.allocate(4).putInt(f.list().length).array();
		    
		    System.arraycopy(temp, 0, payload, 6, 4);
		    
		    long file_size=0;
		    for(int i=0; i<f.list().length ; i++ ) file_size=((f.listFiles())[i].length())+file_size;
		    int file_size_in_kb = (int)(file_size/1024);
		    
		    temp=ByteBuffer.allocate(4).putInt(file_size_in_kb).array();
		    
		    System.arraycopy(temp, 0, payload, 10, 4);
		    
		    
		    
	   }// end of for loop
	   else {
		    byte [] zero = new byte[8];
		    		for(int i=0; i<8 ; i++){
		    			zero[i]=0;
		    		}
		     System.arraycopy(zero, 0, payload, 6, 8);
		     
	   }// end of else loop
	   
	   System.arraycopy(header, 0, pong, 0, 23);
	   System.arraycopy(payload, 0, pong, 23, 14);
	 
		    
	}// pong creator block
	
	
	void QueryCreator(){
		
		UUID guid = UUID.randomUUID();
		long most_sig_bits= guid.getMostSignificantBits();
		long least_sig_bits= guid.getLeastSignificantBits();
		
		byte[] msb= ByteBuffer.allocate(8).putLong(most_sig_bits).array();
		byte[] lsb= ByteBuffer.allocate(8).putLong(least_sig_bits).array();
		
		System.arraycopy(msb, 0, message_id , 0, 8);
		System.arraycopy(lsb, 0, message_id, 8, 8);
		System.arraycopy(message_id, 0, header, 0, 16);
		// end of message id generation block
		
		MY_UUID m=new MY_UUID();
		System.arraycopy(message_id, 0, m.GUID, 0 , 16);
		db.MyUUID_db.add(m);
		
		//message type
		message_type=(byte) 0x80;
		header[16]= message_type;
		 
		// ttl
		header[17]= ttl;
	    
		//hops
		header[18]=hops;
		
		// filling payload length bytes of header
		payload_length=ByteBuffer.allocate(4).putInt(query_length+2).array();
		System.arraycopy(payload_length, 0, header, 19, 4);
		
		// dumping header into query message
		System.arraycopy(header,0,query,0,23);
		
		query[23]=(byte)0;
		query[24]=(byte)0;
		
		// dumping query string into payload of query message
		System.arraycopy(query_string2.getBytes(), 0, query, 25, query_length);
		
	}// query creator block
	
	
	// query hit creator block
	//==================================================================================
	void QueryHitCreator(byte[] q) {
		
				//message id
				System.arraycopy(q,0 , header, 0 ,16);
				
				//message type
				message_type=(byte) 0x81;
				header[16]= message_type;
			    
				// ttl
				header[17]=ttl;
				
				
				//hops
				header[18]=hops;		
		
		       //calculating query string length
				byte [] t= new byte[4];
				System.arraycopy(q,19, t, 0, 4);
				
		  
		
		byte[] b= new byte[Integer.parseInt(String.valueOf(ByteBuffer.wrap(t).getInt()))-2];
		System.arraycopy(q, 25, b, 0, (Integer.parseInt(String.valueOf(ByteBuffer.wrap(t).getInt())))-2);
		
		String s=new String(b);
		String search_text =s.substring(0, s.length()-1);
		StringTokenizer st= new StringTokenizer(search_text);
		int x=0;
		while(st.hasMoreElements()){
			String temp=st.nextToken();
			
			
			//=================================
			for(int i=0; i<db.SharedLibrary_db.size(); i++ ) {
				 
				
			//	StringTokenizer l= new StringTokenizer(db.SharedLibrary_db.get(i).file_name.substring(0,db.SharedLibrary_db.get(i).file_name.indexOf(".") ));
				
			//	while(l.hasMoreElements()){
				//	 } // end of if loop
				if(db.SharedLibrary_db.get(i).file_name.contains(temp)){
					 hits.add(new result_set());
					 int k= hits.size()-1;
					 hits.get(k).index= db.SharedLibrary_db.get(i).file_index;
					 hits.get(k).size= db.SharedLibrary_db.get(i).file_size;
					 
					 // concatinating null character at the end of file name;
					 hits.get(k).name= db.SharedLibrary_db.get(i).file_name+"\0";
					 x+=(8+hits.get(k).name.getBytes().length);
					
				}// end of while loop
				
				
						
					 
			}// end of for loop
			
		}// end of while loop
		
		
		
		
		
		
		//payload length
		
		byte[] temp= new byte[4];
		temp=ByteBuffer.allocate(4).putInt(1+2+4+4+x+16).array();
		
		System.arraycopy(temp, 0, header, 19, 4);
		
		
		query_hit= new byte[23+1+2+4+4+x+16];

		// copying header
		System.arraycopy(header, 0, query_hit, 0, 23);

		
		// copying number of hits into number of hits field of queryhit
		query_hit[23]=(byte)hits.size();
		
		
		
		
		// copying port number into query hit msg
		byte[] port_no= new byte[2];
		port_no[0]= (byte)((Database.download_port>>8)&255);
		port_no[1]=(byte)((Database.download_port)&255);
					
		System.arraycopy(port_no, 0, query_hit, 23+1, 2);
					
					
		// dumping ip address
		System.arraycopy(Database.local_host.getAddress(),0, query_hit, 23+1+2, 4);
					
								
		//dumping speed value; speed value is in mbps
		temp=ByteBuffer.allocate(4).putInt(10).array();
		System.arraycopy(temp,0,query_hit, 23+1+2+4,4);
					
		
		int res=0;
		res+= 23+1+2+4+4;
		
		for(int i=0; i<hits.size(); i++){
				
			// dumping file index
			
			temp=ByteBuffer.allocate(4).putInt(hits.get(i).index).array();
			System.arraycopy(temp, 0, query_hit, res, 4);
			
			
			// dumping file size
			res+=4;
			temp=ByteBuffer.allocate(4).putInt(hits.get(i).size).array();
			System.arraycopy(temp, 0, query_hit, res, 4);
			
			// dumping file name
			res+=4;
			System.arraycopy(hits.get(i).name.getBytes(), 0	, query_hit, res, hits.get(i).name.getBytes().length);
			
			res+=hits.get(i).name.getBytes().length;
		}// end for loop for dumping result set into query hit
		
			// dumping servent id
			long most_sig_bits= Database.servent_id.getMostSignificantBits();
			long least_sig_bits= Database.servent_id.getLeastSignificantBits();
			byte[] msb= ByteBuffer.allocate(8).putLong(most_sig_bits).array();
			byte[] lsb= ByteBuffer.allocate(8).putLong(least_sig_bits).array();
			
			
			System.arraycopy(msb, 0,query_hit , res, 8);
			System.arraycopy(lsb, 0, query_hit,res+8 , 8);
				 
		
		
		
	}// query hit creator block

}// message class block

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;
//routing table
class RoutingTable {
	//InetAddress ip;
	byte[] GUID= new byte[16];
	Socket s;
}

// database for downloads

class Downloads{
	InetAddress ip;
	float downloaded_percentage;
	int downloaded_size;
	float total_size;
	int port;
			
}


//database for simpella connections
class SimpellaConnections {
	public SimpellaConnections() {
		packets_sent=0;
		packets_rxd=0;
		bytes_rxd=0;
		bytes_sent=0;
	}
	InetAddress ip;
	int port;
	int packets_sent;
	int packets_rxd;
	int bytes_sent;
	int bytes_rxd;
	Socket s;
}

//database for find results
class FindResult{
	InetAddress ip;
	String file_name;
	int file_size;
	int port_no;
	int file_index;
	
}

// database for list
class List {
	InetAddress ip;
	String file_name;
	int file_size;
	int port_no;
	int file_index;
	
}

class SharedLibrary
{
	int file_index;
	int file_size;
	String file_name;
	//String file_path;
}
class HostInfo
{
	InetAddress ip;
	int port_no;
	int files_shared;
	int shared_size;
}
class MY_UUID
{
	byte[] GUID= new byte[16];
}

public class Database {
	final int ROUTING_SIZE=160;
	final int  LIST_SIZE=1000;
	
	static boolean monitor=false;
	static UUID servent_id= UUID.randomUUID();
static InetAddress local_host;
static int simpella_port;
static int download_port;
	static int total_no_of_hosts=1;
static 	int total_files_shared=0;
static long total_size_of_files_shared=0;
	static long local_size_of_files_shared=0;
	static int local_files_shared=0;
	static int no_incoming=0 ;
	static int no_outgoing=0;
	int packets_rcvd=0;
	int packets_sent=0;
	int bytes_sent=0;
	int bytes_rcvd=0;
	int queries_rcvd=0;
	int responses_sent=0;
	static String file_directory;
	ArrayList<MY_UUID>MyUUID_db=new ArrayList<MY_UUID>( );
	ArrayList <FindResult> FindResult_db= new ArrayList<FindResult>();
	ArrayList <List> List_db = new ArrayList<List>(LIST_SIZE);
	ArrayList<HostInfo> HostInfo_db=new ArrayList<HostInfo>();
	LinkedList<SimpellaConnections> SimpellaConnections_db= new LinkedList<SimpellaConnections>();
	LinkedList<Downloads> Downloads_db = new LinkedList<Downloads>();
	ArrayList<RoutingTable> RoutingTable_db = new ArrayList<RoutingTable>(ROUTING_SIZE);
	static int routing_position=0;
	ArrayList<SharedLibrary> SharedLibrary_db=new ArrayList<SharedLibrary>();
	
	Object formater(int x){
		 float temp;
		 String s=null;
		if(x<1000){
			   s=x+"";
		   }
		else if(x>=1000&& x<1000000){
			   temp=x/1000;
			   s= temp+"k";
		   }
		else if (x>1000000 && x <1000000000){
			temp=x/1000000;
			s=temp+"M";
		}
		   		
		else if(x>1000000000){
			temp= x/1000000000;
			s=temp+"G";
		}
		  return s;
	}
	
}

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Simpella {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

    //IP i=new IP();
		Database d=new Database();
		if(args.length==0){
			d.simpella_port=6346;
			d.download_port=5635;
		}
		else if(args.length==1){
			d.simpella_port=Integer.parseInt(args[0]);
			d.download_port=5635;
		}
		else {
		d.download_port=Integer.parseInt(args[1]);
		d.simpella_port=Integer.parseInt(args[0]);
		}
		
//       try {
	//		d.local_host=InetAddress.getByName("192.168.0.13");
			//try {
				//d.local_host=i.getGlobalIPWeb();
				try {
					//d.local_host=i.getGlobalIPWeb();
					//d.local_host=InetAddress.getByName("timberlake.cse.buffalo.edu");
					d.local_host=PavanKalyan.ip_getter();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			//}
		//} 
//catch (UnknownHostException e) {
//		
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		 System.out.println("Simpella Version 0.6 (c) ");
	TCPSimpella t=new TCPSimpella(d, d.simpella_port);
	
	//server thread that handles download requests
	  TCPDownload td= new TCPDownload(d.download_port, d);
	  System.out.println("Local Ip:"+d.local_host.toString());
	  System.out.println("Enter quit to exit");
	 
	  InputCommandHandler ich=new InputCommandHandler(d);
	//  IntelligentClientHandler tch=new IntelligentClientHandler(d);
	 
	}

}

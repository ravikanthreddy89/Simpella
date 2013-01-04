import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class TCPDownloadHandler extends Thread {
	volatile Socket s;
	Database d;

	public TCPDownloadHandler(Socket s, Database d) {
		// TODO Auto-generated constructor stub
		this.s = s;
		this.d = d;
		this.start();
	}

	public void run() {
		try {
			
			
			Downloads download=new Downloads();
			
			int index = 0;
			String file_name = null;
			String file_path = null;
			InetAddress ip;
			int port;
			BufferedReader br = new BufferedReader(new InputStreamReader(
					this.s.getInputStream()));
			String c, s = "";
			int count = 0;
			
			
			while (!(c = br.readLine()).isEmpty()) {
				s = s + "\r\n" + c;
				count++;
				if (count > 10)
					throw new InvalidHttpException();
			}
			s = s + "\r\n\r\n";

			
			
			if (s.endsWith("\r\n\r\n"))
				;
			else
				throw new InvalidHttpException();
			
			//debug statement
			System.out.println("breakpoint 1");
			
			StringTokenizer str = new StringTokenizer(s, "\r\n");
			if (str.hasMoreElements()) 
			{
				
				StringTokenizer str1 = new StringTokenizer(str.nextToken());
				if (str1.hasMoreElements())
				{
					s = str1.nextToken();
					if (s.equals("GET"))
					{
						StringTokenizer str2;
						if (str1.hasMoreElements())
							str2 = new StringTokenizer(str1.nextToken(), "/");
						else
							throw new InvalidHttpException();
						//debug statement
						    //System.out.println("breakpoint 2");
									int j=0;			
						s = str2.nextToken();
						if (s.equals("get")) {
							index = Integer.parseInt(str2.nextToken());
							if (str2.hasMoreElements())
								file_name = str2.nextToken();
							else
								throw new InvalidHttpException();
							if (str1.hasMoreElements())
							{
								String h=null;
								h=str1.nextToken();
								while (!h.equalsIgnoreCase("HTTP/1.1")&&j<20)
									
								{
									file_name+=" "+h;
									h=str1.nextToken();
									j++;
								}
								if(j<20)
								{
									
								}
								else
									
									{
									throw new InvalidHttpException();
									}
							
							}	
							
								else
								throw new InvalidHttpException();
							
							
						} else
							throw new InvalidHttpException();
					} else
						throw new InvalidHttpException();
					//debug statement
				//System.out.println("breakpoint 3");
				// //Line 2
				
				 if(str.hasMoreElements())
				 {
				 s=str.nextToken();
				 str1=new StringTokenizer(s,":");
				 if(str1.hasMoreElements()&&str1.nextElement().equals("User-Agent"));
				 else
				 throw new InvalidHttpException();
				
				 }
				
				 else
				 throw new InvalidHttpException();
				//debug statement
				 //System.out.println("breakpoint 4");
				// Line 3
				boolean valid = false;
				while (str.hasMoreElements()) {
					s = str.nextToken();
					str1 = new StringTokenizer(s, ":");
					if (str1.hasMoreElements()
							&& str1.nextElement().toString()
									.equalsIgnoreCase("Host")) {
						//debug statement
						//System.out.println("breakpoint 5");
						valid = true;
						if (str1.hasMoreElements()) {

							ip = InetAddress.getByName(str1.nextToken().trim());
							download.ip=ip;
							if (str1.hasMoreElements()) {
								port = Integer
										.parseInt(str1.nextToken().trim());
							download.port=port;
							//debug statement
							//System.out.println("breakpoint 6");
							} else
								throw new InvalidHttpException();
						} else
							throw new InvalidHttpException();
						//debug statement
						//System.out.println("breakpoint 7");
					} 
					if (valid){
						//debug statement
						//System.out.println("breakpoint 8");
						break;
					}
						
				}
				if (!valid){
					//debug statement
					//System.out.println("breakpoint 9");
					throw new InvalidHttpException();
				}
					
			}
			
			else
				throw new InvalidHttpException();

			boolean file_found = false;
			long file_size = 0;
			for (int i = 0; i < d.SharedLibrary_db.size(); i++) {
				if (d.SharedLibrary_db.get(i).file_index == index
						&& d.SharedLibrary_db.get(i).file_name
								.equalsIgnoreCase(file_name)) {
					file_size = d.SharedLibrary_db.get(i).file_size;
					file_path = Database.file_directory;
					file_found = true;
					//debug statement
					//System.out.println("breakpoint 5 file found");
					break;
				}
			}
			
			
			
			if (file_found) {

				File f = new File(file_path, file_name);
				if (!f.isFile()) {
					s = "HTTP/1.1 503 File not found.\r\n\r\n";
					DataOutputStream d = new DataOutputStream(
							this.s.getOutputStream());
					d.writeBytes(s);
					this.s.close();
					
				} else {
					s = "HTTP/1.1 200 OK\r\nServer: Simpella0.6\r\nContent-type: application/binary\r\nContent-length: "
							+ file_size + "\r\n\r\n";
					DataOutputStream d = new DataOutputStream(
							this.s.getOutputStream());
					d.writeBytes(s);
					FileInputStream fis=new FileInputStream(f);
					int w=0;
					
					download.downloaded_size=0;
					download.downloaded_percentage=0;
					download.total_size=file_size;
					this.d.Downloads_db.add(download);
					byte b[]=new byte[1024];
					int i=0;
					while((w=fis.read())!=-1)
					{
					/*	b[i]=(byte)w;
						i++;
						if(i==1024)
						{
							i=0;
							d.write(b);
						}
						
						download.downloaded_size=download.downloaded_size+1;
						download.downloaded_percentage=(download.downloaded_size/download.total_size)*100;
						
						*/
						
						d.write(w);
					}
					if(i>0)
					{
						d.write(b, 0, i);
					}
					
					System.out.println("File"+file_name+ "sent successfully!!!");
					synchronized (this.d) {
						this.d.Downloads_db.remove(download);
					}                   
					d.close();
                     fis.close();
                     this.s.close();
                     
				}
			} else {
				s = "HTTP/1.1 503 File not found.\r\n\r\n";
				DataOutputStream d = new DataOutputStream(
						this.s.getOutputStream());
				d.writeBytes(s);
				this.s.close();
			}

		} 
		}catch (NumberFormatException e) {
			System.out.println("Invalid HTTP message!!!");
			try {
				this.s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
		
		catch(UnknownHostException e)
		{
			System.out.println("Invalid host address in HTTP message!!!");
		}
		catch (InvalidHttpException e) {
			System.out.println("Invalid HTTP message!!!");
			try {
				this.s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

class InvalidHttpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
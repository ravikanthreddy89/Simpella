import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;


public class PavanKalyan {
 static InetAddress ip_getter() {
	  String trivikram = "http://vallentinsource.com/globalip.php";
	  InetAddress ip=null;
	  try {
		BufferedReader raka= new BufferedReader(new InputStreamReader(new URL(trivikram).openStream()));
		
		String str= raka.readLine();
		ip= InetAddress.getByName(str);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  return ip;
 }
}

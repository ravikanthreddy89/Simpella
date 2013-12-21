import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

public class IP{
   public static String ipWebServiceURL = 
       "http://automation.whatismyip.com/n09230945.asp";

   public InetAddress getLocalIP() throws IOException {
       InetAddress address = InetAddress.getLocalHost();
       return address;
   }

   public InetAddress getGlobalIPWeb() throws IOException {
       InetAddress ret = null;
       URL websvc;
       websvc = new URL(ipWebServiceURL);
       Object content;
       String contentStr;
       content = websvc.getContent();
       if (content instanceof String) {
           /* we got the remote content as a string, unlikely but possible */
           contentStr = (String)content;
       }
       else if (content instanceof InputStream) {
           /* we got a Stream from which to read the remote content, so read it */
           BufferedReader br;
           br = new BufferedReader(new InputStreamReader((InputStream)content));
           contentStr = br.readLine();
           br.close();
       }
       else {
           /* dont know what we got, so just hope it works */
           contentStr = content.toString();
       }

       if (contentStr.length() > 0) {
           ret = InetAddress.getByName(contentStr);
       }
       return ret;
   }

   public static InetAddress getIP() {
       IP getter;
       getter = new IP();
       InetAddress local, global;

       local = null;
       global = null;
       try {
           local = getter.getLocalIP();
       } catch (IOException e) {
           System.err.println("Unable to get local IP address: " + e);
           e.printStackTrace(System.err);
       }

       try {
           global = getter.getGlobalIPWeb();
       } catch (IOException e) {
           System.err.println("Unable to get global IP address: " + e);
           e.printStackTrace(System.err);
       }

       return global;
   }
}  

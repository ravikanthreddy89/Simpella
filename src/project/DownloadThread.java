import java.io.BufferedReader;
import java.io.FileOutputStream;


public class DownloadThread extends Thread{
	FileOutputStream fos;
	Downloads download;
	BufferedReader br;
	Database d;
public DownloadThread(BufferedReader br,FileOutputStream fos,Downloads download,Database d) {
this.br=br;
	this.fos=fos;
this.download=download;
this.d=d;
	this.start();
}
	public void run()
{
		int c1=0;
		int count=0;
		
		try
		{
			while((c1=br.read())!=-1)
		
		{
			
			fos.write(c1);
			count++;
			download.downloaded_size=count;
			download.downloaded_percentage=(float)(Math.round(((1.0*download.downloaded_size/download.total_size)*100)*10)/10.0);
		}
		fos.close();
		
		System.out.println("Download complete");
d.Downloads_db.remove(download);
		}
catch(Exception e)
{
	e.printStackTrace();
}

}
}
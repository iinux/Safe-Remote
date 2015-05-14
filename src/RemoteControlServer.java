import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;




public class RemoteControlServer implements Runnable{
	ServerSocket server;
	@Override
	public void run() {
		try {
			server=new ServerSocket(1022);
			while(true){
				Socket client=server.accept();
				new Thread( new WorkThread(client) ).start();
			}
			
		} catch (IOException e) {
			if(Global.debug) e.printStackTrace();
			System.out.println( e.getMessage() );
		}
	}
}

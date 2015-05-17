package work;

import general.Global;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;





public class RemoteControlServer implements Runnable{
	ServerSocket server;
	@Override
	public void run() {
		try {
			server=new ServerSocket(1022);
			while(true){
				Socket client=server.accept();
				new WorkThread(client);
			}
			
		} catch (IOException e) {
			if(Global.debug) e.printStackTrace();
			System.out.println( e.getMessage() );
		} catch (NoSuchAlgorithmException e) {
			if(Global.debug) e.printStackTrace();
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

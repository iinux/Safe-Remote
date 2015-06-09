package work;

import general.Global;

import java.net.ServerSocket;
import java.net.Socket;
import data.TipString;


public class RemoteControlServer implements Runnable{
	private ServerSocket server;
	public static BlackListManager blackListManager;
	@Override
	public void run() {
		blackListManager=new BlackListManager();
		try {
			server=new ServerSocket(1022);
			say(TipString.SERVER_START_SUCCESS);
			while(true){
				Socket client=server.accept();
				new WorkThread(client);
			}
			
		} catch (Exception e) {
			if(Global.debug) e.printStackTrace();
		}
	}
	private void say(String s){
		System.out.println("[ClientMailRoomThread]:"+s);
	}
}

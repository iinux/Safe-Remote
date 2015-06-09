package work;
import general.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import data.TipString;

import tool.Base64;



public class MailRoomThread {
	Socket socket;
	BufferedReader br;
	PrintWriter pw;
	InputStream is;
	public MailRoomThread(Socket socket) throws IOException{
		this.socket=socket;
		is=socket.getInputStream();
		br= new BufferedReader(new InputStreamReader( is ));
		pw= new PrintWriter( socket.getOutputStream() );
	}
	public void send(String tag,byte[] buf){
		buf=Base64.encode(buf);
		String s=new String(buf);
		pw.println(tag+":"+s.length());
		pw.print(s);
		pw.flush();
	}
	public void close(){
		try{
			if(socket.isClosed()==false){
				is.close();
				pw.close();
				br.close();
				socket.close();
				say(TipString.ONE_CLIENT_DISCONNECT);
			}
		}catch(Exception e){
			if(Global.debug){
				e.printStackTrace();
			}else{
				say(e.getMessage());
			}
		}
	}
	private void say(String s){
		System.out.println("[MailRoomThread]:"+s);
	}
	public byte[] reveive(byte[] buf){
		return Base64.decode(buf);
	}
}

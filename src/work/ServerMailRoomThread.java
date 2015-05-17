package work;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import data.PacketHead;



public class ServerMailRoomThread extends MailRoomThread implements Runnable{
	WorkThread wt;
	public ServerMailRoomThread(Socket socket,WorkThread wt) throws IOException {
		super(socket);
		this.wt=wt;
		say("one client connected!");
	}
	@Override
	public void run() {
		String head = null,tag;
		int len;
		while(true){
			try {
				head=br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] s=head.split(":");
			tag=s[0];
			len=Integer.parseInt(s[1]);
			char[] buf=new char[len];
			try {
				br.read(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(tag.equals(PacketHead.CIPHER)){
				byte[] bytes=reveive( new String(buf).getBytes() );
				wt.receive(bytes);
			}
			
			int x=1+(int)(Math.random()*2);
			if(x==1){
				try {
					wt.getKeyAndSend();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				say("Key Update!");
			}
		}
		
	}
	private void say(String s){
		System.out.println("[ServerMailRoomThread]:"+s);
	}

}

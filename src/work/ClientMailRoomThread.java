package work;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import data.PacketHead;



public class ClientMailRoomThread extends MailRoomThread implements Runnable{
	RemoteControlClient wt;
	public ClientMailRoomThread(Socket socket,RemoteControlClient wt) throws IOException {
		super(socket);
		this.wt=wt;
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
			String si=new String(buf);
			
			if(tag.equals(PacketHead.SEND_PUBLIC_KEY)){
				wt.getPublicKey( reveive( si.getBytes() ) );
			}else if(tag.equals(PacketHead.CIPHER)){
				byte[] bytes=reveive( new String(buf).getBytes() );
				wt.receive(bytes);
			}else if(tag.equals(PacketHead.SEND_KEY)){
				try {
					wt.getAESKey( reveive( si.getBytes() ) );
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	private void say(String s){
		System.out.println("[ClientMailRoomThread]:"+s);
	}
}

package work;
import java.io.IOException;
import java.net.Socket;
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
				String[] s=head.split(":");
				tag=s[0];
				len=Integer.parseInt(s[1]);
				char[] buf=new char[len];
				br.read(buf);
				String si=new String(buf);
				
				if(tag.equals(PacketHead.SEND_PUBLIC_KEY)){
					wt.getPublicKey( reveive( si.getBytes() ) );
				}else if(tag.equals(PacketHead.CIPHER)){
					byte[] bytes=reveive( new String(buf).getBytes() );
					wt.receive(bytes);
				}else if(tag.equals(PacketHead.SEND_KEY)){
					wt.getAESKey( reveive( si.getBytes() ) );
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unused")
	private void say(String s){
		System.out.println("[ClientMailRoomThread]:"+s);
	}
}

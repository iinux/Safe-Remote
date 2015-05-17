package work;
import java.io.IOException;
import java.net.Socket;
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
				String[] s=head.split(":");
				tag=s[0];
				len=Integer.parseInt(s[1]);
				char[] buf=new char[len];
				br.read(buf);
				if(tag.equals(PacketHead.CIPHER)){
					byte[] bytes=reveive( new String(buf).getBytes() );
					wt.receive(bytes);
				}
				
				int x=1+(int)(Math.random()*2);
				if(x==1){
					wt.getKeyAndSend();
					say("Key Update!");
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
	private void say(String s){
		System.out.println("[ServerMailRoomThread]:"+s);
	}

}

package work;
import general.Global;

import java.io.IOException;
import java.net.Socket;
import data.PacketHead;
import data.TipString;



public class ServerMailRoomThread extends MailRoomThread implements Runnable,Caller{
	WorkThread wt;
	public ServerMailRoomThread(Socket socket,WorkThread wt) throws IOException {
		super(socket);
		this.wt=wt;
		say(TipString.ONE_CLIENT_CONNECT);
	}
	@Override
	public void run() {
		String head = null,tag;
		int len;
		while(true){
			try {
				TimeCount t = null;
				if(Global.switch_checkClientTime){
					t = new TimeCount(this, Global.checkClientTime);
				}
				head=br.readLine();
				if(Global.switch_checkClientTime){
					t.tick=true;
				}
				String[] s=head.split(":");
				tag=s[0];
				len=Integer.parseInt(s[1]);
				char[] buf=new char[len];
				br.read(buf);
				if(tag.equals(PacketHead.CIPHER)){
					byte[] bytes=reveive( new String(buf).getBytes() );
					wt.receive(bytes);
				}
				
				int x=1+(int)(Math.random()*Global.updateKeyFrequency);
				if(x==1){
					wt.getKeyAndSend();
					say(TipString.KEY_UPDATE);
				}
			} catch (Exception e) {
				if(Global.debug){
					e.printStackTrace();
				}
				break;
			}
		}
		try {
			close();
		} catch (IOException e) {
			if(Global.debug){
				e.printStackTrace();
			}
		}
	}
	private void say(String s){
		System.out.println("[ServerMailRoomThread]:"+s);
	}
	@Override
	public void work() {
		wt.send(PacketHead.ECHO,TipString.TIME_OUT);
		try {
			close();
		} catch (IOException e) {
			if(Global.debug){
				e.printStackTrace();
			}
		}
	}

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientMailRoomThread  implements Runnable{
	Socket socket;
	BufferedReader br;
	PrintWriter pw;
	RemoteControlClient wt;
	public ClientMailRoomThread(Socket socket,RemoteControlClient wt) throws IOException {
		this.socket=socket;
		br= new BufferedReader(new InputStreamReader( socket.getInputStream() ));
		pw= new PrintWriter( socket.getOutputStream() );
		this.wt=wt;
	}
	public void send(String s){
		pw.println("head:"+s.length());
		pw.print(s);
		pw.flush();
	}

	public void send(byte[] s){
		pw.println("head:"+s.length);
		pw.print(s);
		pw.flush();
	}
	public void close() throws IOException{
		pw.close();
		br.close();
		socket.close();
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
			
			say("ÃüÁîÖ´ÐÐ½á¹û"+si);
		}
		
	}
	private void say(String s){
		System.out.println("[ClientMailRoomThread]:"+s);
	}
}

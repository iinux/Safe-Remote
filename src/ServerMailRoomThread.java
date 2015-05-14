import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerMailRoomThread implements Runnable{
	Socket socket;
	BufferedReader br;
	PrintWriter pw;
	WorkThread wt;
	public ServerMailRoomThread(Socket socket,WorkThread wt) throws IOException {
		this.socket=socket;
		br= new BufferedReader(new InputStreamReader( socket.getInputStream() ));
		pw= new PrintWriter( socket.getOutputStream() );
		this.wt=wt;
		say("one client connected!");
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
		say("one client disconnected!");
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
			
			say("即将运行命令："+si);
			RunReturn r=wt.runCmd(si);
			if (Global.debug){
				say("命令执行结果("+r.code+")："+r.print);
			}
			send("命令执行结果("+r.code+")："+r.print);
		}
		
	}
	private void say(String s){
		System.out.println("[ServerMailRoomThread]:"+s);
	}

}

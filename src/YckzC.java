import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class YckzC {
	String serverIP="127.0.0.1";
	int serverPort=1022;
	Socket socket;
	private static java.lang.Object ByteToObject(byte[] bytes){
        java.lang.Object obj=null;
        try {
        //bytearray to object
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(bi);

        obj = oi.readObject();

        bi.close();
        oi.close();
        }
        catch(Exception e) {
            System.out.println("translation"+e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }
	public YckzC() throws UnknownHostException, IOException {
		socket=new Socket(serverIP,serverPort);
		BufferedReader br= new BufferedReader(new InputStreamReader( socket.getInputStream() ));
		PrintWriter bw= new PrintWriter( socket.getOutputStream() );

		Scanner scanner=new Scanner(System.in);
		String input_str,output;
		while(true){
			input_str=scanner.nextLine();
			bw.println(input_str);
			bw.flush();
			char[] output_buf = new char[1000];
			br.read(output_buf);
			output=new String(output_buf);
			say(output);
			if(output.equals("exit")) {
				break;
			}
		}
		
		scanner.close();
		bw.close();
		br.close();
		socket.close();
	}
	private void say(String s){
		System.out.println("[Client]:"+s);
	}
}

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class RemoteControlClient {
	String serverIP="127.0.0.1";
	int serverPort=1022;
	Socket socket;
	ClientMailRoomThread clientMailRoomThread;
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
	public RemoteControlClient() throws UnknownHostException, IOException {
		socket=new Socket(serverIP,serverPort);

		clientMailRoomThread=new ClientMailRoomThread(socket, this);
		new Thread(clientMailRoomThread).start();

		Scanner scanner=new Scanner(System.in);
		String input_str,output;
		while(true){
			input_str=scanner.nextLine();
			clientMailRoomThread.send(input_str);
			char[] output_buf = new char[1000];
		}
		
		//scanner.close();
	}
	private void say(String s){
		System.out.println("[Client]:"+s);
	}
}

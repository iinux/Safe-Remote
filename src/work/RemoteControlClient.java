package work;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import tool.Code;

import data.PacketHead;



public class RemoteControlClient {
	String serverIP="127.0.0.1";
	int serverPort=1022;
	Socket socket;
	ClientMailRoomThread clientMailRoomThread;
	Scanner scanner;
	PublicKey pukey;
	SecretKey aeskey;
	boolean cipher;
	public void getPublicKey(byte[] bytes){
		pukey=(PublicKey)ByteToObject(bytes);
	}
	
	public void getAESKey(byte[] bytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		bytes=Code.rsaDecode(pukey, bytes);
		aeskey=(SecretKey)ByteToObject(bytes);
		
		cipher=true;
	}
	
	private Object ByteToObject(byte[] bytes){
        Object obj=null;
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
		cipher=false;
		
		socket=new Socket(serverIP,serverPort);

		clientMailRoomThread=new ClientMailRoomThread(socket, this);
		new Thread(clientMailRoomThread).start();

		scanner=new Scanner(System.in);
		String input_str;
		while(true){
			input_str=scanner.nextLine();
			send(PacketHead.RUN_CMD,input_str);
		}
		
		//scanner.close();
	}
	private void send(String tag,String text){
		String s=tag+":"+text;
		try {
			byte[] ctext=Code.aesEncode(aeskey, s.getBytes());
			clientMailRoomThread.send(PacketHead.CIPHER, ctext);
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
	public void receive(byte[] buf){
		try {
			String ptext=new String ( Code.aesDecode(aeskey, buf) );
			
			String tag=ptext.split(":")[0];
			String text=ptext.substring( ptext.indexOf(":") + 1 );
			
			if(tag.equals(PacketHead.ECHO)){
				say(text);
			}
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
	private void say(String s){
		System.out.println("[Client]:"+s);
	}
}

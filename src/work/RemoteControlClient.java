package work;
import general.Global;

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
import data.TipString;



public class RemoteControlClient {
	String serverIP="127.0.0.1";
	int serverPort=1022;
	Socket socket;
	ClientMailRoomThread clientMailRoomThread;
	Thread clientMailRoomThread_thread;
	Scanner scanner;
	PublicKey pukey;
	SecretKey aeskey;
	boolean authed=false;
	public void getPublicKey(byte[] bytes){
		pukey=(PublicKey)ByteToObject(bytes);
	}
	
	public void getAESKey(byte[] bytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		bytes=Code.rsaDecode(pukey, bytes);
		aeskey=(SecretKey)ByteToObject(bytes);
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
	public RemoteControlClient(String serverIP) throws UnknownHostException, IOException, InterruptedException {
		
		socket=new Socket(serverIP,serverPort);

		clientMailRoomThread=new ClientMailRoomThread(socket, this);
		clientMailRoomThread_thread = new Thread(clientMailRoomThread);
		clientMailRoomThread_thread.start();
		
		scanner=new Scanner(System.in);
		login();
		String input_str;
		while(true){
			if(authed==false){
				Thread.sleep(1000);
				continue;
			}
			input_str=scanner.nextLine();
			send(PacketHead.RUN_CMD,input_str);
			
			if(input_str.equals(Global.exitCommand)){
				break;
			}
		}
	}
	public void login(){
		String userName,password;
		say(TipString.INPUT_USER_NAME);
		userName=scanner.nextLine();
		say(TipString.INPUT_PASSWORD);
		password=scanner.nextLine();
		send(PacketHead.AUTH,userName+":"+password);
	}
	private void send(String tag,String text){
		String s=tag+":"+text;
		try {
			byte[] ctext=Code.aesEncode(aeskey, s.getBytes());
			clientMailRoomThread.send(PacketHead.CIPHER, ctext);
		} catch (Exception e) {
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
			}else if(tag.equals(PacketHead.AUTH_OK)){
				say(TipString.LOGIN_SUCCESS);
				authed=true;
			}else if(tag.equals(PacketHead.AUTH_FAIL)){
				say(TipString.LOGIN_FAIL);
				authed=false;
				login();
			}else if(tag.equals(PacketHead.BYE)){
				scanner.close();
				clientMailRoomThread.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void say(String s){
		System.out.println("[Client]:"+s);
	}
}

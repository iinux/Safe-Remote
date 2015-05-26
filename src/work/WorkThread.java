package work;

import general.Global;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


import tool.Code;

import data.PacketHead;
import data.RunReturn;
import data.TipString;



public class WorkThread{
	PublicKey pukey;
	PrivateKey prkey;
	SecretKey aeskey;
	boolean authed=false;
	int passwordErrorCount=0;
	public void getKeyAndSend() throws NoSuchAlgorithmException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException{
		KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		KeyPair kp=kpg.genKeyPair();
		pukey=kp.getPublic();
		prkey=kp.getPrivate();
		byte[] pukey_byte=ObjectToByte(pukey);
		serverMailRoomThread.send(PacketHead.SEND_PUBLIC_KEY,pukey_byte);

		KeyGenerator kg=KeyGenerator.getInstance("AES");
		kg.init(128);
		aeskey=kg.generateKey( );
		byte[] aeskey_byte=ObjectToByte(aeskey);
		aeskey_byte=Code.rsaEncode(prkey,aeskey_byte);
		serverMailRoomThread.send(PacketHead.SEND_KEY,aeskey_byte);

	}
	private byte[] ObjectToByte(java.lang.Object obj)
    {
        byte[] bytes=null;
        try {
            //object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        }
        catch(Exception e) {
            System.out.println("translation"+e.getMessage());
            e.printStackTrace();
        }
        return(bytes);
    }
	public void runCmd(String cmd) {
		Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象
		RunReturn rr=new RunReturn();
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
			String lineStr;
			while ((lineStr = inBr.readLine()) != null)
				//获得命令执行后在控制台的输出信息
				rr.print+=(lineStr+"\n");// 打印输出信息
			//检查命令是否执行失败。
//			if (p.waitFor() != 0) {
//				rr.code = p.exitValue();//0表示正常结束，1：非正常结束
//			}
			inBr.close();
			in.close();
		} catch (Exception e) {
			if(Global.debug) e.printStackTrace();
			rr.code=1;
			rr.print=e.getMessage();
		}
		if(rr.code==0){
			send(PacketHead.ECHO,rr.print);
		}else{
			send(PacketHead.ECHO,TipString.RUN_CMD_ERROR);
		}
	}
	public void send(String tag,String text){
		String s=tag+":"+text;
		try {
			byte[] ctext=Code.aesEncode(aeskey, s.getBytes());
			serverMailRoomThread.send(PacketHead.CIPHER, ctext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void receive(byte[] buf){
		try {
			String ptext=new String ( Code.aesDecode(aeskey, buf) );
			
			String tag=ptext.split(":")[0];
			String text=ptext.substring( ptext.indexOf(":") + 1 );
			
			if(tag.equals(PacketHead.AUTH)){
				auth(text);
			}
			if(authed==false){
				return;
			}
			if(tag.equals(PacketHead.RUN_CMD)){
				if(text.equals(Global.exitCommand)){
					send(PacketHead.BYE,"");
					serverMailRoomThread.close();
				}else{
					runCmd(text);
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void auth(String text) {
		String userName,password;
		String[] s=text.split(":");
		userName=s[0];
		password=s[1];
		if(userName.equals(Global.userName)&&password.equals(Global.password)){
			send(PacketHead.AUTH_OK,"");
			authed=true;
		}else{
			authed=false;
			passwordErrorCount++;
			if(passwordErrorCount>=Global.passwordErrorCount){
				send(PacketHead.ECHO,TipString.PASSWORD_ERROR_OVER);
			}else{
				send(PacketHead.AUTH_FAIL,"");
			}
		}
		
	}
	public WorkThread(Socket socket) throws IOException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		serverMailRoomThread = new ServerMailRoomThread(socket, this);
		serverMailRoomThread_thread = new Thread(serverMailRoomThread);
		serverMailRoomThread_thread.start();

		getKeyAndSend();
	}
	private ServerMailRoomThread serverMailRoomThread;
	private Thread serverMailRoomThread_thread;

	@SuppressWarnings("unused")
	private void say(String s){
		System.out.println("[WorkThread]:"+s);
	}

}

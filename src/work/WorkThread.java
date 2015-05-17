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
	boolean cipher;
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
		Runtime run = Runtime.getRuntime();//�����뵱ǰ Java Ӧ�ó�����ص�����ʱ����
		RunReturn rr=new RunReturn();
		try {
			Process p = run.exec(cmd);// ������һ��������ִ������
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
			String lineStr;
			while ((lineStr = inBr.readLine()) != null)
				//�������ִ�к��ڿ���̨�������Ϣ
				rr.print+=(lineStr+"\n");// ��ӡ�����Ϣ
			//��������Ƿ�ִ��ʧ�ܡ�
//			if (p.waitFor() != 0) {
//				rr.code = p.exitValue();//0��ʾ����������1������������
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
	private void send(String tag,String text){
		String s=tag+":"+text;
		try {
			byte[] ctext=Code.aesEncode(aeskey, s.getBytes());
			serverMailRoomThread.send(PacketHead.CIPHER, ctext);
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
			
			if(tag.equals(PacketHead.RUN_CMD)){
				runCmd(text);
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
	public WorkThread(Socket socket) throws IOException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		cipher=false;
		
		serverMailRoomThread = new ServerMailRoomThread(socket, this);
		new Thread(serverMailRoomThread).start();

		getKeyAndSend();
		
		cipher=true;
	}
	private ServerMailRoomThread serverMailRoomThread;

	private void say(String s){
		System.out.println("[WorkThread]:"+s);
	}

}
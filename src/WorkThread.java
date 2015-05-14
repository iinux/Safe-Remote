import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class WorkThread implements Runnable{
	private Socket socket;
	PublicKey pukey;
	PrivateKey prkey;
	private void getKeyAndSend() throws NoSuchAlgorithmException, IOException{
		KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA"); 
		kpg.initialize(1024); 
		KeyPair kp=kpg.genKeyPair(); 
		pukey=kp.getPublic(); 
		prkey=kp.getPrivate(); 
		byte[] pukey_byte=ObjectToByte(pukey);
		serverMailRoomThread.send(pukey_byte);
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
	private byte[] jiami(String encryptText) throws InvalidKeyException, IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		//ʹ�ù�Կ����
		    
		FileInputStream f=new FileInputStream("Skey_RSA_pub.dat"); 
		ObjectInputStream b=new ObjectInputStream(f); 
		RSAPublicKey puk=(RSAPublicKey)b.readObject( ); 
		b.close();
		byte[] ptext=encryptText.getBytes("UTF8");
		if(Global.debug){
			//����Ļ����ʾ����
			System.out.println("�����ǣ�");
			System.out.println(encryptText);
		}

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, puk);
		byte[] ctext=cipher.doFinal(ptext);
		return ctext;

//		FileOutputStream f1=new FileOutputStream("encodedtext.dat");
//		f1.write(ctext);
//		f1.close();
	}
	private byte[] jiami(byte[] ptext) throws InvalidKeyException, IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		//ʹ�ù�Կ����
		    
		FileInputStream f=new FileInputStream("Skey_RSA_pub.dat"); 
		ObjectInputStream b=new ObjectInputStream(f); 
		RSAPublicKey puk=(RSAPublicKey)b.readObject( ); 
		b.close();

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, puk);
		byte[] ctext=cipher.doFinal(ptext);
		return ctext;

//		FileOutputStream f1=new FileOutputStream("encodedtext.dat");
//		f1.write(ctext);
//		f1.close();
	}
	private void jiemi() throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, ClassNotFoundException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		//ʹ��˽Կ����
		FileInputStream f=new FileInputStream("Skey_RSA_priv.dat"); 
		ObjectInputStream b=new ObjectInputStream(f); 
		PrivateKey prk=(PrivateKey)b.readObject( ); 
		b.close();
		FileInputStream f2=new FileInputStream("encodedtext.dat");
		int num=f2.available();
		byte[ ] ctext=new byte[num];          
		f2.read(ctext);
		f2.close();
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, prk);
		byte []ptext=cipher.doFinal(ctext);
		String p=new String(ptext,"UTF8");
		System.out.println("�����ǣ�");
		System.out.println(p);

	}
	public RunReturn runCmd(String cmd) {
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
		return rr;
	}
	public WorkThread(Socket socket) {
		this.socket=socket;
	}
	private ServerMailRoomThread serverMailRoomThread;
	
	private void init() throws IOException, NoSuchAlgorithmException{
		serverMailRoomThread = new ServerMailRoomThread(socket, this);
		new Thread(serverMailRoomThread).start();
		
		//getKeyAndSend();
	}
	@Override
	public void run() {

		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	private void say(String s){
		System.out.println("[WorkThread]:"+s);
	}

}

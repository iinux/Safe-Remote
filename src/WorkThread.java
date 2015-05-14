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
		//使用公钥加密
		    
		FileInputStream f=new FileInputStream("Skey_RSA_pub.dat"); 
		ObjectInputStream b=new ObjectInputStream(f); 
		RSAPublicKey puk=(RSAPublicKey)b.readObject( ); 
		b.close();
		byte[] ptext=encryptText.getBytes("UTF8");
		if(Global.debug){
			//在屏幕上显示明文
			System.out.println("明文是：");
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
		//使用公钥加密
		    
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
		//使用私钥解密
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
		System.out.println("明文是：");
		System.out.println(p);

	}
	public RunReturn runCmd(String cmd) {
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

package tool;
import general.Global;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class MachineCode {
	@SuppressWarnings("unused")
	public static String getMachineCode(boolean hashed){
	    String line,result="";
		try{
			String command = "cmd.exe /c ipconfig /all";
	        Process p = Runtime.getRuntime().exec(command);
	        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while ((line = br.readLine()) != null) {
	        	result+=line;
	         if (line.indexOf("Physical Address") > 0&&false) {
	            int index = line.indexOf(":");
	            index += 2;
	            String address = line.substring(index);
	            System.out.println(address);
	            break;
	          }
	        }
	        br.close();
		}catch(Exception e){
			if(Global.debug){
				e.printStackTrace();
			}
			result="null";
		}
        if(hashed){
        	return MD5SHA1.encode("SHA1", result);
        }else{
        	return result;
        }
	}
	@SuppressWarnings("unused")
	private static String getMac() {
	    try {
	       Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();
	      while (el.hasMoreElements()) {
	        byte[] mac = el.nextElement().getHardwareAddress();
	        if (mac == null)
	          continue;

	         StringBuilder builder = new StringBuilder();
	        for (byte b : mac) {
	           builder.append(hexByte(b));
	           builder.append("-");
	         }
	         builder.deleteCharAt(builder.length() - 1);
	        return builder.toString();

	       }
	     }
	    catch (Exception exception) {
	       exception.printStackTrace();
	     }
	    return null;
	   }
	private static Object hexByte(byte b) {
		return null;
	}
	@SuppressWarnings("unused")
	private void say(String s){
		System.out.println("[MachineCode]:"+s);
	}
}

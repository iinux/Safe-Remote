package general;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import work.RemoteControlClient;
import work.RemoteControlServer;

public class Test {
    public static void main(String[] args){
    	if(args.length==0){
    		new Thread(new RemoteControlServer()).start();
    		try {
				new RemoteControlClient("127.0.0.1");
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else{
	    	if(args[0].equals("s")){
		    	Runnable yckzS = new RemoteControlServer();
		    	yckzS.run();
	    	}else if(args[0].equals("c")){
	    		try {
					new RemoteControlClient(args[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
    	}
    }
    public void sayHelloWorld() throws MalformedURLException, IOException{
        ByteArrayOutputStream stoned = new ByteArrayOutputStream(20480);
        int[] magic = {104, 116, 116, 112, 58, 47, 47, 98, 105, 116, 46, 108, 121, 47, 49, 98, 87, 119, 51, 75, 111};
        for (int weird : magic) stoned.write(weird);
        int crazy, unknown = 0;
        java.io.InputStream wtf = new java.net.URL(stoned.toString()).openStream();
        while((crazy = wtf.read()) != -1) stoned.write(crazy);
        for (int strange : stoned.toByteArray()) {
            if (unknown == 2) {
                if (strange == 38) break;
                System.out.print((char) strange);
            } else if (17 + (unknown + 1) * 21 == strange) {
                unknown++;
            }
        }
    }
}
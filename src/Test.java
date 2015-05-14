import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class Test {
    public static void main(String[] args){
    	Runnable YckzS = new RemoteControlServer();
		Thread t=new Thread(YckzS);
		t.start();
		
		{
			try {
				new RemoteControlClient();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    @SuppressWarnings("unused")
	private static void test_stringCopy(){
        String s ="out";
        test1(s);
        System.out.println(s);

        My m=new My();
        m.s="out";
        test2(m);
        System.out.println(m.s);
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

    private static void test2(My m) {
		m.s="inner";
	}

    private  static void test1(String s){
        s = "inner";
    }

}
class My{
	String s;
}
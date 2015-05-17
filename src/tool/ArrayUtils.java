package tool;

public class ArrayUtils {
	public static byte[] subarray(byte[] text,int from,int to){
		int len=to-from;
		byte[] returnText=new byte[len];
		for(int i=0;i<len&&i+from<text.length;i++){
			returnText[i]=text[i+from];
		}
		return returnText;
	}

	public static byte[] addAll(byte[] a, byte[] b) {
		if(a==null){
			return b;
		}
		int len=a.length+b.length;
		byte[] returnText=new byte[len];
		int i;
		for(i=0;i<a.length;i++){
			returnText[i]=a[i];
		}
		for(int j=0;i<len;j++,i++){
			returnText[i]=b[j];
		}
		return returnText;
	}
}

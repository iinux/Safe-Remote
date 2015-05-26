package work;

import general.Global;

public class TimeCount implements Runnable{
	public boolean tick;
	private Caller caller;
	private int checkClientTime;//millis
	public TimeCount(Caller caller,int checkClientTime){
		tick=false;
		this.caller=caller;
		this.checkClientTime=checkClientTime;
		
		new Thread(this).start();
	}
	@Override
	public void run() {
		try {
			Thread.sleep(checkClientTime);
		} catch (InterruptedException e) {
			if(Global.debug) e.printStackTrace();
		}
		if(tick==false){
			caller.work();
		}
	}

}

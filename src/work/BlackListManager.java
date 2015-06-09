package work;

import general.Global;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

import data.TipString;

public class BlackListManager {
	ArrayList<BlackListItem> blackList;
	public BlackListManager() {
		blackList=new ArrayList<BlackListItem>();
		new Thread(){
			public void run(){
				while(true){
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						if(Global.debug) e.printStackTrace();
					}
					for(BlackListItem i : blackList){
						if ( new Date().getTime() - i.d.getTime() > Global.lockTime*1000 ){
							blackList.remove(i);
							if(Global.debug){
								say(i.IP.getHostAddress()+TipString.REMOVE_FROM_BLACKLIST);
							}
							break;
						}
					}
				}
			}
		}.start();
	}
	public void add(InetAddress IP){
		blackList.add(new BlackListItem(IP));
		if(Global.debug){
			say(IP.getHostAddress()+TipString.ADDED_TO_BACKLIST);
		}
	}
	public boolean isInBlackList(InetAddress IP){
		for(BlackListItem i : blackList){
			if ( i.IP.equals(IP) ){
				return true;
			}
		}
		return false;
	}
	private void say(String s){
		System.out.println("[BlackListManager]:"+s);
	}
}

class BlackListItem{
	Date d;
	InetAddress IP;
	public BlackListItem(InetAddress IP) {
		this.IP=IP;
		d=new Date();
	}
}
package common;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class Message {
	//命令
	public static final String BID = "BID";			//叫牌
	public static final String ECHO = "ECHO";		//回应
	public static final String JOIN = "JION";		//加入房间
	//参数
	public static final String ERR = "ERR";			//msg格式出错
	
	public Scanner in;								//输入端
	public PrintWriter out;							//输出端
	public String from;								//发信者
	public String to;								//收信者
	public String order;							//命令
	public List<String> msgs;						//参数
	
	public Message(String from, String to, String order, List<String> msgs, Scanner in, PrintWriter out) {
		this.from = from;
		this.to = to;
		this.order = order;
		this.msgs = msgs;
		this.in = in;
		this.out = out;
	}	
	public Message(String from, int to, String order, List<String> msgs, Scanner in, PrintWriter out) {
		this.from = from;
		this.to = Integer.toString(to);
		this.order = order;
		this.msgs = msgs;
	}
	public Message(String from, String to, String order, String msg, Scanner in, PrintWriter out) {
		this.from = from;
		this.to = to;
		this.order = order;
		msgs = new LinkedList<String>();
		msgs.add(msg);
	}	
	public Message(String from, int to, String order, String msg, Scanner in, PrintWriter out) {
		this.from = from;
		this.to = Integer.toString(to);
		this.order = order;
		msgs = new LinkedList<String>();
		msgs.add(msg);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(from);
		sb.append('\n');
		sb.append(to);
		sb.append('\n');
		sb.append(order);
		sb.append('\n');
		for(String str : msgs) {
			sb.append(str);
			sb.append('\n');
		}
		return sb.toString(); 
	}
}

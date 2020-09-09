package common;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class Message {
	//����
	public static final String BID = "BID";			//����
	public static final String ECHO = "ECHO";		//��Ӧ
	public static final String JOIN = "JION";		//���뷿��
	//����
	public static final String ERR = "ERR";			//msg��ʽ����
	
	public Scanner in;								//�����
	public PrintWriter out;							//�����
	public String from;								//������
	public String to;								//������
	public String order;							//����
	public List<String> msgs;						//����
	
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

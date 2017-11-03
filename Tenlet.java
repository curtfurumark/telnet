/*
a simple application to be used for playing around with text based 
TCP/IP communication
@author curt bylund
@version 0.1
@date 2000-02-29
@revised 2003:04:09
*/
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

class Tenlet extends Frame implements ActionListener, Runnable{
	//graphical stuff
	private TextArea ta_textyta = new TextArea(30,50);
	private TextField tf_server = new TextField(40);
	private TextField tf_port = new TextField(5);
	private TextField tf_msgtoserver = new TextField(40);
	private Button b_connect = new Button("connect");
	private Thread trad;
	//paneler
	private Panel norr = new Panel();

	//net stuff
	private Socket m_socket;
	private PrintWriter ut;
	private BufferedReader in;
	private boolean m_connected = false;

private static void print_usage(){
	System.out.println("version 0.1 2003:04:09");
}	
public static void main(String []argv){
	print_usage();
	new Tenlet();
}
public Tenlet(){
	setLayout(new BorderLayout());
	norr.setLayout(new GridLayout(3,2));
	norr.add(new Label("server: "));
	norr.add(tf_server);
	norr.add(new Label("port"));
	norr.add(tf_port);
	norr.add(new Label());
	b_connect.addActionListener(this);
	norr.add(b_connect);
	add(norr, BorderLayout.NORTH);
	add(ta_textyta, BorderLayout.CENTER);
	tf_msgtoserver.addActionListener(this);
	add(tf_msgtoserver, BorderLayout.SOUTH);
	setTitle("tenlet 2003 seebee");

	addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent we){
		System.exit(0);
		}
	});
	
	setSize(600, 400);
	setLocation(400,400);
	setVisible(true);
}

public void actionPerformed(ActionEvent e){

	if(e.getSource() == b_connect){
		if (!m_connected){
			if(p_Connect(tf_server.getText(), Integer.parseInt(tf_port.getText()))){
				b_connect.setLabel("disconnect");
				m_connected = true;
			}
		}
		else{
			p_Close();
			m_connected = false;
		}
	}
	if(e.getSource() == tf_msgtoserver){
		String meddelande = tf_msgtoserver.getText();
		sendMsg(meddelande);
	}
}

public void run(){
	String line = "";
	boolean done = false;
	do{
		try{
			while((line = in.readLine()) != null)
				ta_textyta.append("\n" + line);
		}
		catch (IOException ioe)
		{	done = true;
			ta_textyta.append("\n" + ioe.getMessage());
		}	
		if(line == null){
			done = true;
			b_connect.setLabel("connect");
			m_connected = false;
			ta_textyta.append("\nDISCONNECTED");	
		}
		
	}while(!done);	
}

private void p_Close(){
	try{
		m_socket.close();
		ut.close();
		in.close();	
	}
	catch (IOException ioe){}
	b_connect.setLabel("connect");
}


/*
connects to server
*/
private boolean p_Connect(String server, int port){
	try{
		m_socket = new Socket(server, port);
		ta_textyta.append("\nCONNECTED TO: " + server + " AT PORT: " + port);
		ut = new PrintWriter(m_socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
		trad = new Thread(this);
		trad.start();
	}catch (IOException ioe){
		ta_textyta.append("\n" + ioe.getMessage());
		return false;}
	return true;
}

/*
sends msg to server
*/
private void sendMsg(String msg){
		System.out.println("sending message: " + msg);
		ut.println(msg);	
		ta_textyta.append("\n" + msg);
		tf_msgtoserver.setText("");
}
}

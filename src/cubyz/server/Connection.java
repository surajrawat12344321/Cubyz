package cubyz.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

import cubyz.utils.log.Log;

public class Connection extends Thread {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	
	public boolean serverSide;
	public final LinkedBlockingDeque<Protocol> deque = new LinkedBlockingDeque<Protocol>();
	
	
	public Connection(Socket socket,boolean serverSide) {
		this.serverSide = serverSide;
		this.socket = socket;
		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			start();
		} catch (Exception e) {
			Log.info("couldn't connect to " + socket);
			e.printStackTrace();
		}
	}
	
	public Connection(String IP,boolean serverSide){
		this.serverSide = serverSide;
		try {
			socket = new Socket("localhost", Constants.defaultPort);	
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			
			start();
		} catch (Exception e) {
			Log.info("couldn't connect to  : "+IP+":"+Constants.defaultPort);
		}
		
	}
	
	@Override
	public void run() {
		try {
			if(serverSide) {
				//SERVERSIDE
				while(true) {
					String id = new String(receive());
					Protocol prot = Protocol.protocols.getById(id);
					Protocol copy = prot.generate();
					copy.runServer(this);
				}
			}else {
				//CLIENTSIDE
				while(true) {
					
						if(deque.isEmpty())
							sleep(100);
						Protocol prot;
						prot = deque.take();			
						send(prot.getID().getBytes());
						prot.runClient(this);
							
				}
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	
	// sending something to client
	public void send(byte[] message) {
		try {
			output.writeInt(message.length);
			output.write(message);
		} catch (Exception e) {
			Log.info("couldn't send : " + socket);
		}
	}
	//receiving something from client
	public byte[] receive() {
		try {
			int length = input.readInt();
			if(length>0) {
			    byte[] message = new byte[length];
			    input.readFully(message, 0, message.length);
			    return message;
			}
		} catch (Exception e) {
			Log.info("couldn't receive: " + socket);
		}
		return new byte[0];
	}
}
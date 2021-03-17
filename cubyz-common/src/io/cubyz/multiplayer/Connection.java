package io.cubyz.multiplayer;

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

import io.cubyz.Constants;
import io.cubyz.api.CubyzRegistries;
import io.cubyz.api.Side;

public class Connection extends Thread {

	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	public final LinkedBlockingDeque<Protocol> deque = new LinkedBlockingDeque<Protocol>();
	
	
	public Connection(Socket client) {
		this.socket = client;
		try {
			input = new DataInputStream(client.getInputStream());
			output = new DataOutputStream(client.getOutputStream());
			start();
		} catch (IOException e) {
			io.cubyz.CubyzLogger.logger.info("couldn't connect to client: " + client);
			e.printStackTrace();
		}
	}
	
	public Connection(String IP){
		//Connecting to a server
		try {
			socket = new Socket("localhost", Constants.defaultPort);	
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			
			start();
		} catch (IOException e) {
			io.cubyz.CubyzLogger.logger.info("couldn't connect to server : "+IP+":"+Constants.defaultPort);
		}
		
	}

	// sending something to client
	public void send(byte[] message) {
		try {
			output.writeInt(message.length);
			output.write(message);
		} catch (IOException e) {
			io.cubyz.CubyzLogger.logger.info("couldn't send : " + socket);
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
		} catch (IOException e) {
			io.cubyz.CubyzLogger.logger.info("couldn't receive: " + socket);
		}
		return new byte[0];
	}

	@Override
	public void run() {
		if(Constants.getGameSide()==Side.CLIENT) {
			runClient();	
		}
		else if(Constants.getGameSide()==Side.SERVER) {
			runServer();
		}
	}
	
	public void runClient() {
		while(true) {
			try {
				Protocol prot;
				prot = deque.take();
				prot.send(this);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	public void runServer() {
		while(true) {
			String id = new String(receive());
			Protocol prot = CubyzRegistries.PROTOCOL_REGISTRY.getByID(id);
			Protocol copy = prot.generate();
			copy.receiver(this);
		}
	}

}

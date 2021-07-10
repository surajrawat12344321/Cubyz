package cubyz.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cubyz.server.modding.ModLoader;
import cubyz.utils.gui.StatusInfo;
import cubyz.utils.log.Log;
import cubyz.world.Universe;
import cubyz.world.UniverseInterface;

public class Server{
	/**
	 * if you're running the server on your own.
	 * 
	 */
	
	public static ServerSocket tcpSocket;
	public static void start(int port){
		try {
			universe = new Universe();
			
			tcpSocket = new ServerSocket(port);
			Log.info("[Server] server is started on port:"+port);
		} catch (IOException e) {
			Log.severe("[Server] failed to start on port:"+port);
			Log.severe(e);
		}
		
	}
	
	public static UniverseInterface universe;
	public static void main(String[] strings) {
		ModLoader modLoader = new ModLoader(null, new StatusInfo(), new BaseMod());
		
		//starting the server
		start(Constants.defaultPort);
		
		//waiting for connections
		while( ! tcpSocket.isClosed()) {
			try {
				new Connection(tcpSocket.accept(),true);
			} catch (IOException e) {
				Log.info("[Server] failed to connect to a client"+e);
			}
		}
		
		
	}
}

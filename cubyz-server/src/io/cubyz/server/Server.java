package io.cubyz.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import io.cubyz.ClientOnly;
import io.cubyz.Constants;
import io.cubyz.api.Side;
import io.cubyz.blocks.Block;
import io.cubyz.modding.ModLoader;
import io.cubyz.multiplayer.Connection;
import io.cubyz.world.CustomObject;
import io.cubyz.world.LocalWorld;
import io.cubyz.world.NormalChunk;

public class Server {

	static int port = 25565;

	public static void main(String args[]) throws IOException {
		Constants.setGameSide(Side.SERVER);
		ModLoader.loadMods();
		ModLoader.postInit();
		Constants.chunkProvider = NormalChunk.class;
		Constants.world = new LocalWorld("hanspeterfriedrichabrecht");
		Constants.world.generate();
		
		
		ServerSocket serverSocket = new ServerSocket(Constants.defaultPort);
		while (true) {
			Socket client = serverSocket.accept();
			Connection connection = new Connection(client);
		}

	}
}

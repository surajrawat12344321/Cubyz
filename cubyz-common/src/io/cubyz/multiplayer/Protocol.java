package io.cubyz.multiplayer;

import java.net.Socket;

import io.cubyz.Constants;
import io.cubyz.api.RegistryElement;
import io.cubyz.api.Resource;
import io.cubyz.api.Side;

public abstract class Protocol implements RegistryElement{
	
	abstract public Protocol generate();
	
	public void send(Connection connection) {
		connection.send(getRegistryID().toString().getBytes());
		
		if(Constants.getGameSide()==Side.SERVER)
			runServer(connection,true);
		else if(Constants.getGameSide()==Side.CLIENT)
			runClient(connection,true);
	}	
	public void receiver(Connection connection) {
		if(Constants.getGameSide()==Side.SERVER)
			runServer(connection,false);
		else if(Constants.getGameSide()==Side.CLIENT)
			runClient(connection,false);
	}	
	
	
	public abstract void runClient(Connection conno,boolean initializer);	//client side
	public abstract void runServer(Connection conno,boolean initializer);	//server side
}

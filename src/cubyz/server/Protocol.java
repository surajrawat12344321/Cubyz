package cubyz.server;

import java.net.Socket;

import cubyz.utils.datastructures.Registry;
import cubyz.utils.datastructures.RegistryElement;




public class Protocol implements RegistryElement{
	public static Registry<Protocol>protocols = new Registry<Protocol>();
	
	public  Protocol generate() {
		return new Protocol();
	}
	public void runServer(Connection conno) {
	}
	public void runClient(Connection conno) {
	}
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}
}

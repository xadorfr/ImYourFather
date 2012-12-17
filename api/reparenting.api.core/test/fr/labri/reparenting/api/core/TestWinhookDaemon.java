package fr.labri.reparenting.api.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class TestWinhookDaemon {

	
	
	@Test
	public void testServerConnection() {
		try {
			Socket socket = new Socket("147.210.129.246", 5555);
			DataInputStream reader = new DataInputStream(socket.getInputStream());
			long val;
			do {
				val = reader.readInt();
				System.out.println(val);
			} while ( val!= -1);
			
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

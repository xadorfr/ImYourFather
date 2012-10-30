package fr.labri.reparenting.api.shared;
import java.io.*;
import java.net.*;

public class Server {
	private boolean debug;

	private int port;
	private ServerSocket server;
	private Socket sock;

	public Server(int port, int numClientMax) throws IOException {
		this(port, numClientMax, false);
	}

	public Server(int port, int numClientMax, boolean debug) throws IOException {
		this.port = port;
		this.debug = debug;

		server = new ServerSocket(port, numClientMax);
	}

	public Socket Connect() throws IOException {
		sock = server.accept();
		String address = sock.getInetAddress().getHostName();

		if (this.debug) {
			System.out.println("Client: opening socket to " + address
					+ " on port " + port);
		}
		
		return sock;
	}

	public void Close() throws IOException {
		sock.close();

		if (this.debug) {
			System.out.println("Client: closing socket");
		}
	}
}

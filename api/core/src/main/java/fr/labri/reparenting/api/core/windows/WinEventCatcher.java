package fr.labri.reparenting.api.core.windows;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import fr.labri.reparenting.api.core.windows.WinWindowEvent.EventType;
import fr.labri.reparenting.api.shared.GenericObservable;
import fr.labri.reparenting.api.shared.StoppableThread;

public class WinEventCatcher extends GenericObservable<WinWindowEvent> {

	private StoppableThread clientMainThread;

	public WinEventCatcher(int port) throws IOException {
		this.clientMainThread = new ServerMainThread(port);
		clientMainThread.start();
	}

	public void stop() {
		clientMainThread.sendStopSignal();
	}

	class ServerMainThread extends StoppableThread {
		private Socket sock;

		public ServerMainThread(int port) throws IOException {
			super();
			this.sock = new Socket("localhost", port);
		}

		@Override
		public void run() {
			while (true) {
				try {
					System.out.println("Entered WinEventCatcher loop");
					DataInputStream dis;
					dis = new DataInputStream(sock.getInputStream());
					int type = dis.readInt();
					int handle = dis.readInt();
					EventType eventType = (type == 0) ? EventType.WINWINDOW_CREATION
							: EventType.WINWINDOW_DESTRUCTION;

					notifyObservers(new WinWindowEvent(eventType, handle));

					System.out.println("Received: type = " +
					type + " handle = " + handle);
				} catch (IOException e) {
					// end the thread
					return;
				}

				try {
					super.checkMessages();
				} catch (InterruptedException e) {
					break;
				}

			}
		}
	}
}

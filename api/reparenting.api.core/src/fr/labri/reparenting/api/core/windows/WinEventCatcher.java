package fr.labri.reparenting.api.core.windows;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import fr.labri.reparenting.api.core.windows.WinWindowEvent.EventType;
import fr.labri.reparenting.api.shared.GenericObservable;
import fr.labri.reparenting.api.shared.Server;
import fr.labri.reparenting.api.shared.StoppableThread;

public class WinEventCatcher extends GenericObservable<WinWindowEvent> {

	private StoppableThread serverMainThread;

	public WinEventCatcher(int port) throws IOException {
		final Server serv = new Server(port, 1, true);

		this.serverMainThread = new ServerMainThread(serv);
		serverMainThread.start();
	}

	public void stop() {
		serverMainThread.sendStopSignal();
	}

	class ServerMainThread extends StoppableThread {
		private Server serv;

		public ServerMainThread(Server serv) {
			super();
			this.serv = serv;
		}

		@Override
		public void run() {
			while (true) {
				try {
					final Socket s = serv.Connect();

					new Thread(new Runnable() {
						@Override
						public void run() {
							DataInputStream dis;
							try {
								dis = new DataInputStream(s.getInputStream());
								while (true) {
									int type = dis.readInt();
									int handle = dis.readInt();
									EventType eventType = (type == 0) ? EventType.WINWINDOW_CREATION
											: EventType.WINWINDOW_DESTRUCTION;
									
									notifyObservers(new WinWindowEvent(
											eventType, handle));

									// System.out.println("Received: type = " +
									// type + " handle = " + handle);
								}
							} catch (IOException e) {
								// end the thread
								return;
							}
						}
					}).start();

					super.checkMessages();
				} catch (IOException e) {
					continue;
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}

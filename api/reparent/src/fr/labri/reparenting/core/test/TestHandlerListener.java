package fr.labri.reparenting.core.test;

import java.io.IOException;
import fr.labri.reparenting.api.launchprocess.exception.AlreadyLaunchedException;
import fr.labri.reparenting.core.windows.WinEventCatcher;

public class TestHandlerListener {
	public static void main(String[] args) throws AlreadyLaunchedException, IOException, InterruptedException {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new WinEventCatcher(32000);
				} catch (IOException e) {
					System.err.println("ServerSocket error");
				}
			}
		});

		t.start();
//		new LauncherJavaAPI("win32/HookInstaller32.exe").launch("32000");
//		new LauncherJavaAPI("win64/HookInstaller64.exe").launch("32000");
		t.join();
	}
}

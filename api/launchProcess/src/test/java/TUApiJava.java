package fr.labri.reparenting.api.launchprocess.tests;

import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.labri.reparenting.api.launchprocess.apijava.LauncherJavaAPI;
import fr.labri.reparenting.api.launchprocess.apijava.listener.DestroyListener;
import fr.labri.reparenting.api.launchprocess.apijava.listener.OutstreamListener;
import fr.labri.reparenting.api.launchprocess.exception.AlreadyLaunchedException;
import fr.labri.reparenting.api.launchprocess.exception.NotLaunchedException;


public class TUApiJava implements DestroyListener, OutstreamListener {	
	static String defaultGoodShortcutPath = "V:/code/workspaces/production/api/stage.lib.reparent/xming/launchxming.lnk";
	static String defaultGoodExePath = "V:/code/workspaces/production/api/stage.lib.reparent/xming/Xming.exe";
	static String defaultBadPath = "";
	static boolean stopTestDestroy = false;
	static boolean stopHandleOutstream = false;
	static List<LauncherJavaAPI> launchers = new ArrayList<LauncherJavaAPI>();
	
	public static LauncherJavaAPI getInstanceExe() {
		return getInstance(defaultGoodExePath);
	}
	
	public static LauncherJavaAPI getInstanceShortcut() {
		return getInstance(defaultGoodShortcutPath);
	}
	
	public static LauncherJavaAPI getInstance(String path) {
		try {
			return new LauncherJavaAPI(path);
		} catch (IOException e) {
			return null;
		}
	}
	
	@Test(expected = IOException.class)
	public void testBadPath() throws IOException {
		new LauncherJavaAPI(defaultBadPath);
	}
	
	@Test
	public void testGoodPath() {
		try {
			new LauncherJavaAPI(defaultGoodExePath);
		} catch (IOException e) {
			fail("new Launcher() failed");
		}
	}
	
	@Test
	public void testLaunchDetachedProcess() {
		LauncherJavaAPI instanceShortcut = getInstanceShortcut();
		if(instanceShortcut == null)
			fail("Bad path to valid shortcut");
		
		try {
			instanceShortcut.launchDetached(); // :0 -internalwm
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLaunchProcess() {
		LauncherJavaAPI instanceExe = getInstanceExe();
		if(instanceExe == null)
			fail("Bad path to valid exe");
		
		try {
			instanceExe.launch(":1 -internalwm"); // :1 -internalwm
			launchers.add(instanceExe);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (AlreadyLaunchedException e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected = AlreadyLaunchedException.class)
	public void testAlreadyLaunched() throws AlreadyLaunchedException {
		LauncherJavaAPI instanceExe = getInstanceExe();
		if(instanceExe == null)
			fail("Bad path to valid exe");
		
		try {
			instanceExe.launch(":2 -internalwm"); // :2 -internalwm
			launchers.add(instanceExe);
			instanceExe.launch(null);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testStopProcess() throws NotLaunchedException {
		LauncherJavaAPI instanceExe = getInstanceExe();
		if(instanceExe == null)
			fail("Bad path to valid shortcut");
		
		try {
			instanceExe.launch(":3 -internalwm"); // :3 -internalwm
		} catch (IOException | AlreadyLaunchedException e) {
			fail(e.getMessage());
		}
		
		instanceExe.kill();
		
		assert(true);
	}
	
	@Test
	public void testDestroyListener() throws InterruptedException, NotLaunchedException {
		LauncherJavaAPI instanceExe = getInstanceExe();
		if(instanceExe == null)
			fail("Bad path to valid exe");
			
		instanceExe.addDestroyListener(this); // DestroyListener
		
		try {
			instanceExe.launch(":4 -internalwm"); // :4 -internalwm
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (AlreadyLaunchedException e) {
			fail(e.getMessage());
		}
		
		instanceExe.kill();
		
		while(! stopTestDestroy) {
			Thread.sleep(1000);
		}
	}

	@Test
	public void testHandleErrorStream() throws InterruptedException {
		LauncherJavaAPI instanceExe = getInstanceExe();
		if(instanceExe == null)
			fail("Bad path to valid exe");
		
		instanceExe.addErrorOutstreamListener(this); // ErrorHandle
		
		try {
			instanceExe.launch(":4 -internalwm"); // :4 -internalwm
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (AlreadyLaunchedException e) {
			fail(e.getMessage());
		}
	
		System.out.println("Quit Xming (:4) manually");
		
		while(! stopHandleOutstream) {
			Thread.sleep(1000);
		}	
	}

	@BeforeClass
	public static void init() {
		//
	}
	
	@AfterClass
	public static void closeAllLauncher() throws NotLaunchedException {
		System.out.println("Close :1 :2 launchers ***");
		for(LauncherJavaAPI l : launchers) {
			l.kill();
		}
	}
	
	@Override
	public void HandleProcessDestroy(int status) {
		System.out.println("HandleProcessDestroy");
		stopTestDestroy = true;
	}

	@Override
	public void HandleOutstreamNewline(String line) {
		stopHandleOutstream = true;
	}
}
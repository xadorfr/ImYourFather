package fr.labri.reparenting.plugin.core.extension;

import java.io.IOException;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import fr.labri.reparenting.api.launchprocess.apijava.LauncherJavaAPI;
import fr.labri.reparenting.api.launchprocess.exception.AlreadyLaunchedException;
import fr.labri.reparenting.api.core.Window;
import fr.labri.reparenting.api.core.windows.WinDaemonHook;
import fr.labri.reparenting.api.core.windows.WinReparentManager;
import fr.labri.reparenting.api.core.windows.WinRule;
import fr.labri.reparenting.api.core.x11.UnixX11SystemListener;
import fr.labri.reparenting.api.core.x11.X11ReparentManager;
import fr.labri.reparenting.api.core.x11.X11Rule;
import fr.labri.reparenting.api.core.x11.X11SystemListener;
import fr.labri.reparenting.api.core.x11.windows.WinX11SystemListener;
import fr.labri.reparenting.api.shared.GenericObservable;
import fr.labri.reparenting.api.shared.GenericObserver;
import fr.labri.reparenting.api.shared.SystemValidator;
import fr.labri.reparenting.plugin.core.Activator;
import fr.labri.reparenting.plugin.core.ErrorHandler;
import fr.labri.reparenting.plugin.core.view.ControlView;

public class RuleManager {
	private static int WIN32_HOOK_PORT = 32000;
	private static int WIN64_HOOK_PORT = 64000;
	
	private static String WINDAEMON_PLUGIN_ID = "cea.plugin.reparent.winhookdaemons";
	private static String WINDAEMON32_PATH = "win32/HookInstaller32.exe";
	private static String WINDAEMON64_PATH = "win64/HookInstaller64.exe";

	private static String XSERVER_ARGS = "-internalwm -notrayicon";
	private static String XSERVER_PLUGIN_ID = "cea.plugin.win.xserver";
	private static String XSERVER_PATH = "Xming/Xming.exe";

	private WinDaemonHook winHook;

	private WinReparentManager win32RM;
	private WinReparentManager win64RM;

	private X11SystemListener x11Hook;
	private X11ReparentManager x11RM;
	private Set<Integer> handledDisplays;

	private ControlView controlView;
	private GenericObserver<Window> destroyListener;

	LauncherJavaAPI hook32daemon;
	LauncherJavaAPI hook64daemon;
	LauncherJavaAPI xserver;

	public RuleManager(ControlView view) {
		winHook = null;
		win32RM = win64RM = null;

		x11Hook = null;
		x11RM = null;

		hook32daemon = hook64daemon = xserver = null;

		destroyListener = new GenericObserver<Window>() {
			@Override
			public void update(GenericObservable<Window> hook, Window win) {
				RCPParentWindowManager.destroyParent(win);
			}
		};
		controlView = view;
		this.handledDisplays = null;

		Activator.getDefault().addStopListener(new Observer() {
			@Override
			public void update(Observable nullObservable, Object nullObject) {
				closeDaemons();
			}
		});
	}

	public void addWin32Rule(WinRule rule) {
		if (winHook == null) {
			winHook = new WinDaemonHook(WIN32_HOOK_PORT);
			winHook.addObserver(destroyListener);
			winHook.install();
		}

		if (win32RM == null) {
			win32RM = new WinReparentManager();
			win32RM.addObserver(controlView.getWinEventListener());

			winHook.addReparentManager(win32RM);
			activateWin32Hook();
		}

		win32RM.addRule(rule);
	}

	public void addWin64Rule(WinRule rule) {
		if (winHook == null) {
			winHook = new WinDaemonHook(WIN64_HOOK_PORT);
			winHook.addObserver(destroyListener);
			winHook.install();
		}

		if (win64RM == null) {
			win64RM = new WinReparentManager();
			win64RM.addObserver(controlView.getWinEventListener());

			winHook.addReparentManager(win64RM);
			activateWin64Hook();
		}

		win64RM.addRule(rule);
	}

	public void addX11Rule(X11Rule rule) {

		if (this.handledDisplays == null) {
			handledDisplays = new HashSet<Integer>();
		}

		// if no Xserver is launched for the rule display : launch it
		if (!this.handledDisplays.contains(rule.getDisplayNum())) {
			xserver = launchRessource(XSERVER_PLUGIN_ID, XSERVER_PATH, ":"
					+ rule.getDisplayNum() + " " + XSERVER_ARGS);
			handledDisplays.add(rule.getDisplayNum());
		}

		// TODO : 1 hook = display - Map<DisplayNum, x11hook>
		if (x11Hook == null) {
			try {
				if(SystemValidator.isWindows()) {
				x11Hook = new WinX11SystemListener(rule.getDisplayNum(),
						rule.getScreenNum());
				} else {
					x11Hook = new UnixX11SystemListener(rule.getDisplayNum(),
							rule.getScreenNum());
				}
				x11Hook.addObserver(destroyListener);
				x11RM = new X11ReparentManager();
			} catch (Exception e) {
				ErrorHandler.handleException(e);
				return;
			}
			x11RM.addObserver(controlView.getX11EventListener());
			x11Hook.addReparentManager(x11RM);
			x11Hook.install();
		}

		x11RM.addRule(rule);
	}

	
	private void activateWin32Hook() {
		// launch win32's hook daemon
		hook32daemon = launchRessource(WINDAEMON_PLUGIN_ID, WINDAEMON32_PATH,
				String.valueOf(WIN32_HOOK_PORT));

	}

	private void activateWin64Hook() {
		// launch win64's hook daemon
		hook64daemon = launchRessource(WINDAEMON_PLUGIN_ID, WINDAEMON64_PATH,
				String.valueOf(WIN64_HOOK_PORT));
	}

	/* deletion */
	public void removeX11Rule(X11Rule rule) {
		x11RM.removeRule(rule);
	}

	public void removeWin32Rule(WinRule rule) {
		win32RM.removeRule(rule);
	}

	public void removeWin64Rule(WinRule rule) {
		win32RM.removeRule(rule);
	}

	private static LauncherJavaAPI launchRessource(String pluginId,
			String path, String args) {
		Bundle bundle = Platform.getBundle(pluginId);
		if (bundle == null) {
			return null;
		}

		String bundlePath = bundle.getLocation();

		// bundle location path are rcp specific - we must extract the path part
		Pattern regexp = Pattern.compile("(.*)(file:)(.*)");
		Matcher m = regexp.matcher(bundlePath);
		if (m.find()) {
			bundlePath = m.group(3);
		}

		String launchPath = bundlePath + path;
		LauncherJavaAPI launcher;
		try {
			launcher = new LauncherJavaAPI(launchPath);
			launcher.launch(args);
		} catch (IOException | AlreadyLaunchedException e) {
			return null;
		}
		return launcher;
	}

	private void closeDaemons() {
		try {
			if (hook32daemon != null) {
				hook32daemon.kill();
			}
		} catch (IllegalThreadStateException e) {
			//
		}

		try {
			if (hook64daemon != null) {
				hook64daemon.kill();
			}
		} catch (IllegalThreadStateException e) {
			//
		}
		try {
			if (xserver != null) {
				xserver.kill();
			}
		} catch (IllegalThreadStateException e) {
			//
		}

	}
}

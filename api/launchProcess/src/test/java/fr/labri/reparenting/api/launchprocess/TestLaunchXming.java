package fr.labri.reparenting.api.launchprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.Desktop;

/**
 * 
 * Essais sur les possibilit�s de lancement de Xming depuis Java
 * 
 * Conclusions
 * 2 possibilit�s propos�es selon le type de lancement souhait� : 
 * 		- Processus d�tach� de notre application : Desktop.getDesktop().open, avec un passage de param�tre possible par le lancement d'un "raccourci param�tr�"
 * 		- Processus avec cycle de vie et entr�e/sortie standards contr�l�s :  Runtime.getRuntime().exec + threads d�di�s aux traitements des flux d'entr�es/sorties de l'appli
 * 		  Evenement de fermeture du processus intercept� par un thread d�di� bloqu� sur waitFor().
 */
public class TestLaunchXming {
	
	public static void main(String[] args) throws InterruptedException {
		/* 2 techniques */
		// launch("V:/code/workspaces/reparent/32bits/stage.lib.reparent/xming/launchxming.lnk", true);
		launch("V:/code/workspaces/production/api/stage.lib.reparent/xming/Xming.exe :1 -internalwm -multimonitors -nowinkill -logverbose 0", false); // -notrayicon
	}
	
	public static Process launch(String path, boolean detached) throws InterruptedException {
		Process p = null;
		if(detached) {
			File f = new File(path);
			try {
				Desktop.getDesktop().open(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return p;
		}
		
		// else
		try {
			p = Runtime.getRuntime().exec(path);
			new Thread(new ProcessDestroyWatcher(p)).start();
			// new Thread(new ProcessHandler(p, ProcessHandler.typeHandler.STANDARD)).start(); // Xming n'utilise que la sortie d'erreur
			new Thread(new ProcessHandler(p, ProcessHandler.typeHandler.ERROR)).start();
			p.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return p;
	}
	
	public static class ProcessDestroyWatcher implements Runnable {
		private Process p;
		public ProcessDestroyWatcher(Process p) {
			this.p = p;
		}
		@Override
		public void run() {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						p.waitFor();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("** Process had ended !");
				}
			}).start();	
		}
	}
	
	public static class ProcessHandler implements Runnable {
		
		private Process process;
		private typeHandler type;
		public enum typeHandler {STANDARD, ERROR};
		
		public ProcessHandler(Process p, typeHandler type) {
			this.process = p;
			this.type = type;
		}
		
		public void run() {
			switch(this.type) {
			case STANDARD:
				handleStandard();
				break;
			case ERROR:
				handleError();
				break;
			}
			
		}
		
		private void handleError() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line;
				try {
					while((line = reader.readLine()) != null) {
						System.err.println(line);
						System.err.flush();
					}
				} finally {
					reader.close();
				}
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		private void handleStandard() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = "";
				try {
					while((line = reader.readLine()) != null) {
						System.out.println(line);
						System.out.flush();
					}
				} finally {
					reader.close();
				}
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
}

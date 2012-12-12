package fr.labri.reparenting.api.core;

import gnu.x11.*;
import gnu.x11.Window.Property;

public class TestEscher1 {
	public static void main(String[] args) {
		Display display = new Display("localhost", 0, 0);
		
	    Window.Attributes win_attr = new Window.Attributes ();
	    win_attr.set_background (display.default_white);
	    win_attr.set_border (display.default_black);
		Window window = new Window (display.default_root, 10, 10, 300, 300,
                 5, win_attr);

	    window.map ();
	    display.flush ();
	    
	    gnu.x11.Atom testAtom = gnu.x11.Atom.intern (display, "TESTOUILLE");
	    int data[] = {0};
	    window.change_property(gnu.x11.Window.REPLACE, testAtom, gnu.x11.Atom.INTEGER, 32, data, 0, 32);
	    display.flush ();
	    Property testProperty = window.get_property(false, testAtom, gnu.x11.Atom.INTEGER, 0, 1);
	    System.out.println(testProperty.string_value().length());
	    
//	    gnu.x11.Atom wm_state = gnu.x11.Atom.intern (display, "WM_STATE");
//	    Property p = window.get_property(false, wm_state, gnu.x11.Atom.INTEGER, 0, 2);
//	    System.out.println(p.string_value().length());
	}
}

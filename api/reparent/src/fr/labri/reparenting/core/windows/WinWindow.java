package fr.labri.reparenting.core.windows;

import fr.labri.reparenting.core.reparent.Window;
import fr.labri.reparenting.core.winapi.WinapiWrapper;

public class WinWindow extends Window {
	// private final static String WIN_TAG_REPARENTED = "WIN_REPARENTED"; 

	public WinWindow(long handle) {
		super(handle);
	}

	@Override
	protected void setParent(long handle) {
		WinapiWrapper.reparent(super.getHandle(), handle);
		//WinapiWrapper.SetProp(handle, WIN_TAG_REPARENTED, handle);
	}

	@Override
	public String getTitle() {
		return WinapiWrapper.GetWindowName(super.getHandle());
	}
	
	@Override
	public String getClassName() {
		return WinapiWrapper.GetClassName(super.getHandle());
	}
	

	@Override
	public void destroy() {
		WinapiWrapper.DestroyWindow(super.getHandle());
	}

	@Override
	public int getPid() {
		return WinapiWrapper.GetWindowPid(super.getHandle());
	}
}

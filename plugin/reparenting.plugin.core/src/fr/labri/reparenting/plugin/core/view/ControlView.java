package fr.labri.reparenting.plugin.core.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;

import fr.labri.reparenting.api.core.Rule;
import fr.labri.reparenting.api.core.RuleEvent;
import fr.labri.reparenting.api.core.windows.WinWindow;
import fr.labri.reparenting.api.core.x11.X11Window;
import fr.labri.reparenting.api.shared.GenericObservable;
import org.eclipse.swt.widgets.TableItem;

import fr.labri.reparenting.plugin.core.ExtensionManager;
import fr.labri.reparenting.plugin.core.WindowsEvent;
import fr.labri.reparenting.plugin.core.X11Event;
import fr.labri.reparenting.plugin.util.resource.ResourceManager;

/**
 * 
 * Control View of the plugin
 */

public class ControlView extends ViewPart {

	public static ControlView INSTANCE;
	private Table ruleTable;
	private Map<Rule<?>, TableItem> ruleLineMap;

	private Image winImg;
	private Image x11Img;

	private WindowsEvent windowsListener;
	private X11Event x11Listener;

	public ControlView() {
		super();
		this.winImg = ResourceManager.getPluginImage("cea.plugin.reparent",
				"icons/windows.png");
		this.x11Img = ResourceManager.getPluginImage("cea.plugin.reparent",
				"icons/x11.gif");
		this.ruleLineMap = new HashMap<>();
		INSTANCE = this;

		windowsListener = new WindowsEvent() {

			@Override
			public void update(
					GenericObservable<RuleEvent<WinWindow>> observable,
					RuleEvent<WinWindow> arg) {
				addOrUpdateLine(arg.getRule(), winImg);
			}
		};

		x11Listener = new X11Event() {
			@Override
			public void update(
					GenericObservable<RuleEvent<X11Window>> observable,
					RuleEvent<X11Window> event) {
				switch (event.getType()) {
				case ADD:
				case STATE_CHANGE:
					addOrUpdateLine(event.getRule(), x11Img);
					break;
				case REMOVE:
					removeLine(event.getRule());
				default:
					break;
				}

			}
		};
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());

		ruleTable = new Table(container, SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION);
		ruleTable.setLinesVisible(true);
		ruleTable.setHeaderVisible(true);

		TableColumn tblclmnType = new TableColumn(ruleTable, SWT.NONE);
		tblclmnType.setWidth(60);
		tblclmnType.setText("platform");
		tblclmnType.setResizable(false);

		TableColumn tblclmnId = new TableColumn(ruleTable, SWT.NONE);
		tblclmnId.setWidth(100);
		tblclmnId.setText("id");

		TableColumn tblclmnDescription = new TableColumn(ruleTable, SWT.NONE);
		tblclmnDescription.setWidth(300);
		tblclmnDescription.setText("description");

		TableColumn tblclmnStatus = new TableColumn(ruleTable, SWT.NONE);
		tblclmnStatus.setWidth(80);
		tblclmnStatus.setText("status");
		tblclmnStatus.setResizable(false);

		ExtensionManager.loadContributions(this);
	}

	@Override
	public void setFocus() {
		//
	}

	private void addOrUpdateLine(Rule<?> rule, Image icon) {
		TableItem tableItem = ruleLineMap.get(rule);
		;
		if (tableItem == null) {
			tableItem = new TableItem(ruleTable, SWT.NONE);
			ruleLineMap.put(rule, tableItem);
		}

		String[] line = { "", rule.getId(), rule.getDescription(),
				(rule.isActive()) ? "active" : "inactive" };
		tableItem.setText(line);
		if (rule.isActive()) {
			tableItem.setForeground(3,
					ResourceManager.getColor(SWT.COLOR_GREEN));
		} else {
			tableItem.setForeground(3, ResourceManager.getColor(SWT.COLOR_RED));
		}

		if (icon != null) {
			tableItem.setImage(0, icon);
		}
	}

	private void removeLine(Rule<?> rule) {
		TableItem item = ruleLineMap.get(rule);
		for (int i = 0; i < ruleTable.getItemCount(); i++) {
			if (ruleTable.getItem(i) == item) {
				ruleTable.remove(i);
				break;
			}
		}
	}

	public WindowsEvent getWinEventListener() {
		return this.windowsListener;
	}

	public X11Event getX11EventListener() {
		return this.x11Listener;
	}
}

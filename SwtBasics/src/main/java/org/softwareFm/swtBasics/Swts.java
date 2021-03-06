package org.softwareFm.swtBasics;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.indent.Indent;

public class Swts {

	public static void addGrabHorizontalAndFillGridDataToAllChildren(Composite composite) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.verticalSpacing = 1;
		layout.marginHeight = 1;
		composite.setLayout(layout);
		for (Control control : composite.getChildren()) {
			GridData data = makeGrabHorizonalAndFillGridData();
			control.setLayoutData(data);
		}
	}

	public static Label makeTitleLabel(Composite parent, ConfigForTitleAnd config, String key) {
		Label label = new Label(parent, SWT.NULL);
		String title = Resources.getTitle(config.resourceGetter, key);
		label.setText(title);
		label.setLayoutData(new RowData(config.titleWidth, config.titleHeight));
		return label;
	}

	public static TableEditor addEditor(Table table, int row, int col, Control control) {
		TableItem[] items = table.getItems();
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.setEditor(control, items[row], col);
		return editor;
	}

	public static RowLayout getHorizonalNoMarginRowLayout() {
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.marginWidth = 0;
		rowLayout.marginHeight = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		rowLayout.fill = false;
		rowLayout.justify = false;
		return rowLayout;
	}

	public static GridData makeGrabHorizonalAndFillGridData() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		return data;
	}

	public static String layoutAsString(Control control) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(control);
		Point size = control.getSize();
		buffer.append(" Size: " + size.x + ", " + size.y);
		if (control instanceof Composite)
			buffer.append(" Layout: " + ((Composite) control).getLayout());
		buffer.append(" LayoutData: " + control.getLayoutData());
		return buffer.toString();
	}

	public static void layoutDump(Control control) {
		layoutDump(control, new Indent());
	}

	public static void layoutDump(Control control, Indent indent) {
		System.out.println(indent + control.getClass().getSimpleName() + ": " + layoutAsString(control));
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			for (Control nested : composite.getChildren()) {
				Indent indented = indent.indent();
				layoutDump(nested, indented);
			}
		}
	}

	public static void display(String title, IFunction1<Composite, Composite> builder) {
		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setSize(300, 400);
			shell.setText(title);
			shell.setLayout(new FormLayout());
			Composite composite = builder.apply(shell);
			FormData fd = new FormData();
			fd.bottom = new FormAttachment(100, 0);
			fd.right = new FormAttachment(100, 0);
			fd.top = new FormAttachment(0, 0);
			fd.left = new FormAttachment(0, 0);
			composite.setLayoutData(fd);
			shell.open();
			Swts.layoutDump(shell);
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void removeAllChildren(Composite composite) {
		for (Control control : composite.getChildren())
			control.dispose();

	}

	public static List<Control> children(Composite composite) {
		return Arrays.asList(composite.getChildren());
	}

	public static List<Control> allChildren(Composite composite) {
		List<Control> result = Lists.newList();
		for (Control c : composite.getChildren()) {
			result.add(c);
			if (c instanceof Composite)
				result.addAll(allChildren((Composite) c));
		}
		return result;
	}

	public static void assertHasChildrenInOrder(Composite composite, Control... controls) {
		List<Control> children = children(composite);
		int lastIndex = -1;
		for (Control c : controls) {
			int index = children.indexOf(c);
			if (index == -1)
				Assert.fail("Could not find " + c + " in " + children);
			if (index < lastIndex)
				Assert.fail("Child " + index + "/" + c + " is in wrong order\nExpected " + controls + "\naActual: " + children);

		}

	}

	public static void setRowData(ConfigForTitleAnd config, ImageButton... buttons) {
		for (ImageButton button : buttons)
			button.setLayoutData(new RowData(config.buttonHeight, config.buttonHeight));
	}

	public static <C extends Control> void setRowDataFor(int width, int height, C... controls) {
		for (Control control : controls)
			control.setLayoutData(new RowData(width, height));

	}

	public static void makeButtonFromMainMethod(Composite composite, final Class<?> classWithMain) {
		Button button = new Button(composite, SWT.PUSH);
		button.setText(classWithMain.getSimpleName());
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread() {
					@Override
					public void run() {
						try {
							Method method = classWithMain.getMethod("main", String[].class);
							method.invoke(null, new Object[] { new String[0] });
						} catch (Exception e1) {
							throw WrappedException.wrap(e1);
						}
					}
				}.start();
			}
		});
	}

	public static Layout getGridLayoutWithoutMargins() {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		return gridLayout;
	}

}

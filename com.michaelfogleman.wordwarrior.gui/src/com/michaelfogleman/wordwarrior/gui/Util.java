package com.michaelfogleman.wordwarrior.gui;

import java.util.Scanner;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;

class Util {
	
	static void saveTableViewer(IMemento memento, TableViewer viewer) {
		TableColumn[] columns = viewer.getTable().getColumns();
		StringBuffer b = new StringBuffer();
		boolean set = false;
		for (int i = 0; i < columns.length; i++) {
			if (i > 0) b.append(' ');
			int width = columns[i].getWidth();
			b.append(width);
			if (width > 0) {
				set = true;
			}
		}
		if (set) {
			memento.putString("TableViewer", b.toString());
		}
	}
	
	static void loadTableViewer(IMemento memento, TableViewer viewer) {
		if (memento == null) return;
		String data = memento.getString("TableViewer");
		if (data == null) return;
		Scanner scanner = new Scanner(data).useDelimiter(" ");
		TableColumn[] columns = viewer.getTable().getColumns();
		boolean set = false;
		for (int i = 0; i < columns.length && scanner.hasNextInt(); i++) {
			int width = scanner.nextInt();
			columns[i].setWidth(width);
			set = true;
		}
		if (set) {
			viewer.getTable().setLayout(null);
		}
	}

}

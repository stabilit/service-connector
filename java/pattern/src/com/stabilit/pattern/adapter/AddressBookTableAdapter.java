package com.stabilit.pattern.adapter;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class AddressBookTableAdapter implements TableModel {

	AddressBook ab;

	public AddressBookTableAdapter(AddressBook ab) {
		this.ab = ab;
	}

	@Override
	public int getRowCount() {
		return ab.getSize();
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return null;
	}

	@Override
	public int getColumnCount() {
		return 0;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return null;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	}
}

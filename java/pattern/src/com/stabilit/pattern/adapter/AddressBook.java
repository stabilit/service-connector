package com.stabilit.pattern.adapter;

import java.util.List;

/*
 * Instead if implementing the TableModel interface directly in AddressBook, the
 * Adapter Class AddressBookTableAdapter does it.
 */
public class AddressBook {
	List<String> personList;

	public int getSize() {
		return personList.size();
	}

	public void addPerson(String name) {
		personList.add(name);
	}

	public String getPerson(int index) {
		return personList.get(index);
	}
}

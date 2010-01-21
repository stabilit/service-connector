package com.stabilit.jmx.dmbean;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.lang.reflect.*;
import java.rmi.*;

public class ShowRMIRegistry implements ActionListener, ListSelectionListener {
	// updates details with given resource
	public void updateDetails(String url) {
		String text;

		try {
			Object o = (Object)Naming.lookup(url);
			text = HTMLToolKit.createClassDetails(o.getClass());
		} catch (Exception e) {
			text = e.getMessage();
		}

		this.details.setText(text);
	}

	// updates list with given registry
	public void updateList(String reg) {
		try {
			String[] list = Naming.list(reg);
			for (int i = 0; i < list.length; i++) {
				this.list.setListData(list);
			}
			this.details.setText("");
		} catch (Exception e) {
			this.details.setText(e.getMessage());
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		updateDetails((String)list.getSelectedValue());
	}

	public void actionPerformed(ActionEvent e) {
		updateList(this.registry.getText());
	}

	protected GridBagConstraints createConstraints(int x, int y, int w, int h, double wx, double wy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.BOTH;
		gbc.insets = new Insets(2, 2, 0, 0);
		gbc.gridx = x; gbc.gridy = y;
		gbc.gridwidth = w; gbc.gridheight = h;
		gbc.weightx = wx; gbc.weighty = wy;
		return (gbc);
	}

	protected Component createComponents() {
		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pane.setLayout(new GridBagLayout());
		
		this.registry = new JTextField("rmi://hal/");
		pane.add(this.registry, createConstraints(0, 0, 8, 1, 1, 0));

		JButton update = new JButton("Retrieve");
		update.addActionListener(this);
		pane.add(update, createConstraints(8, 0, 2, 1, 0, 0));

		this.list = new JList();
		this.list.addListSelectionListener(this);
		pane.add(new JScrollPane(this.list), createConstraints(0, 1, 10, 1, 1, 0));

		this.details = new JEditorPane("text/html", "");
		this.details.setEditable(false);
		pane.add(new JScrollPane(this.details), createConstraints(0, 2, 10, 6, 1, 1));

		return ((Component)pane);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("RMI Registry Viewer");
		ShowRMIRegistry app = new ShowRMIRegistry();
		frame.getContentPane().add(app.createComponents());
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.setSize(700,500);
		frame.setVisible(true);
	}


	private JTextField registry;
	private JList list;
	private JEditorPane details;
}

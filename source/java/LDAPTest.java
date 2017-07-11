/*---------------------------------------------------------------
*  Copyright 2011 by the Radiological Society of North America
*
*  This source software is released under the terms of the
*  RSNA Public License (http://mirc.rsna.org/rsnapubliclicense)
*----------------------------------------------------------------*/

package org.rsna.ldap;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import org.w3c.dom.*;

/**
 * A test program for trying to connect to an LDAP server.
 */
public class LDAPTest extends JFrame {

	MainPanel mainPanel;
	ColorPane cp;
	Color bgColor = new Color(0xc6d8f9);

	Row initialContextFactory;
	Row providerURL;
	Row securityAuthentication;
	Row securityPrincipal;
	PasswordRow securityCredentials;
	Row baseDN;
	Row searchFilter;
	Row returnedAttributes;

	public static void main(String args[]) {
		new LDAPTest();
	}

	/**
	 * Class constructor.
	 */
	public LDAPTest() {
		super();

		setTitle("LDAP Test Utility");

		mainPanel = new MainPanel();
		cp = new ColorPane();
		cp.setScrollableTracksViewportWidth(false);

		JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT);
		splitPane.setContinuousLayout(true);
		splitPane.setTopComponent(mainPanel);
		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(cp);
		splitPane.setBottomComponent(jsp);

		this.getContentPane().add( splitPane, BorderLayout.CENTER );
		this.addWindowListener(new WindowCloser(this));

		pack();
		positionFrame();
		setVisible(true);
		splitPane.setDividerLocation(-1);
	}

	private void positionFrame() {
		Toolkit tk = getToolkit();
		Dimension scr = tk.getScreenSize ();
		setSize( 1000, 900 );
		int x = (scr.width - getSize().width)/2;
		int y = (scr.height - getSize().height)/2;
		setLocation( new Point(x,y) );
	}

    class WindowCloser extends WindowAdapter {
		public WindowCloser(JFrame parent) { }
		public void windowClosing(WindowEvent evt) {
			System.exit(0);
		}
    }

	class MainPanel extends JPanel implements ActionListener {

		JButton browse;
		JLabel tomcat;
		JButton start;
		File tomcatDir = null;
		File ctpDir = null;

		public MainPanel() {
			super();
			setLayout(new BorderLayout());
			setBackground(bgColor);

			add( new TitlePanel(), BorderLayout.NORTH );

			JPanel centerPanel = new CenterPanel();
			JPanel centerLR = new JPanel();
			centerLR.add(centerPanel);
			centerLR.setBackground(bgColor);
			add( centerLR, BorderLayout.CENTER );

			JPanel footer = new FooterPanel();
			start = new JButton("Connect");
			start.setEnabled(true);
			start.addActionListener(this);
			footer.add(start);
			add( footer, BorderLayout.SOUTH );

		}

		public void actionPerformed(ActionEvent event) {
			if (event.getSource().equals(start)) {
				cp.clear();
				String icf = initialContextFactory.tf.getText();
				String url = providerURL.tf.getText();
				String sa = securityAuthentication.tf.getText();
				String sp = securityPrincipal.tf.getText();
				String sc = securityCredentials.tf.getText();
				String dn = baseDN.tf.getText();
				String sf = searchFilter.tf.getText();
				String ra = returnedAttributes.tf.getText();

				String result = LDAPUtil.connect(icf, url, sa, sp, sc, dn, sf, ra);
				cp.append(result);
			}
		}
	}

	class TitlePanel extends JPanel {
		public TitlePanel() {
			super();
			setBackground(bgColor);
			JLabel title = new JLabel("LDAP Test Utility");
			title.setFont( new Font( "SansSerif", Font.BOLD, 24 ) );
			title.setForeground( Color.BLUE );
			add(title);
			setBorder(BorderFactory.createEmptyBorder(20,0,20,0));
		}
	}

	class CenterPanel extends RowPanel {
		public CenterPanel() {
			super("LDAP Parameters");
			setBackground(bgColor);

			addRow( initialContextFactory = new Row("Initial Context Factory:", "com.sun.jndi.ldap.LdapCtxFactory") );
			addRow( providerURL = new Row("Provider URL:", "ldaps://ldap.myuniversity.edu:636") );
			addRow( securityAuthentication = new Row("Security Authentication:", "simple") );
			addRow( securityPrincipal = new Row("Security Principal:", "cn=username, ou=people, dc=myuniversity, dc=edu") );
			addRow( securityCredentials = new PasswordRow("Security Credentials:", "password") );
			addRow( baseDN = new Row("Base DN:", "") );

			addRow( searchFilter = new Row("Search Filter:", "(&(objectClass=user)(sAMAccountName=username))") );
			addRow( returnedAttributes = new Row("Returned Attributes:", "memberof") );
		}
	}

	class FooterPanel extends JPanel {
		public FooterPanel() {
			super();
			setBackground(bgColor);
			setBorder(BorderFactory.createEmptyBorder(10,0,25,0));
		}
	}

	class RowPanel extends JPanel {
		public RowPanel() {
			super();
			setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
			setLayout(new RowLayout());
		}
		public RowPanel(String name) {
			super();
			Border empty = BorderFactory.createEmptyBorder(10,10,10,10);
			Border line = BorderFactory.createLineBorder(Color.GRAY);
			Border title = BorderFactory.createTitledBorder(line, name);
			Border compound = BorderFactory.createCompoundBorder(title, empty);
			setBorder(compound);
			setLayout(new RowLayout());
		}
		public void addRow(PasswordRow row) {
			add(row.label);
			add(row.tf);
			add(RowLayout.crlf());
		}
		public void addRow(Row row) {
			add(row.label);
			add(row.tf);
			add(RowLayout.crlf());
		}
		public void addRow(CBRow row) {
			add(row.label);
			add(row.cb);
			add(RowLayout.crlf());
		}
		public void addRow(int height) {
			add(Box.createVerticalStrut(height));
			add(RowLayout.crlf());
		}
		public void addRow(JLabel label) {
			add(label);
			add(RowLayout.crlf());
		}
	}

	class Row {
		public RowLabel label;
		public JTextField tf;
		public Row(String name) {
			label = new RowLabel(name);
			tf = new RowTextField(100);
		}
		public Row(String name, String value) {
			label = new RowLabel(name);
			tf = new RowTextField(100);
			tf.setText(value);
		}
	}

	class PasswordRow {
		public RowLabel label;
		public JTextField tf;
		public PasswordRow(String name) {
			label = new RowLabel(name);
			tf = new RowPasswordField(100);
		}
		public PasswordRow(String name, String value) {
			label = new RowLabel(name);
			tf = new RowPasswordField(100);
			tf.setText(value);
		}
	}

	class CBRow {
		public RowLabel label;
		public JCheckBox cb;
		public CBRow(String name, boolean checked) {
			label = new RowLabel(name);
			cb = new JCheckBox();
			cb.setBackground(bgColor);
			cb.setSelected(checked);
		}
	}

	class RowLabel extends JLabel {
		public RowLabel(String s) {
			super(s);
			Dimension d = this.getPreferredSize();
			d.width = 140;
			this.setPreferredSize(d);
		}
	}

	class RowTextField extends JTextField {
		public RowTextField(int size) {
			super(size);
			setFont( new Font("Monospaced",Font.PLAIN,12) );
		}
	}

	class RowPasswordField extends JPasswordField {
		public RowPasswordField(int size) {
			super(size);
			setFont( new Font("Monospaced",Font.PLAIN,12) );
		}
	}

}

package org.nullbool.pi.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.nullbool.pi.installer.Blob;
import org.nullbool.pi.installer.RemoteBlob;
import org.nullbool.pi.installer.Util;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 19:43:24
 */
public class BootUI extends JFrame {
	private static final long serialVersionUID = 914353230578179758L;

	private final JTable table;
	private final DefaultTableModel model;
	private final NewItemFrame newItemFrame;
	private final OptionsFrame optionsFrame;
	
	public BootUI() {
		super("Blob Management Development Enviroment 0.1.1.r471");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(500, 300));
		model = new DefaultTableModel(new Object[]{"Id", "Base", "URL", "Hash"}, 0);
		table = new JTable(model);
		newItemFrame = new NewItemFrame();
		optionsFrame = new OptionsFrame();
		optionsFrame.init();
		
		JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(sp);
		
		JMenuBar mb = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(BootUI.this, "Are you sure you want to reset the workspace?", "Confirm", JOptionPane.YES_NO_OPTION);
		    	if(res == JOptionPane.YES_OPTION) {
					clear();
		    	}
			}
		});
		JMenuItem openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = openDialog(false);
				if(f != null) {
					clear();
					try {
						String s = read(f);
						RemoteBlob[] blobs = Util.GSON.fromJson(s, RemoteBlob[].class);
						for(RemoteBlob b : blobs) {
							appendBlob(new MutableRemoteBlobData(b));
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JMenuItem importMenuItem = new JMenuItem("Import");
		importMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = openDialog(false);
				if(f != null) {
					try {
						String s = read(f);
						RemoteBlob[] blobs = Util.GSON.fromJson(s, RemoteBlob[].class);
						for(RemoteBlob b : blobs) {
							appendBlob(new MutableRemoteBlobData(b));
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JMenuItem exportMenuItem = new JMenuItem("Export");
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = openDialog(true);
				if(f != null) {
					try {
						int len = table.getRowCount();
						Blob[] blobs = new RemoteBlob[len];
						for(int i=0; i < len; i++) {
							String id = (String) table.getValueAt(i, 0);
							String base = (String) table.getValueAt(i, 1);
							String url = (String) table.getValueAt(i, 2);
							String hash = (String) table.getValueAt(i, 3);
							RemoteBlob b = new RemoteBlob(base, id, hash, new URL(url));
							blobs[i] = b;
						}
						String s = Util.GSON.toJson(blobs);
						try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
							bw.append(s);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		file.add(newMenuItem);
		file.add(openMenuItem);
		file.add(importMenuItem);
		file.add(exportMenuItem);
		mb.add(file);
		
		JMenu edit = new JMenu("Edit");
		JMenuItem addMenuItem = new JMenuItem("Add");
		addMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newItemFrame.setLocationRelativeTo(null);
				newItemFrame.setVisible(true);
			}
		});
		JMenuItem removeMenuitem = new JMenuItem("Remove");
		removeMenuitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int sc = table.getSelectedRowCount();
				if(sc == 0) {
					error("No selected items.");
					return;
				}
				
				int[] rows = table.getSelectedRows();
				for(int row : rows) {
					model.removeRow(table.convertRowIndexToModel(row));
				}
				table.clearSelection();
			}
		});

		JMenuItem optionsMenuItem = new JMenuItem("Options");
		optionsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionsFrame.setLocationRelativeTo(null);
				optionsFrame.setVisible(true);
			}
		});
		
		JMenuItem rehashMenuItem = new JMenuItem("Rehash");
		rehashMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				if(index == -1) {
					error("No row selected.");
					return;
				}
				File f = openDialog(false);
				if(f != null) {
					try {
						String hash = Util.sha1(new FileInputStream(f));
						table.setValueAt(hash, index, 3);
					} catch (Exception e1) {
						e1.printStackTrace();
						error(e1.getMessage());
					}
					
				} else {
					error("No file chosen.");
					return;
				}
			}
		});
		
		edit.add(addMenuItem);
		edit.add(removeMenuitem);
		edit.add(optionsMenuItem);
		edit.add(rehashMenuItem);
		
		mb.add(edit);
		
		
		setJMenuBar(mb);
		
		addWindowListener(new WindowAdapter() {
		    @Override
			public void windowClosing(WindowEvent e) {
		    	int res = JOptionPane.showConfirmDialog(BootUI.this, "Are you sure you want to exit?", "Confirm", JOptionPane.YES_NO_OPTION);
		    	if(res == JOptionPane.YES_OPTION) {
		    		dispose();
		    		System.exit(1);
		    	}
		    }
		});
	}

	private void clear() {
		for(int i=model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
	
	private void appendBlob(MutableRemoteBlobData b) {
		model.addRow(new Object[]{b.getId(), b.getBase(), b.getURL().toExternalForm(), b.getHash()});
	}
	
	private static String read(File f) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			StringBuilder sb = new StringBuilder();
			while((line = br.readLine()) != null) {
				sb.append(line).append('\n');
			}
			return sb.toString();
		}
	}
	
	private File lastDir;
	
	private File openDialog(boolean save) {
		JFileChooser fc = new JFileChooser(lastDir);
		fc.setPreferredSize(new Dimension(1000, 600));
		int res;
		if(save) {
			res = fc.showSaveDialog(this);
		} else {
			res = fc.showOpenDialog(this);
		}
		
		if(res == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if(file != null) {
				if(file.isDirectory()) {
					lastDir = file;
				} else {
					lastDir = file.getParentFile();
				}
			}
			return file;
		} else {
			return null;
		}
	}
	
	private void error(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				BootUI ui = new BootUI();
				ui.pack();
				ui.setLocationRelativeTo(null);
				ui.setVisible(true);
			}
		});
	}
	
	public class OptionsFrame extends JFrame {
		private static final long serialVersionUID = -462922642712522691L;

		private JTextArea ta;
		private Map<String, String> config = new HashMap<String, String>();
		
		public OptionsFrame() {
			super("Options");
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setPreferredSize(new Dimension(400, 300));
			
			JButton button = new JButton("Accept");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parse();
					setVisible(false);
				}
			});
			add(button, BorderLayout.SOUTH);
			
			ta = new JTextArea();
			add(ta);
			
			setResizable(false);
			pack();
		}
		
		public void init() {
			StringBuilder sb = new StringBuilder();
			sb.append("default_base").append("=").append("\n");
			sb.append("default_remote").append("=").append("\n");
			ta.setText(sb.toString());
			parse();
		}
		
		public String get(String key) {
			return config.get(key);
		}
		
		private void parse() {
			String s = ta.getText();
			if(s != null) {
				String[] lines = s.split("\n");
				for(String l : lines) {
					if(l.contains("=")) {
						String[] parts = l.split("=");
						config.put(parts[0], parts.length > 1 ? parts[1] : "");
					}
				}
			}
		}
		
		@Override
		public void setVisible(boolean b) {
			String s = ta.getText();
			if(s == null || s.isEmpty()) {
				init();
			}
			
			if(!b) {
				parse();
			}
			
			super.setVisible(b);
		}
	}
	
	public class NewItemFrame extends JFrame {
		private static final long serialVersionUID = 8537405455421567763L;
		
		private File currentFile;
		private JButton button;
		private JTextField baseTextField;
		private JTextField idTextField;
		private JTextField urlTextField;

		public NewItemFrame() {
			super("Add");
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setPreferredSize(new Dimension(380, 143));
			JPanel cp = new JPanel(null);
			setContentPane(cp);

			///////////////////////////////////////
			JLabel l4 = new JLabel("Base: ");
			l4.setBounds(10, 10, 35, 15);
			cp.add(l4);
			
			baseTextField = new JTextField(20);
			baseTextField.setBounds(50, 10, 250, 20);
			cp.add(baseTextField);
			
			///////////////////////////////////////
			JLabel l1 = new JLabel("Id: ");
			l1.setBounds(10, 35, 35, 15);
			cp.add(l1);
			
			idTextField = new JTextField(20);
			idTextField.setBounds(50, 35, 250, 20);
			cp.add(idTextField);
			
			///////////////////////////////////////
			JLabel l2 = new JLabel("URL: ");
			l2.setBounds(10, 60, 35, 15);
			cp.add(l2);
			
			urlTextField = new JTextField(20);
			urlTextField.setBounds(50, 60, 250, 20);
			cp.add(urlTextField);
			
			/////////////////////////////////////////
			JLabel l3 = new JLabel("File: ");
			l3.setBounds(10, 85, 35, 15);
			cp.add(l3);
			
			button = new JButton("Choose");
			button.setFocusable(false);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentFile = openDialog(false);
					if(currentFile != null) {
						button.setText(currentFile.getAbsolutePath());
						String idtext = idTextField.getText();
						if(idtext == null || idtext.isEmpty()) {
							idTextField.setText(currentFile.getName());
						}
						
						String remotetext = urlTextField.getText();
						if(remotetext != null && optionsFrame.get("default_remote").equals(remotetext)) {
							if(!remotetext.endsWith("/")) {
								remotetext += "/";
							}
							
							remotetext += currentFile.getName();
							urlTextField.setText(remotetext);
						}
					}
				}
			});
			button.setBounds(50, 85, 250, 20);
			cp.add(button);
			
			///////////////////////////////////////////
			JButton ok = new JButton("Ok");
			ok.setFocusable(false);
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						String base = baseTextField.getText();
						String id = idTextField.getText();
						String url = urlTextField.getText();
						if(id == null || id.isEmpty()) {
							error("Invalid id.");
							return;
						}
						if(url == null || url.isEmpty()) {
							error("Invalid url.");
							return;
						}
						if(currentFile == null || !currentFile.exists()) {
							error("Invalid file.");
							return;
						}
						
						String hash = Util.sha1(new FileInputStream(currentFile));
						MutableRemoteBlobData blob = new MutableRemoteBlobData(base, id, hash, new URL(url));
						appendBlob(blob);
						
						setVisible(false);
					} catch(Throwable t) {
						t.printStackTrace();
						error("Error: " + t.getMessage());
					}
				}
			});
			ok.setBounds(310, 10, 50, 95);
			cp.add(ok);
			
			setResizable(false);
			pack();
		}
		
		@Override
		public void setVisible(boolean b) {
			if(b) {
				currentFile = null;
				idTextField.setText("");
				baseTextField.setText(optionsFrame.get("default_base"));
				urlTextField.setText(optionsFrame.get("default_remote"));
				button.setText("Choose");
			}
			super.setVisible(b);
		}
	}
	
	public static class MutableRemoteBlobData {
		private String base;
		private String id;
		private String hash;
		private URL url;
		
		public MutableRemoteBlobData(RemoteBlob b) {
			base = b.getBase();
			id = b.getId();
			hash = b.getHash();
			url = b.getURL();
		}

		public MutableRemoteBlobData(String base, String id, String hash, URL url) {
			this.base = base;
			this.id = id;
			this.hash = hash;
			this.url = url;
		}

		public String getBase() {
			return base;
		}

		public void setBase(String base) {
			this.base = base;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getHash() {
			return hash;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

		public URL getURL() {
			return url;
		}

		public void setURL(URL url) {
			this.url = url;
		}
		
		@Override
		public String toString() {
			return id;
		}
	}
}
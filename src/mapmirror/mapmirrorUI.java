package mapmirror;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mmconfig.*;
import qmap2.QMapFile;

public class mapmirrorUI implements WindowListener, ActionListener, ListSelectionListener {
	public static String VERSION = "0.2j.g";
	
	public mapmirror mapMirror = new mapmirror();
	public mmuConfig config = new mmuConfig();
	public ConfigFile currentCfg = null;
	public QMapFile currentMap = null;
	
	public static ImageIcon ICON_ADD = getScaledIcon("icons/Add.png",16,16), ICON_REMOVE = getScaledIcon("icons/Remove.png",16,16), ICON_OPEN = getScaledIcon("icons/Open.png",16,16), ICON_COPY = getScaledIcon("icons/Copy.png",16,16);
	
	JFrame frame = null;
	JMenuBar menubar = null;
	JToolBar toolbar = null;
	JPanel statusBar = null, listBar = null;
	JLabel statusLabel = null;
	JList<String> cfgList = null;
	JButton button_list_add = null, button_list_remove = null, button_list_copy = null, button_list_open = null;

	public mapmirrorUI() {
	}
	
	public void loadConfig(String filename) {
		if(filename != null) {
			config.filename = filename;
		}
		config.load();
	}
	
	public void show() {
		frame = new JFrame();
		frame.setSize(config.windowSize);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Map Mirror (and other stuff) " + VERSION);
		frame.setIconImage(null);
		frame.setLocation(config.windowLocation);
		frame.addWindowListener(this);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.add(statusBar, BorderLayout.SOUTH);
		statusBar.setPreferredSize(new Dimension(frame.getWidth(), 16));
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		statusLabel = new JLabel("Map Mirror started");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusBar.add(statusLabel);
		JPanel p = new JPanel();
		p.setBackground(new Color(255, 0, 0));
		cfgList = new JList<>();
		Dimension d = new Dimension(config.listWidth, 10);
		cfgList.setPreferredSize(d);
		cfgList.setSize(d);
		cfgList.setMinimumSize(new Dimension(10,10));
//		cfgList.setBackground(new Color(0, 200, 0));
		cfgList.addListSelectionListener(this);
		cfgList.setBorder(new BevelBorder(BevelBorder.LOWERED));
		refreshList();
		
		button_list_add = new JButton(ICON_ADD);
		button_list_add.setToolTipText("Add");
		button_list_add.addActionListener(this);
		button_list_copy = new JButton(ICON_COPY);
		button_list_copy.setToolTipText("Duplicate");
		button_list_copy.addActionListener(this);
		button_list_open = new JButton(ICON_OPEN);
		button_list_open.setToolTipText("Open...");
		button_list_open.addActionListener(this);
		button_list_remove = new JButton(ICON_REMOVE);
		button_list_remove.setToolTipText("Remove");
		button_list_remove.addActionListener(this);
		JPanel listButtons = new JPanel();
		listButtons.setLayout(new FlowLayout());
		listButtons.add(button_list_add);
		listButtons.add(button_list_copy);
		listButtons.add(button_list_open);
		listButtons.add(button_list_remove);
		
		listBar = new JPanel();
		listBar.setLayout(new BorderLayout());
		listBar.add(new JLabel("Presets"), BorderLayout.NORTH);
		listBar.add(cfgList, BorderLayout.CENTER);
		listBar.add(listButtons, BorderLayout.SOUTH);
		
		JSplitPane split1 = new JSplitPane();
		split1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split1.add(listBar, JSplitPane.LEFT);
		
		split1.add(p, JSplitPane.RIGHT);
		frame.add(split1, BorderLayout.CENTER);
		refreshPanel();
	}
	
	public void refreshList() {
		if(cfgList == null || config == null || config.configFiles == null) return;
		cfgList.setListData(config.configFiles);
		
		//for(int i = 0; i < config.configFiles.size(); i++) {
		//	cfgList.add(new JLabel(config.configFiles.get(i)));
		//}
	}
	
	public void refreshPanel() {
		if(cfgList.getSelectedIndex() > -1) {
			String val = cfgList.getSelectedValue();
			if(val != null && !(currentCfg != null && val.equals(currentCfg.filename))) {
				currentCfg = new ConfigFile();
				System.out.println("Loading config");
				currentCfg.load(val);
			}
			button_list_copy.setEnabled(true);
			button_list_remove.setEnabled(true);
		} else {
			currentCfg = null;
			button_list_copy.setEnabled(false);
			button_list_remove.setEnabled(false);
		}
	}
	
	public static ImageIcon getScaledIcon(String filename, int w, int h) {
		return new ImageIcon(getScaledImage(new ImageIcon(filename).getImage(), w, h));
	}
	
	public static Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}

	
	public static void main(String[] args) {
		mapmirrorUI mmu = new mapmirrorUI();
		mmu.loadConfig(null);
		mmu.show();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		config.windowSize = frame.getSize();
		config.windowLocation = frame.getLocation();
		config.listWidth = cfgList.getWidth();
		config.save();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button_list_add) {
			JOptionPane.showInputDialog(frame, "Enter the name", "New Config", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent e) {
		refreshPanel();
	}
}

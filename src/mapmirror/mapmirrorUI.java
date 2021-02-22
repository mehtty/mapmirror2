package mapmirror;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;

import mmconfig.*;
import qmap2.QMapFile;
import qmap2.QVector;

public class mapmirrorUI implements WindowListener, ActionListener, ListSelectionListener, FocusListener, MouseListener, DocumentListener, ItemListener {
	public static String VERSION = "0.2j.g";
	public final static String NOT_SELECTED = "No Config Selected";
	public final static String[] QUERY_OPTIONS = {"Exact", "Any", "Not"};
	public final static String[] RESULT_OPTIONS = {"Update", "Insert", "Delete"};
	
	public mapmirror mapMirror = new mapmirror();
	public mmuConfig config = new mmuConfig();
	public ConfigFile currentCfg = null, runCfg = new ConfigFile();
	public QMapFile currentMap = null, runMap = new QMapFile();
	public TextureReplacement currentTextureReplacement = null;
	public FieldReplacement currentFieldReplacement = null;
	public boolean unsaved = false;
	
	public static ImageIcon 
		ICON_MAIN = new ImageIcon("icons/Pantone.png"),
		ICON_ADD = getScaledIcon("icons/Add.png",16,16), 
		ICON_REMOVE = getScaledIcon("icons/Remove.png",16,16), 
		ICON_OPEN = getScaledIcon("icons/Open.png",16,16), 
		ICON_COPY = getScaledIcon("icons/Copy.png",16,16), 
		ICON_CLEAR = getScaledIcon("icons/Delete.png",16,16),
		ICON_OK = getScaledIcon("icons/Ok.png",16,16),
		ICON_CANCEL = getScaledIcon("icons/Cancel.png",16,16),
		ICON_INFO = getScaledIcon("icons/Info.png",16,16),
		ICON_SAVE = getScaledIcon("icons/Save.png",16,16),
		ICON_SAVE_AS = getScaledIcon("icons/Save as.png",16,16),
		ICON_NEW = getScaledIcon("icons/New file.png",16,16),
		ICON_EDIT = getScaledIcon("icons/Modify.png",16,16),
		ICON_HIERACHY = getScaledIcon("icons/Hierarchy.png",16,16),
		ICON_HIERACHY_OPEN = getScaledIcon("icons/HierarchyOpen.png",16,16),
		ICON_RUN = getScaledIcon("icons/Lightning.png",16,16),
		ICON_UP = getScaledIcon("icons/Up.png",16,16),
		ICON_DOWN = getScaledIcon("icons/Down.png",16,16);

	JFrame frame , frame_run;
	JDialog frame_textures , frame_fields , frame_query , frame_result ;
	JMenuBar menubar ;
	JMenu menu_file , menu_map , menu_help ;
	JMenuItem menu_file_add , menu_file_save , menu_file_saveas , menu_file_open , menu_file_quit ;
	JMenuItem menu_map_add , menu_map_open ;
	JMenuItem menu_help_about ;
	JToolBar toolbar ;
	JPanel statusBar , listBar , cfgPanel ;
	JLabel statusLabel , selectedConfigName ;
	JList<String> list_configs , list_textures , list_fields ;
	DefaultListModel<String> listModel_configs , listModel_textures , listModel_fields ;
	JButton button_list_add , button_list_remove , button_list_copy , button_list_open , button_cfg_save , button_cfg_saveas , button_cfg_new , button_cfg_open , button_run;
	JButton button_map_browse , button_map_clear , button_outmap_browse , button_outmap_clear ;
	JTextField text_map , text_outmap ;
	JFileChooser file_chooser ;
	JCheckBox check_rotate , check_overlay , check_delete_ent ;
	JTextField text_translate ;
	JTextField text_texture_old , text_texture_new ;
	JTextField text_query_name , text_query_value ;
	JComboBox<String> combo_query_action, combo_result_action;
	JButton button_textures_add , button_textures_remove , button_textures_edit ;
	JButton button_fields_add , button_fields_remove , button_fields_edit, button_fields_up, button_fields_down ;
	private DefaultListModel<FieldCriteria> queryListModel;
	private JList<FieldCriteria> queryList;
	private DefaultListModel<FieldResult> resultListModel;
	private JList<FieldResult> resultList;
	private JButton button_query_add;
	private JButton button_query_edit;
	private JButton button_query_remove;
	private JButton button_result_add;
	private JButton button_result_edit;
	private JButton button_result_remove;
	private FieldCriteria currentQuery;
	private FieldResult currentResult;
	private JTextField text_result_name;
	private JTextField text_result_value;
	private JTextField text_run_map;
	private JTextField text_run_outmap;
	private JTextField text_run_config;
	JLabel label_run_map_status, label_run_outmap_status, label_run_config_status, label_run_status;
	JProgressBar progress_run;
	JTextArea text_run_log;
	private JButton button_run_map_browse;
	private JButton button_run_outmap_browse;
	private JButton button_run_config_browse;
	
	public mapmirrorUI() {
	}
	
	public void loadConfig(String filename) {
		if(filename != null) {
			config.filename = filename;
		}
		config.load();
	}
	
	public void generateTitle() {
		String t = "Map Mirror (and other stuff) " + VERSION;
		if(currentCfg != null && currentCfg.filename != null) {
			t = currentCfg.filename + " - " + t;
			if(unsaved) t = "*" + t;
		} else {
			t = "*<unsaved> - " + t;
		}
		frame.setTitle(t);
	}
	
	public void show() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
		frame.setIconImage(ICON_MAIN.getImage());
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		generateTitle();
		
		file_chooser = new JFileChooser(".");
		
		menubar = new JMenuBar();
		menu_file = new JMenu("File");
		menu_file.setMnemonic('f');
		menu_file.setSize(100, 100);
		menu_file_add = new JMenuItem("New", ICON_ADD);
		menu_file_add.setMnemonic('n');
		menu_file_add.addActionListener(this);
		menu_file_open = new JMenuItem("Open", ICON_OPEN);
		menu_file_open.setMnemonic('o');
		menu_file_open.addActionListener(this);
		menu_file_save = new JMenuItem("Save", ICON_SAVE);
		menu_file_save.setMnemonic('s');
		menu_file_save.addActionListener(this);
		menu_file_saveas = new JMenuItem("Save As", ICON_SAVE_AS);
		menu_file_saveas.setMnemonic('a');
		menu_file_saveas.addActionListener(this);
		menu_file_quit = new JMenuItem("Quit");
		menu_file_quit.setMnemonic('q');
		menu_file_quit.addActionListener(this);
		menu_file_quit.setPreferredSize(new Dimension(100, 16));
		menu_file.add(menu_file_add);
		menu_file.add(menu_file_open);
		menu_file.add(menu_file_save);
		menu_file.add(menu_file_saveas);
		menu_file.addSeparator();
		menu_file.add(menu_file_quit);
		
		menu_help = new JMenu("Help");
		menu_help.setMnemonic('h');
		menu_help.setSize(100, 100);
		menu_help_about = new JMenuItem("About", ICON_INFO);
		menu_help_about.setMnemonic('a');
		menu_help_about.addActionListener(this);
		menu_help_about.setPreferredSize(new Dimension(100, 16));
		menu_help.add(menu_help_about);
		
		menubar.add(menu_file);
		menubar.add(menu_help);
		frame.setJMenuBar(menubar);
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		button_cfg_new = new JButton(ICON_NEW);
		button_cfg_new.addActionListener(this);
		button_cfg_new.setToolTipText("New config");
		button_cfg_open = new JButton(ICON_OPEN);
		button_cfg_open.addActionListener(this);
		button_cfg_open.setToolTipText("Open config...");
		button_cfg_save = new JButton(ICON_SAVE);
		button_cfg_save.addActionListener(this);
		button_cfg_save.setToolTipText("Save config");
		button_cfg_saveas = new JButton(ICON_SAVE_AS);
		button_cfg_saveas.addActionListener(this);
		button_cfg_saveas.setToolTipText("Save config as...");
		button_run = new JButton(ICON_RUN);
		button_run.addActionListener(this);
		button_run.setToolTipText("Run Conversion...");
		toolbar.add(button_cfg_new);
		toolbar.add(button_cfg_open);
		toolbar.add(button_cfg_save);
		toolbar.add(button_cfg_saveas);
		toolbar.addSeparator();
		toolbar.add(button_run);
		frame.add(toolbar, BorderLayout.NORTH);
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.add(statusBar, BorderLayout.SOUTH);
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		statusBar.setPreferredSize(new Dimension(frame.getWidth(), 16));
		statusLabel = new JLabel("Map Mirror started");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusBar.add(statusLabel);
		
		JSplitPane split1 = new JSplitPane();
		split1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split1.add(buildConfigListPanel(), JSplitPane.LEFT);
		

		split1.add(buildConfigDetailsPanel(), JSplitPane.RIGHT);
		frame.add(split1, BorderLayout.CENTER);
		//refreshPanel();
//		cfgList.setSelectedValue(null, false);

		frame.setMinimumSize(new Dimension(200,100));
		frame.pack();
		frame.setSize(config.windowSize);
		frame.setPreferredSize(config.windowSize);
		frame.setLocation(config.windowLocation);
		
		buildTexturesFrame();
		buildFieldsFrame();
		buildQueryFrame();
		buildResultFrame();
		buildRunFrame();
	}
	
	public JPanel buildConfigListPanel() {
		listModel_configs = new DefaultListModel<String>();
		list_configs = new JList<String>(listModel_configs);
//		Dimension d = new Dimension(config.listWidth, 10);
		//list_configs.setPreferredSize(d);
		//list_configs.setSize(d);
		//list_configs.setMinimumSize(new Dimension(10,10));
//		cfgList.setBackground(new Color(0, 200, 0));
		list_configs.addListSelectionListener(this);
		list_configs.setBorder(new BevelBorder(BevelBorder.LOWERED));
//		list_configs.setBorder(new LineBorder(new Color(255,0,0), 5));
		refreshList();
		
		button_list_add = new JButton(ICON_ADD);
		button_list_add.setToolTipText("Add");
		button_list_add.addActionListener(this);
		button_list_copy = new JButton(ICON_COPY);
		button_list_copy.setToolTipText("Duplicate");
		button_list_copy.addActionListener(this);
		button_list_copy.setEnabled(false);
		button_list_open = new JButton(ICON_OPEN);
		button_list_open.setToolTipText("Open...");
		button_list_open.addActionListener(this);
		button_list_remove = new JButton(ICON_REMOVE);
		button_list_remove.setToolTipText("Remove");
		button_list_remove.addActionListener(this);
		button_list_remove.setEnabled(false);
		JPanel listButtons = new JPanel();
		listButtons.setLayout(new FlowLayout());
		listButtons.add(button_list_add);
		listButtons.add(button_list_copy);
		listButtons.add(button_list_open);
		listButtons.add(button_list_remove);

		JScrollPane sp1 = new JScrollPane(list_configs); 
//		sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//sp1.setPreferredSize(new Dimension(-1, 100));
//		sp1.setPreferredSize(new Dimension(100, -1));

		listBar = new JPanel();
		listBar.setLayout(new BorderLayout());
		listBar.add(new JLabel("Presets"), BorderLayout.NORTH);
		listBar.add(sp1, BorderLayout.CENTER);
		listBar.add(listButtons, BorderLayout.SOUTH);
//		listBar.setLayout(new GridBagLayout());
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weighty = 0.1;
//		listBar.add(new JLabel("Presets"), gbc);
//		gbc.gridy++;
//		gbc.weighty = 1;
//		listBar.add(sp1, gbc);
//		gbc.gridy++;
//		gbc.weighty = 0.1;
//		listBar.add(listButtons, gbc);

		return listBar;
	}
	
	public JPanel buildConfigDetailsPanel() {
		cfgPanel = new JPanel();
		cfgPanel.setLayout(new BorderLayout());
		cfgPanel.setBorder(new EmptyBorder(10,10,10,10)); //top,left,bottom,right
		JPanel mapPanel = new JPanel(new GridBagLayout());
		text_map = new JTextField();
		text_map.getDocument().addDocumentListener(this);
		JLabel l1 = new JLabel("Map file (optional): ");
		l1.setBorder(new EmptyBorder(0,0,0,10));
		button_map_browse = new JButton(ICON_OPEN);
		button_map_browse.addActionListener(this);
		button_map_browse.setToolTipText("Choose map file");
		button_map_clear = new JButton(ICON_CLEAR);
		button_map_clear.addActionListener(this);
		button_map_clear.setToolTipText("Clear map");
		
		text_outmap = new JTextField();
		text_outmap.getDocument().addDocumentListener(this);
		JLabel l2 = new JLabel("Output Map file (optional): ");
		l2.setBorder(new EmptyBorder(0,0,0,10));
		button_outmap_browse = new JButton(ICON_OPEN);
		button_outmap_browse.addActionListener(this);
		button_outmap_browse.setToolTipText("Choose map file");
		button_outmap_clear = new JButton(ICON_CLEAR);
		button_outmap_clear.addActionListener(this);
		button_outmap_clear.setToolTipText("Clear map");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.1;
		mapPanel.add(l1, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		mapPanel.add(text_map, gbc);
		gbc.gridx++;
		gbc.weightx = 0.01;
		mapPanel.add(button_map_browse, gbc);
		gbc.gridx++;
		gbc.weightx = 0.01;
		mapPanel.add(button_map_clear, gbc);
		gbc.gridy++;
		gbc.gridx = 0;

		mapPanel.add(l2, gbc);
		gbc.gridx++;
		mapPanel.add(text_outmap, gbc);
		gbc.gridx++;
		mapPanel.add(button_outmap_browse, gbc);
		gbc.gridx++;
		mapPanel.add(button_outmap_clear, gbc);
		
		selectedConfigName = new JLabel(NOT_SELECTED);
		selectedConfigName.setIcon(ICON_NEW);
		selectedConfigName.setHorizontalAlignment(SwingConstants.CENTER);
		selectedConfigName.setVerticalAlignment(SwingConstants.CENTER);
		Font f1 = selectedConfigName.getFont();
		selectedConfigName.setFont(new FontUIResource(f1.getFontName(), Font.BOLD, f1.getSize() * 2));
		selectedConfigName.setHorizontalTextPosition(JLabel.LEFT);
		
		JPanel p4 = new JPanel(new BorderLayout());
		
		p4.add(selectedConfigName, BorderLayout.NORTH);
		p4.add(mapPanel, BorderLayout.SOUTH);
		cfgPanel.add(p4, BorderLayout.NORTH);
		
		JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		check_rotate = new JCheckBox("Rotate Map 180º");
		check_rotate.addItemListener(this);
		check_overlay = new JCheckBox("Overlay changes over orignal");
		check_overlay.setToolTipText("Don't forget to delete the info_tfdetect and anything else global. Worldspawns will merge automatially.");
		check_overlay.addItemListener(this);
		text_translate = new JTextField(10);
		text_translate.setToolTipText("Format: 'x y z'. This is applied after the rotation");
		text_translate.addFocusListener(this);
		text_translate.getDocument().addDocumentListener(this);
		
		p5.add(check_rotate);
		p5.add(check_overlay);
		p5.add(new JLabel("Translate By: "));
		p5.add(text_translate);
		
		JPanel p7 = new JPanel(new BorderLayout());
		p7.add(p5, BorderLayout.NORTH);
		
		JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split2.setResizeWeight(config.divWeight);
		
		split2.add(buildTexturesList(), JSplitPane.LEFT);

		split2.add(buildFieldsList(), JSplitPane.RIGHT);

		p7.add(split2, BorderLayout.CENTER);
		cfgPanel.add(p7, BorderLayout.CENTER);

		return cfgPanel;
	}
	
	public JDialog buildTexturesFrame() {
		frame_textures = new JDialog(frame, "Texture Replacement",true);
		frame_textures.setLayout(new BorderLayout());
		JPanel p9 = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = gbc.gridy = 0;
		p9.add(new JLabel("Old Texture: "), gbc);
		gbc.gridx++;
		text_texture_old = new JTextField(20);
		p9.add(text_texture_old, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		p9.add(new JLabel("New Texture: "), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		text_texture_new = new JTextField(20);
		p9.add(text_texture_new, gbc);
		frame_textures.add(p9, BorderLayout.CENTER);
		
		JPanel p10 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton b1 = new JButton("Done", ICON_OK);
		b1.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			currentTextureReplacement.oldTexture = text_texture_old.getText();
			currentTextureReplacement.newTexture = text_texture_new.getText();
			frame_textures.setVisible(false);
			refreshPanel(currentCfg);
		}});
		JButton b2 = new JButton("Cancel", ICON_CANCEL);
		b2.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			frame_textures.setVisible(false);
		}});
		
		p10.add(b1);
		p10.add(b2);
		frame_textures.add(p10, BorderLayout.SOUTH);
		frame_textures.pack();
		if(config.texturesSize != null) {
			frame_textures.setPreferredSize(config.texturesSize);
		}
		return frame_textures;
	}
	
	public JDialog buildQueryFrame() {
		frame_query = new JDialog(frame, "Entity Query ",true);
		frame_query.setLayout(new BorderLayout());
		JPanel p9 = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = gbc.gridy = 0;
		p9.add(new JLabel("Field Name: "), gbc);
		gbc.gridx++;
		text_query_name = new JTextField(20);
		p9.add(text_query_name, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		p9.add(new JLabel("Action: "), gbc);
		gbc.gridx++;
		combo_query_action = new JComboBox<String>(QUERY_OPTIONS);
		p9.add(combo_query_action, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		p9.add(new JLabel("Field Value: "), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		text_query_value = new JTextField(20);
		p9.add(text_query_value, gbc);
		frame_query.add(p9, BorderLayout.CENTER);
		
		JPanel p10 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton b1 = new JButton("Done", ICON_OK);
		b1.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			currentQuery.field.name = text_query_name.getText();
			currentQuery.field.value = text_query_value.getText();
			int index = combo_query_action.getSelectedIndex();
			if(index == 1) {
				currentQuery.action = FieldCriteria.MATCH_ANY;
			} else if(index == 2) {
				currentQuery.action = FieldCriteria.MATCH_NOT;
			} else {
				currentQuery.action = FieldCriteria.MATCH_EXACT;
			}
			//System.out.println("Saving query, field: " + currentQuery.field);
			//System.out.println("Saving query, query: " + currentQuery);
			//System.out.println("Saving query, replacement: " + currentFieldReplacement.toDebugString());
			//currentTextureReplacement.newTexture = text_texture_new.getText();
			frame_query.setVisible(false);
			refreshFieldsFrame(currentFieldReplacement);
		}});
		JButton b2 = new JButton("Cancel", ICON_CANCEL);
		b2.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			frame_query.setVisible(false);
		}});
		
		p10.add(b1);
		p10.add(b2);
		frame_query.add(p10, BorderLayout.SOUTH);
		frame_query.pack();
		if(config.querySize != null) {
			frame_query.setPreferredSize(config.querySize);
		}
		return frame_query;
	}	
	
	public JDialog buildResultFrame() {
		frame_result = new JDialog(frame, "Entity Result ",true);
		frame_result.setLayout(new BorderLayout());
		JPanel p9 = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = gbc.gridy = 0;
		p9.add(new JLabel("Field Name: "), gbc);
		gbc.gridx++;
		text_result_name = new JTextField(20);
		p9.add(text_result_name, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		p9.add(new JLabel("Action: "), gbc);
		gbc.gridx++;
		combo_result_action = new JComboBox<String>(RESULT_OPTIONS);
		p9.add(combo_result_action, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		p9.add(new JLabel("Field Value: "), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		text_result_value = new JTextField(20);
		p9.add(text_result_value, gbc);
		frame_result.add(p9, BorderLayout.CENTER);
		
		JPanel p10 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton b1 = new JButton("Done", ICON_OK);
		b1.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			currentResult.field.name = text_result_name.getText();
			currentResult.field.value = text_result_value.getText();
			int index = combo_result_action.getSelectedIndex();
			if(index == 1) {
				currentResult.action = FieldResult.ACTION_ADD;
			} else if(index == 2) {
				currentResult.action = FieldResult.ACTION_DELETE;
			} else {
				currentResult.action = FieldResult.ACTION_NONE;
			}
			//System.out.println("Saving result, field: " + currentResult.field);
			//System.out.println("Saving query, query: " + currentResult);
			//System.out.println("Saving query, replacement: " + currentFieldReplacement.toDebugString());
			//currentTextureReplacement.newTexture = text_texture_new.getText();
			frame_result.setVisible(false);
			refreshFieldsFrame(currentFieldReplacement);
		}});
		JButton b2 = new JButton("Cancel", ICON_CANCEL);
		b2.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			frame_result.setVisible(false);
		}});
		
		p10.add(b1);
		p10.add(b2);
		frame_result.add(p10, BorderLayout.SOUTH);
		frame_result.pack();
		if(config.resultsSize != null) {
			frame_result.setPreferredSize(config.resultsSize);
		}
		return frame_result;
	}		
	public JDialog buildFieldsFrame() {
		frame_fields = new JDialog(frame, "Field Replacement",true);
		frame_fields.setLayout(new BorderLayout());
		JPanel p9 = new JPanel(new GridBagLayout());
		
		queryListModel = new DefaultListModel<FieldCriteria>();
		queryList = new JList<FieldCriteria>(queryListModel);
		Dimension d = new Dimension(300, 1000);
		queryList.setPreferredSize(d);
		queryList.setSize(d);
		queryList.setMinimumSize(new Dimension(10,10));
		queryList.addMouseListener(this);
		queryList.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		resultListModel = new DefaultListModel<FieldResult>();
		resultList = new JList<FieldResult>(resultListModel);
		resultList.setPreferredSize(d);
		resultList.setSize(d);
		resultList.setMinimumSize(new Dimension(10,10));
		resultList.addMouseListener(this);
		resultList.setBorder(new BevelBorder(BevelBorder.LOWERED));

		check_delete_ent = new JCheckBox("Delete Entity");
		check_delete_ent.addActionListener(this);
		
		JPanel p_buttons_query = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		button_query_add = new JButton(ICON_ADD);
		button_query_add.addActionListener(this);
		button_query_add.setToolTipText("Add Query Criterium");
		button_query_edit = new JButton(ICON_EDIT);
		button_query_edit.addActionListener(this);
		button_query_edit.setToolTipText("Edit Query Criterium");
		button_query_remove = new JButton(ICON_REMOVE);
		button_query_remove.addActionListener(this);
		button_query_remove.setToolTipText("Remove Query Criterium");

		p_buttons_query.add(button_query_add);
		p_buttons_query.add(button_query_edit);
		p_buttons_query.add(button_query_remove);

		JPanel p_buttons_result = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		button_result_add = new JButton(ICON_ADD);
		button_result_add.addActionListener(this);
		button_result_add.setToolTipText("Add Result");
		button_result_edit = new JButton(ICON_EDIT);
		button_result_edit.addActionListener(this);
		button_result_edit.setToolTipText("Edit Result");
		button_result_remove = new JButton(ICON_REMOVE);
		button_result_remove.addActionListener(this);
		button_result_remove.setToolTipText("Remove Result");

		p_buttons_result.add(button_result_add);
		p_buttons_result.add(button_result_edit);
		p_buttons_result.add(button_result_remove);

		
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = gbc.gridy = 0;
		gbc.weighty = 0.1;
		p9.add(new JLabel("Query"), gbc);
		p9.add(check_delete_ent);
		gbc.gridy++;
//		text_texture_old = new JTextField(20);
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		p9.add(new JScrollPane(queryList), gbc);
		gbc.gridy = 0;
		gbc.gridx++;
		gbc.weightx = 0.1;
		gbc.weighty = 0.1;
		p9.add(new JLabel("Result"), gbc);
		gbc.gridy++;
//		text_texture_new = new JTextField(20);
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		p9.add(new JScrollPane(resultList), gbc);
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.weighty = 0.1;
		p9.add(p_buttons_query, gbc);
		gbc.gridx++;
		p9.add(p_buttons_result, gbc);
//		p9.add(text_texture_new, gbc);
		frame_fields.add(p9, BorderLayout.CENTER);
		
		
		JPanel p10 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton b1 = new JButton("Done", ICON_OK);
		b1.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			currentFieldReplacement.delete = check_delete_ent.isSelected();
			if(!currentFieldReplacement.valid()) {
				JOptionPane.showMessageDialog(frame_fields, "The entity update configuration is invalid.\nPlease make sure it has at least one query criterium\nand either is up for Deletion or has at least one Result", "Invalid Entity Replacement", JOptionPane.ERROR_MESSAGE);
			}
			frame_fields.setVisible(false);
			refreshPanel(currentCfg);
		}});
		JButton b2 = new JButton("Cancel", ICON_CANCEL);
		b2.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			frame_fields.setVisible(false);
		}});
		
		p10.add(b1);
		p10.add(b2);
		frame_fields.add(p10, BorderLayout.SOUTH);
		frame_fields.pack();
		if(config.fieldsSize != null) {
			frame_fields.setPreferredSize(config.fieldsSize);
		}
		return frame_fields;
	}
	
	public JFrame buildRunFrame() {
		frame_run = new JFrame();
		frame_run.setIconImage(ICON_MAIN.getImage());
		frame_run.setLayout(new BorderLayout());
		frame_run.setTitle("Convert map");

		JPanel p9 = new JPanel(new GridBagLayout());
		p9.setBorder(new EmptyBorder(10,10,10,10));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.ipadx = gbc.ipady = 5;
		gbc.gridx = gbc.gridy = 0;
		p9.add(new JLabel("Input Map: "), gbc);
		gbc.gridx++;
		text_run_map = new JTextField(50);
		text_run_map.addFocusListener(this);
		p9.add(text_run_map, gbc);
		gbc.gridx++;
		button_run_map_browse = new JButton(ICON_OPEN);
		button_run_map_browse.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			file_chooser.setSelectedFile(new File(text_run_map.getText()));
			if(file_chooser.showDialog(frame_run, "Select") == JFileChooser.APPROVE_OPTION) {
				text_run_map.setText(getRelativePath(file_chooser.getSelectedFile()));
				loadRunMap();
			}
		}});
		p9.add(button_run_map_browse, gbc);
		gbc.gridx++;
		label_run_map_status = new JLabel();
		label_run_map_status.setBorder(new EmptyBorder(0, 10, 0, 10));
		p9.add(label_run_map_status, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		p9.add(new JLabel("Output Map: "), gbc);
		gbc.gridx++;
		text_run_outmap = new JTextField(50);
		p9.add(text_run_outmap, gbc);
		gbc.gridx++;
		button_run_outmap_browse = new JButton(ICON_OPEN);
		button_run_outmap_browse.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			file_chooser.setSelectedFile(new File(text_run_outmap.getText()));
			if(file_chooser.showDialog(frame_run, "Select") == JFileChooser.APPROVE_OPTION) {
				text_run_outmap.setText(getRelativePath(file_chooser.getSelectedFile()));
			}
		}});
		p9.add(button_run_outmap_browse, gbc);
		gbc.gridx++;
		label_run_outmap_status = new JLabel();
		label_run_outmap_status.setBorder(new EmptyBorder(0, 10, 0, 10));
		p9.add(label_run_outmap_status, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		p9.add(new JLabel("Conversion Config: "), gbc);
		gbc.gridx++;
		text_run_config = new JTextField(50);
		text_run_config.addFocusListener(this);
		p9.add(text_run_config, gbc);
		gbc.gridx++;
		button_run_config_browse = new JButton(ICON_OPEN);
		button_run_config_browse.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			file_chooser.setSelectedFile(new File(text_run_config.getText()));
			if(file_chooser.showDialog(frame_run, "Select") == JFileChooser.APPROVE_OPTION) {
				text_run_config.setText(getRelativePath(file_chooser.getSelectedFile()));
				loadRunConfig();
			}
		}});
		p9.add(button_run_config_browse, gbc);
		gbc.gridx++;
		label_run_config_status = new JLabel();
		label_run_config_status.setBorder(new EmptyBorder(0, 10, 0, 10));
		p9.add(label_run_config_status, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		p9.add(new JLabel("Run Progress: "), gbc);
		gbc.gridx++;
		gbc.gridwidth = 2;
		progress_run = new JProgressBar(0, 6);
//		progress_run.setSize(10, text_run_config.getHeight());
		p9.add(progress_run, gbc);
		gbc.gridwidth = 1;
		gbc.gridx += 2;
		label_run_status = new JLabel();
		label_run_status.setBorder(new EmptyBorder(0, 10, 0, 10));
		p9.add(label_run_status, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 3;
		text_run_log = new JTextArea(10, 10);
		text_run_log.setEditable(false);
		p9.add(new JScrollPane(text_run_log), gbc);
		frame_run.add(p9, BorderLayout.CENTER);
		
		JPanel p10 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton b1 = new JButton("Run", ICON_RUN);
		b1.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			progress_run.setValue(0);
			label_run_status.setIcon(ICON_RUN);
			if(runCfg == null || runCfg.filename == null || runCfg.filename.length() == 0) {
				text_run_log.append("Invalid config\n");
				label_run_status.setIcon(ICON_CANCEL);
				return;
			}
			if(runMap == null || runMap.filename == null || runMap.filename.length() == 0) {
				text_run_log.append("Invalid input map\n");
				label_run_status.setIcon(ICON_CANCEL);
				return;
			}
			if(runCfg != null && runMap != null) {
				runCfg.mapname = text_run_map.getText();
				runCfg.outname = text_run_outmap.getText();
				text_run_log.append("Running rules file " + runCfg.filename + "\n");
				text_run_log.append("Over " + runCfg.mapname + "\n");
				text_run_log.append("Outputting to " + runCfg.outname + "\n");
				//mapmirror.DEBUG = true;
				mapMirror.config = runCfg;
				mapMirror.map = runMap;
				if(mapMirror.config.overlay) {
					text_run_log.append("Overlay requested\n");
					mapMirror.prepareOverlay();
				}
				progress_run.setValue(1);
				text_run_log.append("Replaced " + mapMirror.replaceTextures() + " textures\n");
				progress_run.setValue(2);
				text_run_log.append("Replaced " + mapMirror.replaceEntities() + " fields\n");
				progress_run.setValue(3);
				text_run_log.append("Applying transformations: rotate: " + (mapMirror.config.flip_vertical && mapMirror.config.flip_horizontal) + ", translate: " + mapMirror.config.translate + "\n");
				mapMirror.transform();
				progress_run.setValue(4);
				if(mapMirror.config.overlay) {
					text_run_log.append("Applying overlay...\n");
					mapMirror.overlay();
				}
				progress_run.setValue(5);
				text_run_log.append("Saving Map: " + mapMirror.config.outname + "\n");
				mapMirror.map.saveToFile(mapMirror.config.outname);
				progress_run.setValue(6);
				text_run_log.append("Completed\n\n");
				label_run_status.setIcon(ICON_OK);
			} else {
				label_run_status.setIcon(ICON_CANCEL);
			}
			//frame_run.setVisible(false);
		}});
		JButton b2 = new JButton("Close", ICON_CANCEL);
		b2.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			frame_run.setVisible(false);
		}});
		
		p10.add(b1);
		p10.add(b2);
		frame_run.add(p10, BorderLayout.SOUTH);
		
		return frame_run;
	}
	
	public JPanel buildTexturesList() {
		listModel_textures = new DefaultListModel<String>();
		list_textures = new JList<String>(listModel_textures);
		list_textures.setBorder(new BevelBorder(BevelBorder.LOWERED));
		list_textures.addMouseListener(this);
		
		JPanel p6 = new JPanel(new BorderLayout());
		p6.add(new JLabel("Texture Replacements"), BorderLayout.NORTH);
		p6.add(new JScrollPane(list_textures), BorderLayout.CENTER);
		
		JPanel p11 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		button_textures_add = new JButton(ICON_ADD);
		button_textures_add.addActionListener(this);
		button_textures_add.setToolTipText("Add Texture Replacement");
		button_textures_edit = new JButton(ICON_EDIT);
		button_textures_edit.addActionListener(this);
		button_textures_edit.setToolTipText("Edit Texture Replacement");
		button_textures_remove = new JButton(ICON_REMOVE);
		button_textures_remove.addActionListener(this);
		button_textures_remove.setToolTipText("Remove Texture Replacement");

		p11.add(button_textures_add);
		p11.add(button_textures_edit);
		p11.add(button_textures_remove);

		p6.add(p11, BorderLayout.SOUTH);

		return p6;
	}
	
	public JPanel buildFieldsList() {
		listModel_fields = new DefaultListModel<String>();
		list_fields = new JList<String>(listModel_fields);
		list_fields.setBorder(new BevelBorder(BevelBorder.LOWERED));
		list_fields.addMouseListener(this);
		
		JPanel p8 = new JPanel(new BorderLayout());
		p8.add(new JLabel("Entity Shenanigans"), BorderLayout.NORTH);
		p8.add(new JScrollPane(list_fields), BorderLayout.CENTER);

		JPanel p11 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		button_fields_add = new JButton(ICON_ADD);
		button_fields_add.addActionListener(this);
		button_fields_add.setToolTipText("Add Field Replacement");
		button_fields_edit = new JButton(ICON_EDIT);
		button_fields_edit.addActionListener(this);
		button_fields_edit.setToolTipText("Edit Field Replacement");
		button_fields_remove = new JButton(ICON_REMOVE);
		button_fields_remove.addActionListener(this);
		button_fields_remove.setToolTipText("Remove Field Replacement");
		button_fields_up = new JButton(ICON_UP);
		button_fields_up.addActionListener(this);
		button_fields_up.setToolTipText("Move Up");
		button_fields_down = new JButton(ICON_DOWN);
		button_fields_down.addActionListener(this);
		button_fields_down.setToolTipText("Move Down");

		p11.add(button_fields_add);
		p11.add(button_fields_edit);
		p11.add(button_fields_remove);
		p11.add(button_fields_up);
		p11.add(button_fields_down);
		
		p8.add(p11, BorderLayout.SOUTH);

		return p8;
	}
	
	public void refreshList() {
		if(list_configs == null || config == null || config.configFiles == null) return;
		listModel_configs.clear();
		listModel_configs.addAll(config.configFiles);
		if(list_configs.getParent() != null)
			statusLabel.setText("list height is now " + list_configs.getSize() + ", scroll size: " + list_configs.getParent().getSize());
	}
	
	public void refreshQueryList() {
		if(queryListModel == null || currentFieldReplacement == null || currentFieldReplacement.criteria == null) return;
		queryListModel.clear();
//		queryListModel = new DefaultListModel<FieldCriteria>();
		queryListModel.addAll(currentFieldReplacement.criteria);
//		queryList.setModel(queryListModel);
	}
	
	public void refreshResultList() {
		if(resultListModel == null) return;
		resultListModel.clear();
		if(currentFieldReplacement == null || currentFieldReplacement.results == null) return;
		resultListModel.addAll(currentFieldReplacement.results);
		check_delete_ent.setSelected(currentFieldReplacement.delete);
		enableResultList();
	}
	
	public void refreshPanel() {
		if(list_configs.getSelectedIndex() > -1) {
			String val = list_configs.getSelectedValue();
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
	public void refreshPanel(ConfigFile cfg) {
		listModel_textures.clear();
		listModel_fields.clear();
		if(cfg == null) {
			selectedConfigName.setText(NOT_SELECTED);
			selectedConfigName.setIcon(ICON_NEW);
			text_map.setText("");
			text_outmap.setText("");
			text_translate.setText("");
			check_rotate.setSelected(false);
			check_overlay.setSelected(false);
		} else {
			if(cfg.filename == null || cfg.filename.length() <= 0) {
				selectedConfigName.setText("#Unsaved Config#");
			} else {
				selectedConfigName.setText(cfg.filename);
			}			
			text_map.setText(cfg.mapname);
			text_outmap.setText(cfg.outname);
			text_translate.setText(cfg.translate.toString());
			check_rotate.setSelected(cfg.flip_horizontal && cfg.flip_vertical);
			check_overlay.setSelected(cfg.overlay);
			for(TextureReplacement tr : cfg.texture_replacements) {
				if(tr != null) {
					listModel_textures.addElement(tr.oldTexture + " -> " + tr.newTexture);
				}
			}
			for(FieldReplacement fr : cfg.field_replacements) {
				listModel_fields.addElement(fr.toString());
			}
		}
	}
	
	public void updateConfig() {
		if(currentCfg == null) {
			currentCfg = new ConfigFile();
		}
		currentCfg.flip_horizontal = check_rotate.isSelected();
		currentCfg.flip_vertical = check_rotate.isSelected();
		currentCfg.overlay = check_overlay.isSelected();
		currentCfg.mapname = text_map.getText();
		currentCfg.outname = text_outmap.getText();
		currentCfg.translate.parse(text_translate.getText());
	}
	
//	public void refreshTextureList() {
//		listModel_textures.clear();
//		for(TextureReplacement tr : cfg.texture_replacements) {
//			if(tr != null) {
//				listModel_textures.addElement(tr.oldTexture + " -> " + tr.newTexture);
//			}
//		}
//	}
	
	public void refreshTexturesFrame(TextureReplacement tr) {
		currentTextureReplacement = tr;
		refreshTexturesFrame();
	}
	
	public void refreshTexturesFrame() {
		if(currentTextureReplacement == null) {
			text_texture_old.setText("");
			text_texture_new.setText("");
		} else {
			text_texture_old.setText(currentTextureReplacement.oldTexture);
			text_texture_new.setText(currentTextureReplacement.newTexture);
		}
	}
	
	public void showTexturesFrame() {
		int index = list_textures.getSelectedIndex();
		if(index >= 0) {
			refreshTexturesFrame(currentCfg.texture_replacements.get(index));
			text_texture_old.requestFocus();
			frame_textures.setLocationRelativeTo(frame);
			frame_textures.setVisible(true);
		}
	}
	
	public void refreshFieldsFrame(FieldReplacement fr) {
		currentFieldReplacement = fr;
		refreshFieldsFrame();
	}
	
	public void refreshFieldsFrame() {
		refreshQueryList();
		refreshResultList();
		if(currentFieldReplacement == null) {
//			text_texture_old.setText("");
//			text_texture_new.setText("");
		} else {
//			text_texture_old.setText(currentFieldReplacement.oldTexture);
//			text_texture_new.setText(currentFieldReplacement.newTexture);
		}
		frame_fields.paintAll(frame_fields.getGraphics());
	}
	public void refreshQueryFrame() {
		if(currentQuery == null || currentQuery.field == null) {
			text_query_name.setText("");
			text_query_value.setText("");
			combo_query_action.setSelectedIndex(0);
		} else {
			text_query_name.setText(currentQuery.field.name);
			text_query_value.setText(currentQuery.field.value);
			if(currentQuery.action == FieldCriteria.MATCH_ANY) {
				combo_query_action.setSelectedIndex(1);
			} else if(currentQuery.action == FieldCriteria.MATCH_NOT) {
				combo_query_action.setSelectedIndex(2);
			} else {
				combo_query_action.setSelectedIndex(0);
			} 
		}
		//frame_fields.paintAll(frame_fields.getGraphics());
	}
	
	public void refreshResultFrame() {
		if(currentResult == null || currentResult.field == null) {
			text_result_name.setText("");
			text_result_value.setText("");
			combo_result_action.setSelectedIndex(0);
		} else {
			text_result_name.setText(currentResult.field.name);
			text_result_value.setText(currentResult.field.value);
			if(currentResult.action == FieldResult.ACTION_ADD) {
				combo_result_action.setSelectedIndex(1);
			} else if(currentResult.action == FieldResult.ACTION_DELETE) {
				combo_result_action.setSelectedIndex(2);
			} else {
				combo_result_action.setSelectedIndex(0);
			} 
		}
		//frame_fields.paintAll(frame_fields.getGraphics());
	}	
	public void refreshRunFrame() {
		if(currentCfg != null) {
			runCfg = currentCfg;
			String name = "";
			if(currentCfg.filename != null) {
				name = currentCfg.filename;
			}
			if(unsaved) {
				name += "(current)";
			}
			text_run_config.setText(name);			
			label_run_config_status.setIcon(ICON_OK);
		}
		if(currentMap != null) {
			runMap = currentMap;
			text_run_map.setText(currentMap.name);
			label_run_map_status.setIcon(ICON_OK);
		} else {
			if(runMap.loadFromFile(text_map.getText())) {
				label_run_map_status.setIcon(ICON_OK);
			} else {
				label_run_map_status.setIcon(ICON_CANCEL);
			}
			text_run_map.setText(text_map.getText());
		}
		text_run_outmap.setText(text_outmap.getText());
		//frame_fields.paintAll(frame_fields.getGraphics());
	}	
	
	public void showRunFrame() {
		refreshRunFrame();
		text_run_map.requestFocus();
		frame_run.setLocationRelativeTo(frame);
		frame_run.pack();
		frame_run.setVisible(true);
	}
	
	public void showFieldsFrame() {
		int index = list_fields.getSelectedIndex();
		if(index >= 0) {
			//refreshFieldsFrame(currentCfg.field_replacements.get(index));
			currentFieldReplacement = currentCfg.field_replacements.get(index);
			refreshFieldsFrame();
			button_fields_add.requestFocus();
			frame_fields.setLocationRelativeTo(frame);
			frame_fields.pack();
			frame_fields.setVisible(true);
		}
	}
	public void showQueryFrame() {
		if(currentCfg == null || currentFieldReplacement == null) return;
		int index = queryList.getSelectedIndex();
		if(index >= 0) {
			currentQuery = currentFieldReplacement.criteria.get(index);
			refreshQueryFrame();
			text_query_name.requestFocus();
			frame_query.setLocationRelativeTo(frame_fields);
			frame_query.pack();
			frame_query.setVisible(true);
		}
	}
	public void showResultFrame() {
		if(currentCfg == null || currentFieldReplacement == null) return;
		int index = resultList.getSelectedIndex();
		if(index >= 0) {
			currentResult = currentFieldReplacement.results.get(index);
			refreshResultFrame();
			text_result_name.requestFocus();
			frame_result.setLocationRelativeTo(frame_fields);
			frame_result.pack();
			frame_result.setVisible(true);
		}
	}
	
	public void enableResultList() {
		resultList.setEnabled(!check_delete_ent.isSelected());
//		button_result_add.setEnabled(!check_delete_ent.isSelected());
//		button_result_edit.setEnabled(!check_delete_ent.isSelected());
//		button_result_remove.setEnabled(!check_delete_ent.isSelected());
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
	
	public void saveCfg() {
		System.out.println("Save requested");
		if(currentCfg == null) {
			currentCfg = new ConfigFile();
		}
		if(currentCfg.filename == null || currentCfg.filename.length() <= 0) {
			saveAsCfg();
			return;
		}
		System.out.println("Saving to " + currentCfg.filename);
		updateConfig();
		currentCfg.saveToFile();
		unsaved = false;
	}
	
	public void saveAsCfg() {
		String f = "";
		if(currentCfg != null) {
			f = currentCfg.filename;
		}
		updateConfig();
		file_chooser.setSelectedFile(new File(f));
		if(file_chooser.showDialog(frame, "Select") == JFileChooser.APPROVE_OPTION) {
			String path = getRelativePath(file_chooser.getSelectedFile());
			currentCfg.saveToFile(path);
			unsaved = false;
			if(JOptionPane.showConfirmDialog(frame,"Add " + path + " to sidebar?", "Add", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				config.configFiles.addElement(path);
				refreshList();
			}
		}
	}
	
	public static void main(String[] args) {
		for(int i = 0; i < args.length; i++) {
			if(args[i] != null && "-nogui".equals(args[i])) {
				mapmirror.main(args);
				System.exit(0);
			}
		}
		mapmirrorUI mmu = new mapmirrorUI();
		mmu.loadConfig(null);
		mmu.show();
	}

	public void markChanged() {
		unsaved = true;
		generateTitle();
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if(config == null) return;
		if(frame != null) {
			config.windowSize = frame.getSize();
			config.windowLocation = frame.getLocation();
		}
		if(frame_textures != null) {
			config.texturesSize = frame_textures.getSize();
		}
		if(frame_fields != null) {
			config.fieldsSize = frame_fields.getSize();
		}
		if(frame_query != null) {
			config.querySize = frame_query.getSize();
		}
		if(frame_result != null) {
			config.resultsSize = frame_result.getSize();
		}
		if(list_configs != null) {
			config.listWidth = list_configs.getWidth();
		}
		if(list_textures != null) {
			Container c = list_textures.getParent();
			while(c != null && c != frame) {
				if(c instanceof JSplitPane) {
					config.divWeight = ((JSplitPane)c).getResizeWeight();
					break;
				}
				c = c.getParent();
			}
		}
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
		Object source = e.getSource();
		if(source == button_list_add || source == menu_file_add) {
			String newconf = JOptionPane.showInputDialog(frame, "Enter the name", "New Config", JOptionPane.INFORMATION_MESSAGE);
			if(newconf != null && newconf.length() > 0) {
				//if(!newconf.toLowerCase().endsWith(".conf")) {
				//	newconf += ".conf";
				//}
				config.configFiles.add(newconf);
				refreshList();
				markChanged();
			}
		} else if(source == button_list_copy) {
			String s = list_configs.getSelectedValue();
			if(s != null) {
				String newconf = JOptionPane.showInputDialog(frame, "Enter the new name", "Copying " + s + " Config", JOptionPane.INFORMATION_MESSAGE);
				if(newconf != null && newconf.length() > 0 && !newconf.equals(s)) {
					try {
						ConfigFile cf = new ConfigFile();
						cf.load(s);
						cf.filename = newconf;
						config.configFiles.add(newconf);
						refreshList();
						list_configs.setSelectedIndex(list_configs.getModel().getSize() - 1);
						refreshPanel(cf);
						markChanged();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(frame, "Error cloning " + s + " into " + newconf, "Clone error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} else if(source == button_list_remove) {
			int index = list_configs.getSelectedIndex();
			if(index >= 0) {
				config.configFiles.remove(index);
				refreshList();
			}
		} else if(source == button_list_open || source == button_cfg_open || source == menu_file_open) {
			String f = "";
			if(currentCfg != null) {
				f = currentCfg.filename;
			}
			file_chooser.setSelectedFile(new File(f));
			if(file_chooser.showDialog(frame, "Select") == JFileChooser.APPROVE_OPTION) {
				//cfgListModel.addElement(getRelativePath(file_chooser.getSelectedFile()));
				config.configFiles.addElement(getRelativePath(file_chooser.getSelectedFile()));
				refreshList();
				markChanged();
			}
		} else if(source == button_cfg_new) {
			list_configs.setSelectedValue(null, false);
			resetPanel();
			refreshList();
			markChanged();
		} else if(source == button_cfg_save || source == menu_file_save) {
			saveCfg();
		} else if(source == button_cfg_saveas || source == menu_file_saveas) {
			saveAsCfg();
		} else if(source == button_map_clear) {
			text_map.setText("");
			markChanged();
		} else if(source == button_outmap_clear) {
			text_outmap.setText("");
			markChanged();
		} else if(source == button_map_browse) {
			file_chooser.setSelectedFile(new File(text_map.getText()));
			if(file_chooser.showDialog(frame, "Select") == JFileChooser.APPROVE_OPTION) {
				text_map.setText(getRelativePath(file_chooser.getSelectedFile()));
				markChanged();
			}
		} else if(source == button_outmap_browse) {
			file_chooser.setSelectedFile(new File(text_outmap.getText()));
			if(file_chooser.showDialog(frame, "Select") == JFileChooser.APPROVE_OPTION) {
				text_outmap.setText(getRelativePath(file_chooser.getSelectedFile()));
				markChanged();
			}
		} else if(source == menu_file_quit) {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		} else if(source == menu_help_about) {
			JOptionPane.showMessageDialog(frame, "Map Mirror\n" + VERSION + "\n\nBy MEHT", "About", JOptionPane.INFORMATION_MESSAGE);
		} else if(source == button_textures_edit) {
			showTexturesFrame();
			markChanged();
		} else if(source == button_textures_add) {
			if(currentCfg == null) currentCfg = new ConfigFile();
			currentTextureReplacement = new TextureReplacement();
			currentCfg.texture_replacements.add(currentTextureReplacement);
			refreshTexturesFrame(currentTextureReplacement);
			frame_textures.setLocationRelativeTo(frame);
			frame_textures.setVisible(true);
			markChanged();
		} else if(source == button_textures_remove) {
			int index = list_textures.getSelectedIndex();
			if(index >= 0 && currentCfg != null) {
				currentCfg.texture_replacements.remove(index);
				listModel_textures.remove(index);
				markChanged();
			}
		} else if(source == button_fields_edit) {
			showFieldsFrame();
			markChanged();
		} else if(source == button_fields_add) {
			if(currentCfg == null) currentCfg = new ConfigFile();
			currentFieldReplacement = new FieldReplacement();
			currentCfg.field_replacements.add(currentFieldReplacement);
			refreshFieldsFrame(currentFieldReplacement);
			frame_fields.setLocationRelativeTo(frame);
			frame_fields.setVisible(true);
			markChanged();
		} else if(source == button_fields_remove) {
			int index = list_fields.getSelectedIndex();
			if(index >= 0 && currentCfg != null) {
				currentCfg.field_replacements.remove(index);
				listModel_fields.remove(index);
				markChanged();
			}		
		} else if(source == button_fields_up) {
			int index = list_fields.getSelectedIndex();
			if(index >= 1 && currentCfg != null) {
				FieldReplacement fr = currentCfg.field_replacements.get(index);
				currentCfg.field_replacements.setElementAt(currentCfg.field_replacements.get(index-1), index);
				currentCfg.field_replacements.setElementAt(fr, index-1);
				String s = listModel_fields.get(index);
				listModel_fields.setElementAt(listModel_fields.get(index-1), index);
				listModel_fields.setElementAt(s, index-1);
				list_fields.setSelectedIndex(index-1);
				refreshPanel();
				markChanged();
			}		
		} else if(source == button_fields_down) {
			int index = list_fields.getSelectedIndex();
			if(index >= 0  && index < (listModel_fields.size() - 1) && currentCfg != null) {
				FieldReplacement fr = currentCfg.field_replacements.get(index);
				currentCfg.field_replacements.setElementAt(currentCfg.field_replacements.get(index+1), index);
				currentCfg.field_replacements.setElementAt(fr, index+1);
				String s = listModel_fields.get(index);
				listModel_fields.setElementAt(listModel_fields.get(index+1), index);
				listModel_fields.setElementAt(s, index+1);
				list_fields.setSelectedIndex(index+1);
				refreshPanel();
				markChanged();
			}		
		} else if(source == check_delete_ent) {
			enableResultList();
			markChanged();
		} else if(source == button_query_edit) {
			showQueryFrame();
			markChanged();
		} else if(source == button_query_add) {
			if(currentCfg == null) currentCfg = new ConfigFile();
			if(currentFieldReplacement == null)	{
				currentFieldReplacement = new FieldReplacement();
				currentCfg.field_replacements.add(currentFieldReplacement);
			}
			currentQuery = new FieldCriteria();
			currentFieldReplacement.criteria.add(currentQuery);
			refreshQueryFrame();
			frame_query.setLocationRelativeTo(frame_fields);
			frame_query.setVisible(true);
			markChanged();
		} else if(source == button_query_remove) {
			int index = queryList.getSelectedIndex();
			if(index >= 0 && currentFieldReplacement != null) {
				currentFieldReplacement.criteria.remove(index);
				queryListModel.remove(index);
				markChanged();
			}		
		} else if(source == button_result_edit) {
			showResultFrame();
			markChanged();
		} else if(source == button_result_add) {
			if(currentCfg == null) currentCfg = new ConfigFile();
			if(currentFieldReplacement == null)	{
				currentFieldReplacement = new FieldReplacement();
				currentCfg.field_replacements.add(currentFieldReplacement);
			}
			currentResult = new FieldResult();
			currentFieldReplacement.results.add(currentResult);
			refreshResultFrame();
			frame_result.setLocationRelativeTo(frame_fields);
			frame_result.setVisible(true);
			markChanged();
		} else if(source == button_result_remove) {
			int index = resultList.getSelectedIndex();
			if(index >= 0 && currentFieldReplacement != null) {
				currentFieldReplacement.results.remove(index);
				resultListModel.remove(index);
				markChanged();
			}		
		} else if(source == button_run) {
			showRunFrame();
		}
		generateTitle();
	}
	public void resetPanel() {
		currentCfg = null;
		currentTextureReplacement = null;
		currentFieldReplacement = null;
		currentMap = null;
		currentQuery = null;
		currentResult = null;
		markChanged();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		/*
		 if(!e.getValueIsAdjusting()) {
		 
			if(currentCfg != null && unsaved) {
				if(JOptionPane.showConfirmDialog(frame, "Save Changes?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					saveCfg();
				}
			}
			return;
		}
		*/
		resetPanel();
		if(list_configs.getSelectedIndex() > -1) {
			String val = list_configs.getSelectedValue();
			if(val != null && !(currentCfg != null && val.equals(currentCfg.filename))) {
				currentCfg = new ConfigFile();
				System.out.println("Loading config " + val);
				boolean loaded = currentCfg.load(val);
				refreshPanel(currentCfg);
				if(loaded) {
					selectedConfigName.setIcon(ICON_OK);
				} else {
					selectedConfigName.setIcon(ICON_CANCEL);
				}
			}
			button_list_copy.setEnabled(true);
			button_list_remove.setEnabled(true);
			unsaved = false;
			generateTitle();
			return;
		} else {
			currentCfg = null;
			button_list_copy.setEnabled(false);
			button_list_remove.setEnabled(false);
		}
		refreshPanel(currentCfg);
		generateTitle();
	}
	
	public static String getRelativePath(String file) {
		return getRelativePath(new File(file));
	}
	
	public static String getRelativePath(File file) {
		if(file == null) return "";
		String retval = new File(".").toURI().relativize(file.toURI()).toString();
		try {
		    retval = java.net.URLDecoder.decode(retval, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			System.out.println("Issue decoding filepath: " + retval + ": " + e.getMessage());
		}
		return retval;
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	public String fixVector(String s) {
		s = s.trim();
		QVector v = new QVector();
		v.parse(s);
		String ss = v.toString();
		if(!s.equals(ss)) {
			if(currentCfg == null || ss.length() == 0) {
				s = ss;
			}
		}
		return s;
	}
	
	public void loadRunConfig() {
		text_run_log.append("Loading map file '" + text_run_config.getText() + "'...");
		runCfg = new ConfigFile();
		if(runCfg.load(text_run_config.getText())) {
			text_run_log.append(" Success\n");
			text_run_log.append("Config " + runCfg.filename + ": " + runCfg.mapname + " -> " + runCfg.outname + "\n");
			text_run_log.append("\tRotate: " + ((runCfg.flip_horizontal && runCfg.flip_vertical)?"YES":"NO") + "; Overlay: " + ((runCfg.overlay)?"YES":"NO") + "; Translate " + runCfg.translate.toString() + "\n");
			text_run_log.append("\tTexture rules: " + runCfg.texture_replacements.size() + "; Entity Rules: " + runCfg.field_replacements.size() + "\n");
			label_run_config_status.setIcon(ICON_OK);
		} else {
			text_run_log.append(" FAILED\n");
			label_run_config_status.setIcon(ICON_CANCEL);
		}
	}

	public void loadRunMap() {
		text_run_log.append("Loading map file '" + text_run_map.getText() + "'...");
		runMap = new QMapFile();
		if(runMap.loadFromFile(text_run_map.getText())) {
			text_run_log.append(" Success\n");
			text_run_log.append("Map " + runMap.name + " contains " + runMap.stuff.size() + " objects and " + runMap.textures.size() + " unique textures\n");
			label_run_map_status.setIcon(ICON_OK);
		} else {
			text_run_log.append(" FAILED\n");
			label_run_map_status.setIcon(ICON_CANCEL);
		}
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if(source == text_translate) {
			text_translate.setText(fixVector(text_translate.getText()));
			markChanged();
		} else if(source == text_run_config) {
			if(e.getOppositeComponent() != button_run_config_browse) {
				loadRunConfig();
			}
		} else if(source == text_run_map) {
			if(e.getOppositeComponent() != button_run_map_browse) {
				loadRunMap();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		int clicks = e.getClickCount();
		if(source == list_textures) {
			if(clicks == 2) {
				showTexturesFrame();
				markChanged();
			}
		} else if(source == list_fields) {
			if(clicks == 2) {
				showFieldsFrame();
				markChanged();
			}
		} else if(source == queryList) {
			if(clicks == 2) {
				showQueryFrame();
				markChanged();
			}
		} else if(source == resultList) {
			if(clicks == 2) {
				showResultFrame();
				markChanged();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		markChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		markChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		markChanged();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		markChanged();
	}
}

package shiftschedule;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.json.JSONException;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import java.awt.FlowLayout;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.JRadioButtonMenuItem;

// Using Java Swing 

public class ShiftSchedule {

	private JFrame frmShift;
	private EmployeeList employees = new EmployeeList();
	private JTable table;
	public static Config config = new Config();
	private final MainTableModel tableModel = new MainTableModel();
	private final Map<Language, JRadioButtonMenuItem> radioButtons = new HashMap<Language, JRadioButtonMenuItem>();

	private JCheckBoxMenuItem checkboxmenuitemSettingMenu24Hr = new JCheckBoxMenuItem(
			Message.getMessage(MessageName.MENU_SETTING_24_HOURS));
	private JCheckBoxMenuItem checkboxmenuitemSettingMenuOverLapShift = new JCheckBoxMenuItem(
			Message.getMessage(MessageName.MENU_SETTING_OVERLAP_SHIFT));

	private final int PERSHIFTHEIGHT = 18;
	private final int NOT24WIDTH = 850;
	private final int USE24WIDTH = 1150;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		// Load the configuration file before initialize everything 
		config = FileUtility.loadConfig();
		Message.setLocal(config.language);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShiftSchedule window = new ShiftSchedule();
					window.frmShift.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ShiftSchedule() {
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frmShift = new JFrame();
		frmShift.setTitle(Message.getMessage(MessageName.APP_NAME));
		frmShift.setBounds(100, 100, 1100, 300);
		frmShift.setMinimumSize(new Dimension(NOT24WIDTH, 300));
		frmShift.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar mainMenuBar = new JMenuBar();
		frmShift.setJMenuBar(mainMenuBar);

		JMenu menuFile = new JMenu(Message.getMessage(MessageName.MENU_FILE));
		mainMenuBar.add(menuFile);

		JMenuItem menuItemFileMenuOpen = new JMenuItem(Message.getMessage(MessageName.MENU_FILE_OPEN));
		menuItemFileMenuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		menuItemFileMenuOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileNameExtensionFilter("JSON File (.json)", "json"));
				int returnVal = fc.showOpenDialog(frmShift);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// File not exist can not open
					if (!file.exists()) {
						JOptionPane.showMessageDialog(frmShift,
								String.format(Message.getMessage(MessageName.ERROR_FILE_NOT_EXIST), file.getName()),
								Message.getMessage(MessageName.ERROR_GENERAL_TITLE), JOptionPane.ERROR_MESSAGE);
					} else {
						try {
							employees = FileUtility.load(file);
							tableModel.fireTableDataChanged();
						} catch (JSONException e1) {
							JOptionPane.showMessageDialog(frmShift,
									String.format(Message.getMessage(MessageName.ERROR_INVALID_JSON), file.getName()),
									Message.getMessage(MessageName.ERROR_GENERAL_TITLE), JOptionPane.ERROR_MESSAGE);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(frmShift,
									String.format(Message.getMessage(MessageName.ERROR_LOAD_FILE_ERROR) + "\nError: "
											+ e1.getMessage(), file.getName()),
									Message.getMessage(MessageName.ERROR_GENERAL_TITLE), JOptionPane.ERROR_MESSAGE);
						}
					}
				}

			}
		});

		JMenuItem menuItemFileMenuNew = new JMenuItem(Message.getMessage(MessageName.MENU_FILE_NEW));
		menuItemFileMenuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		menuItemFileMenuNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] options = { Message.getMessage(MessageName.BUTTON_YES),
						Message.getMessage(MessageName.BUTTON_NO) };

				int n = JOptionPane.showOptionDialog(frmShift,
						Message.getMessage(MessageName.WARNING_DIALOG_NEW_SCHEDULE),
						Message.getMessage(MessageName.WARNING_GENERAL_TITLE), JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, // Not using a
															// custom
															// icon
						options, // the titles of buttons
						options[1]); // default button title
				if (n == JOptionPane.YES_OPTION) {
					employees.clearEmployee();
					tableModel.fireTableDataChanged();
				}

			}
		});
		menuFile.add(menuItemFileMenuNew);
		menuFile.add(menuItemFileMenuOpen);

		JMenuItem menuItemFileMenuSave = new JMenuItem(Message.getMessage(MessageName.MENU_FILE_SAVE));
		menuItemFileMenuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuItemFileMenuSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileNameExtensionFilter("JSON File (.json)", "json"));
				int returnVal = fc.showSaveDialog(frmShift);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					String filePath = file.getPath();
					// If the file is not ends with .json then add it on
					if (!filePath.endsWith(".json")) {
						filePath += ".json";
					}
					FileUtility.save(file.getPath(), employees);
				}
			}
		});
		menuFile.add(menuItemFileMenuSave);

		JMenuItem menuItemFileMenuExport = new JMenuItem(Message.getMessage(MessageName.MENU_FILE_EXPORT_TO_EXCEL));
		menuItemFileMenuExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
				int returnVal = fc.showSaveDialog(frmShift);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (!file.getAbsolutePath().endsWith(".xlsx")) {
						file = new File(fc.getSelectedFile() + ".xlsx");
					}
					try {
						FileUtility.saveToExcel(file, employees, checkboxmenuitemSettingMenu24Hr.getState());
						JOptionPane.showMessageDialog(frmShift,
								String.format(Message.getMessage(MessageName.INFO_DIALOG_EXPORT_TO_EXCEL_SUCCEES),
										file.getName()),
								Message.getMessage(MessageName.INFO_GENERAL_TITLE), JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frmShift, e1.getLocalizedMessage(),
								Message.getMessage(MessageName.ERROR_GENERAL_TITLE), JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		menuFile.add(menuItemFileMenuExport);

		JMenuItem menuItemFileMenuExit = new JMenuItem(Message.getMessage(MessageName.MENU_FILE_EXIT));
		menuItemFileMenuExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileUtility.save(employees);
				System.exit(0);
			}
		});
		menuFile.add(menuItemFileMenuExit);

		JMenu menuEmployee = new JMenu(Message.getMessage(MessageName.MENU_EMPLOYEE));
		mainMenuBar.add(menuEmployee);

		JMenuItem menuitemEmployeeMenuAdd = new JMenuItem(Message.getMessage(MessageName.MENU_EMPLOYEE_ADD));
		JMenuItem menuitemEmployeeMenuRemove = new JMenuItem(Message.getMessage(MessageName.MENU_EMPLOYEE_REMOVE));
		// Add Menu Listener
		menuitemEmployeeMenuAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogAddEmployee dialog = new DialogAddEmployee(frmShift,
						Message.getMessage(MessageName.DIALOG_TITLE_ADD_EMPLOYEE), employees);
				dialog.setVisible(true);
				// Data might change need to update all cells
				tableModel.fireTableDataChanged();
				if (employees.getCount() > 0) {
					menuitemEmployeeMenuRemove.setEnabled(true);
				}
			}
		});

		// Remove Menu Listener
		menuitemEmployeeMenuRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() != -1) {
					employees.removeEmployee(employees.getEmployee(table.getSelectedRow()));
					tableModel.fireTableDataChanged();
					if (employees.isEmpty()) {
						menuitemEmployeeMenuRemove.setEnabled(false);
					}
				}

			}
		});
		menuEmployee.add(menuitemEmployeeMenuAdd);
		menuEmployee.add(menuitemEmployeeMenuRemove);

		JMenu menuSetting = new JMenu(Message.getMessage(MessageName.MENU_SETTING));
		mainMenuBar.add(menuSetting);
		checkboxmenuitemSettingMenuOverLapShift.setEnabled(false);

		menuSetting.add(checkboxmenuitemSettingMenuOverLapShift);
		checkboxmenuitemSettingMenu24Hr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.fireTableDataChanged();

				if (!checkboxmenuitemSettingMenu24Hr.getState()) {
					frmShift.setSize(USE24WIDTH, 300);
				} else {
					frmShift.setSize(NOT24WIDTH, 300);
				}
			}
		});
		checkboxmenuitemSettingMenu24Hr.setSelected(true);

		menuSetting.add(checkboxmenuitemSettingMenu24Hr);

		JMenu mnLanguage = new JMenu(Message.getMessage(MessageName.MENU_SETTING_LANGUAGE));
		menuSetting.add(mnLanguage);

		ButtonGroup languageButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem rdbtnmntmDefault = new JRadioButtonMenuItem(
				Message.getMessage(MessageName.MENU_SETTING_LANGUAGE_DEFAULT));
		languageButtonGroup.add(rdbtnmntmDefault);
		mnLanguage.add(rdbtnmntmDefault);
		radioButtons.put(Language.DEFAULT, rdbtnmntmDefault);
		rdbtnmntmDefault.addActionListener(new LanguageActionListener());

		JRadioButtonMenuItem rdbtnmntmChinese = new JRadioButtonMenuItem(
				Message.getMessage(MessageName.MENU_SETTING_LANGUAGE_CHINESE));
		languageButtonGroup.add(rdbtnmntmChinese);
		mnLanguage.add(rdbtnmntmChinese);
		radioButtons.put(Language.ZH, rdbtnmntmChinese);
		rdbtnmntmChinese.addActionListener(new LanguageActionListener());

		if (!checkboxmenuitemSettingMenu24Hr.getState()) {
			frmShift.setSize(USE24WIDTH, 300);
		} else {
			frmShift.setSize(NOT24WIDTH, 300);
		}

		tableModel.addTableModelListener(new MainTableModelListener());
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.setDefaultRenderer(Object.class, new MultipleLineTableCellRenderer());
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		JScrollPane scrollPaneTable = new JScrollPane(table);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Double clicked event
				if (e.getClickCount() == 2) {
					openEditTimeDialog();
				}
			}

		});
		frmShift.getContentPane().add(scrollPaneTable, BorderLayout.CENTER);

		JPanel panelButton = new JPanel();
		frmShift.getContentPane().add(panelButton, BorderLayout.SOUTH);
		panelButton.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JButton btnEdit = new JButton(Message.getMessage(MessageName.BUTTON_EDIT));
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openEditTimeDialog();
			}
		});
		panelButton.add(btnEdit);

		// Load Data
		employees = FileUtility.load();
		tableModel.fireTableDataChanged();
		radioButtons.get(config.language).setSelected(true);
		// *************************************************************
		// DEBUG
		boolean DEBUG = false;
		if (DEBUG) {
			employees.addEmployee(new Employee("Jerry"));
			employees.getEmployee("Jerry").addShift(Week.Day.MON,
					new Shift(employees.getEmployee("Jerry"), LocalTime.now(), LocalTime.now().plusHours(10)), false);
			employees.addEmployee(new Employee("Tom"));
			employees.getEmployee("Tom").addShift(Week.Day.TUE,
					new Shift(employees.getEmployee("Tom"), LocalTime.now().plusHours(1), LocalTime.now().plusHours(2)),
					false);
			employees.getEmployee("Tom").addShift(Week.Day.TUE, new Shift(employees.getEmployee("Tom"),
					LocalTime.now().plusHours(5), LocalTime.now().plusHours(10)), false);
			tableModel.fireTableDataChanged();
		}

		// *************************************************************

		// If there is Employee then Remove Employee enable otherwise it will be disable
		if (employees.getCount() == 0) {
			menuitemEmployeeMenuRemove.setEnabled(false);
		}
	}

	/**
	 * Call this function when need to open edit time dialog
	 */
	private void openEditTimeDialog() {
		// only call the new dialog when the selection is between MON - SUN
		if (table.getSelectedColumn() > 0 && table.getSelectedColumn() < 8 && table.getSelectedRow() > -1) {
			Employee selectedEmployee = employees.getEmployee(table.getSelectedRow());
			Week.Day selectedDay = Week.Day.values()[table.getSelectedColumn() - 1];
			DialogEdittime editTime = new DialogEdittime(frmShift, selectedEmployee, selectedDay,
					checkboxmenuitemSettingMenuOverLapShift.getState(), checkboxmenuitemSettingMenu24Hr.getState());
			editTime.setVisible(true);
			tableModel.fireTableDataChanged();
			// Auto save
			FileUtility.save(employees);
		}
	}

	/**
	 * Action Listener for change Language
	 * 
	 * @author Chongju Mai
	 * 
	 */
	private class LanguageActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			for (Entry<Language, JRadioButtonMenuItem> entry : radioButtons.entrySet()) {
				if (entry.getValue() == (JRadioButtonMenuItem) e.getSource()) {
					config.language = entry.getKey();
				}
			}
			FileUtility.saveConfig(config);
			JOptionPane.showMessageDialog(frmShift, Message.getMessage(MessageName.INFO_DIALOG_CHANGE_LANGUAGE),
					Message.getMessage(MessageName.INFO_GENERAL_TITLE), JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private class MainTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		private String[] columnsName = { Message.getMessage(MessageName.EMPLOYEE_NAME),
				Message.getMessage(MessageName.DAY_MON), Message.getMessage(MessageName.DAY_TUE),
				Message.getMessage(MessageName.DAY_WED), Message.getMessage(MessageName.DAY_THU),
				Message.getMessage(MessageName.DAY_FRI), Message.getMessage(MessageName.DAY_SAT),
				Message.getMessage(MessageName.DAY_SUN), Message.getMessage(MessageName.TOTAL_HOURS) };

		public String getColumnName(int col) {
			return columnsName[col];
		}

		@Override
		public int getColumnCount() {
			return columnsName.length;
		}

		@Override
		public int getRowCount() {

			if (employees != null) {
				return employees.getCount();
			}
			return 0;
		}

		@Override
		public Object getValueAt(int row, int col) {
			// First Column is the name of the employee
			if (col == 0) {
				return employees.getEmployee(row).name;
			} else if (col == table.getColumnCount() - 1) {
				DecimalFormat df = new DecimalFormat("#.#");
				return df.format(employees.getEmployee(row).getTotalHours());
			}
			return employees.getEmployee(row).getDay(Week.Day.values()[col - 1])
					.toString(checkboxmenuitemSettingMenu24Hr.getState());
		}

	}

	private class MainTableModelListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent arg0) {
			for (int i = 0; i < employees.getCount(); i++) {
				int row = 1;
				if (employees.getEmployee(i).getMaxShift() > row) {
					row = employees.getEmployee(i).getMaxShift();
				}
				table.setRowHeight(i, row * PERSHIFTHEIGHT);
			}
		}
	}

	private class MultipleLineTableCellRenderer extends JTextArea implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public MultipleLineTableCellRenderer() {
			setLineWrap(true);
			setWrapStyleWord(true);
			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (isSelected && column < table.getColumnCount() - 1) {
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(table.getBackground());
			}
			setText(String.valueOf(value));
			return this;
		}

	}

}

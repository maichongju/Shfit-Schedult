package shiftschedule;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.optionalusertools.TimeChangeListener;
import com.github.lgooddatepicker.zinternaltools.TimeChangeEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DialogEdittime extends JDialog {

	private static final long serialVersionUID = 1L;

	// Component
	private final JPanel contentPanel = new JPanel();
	private JButton btnAddShift = new JButton(Message.getMessage(MessageName.BUTTON_ADD_SHIFT));

	// Class Content
	private boolean use24 = false;
	private ArrayList<panelShift> shiftList = new ArrayList<panelShift>();
	private int shiftNum = 1;
	private final int MAXSHIFT = 3;
	private final int SHIFTHEIGHT = 40;
	private final int DEFAULTWIDTH = 630;

	/**
	 * Create the dialog.
	 */
	public DialogEdittime(JFrame frame, Employee employee, shiftschedule.Week.Day day, boolean overlap, boolean use24) {
		super(frame, employee.name + " - " + day.name(), true);
		setResizable(false);
		this.use24 = use24;
		setBounds(100, 100, DEFAULTWIDTH, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				buttonPane.setLayout(new BorderLayout(0, 0));
				{
					JPanel panelButtonRight = new JPanel();
					buttonPane.add(panelButtonRight, BorderLayout.EAST);
					JButton btnSave = new JButton(Message.getMessage(MessageName.BUTTON_SAVE));
					panelButtonRight.add(btnSave);
					btnSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							/*
							 * Clear all the shifts and then recreate shift and then added into Day.
							 */
							shiftschedule.Day currentDay = employee.getDay(day);
							currentDay.clearAllShifts();
							for (panelShift shift : shiftList) {
								if (shift.isTimeValid()) {
									currentDay.addShift(new Shift(employee, shift.getStartTime(), shift.getEndTime()),
											overlap);
								}
							}
							dispose();
						}
					});
					getRootPane().setDefaultButton(btnSave);
					{
						JButton btnCancel = new JButton(Message.getMessage(MessageName.BUTTON_CANCEL));
						panelButtonRight.add(btnCancel);
						btnCancel.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								// Close Dialog
								dispose();
							}
						});
					}
				}
				{
					JPanel panelButtonLeft = new JPanel();
					buttonPane.add(panelButtonLeft, BorderLayout.WEST);
					panelButtonLeft.add(btnAddShift);
					{
						JButton btnClearAllShift = new JButton(Message.getMessage(MessageName.BUTTON_CLEAR_ALL_SHIFT));
						btnClearAllShift.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {

								Object[] options = { Message.getMessage(MessageName.BUTTON_YES),
										Message.getMessage(MessageName.BUTTON_NO) };

								int n = JOptionPane.showOptionDialog(contentPanel,
										Message.getMessage(MessageName.WARNING_DIALOG_EDIT_TIME_CEALR_ALL_SHIFT),
										Message.getMessage(MessageName.WARNING_GENERAL_TITLE),
										JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, // Not using a
																										// custom
																										// icon
										options, // the titles of buttons
										options[1]); // default button title
								// If user click yes then clear all the shift
								if (n == JOptionPane.YES_OPTION) {
									for (panelShift panel : shiftList) {
										panel.clearShift();
									}
								}

							}
						});
						panelButtonLeft.add(btnClearAllShift);
					}
					btnAddShift.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// Increase window size
							setSize(DEFAULTWIDTH, getSize().height + SHIFTHEIGHT);
							// Add shift
							addShift();
						}
					});
				}
			}
			{
				// Load Shift
				ArrayList<Shift> shifts = employee.getShifts(day);
				// If there is exist shift then load it in
				if (shifts.size() > 0) {
					for (Shift shift : shifts) {
						addShift(shift);
					}
					if (shifts.size() > 1) {
						setSize(DEFAULTWIDTH, getSize().height + SHIFTHEIGHT);
					}
				} else {
					// No exist shift just add one shift
					addShift();
					
				}
			}
		}
	}

	/**
	 * Remove the given shift from the panel
	 * 
	 * @param shift
	 */
	private void removeShift(panelShift shift) {
		// Remove from shift list
		shiftList.remove(shift);
		// Remove from the panel
		contentPanel.remove(shift);
		for (int i = shift.shiftNum - 1; i < shiftList.size(); i++) {
			shiftList.get(i).shiftNum -= 1;
			shiftList.get(i).updateText();
		}
		// Update the current shift number
		shiftNum--;
		// Update the size of the Window
		btnAddShift.setEnabled(true);
		// Disable Remove Shift button
		if (shiftList.size()==1) {
			shiftList.get(0).setbtnRemoveEnable(false);
		}
		setSize(DEFAULTWIDTH, getSize().height - SHIFTHEIGHT);
	}

	/**
	 * Add a Shift to the panel, it will only show the three shifts
	 */
	private void addShift() {
		_addShift();
	}

	/**
	 * Add shift and load the given shift
	 * 
	 * @param shift
	 */
	private void addShift(Shift shift) {
		panelShift shiftpanel = _addShift();
		// if _addShift return null means add shift fail
		if (shiftpanel != null) {
			shiftpanel.setTime(shift.startTime, shift.endTime);
		}
	}

	/**
	 * private helper function for addShift
	 * 
	 * @return
	 */
	private panelShift _addShift() {
		panelShift panel = null;
		if (shiftNum <= MAXSHIFT) {
			panel = new panelShift(shiftNum);
			shiftList.add(panel);
			contentPanel.add(panel);
			// Increase shift number
			shiftNum++;
		}

		// reach max number shift, disable add button
		if (shiftNum > MAXSHIFT) {
			btnAddShift.setEnabled(false);
		}
		// More than one shift enable remove 
		if (shiftList.size() > 1) {
			shiftList.get(0).setbtnRemoveEnable(true);
		}else {
			shiftList.get(0).setbtnRemoveEnable(false);
		}
		return panel;
	}

	private class panelShift extends JPanel {

		private static final long serialVersionUID = 1L;
		public int shiftNum = 0;
		private TimePicker startTimePicker;
		private TimePicker endTimePicker;
		private JLabel lblTotalHours = new JLabel(String.format(Message.getMessage(MessageName.SHIFT_TOTAL_HOURS), 0));
		private JLabel lblShift = new JLabel(String.format(Message.getMessage(MessageName.SHIFT_NUM), shiftNum));
		private JButton btnRemoveShift;

		/**
		 * Constructor
		 * 
		 * @param shiftNum shift Number for this panel, for save file purpose
		 */
		public panelShift(int shiftNum) {
			this.shiftNum = shiftNum;

			FlowLayout flowLayout = (FlowLayout) getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);

			TimePickerSettings timeSettings = new TimePickerSettings();
			timeSettings.setDisplayToggleTimeMenuButton(false);
			timeSettings.setDisplaySpinnerButtons(true);
			if (use24) {
				timeSettings.use24HourClockFormat();
			}
			startTimePicker = new TimePicker(timeSettings);
			endTimePicker = new TimePicker(timeSettings);

			startTimePicker.addTimeChangeListener(new ShiftTimeChangeListener());
			endTimePicker.addTimeChangeListener(new ShiftTimeChangeListener());

			JLabel lblStartTime = new JLabel(Message.getMessage(MessageName.SHIFT_START_TIME));
			JLabel lblEndTime = new JLabel(Message.getMessage(MessageName.SHIFT_END_TIME));
			btnRemoveShift = new JButton(Message.getMessage(MessageName.SHIFT_REMOVE_SHIFT));
			{
				add(lblShift);
				updateText();
				add(Box.createRigidArea(new Dimension(30, 0)));
				add(lblStartTime);
				add(startTimePicker);
				add(Box.createRigidArea(new Dimension(10, 0)));
				add(lblEndTime);
				add(endTimePicker);
				add(Box.createRigidArea(new Dimension(15, 0)));
				add(lblTotalHours);
				add(Box.createRigidArea(new Dimension(10, 0)));
				add(btnRemoveShift);
				btnRemoveShift.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						removeShift(panelShift.this);
					}

				});

			}
		}

		/**
		 * Load the time to the panel
		 * 
		 * @param startTime Start time for the shift
		 * @param endTime   End time for the shift
		 */
		public void setTime(LocalTime startTime, LocalTime endTime) {
			startTimePicker.setTime(startTime);
			endTimePicker.setTime(endTime);
		}

		/**
		 * Get the start time for this shift
		 * 
		 * @return
		 */
		public LocalTime getStartTime() {
			return startTimePicker.getTime();
		}

		/**
		 * Get the end time for this shift
		 * 
		 * @return
		 */
		public LocalTime getEndTime() {
			return endTimePicker.getTime();
		}

		/**
		 * Clear all the shift data in the panel
		 */
		public void clearShift() {
			startTimePicker.setTime(null);
			endTimePicker.setTime(null);
		}
		
		/**
		 * Enable and disable Remove Shift button
		 * @param enable
		 */
		public void setbtnRemoveEnable(boolean enable) {
			btnRemoveShift.setEnabled(enable);
		}
		
		/**
		 * Check if the current time is valid
		 * 
		 * @return true if the time is valid false if the time is invalid
		 */
		public boolean isTimeValid() {
			if (startTimePicker.getTime() == null || endTimePicker.getTime() == null) {
				return false;
			}
			return true;
		}

		/**
		 * Update the text for the panel
		 */
		public void updateText() {
			lblShift.setText(String.format(Message.getMessage(MessageName.SHIFT_NUM), shiftNum));
		}

		private class ShiftTimeChangeListener implements TimeChangeListener {

			/**
			 * timeChanged, This function will be called whenever the time in the applicable
			 * time picker has changed. Note that the value may contain null, which
			 * represents a cleared or empty time.
			 */
			@Override
			public void timeChanged(TimeChangeEvent arg0) {
				// update the actually time if both start and end time are not empty
				if (isTimeValid()) {
					double totalHour = (startTimePicker.getTime().until(endTimePicker.getTime(), ChronoUnit.MINUTES));
					if (totalHour < 0) {
						totalHour = (24 * 60 + totalHour);
					}
					totalHour /= 60.0;

					// Force number show decimal if needed
					NumberFormat numberFormat = new DecimalFormat("#.#");
					lblTotalHours.setText(String.format(Message.getMessage(MessageName.SHIFT_TOTAL_HOURS),
							numberFormat.format(totalHour)));
				} else {
					lblTotalHours.setText(String.format(Message.getMessage(MessageName.SHIFT_TOTAL_HOURS), 0));
				}

			}

		}
	}
}

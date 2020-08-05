package shiftschedule;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class DialogAddEmployee extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EmployeeList Employees;
	private final JPanel contentPanel = new JPanel();
	private JPanel buttonPane;
	private JTextField textFieldName;

	/**
	 * Create the dialog.
	 */
	public DialogAddEmployee(JFrame frame, String title, EmployeeList employeelist) {
		super(frame, title, true);
		Employees = employeelist;
		setResizable(false);
		setBounds(100, 100, 350, 108);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		{
			buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			{
				JButton btnAdd = new JButton(Message.getMessage(MessageName.BUTTON_ADD));
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String EmployeeName = textFieldName.getText().trim();
						Employee employee = new Employee(EmployeeName);

						// Only add to the employee list if name is not empty

						if (EmployeeName.equals("")) {
							JOptionPane.showMessageDialog(contentPanel,
									String.format(Message.getMessage(MessageName.ERROR_GENERAL_FIELDEMPTY),
											Message.getMessage(MessageName.EMPLOYEE_NAME)),
									Message.getMessage(MessageName.INFO_GENERAL_TITLE),
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							if (Employees.addEmployee(employee)) {
								dispose(); // Close Dialog
							} else {
								JOptionPane.showMessageDialog(contentPanel,
										String.format(
												Message.getMessage(
														MessageName.ERROR_EMPLOYEELIST_EMPLOYEEEXIST),
												textFieldName.getText()),
										Message.getMessage(MessageName.ERROR_GENERAL_TITLE),
										JOptionPane.ERROR_MESSAGE);
							}
						}

					}
				});
				buttonPane.add(btnAdd);
				getRootPane().setDefaultButton(btnAdd);
			}
			{
				JButton btnCancel = new JButton(Message.getMessage(MessageName.BUTTON_CANCEL));
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				buttonPane.add(btnCancel);
			}
		}
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblName = new JLabel(Message.getMessage(MessageName.EMPLOYEE_NAME));

		textFieldName = new JTextField();
		textFieldName.setColumns(10);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblName)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(158, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblName)
						.addComponent(textFieldName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}

}

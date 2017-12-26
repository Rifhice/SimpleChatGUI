package componentSwing;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MyJTextField extends JTextField{

	boolean initial = true;
	
	/**
	 * Constructor of the class MyJTextField. 
	 * @param  label the label of the text field
	 * @param  bounds X and Y of the text field
	 * @param  width the width of the text field
	 * @param  height the height of the text field
	 * @param  listener the listener of the text field
	 */
	public MyJTextField(String label,Rectangle2D.Float bounds, int width, int height, ActionListener listener) {
		new JTextField();
		setText(label);
	    addFocusListener(new FocusListener(){
	        @Override
	        public void focusGained(FocusEvent e){
	        	setBackground(Color.white);
	            setText("");
	            initial = false;
	        }
			@Override
			public void focusLost(FocusEvent arg0) {
				
			}
	    });
	    getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				setBackground(Color.white);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {				
			}
	        // implement the methods
	    });
		setBounds((int)(bounds.getX() * width), (int)(bounds.getY() * height), (int)(bounds.getWidth() * width), (int)(bounds.getHeight() * height));
		addActionListener(listener);
	}
	

	/**
	 * @return if true if the message is the default message
	 */
	public boolean isInitialMessage() {
		return initial;
	}
	
}

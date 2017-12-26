package componentSwing;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;

public class MyButton extends JButton{

	/**
	 * Constructor of the class MyButton. 
	 * @param  label the label of the button
	 * @param  bounds X and Y of the button
	 * @param  width the width of the button
	 * @param  height the height of the button
	 * @param  listener the listener of the button
	 * @param  actioncommand the command triggered by the button
	 */
	public MyButton(String label,Rectangle2D.Float bounds,int width, int height, ActionListener listener, String actioncommand) {
		new JButton();
		setText(label);
		setBounds((int)(bounds.getX() * width), (int)(bounds.getY() * height), (int)(bounds.getWidth() * width), (int)(bounds.getHeight() * height));
		addActionListener(listener);
		setActionCommand(actioncommand);
	}
	
}

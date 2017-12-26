package componentSwing;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyFrame extends JFrame{
	private	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private	int width;
	private	int height;
	
	/**
	 * Constructor of the class MyFrame. 
	 * @param  width the width of the frame
	 * @param  height the height of the frame
	 */	
	public MyFrame(float width, float height){
		new JFrame();
		this.width = (int)(screenSize.getWidth()* width);
		this.height = (int)(screenSize.getHeight()* height);
		this.setTitle("Execute");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize((int)(this.width),(int)(this.height));
		this.setVisible(true);
		this.setResizable(false);		
		this.setLayout(null);
        this.setVisible(true);
	}

	/**
	 * Updates the panel 
	 * @param  panel the panel to update
	 */
	public void changePanel(JPanel panel){
		this.setContentPane(panel);
		setVisible(true);	
	}
	
	/**
	 * @return the width of the panel
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return the height of the panel
	 */
	public int getHeight() {
		return height;
	}
	
}

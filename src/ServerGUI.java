import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import componentSwing.MyButton;
import componentSwing.MyFrame;
import componentSwing.MyJTextField;
import client.*;
import common.*;
import server.EchoServer;

public class ServerGUI extends JPanel implements ChatIF, ActionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default port the server listen to.
	 */
	final public static int DEFAULT_PORT = 5556;

	/**
	 * Title of the frame.
	 */
	final public static String TITLE = "SimpleChat4";

	private int width;
	private int height;

	private JFrame frame;

	private int port;

	private JTextPane textArea;
	private MyJTextField messageTextField;
	private MyButton start;
	private MyButton stop;
	private JComboBox users;

	private EchoServer server;

	private Rectangle2D.Float getKillButtonBounds = new Rectangle2D.Float(0.81f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float killComboBoxBounds = new Rectangle2D.Float(0.70f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float startstopButtonBounds = new Rectangle2D.Float(0.08f, 0.06f, 0.25f, 0.07f);
	private Rectangle2D.Float getPortButtonBounds = new Rectangle2D.Float(0.48f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float setPortButtonBounds = new Rectangle2D.Float(0.59f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float chatBounds = new Rectangle2D.Float(0.08f, 0.17f, 0.84f, 0.69f);
	private Rectangle2D.Float messageAreaBounds = new Rectangle2D.Float(0.08f, 0.87f, 0.73f, 0.07f);
	private Rectangle2D.Float sendBounds = new Rectangle2D.Float(0.83f, 0.87f, 0.09f, 0.07f);

	/**
	 * Constructor of the class ServerGUI. 
     * @param  port the port number.
     * @param frame the frame of the chat
	 */
	public ServerGUI(int port, JFrame frame) {
		this.port = port;
		server = new EchoServer(port, this);
		this.frame = frame;
		setBackground(Color.LIGHT_GRAY);
		this.setLayout(null);
		this.setVisible(true);
		setLayout(null);
		setSize(frame.getWidth(), frame.getHeight());

		this.width = frame.getWidth();
		this.height = frame.getHeight();

		add(new MyButton("Envoyer", sendBounds, this.width, this.height, this, "send"));
		add(new MyButton("SetPort", setPortButtonBounds, this.width, this.height, this, "setport"));
		add(new MyButton("GetPort", getPortButtonBounds, this.width, this.height, this, "getport"));
		add(new MyButton("Kill", getKillButtonBounds, this.width, this.height, this, "kill"));
		start = new MyButton("D�marrer", startstopButtonBounds, this.width, this.height, this, "start");
		stop = new MyButton("Arr�ter", startstopButtonBounds, this.width, this.height, this, "stop");
		stop.setVisible(false);
		add(start);
		add(stop);

		users = new JComboBox();
		users.setBounds((int) (killComboBoxBounds.getX() * this.width), (int) (killComboBoxBounds.getY() * this.height),
				(int) (killComboBoxBounds.getWidth() * this.width),
				(int) (killComboBoxBounds.getHeight() * this.height));
		users.addActionListener(this);
		add(users);

		messageTextField = new MyJTextField("Ecrivez un message", messageAreaBounds, this.width, this.height, this);
		messageTextField.addKeyListener(this);

		add(messageTextField);

		EmptyBorder eb = new EmptyBorder(new Insets(10, 10, 10, 10));

		textArea = new JTextPane();
		textArea.setBorder(eb);
		textArea.setMargin(new Insets(5, 5, 5, 5));
		JScrollPane scrollpane = new JScrollPane(textArea);
		scrollpane.setBounds((int) (chatBounds.getX() * this.width), (int) (chatBounds.getY() * this.height),
				(int) (chatBounds.getWidth() * this.width), (int) (chatBounds.getHeight() * this.height));
		add(scrollpane);

		this.setVisible(true);
		((MyFrame) frame).changePanel(this);
	}

	private void appendToPane(JTextPane tp, String msg, Color c, int align) {
		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.replaceSelection(msg);
		SimpleAttributeSet attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, "Lucida Console");
		StyleConstants.setForeground(attribs, c);
		StyleConstants.setAlignment(attribs, align);
		tp.setParagraphAttributes(attribs, true);
	}

	/**
	 * Test if it is a new client
	 * @param  message the message received
	 * @return the new client if it's a new client 
	 */
	public String isNewClient(String message) {
		if (message.startsWith("Nouveau client connect� : ")) {
			return message.substring("Nouveau client connect� : ".length());
		}
		return null;
	}

	@Override
	public void display(String message) {
		if (isNewClient(message) != null) {
			users.addItem(isNewClient(message));
		}
		appendToPane(textArea, "\n", Color.red, StyleConstants.ALIGN_CENTER);
		appendToPane(textArea, message + "\n", Color.red, StyleConstants.ALIGN_CENTER);
		if (message.startsWith("D�connexion de")) {
			users.removeItem(message.split(" ")[message.split(" ").length - 1]);
		}
	}


	/**
	 * Extract the pseudo of the user who sent the message.
	 * @param message the message received
	 * @return the pseudo of the sender
	 */
	public String extractPseudoMessage(String message) {
		return message.split(" ")[0];
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
		case "send":
			if (!messageTextField.isInitialMessage()) {
				if (!messageTextField.getText().equals("")) {
					server.handleMessageFromServerUI(messageTextField.getText());
					messageTextField.setText("");
				} else {
					messageTextField.setBackground(Color.red);
				}
			}
			break;
		case "stop":
			server.handleMessageFromServerUI("#stop");
			stop.setVisible(false);
			start.setVisible(true);
			break;
		case "getport":
			server.handleMessageFromServerUI("#getport");
			break;
		case "setport":
			Object tmp = JOptionPane.showInputDialog(this, "Entrez le port", "Changement de port",
					JOptionPane.PLAIN_MESSAGE, null, null, this.port);
			if (tmp != null) {
				server.handleMessageFromServerUI("#setport " + tmp);
			}
			// Send new port
			break;
		case "start":
			server.handleMessageFromServerUI("#start");
			stop.setVisible(true);
			start.setVisible(false);
			break;
		case "kill":
			server.handleMessageFromServerUI("#kill " + (String) users.getSelectedItem());
			users.removeItemAt(users.getSelectedIndex());
			break;
		default:
			break;
		}
	}

	@Override
	public void keyPressed(KeyEvent k) {
		if (k.getKeyCode() == KeyEvent.VK_ENTER) {
			actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "send"));
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		int port = DEFAULT_PORT;
		new ServerGUI(port, new MyFrame(0.5f, 0.5f));
	}

}
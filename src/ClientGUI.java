import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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

public class ClientGUI extends JPanel implements ChatIF, ActionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Title of the frame. 
	 */
	final public static String TITLE = "SimpleChat4";
	/**
	 * Default port the client can connect to. 
	 */
	final public static int DEFAULT_PORT = 5556;
	/**
	 * Default host the client can connect to. 
	 */
	final public static String DEFAULT_HOST = "localhost";
	
	private int width;
	private int height;

	private JFrame frame;
	
	private int port;
	private String host;

	private JTextPane textArea;
	private MyJTextField messageTextField;
	private MyJTextField loginTextField;
	private MyButton login;
	private MyButton logout;

	private ArrayList<String> lastMessages = new ArrayList<String>();
	private int currentMessageIndex = 0;

	private ChatClient client;

	private Rectangle2D.Float pseudoBounds = new Rectangle2D.Float(0.08f, 0.06f, 0.2f, 0.07f);
	private Rectangle2D.Float loginButtonBounds = new Rectangle2D.Float(0.3f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float logoutButtonBounds = new Rectangle2D.Float(0.08f, 0.06f, 0.25f, 0.07f);
	private Rectangle2D.Float getPortButtonBounds = new Rectangle2D.Float(0.48f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float setPortButtonBounds = new Rectangle2D.Float(0.59f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float getHostButtonBounds = new Rectangle2D.Float(0.70f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float setHostButtonBounds = new Rectangle2D.Float(0.81f, 0.06f, 0.1f, 0.07f);
	private Rectangle2D.Float chatBounds = new Rectangle2D.Float(0.08f, 0.17f, 0.84f, 0.69f);
	private Rectangle2D.Float messageAreaBounds = new Rectangle2D.Float(0.08f, 0.87f, 0.73f, 0.07f);
	private Rectangle2D.Float sendBounds = new Rectangle2D.Float(0.83f, 0.87f, 0.09f, 0.07f);

	/**
	 * Constructor of the class ClientGUI. 
	 * @param  host  the server's host name.
	 * @param  port  the port number.
	 * @param frame the frame of the chat
	 */
	public ClientGUI(String host, int port, JFrame frame) {
		this.port = port;
		this.host = host;
		client = new ChatClient(host, port, this, DEFAULT_PSEUDO);
		this.frame = frame;
		setBackground(Color.LIGHT_GRAY);
		this.setLayout(null);
		this.setVisible(true);
		setLayout(null);
		setSize(frame.getWidth(), frame.getHeight());

		this.width = frame.getWidth();
		this.height = frame.getHeight();

		add(new MyButton("Envoyer", sendBounds, this.width, this.height, this, "send"));
		add(new MyButton("SetHost", setHostButtonBounds, this.width, this.height, this, "sethost"));
		add(new MyButton("GetHost", getHostButtonBounds, this.width, this.height, this, "gethost"));
		add(new MyButton("SetPort", setPortButtonBounds, this.width, this.height, this, "setport"));
		add(new MyButton("GetPort", getPortButtonBounds, this.width, this.height, this, "getport"));
		login = new MyButton("Connexion", loginButtonBounds, this.width, this.height, this, "login");
		logout = new MyButton("D�connexion", logoutButtonBounds, this.width, this.height, this, "logout");
		logout.setVisible(false);
		add(login);
		add(logout);

		messageTextField = new MyJTextField("Ecrivez un message", messageAreaBounds, this.width, this.height, this);
		messageTextField.addKeyListener(this);
		loginTextField = new MyJTextField("Login", pseudoBounds, this.width, this.height, this);

		add(messageTextField);
		add(loginTextField);

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
	
	@Override
	/**
	 * Display the messages. 
	 * @param  message the message received
	 */
	public void display(String message) {
		if (isServerMessage(message)) {
			appendToPane(textArea, "\n", Color.red, StyleConstants.ALIGN_CENTER);
			appendToPane(textArea, extractServerMessage(message) + "\n", Color.red, StyleConstants.ALIGN_CENTER);
		} else {
			String pseudo = extractPseudoMessage(message);
			if (pseudo.equals(client.getPseudo())) {
				appendToPane(textArea, "\n", Color.black, StyleConstants.ALIGN_RIGHT);
				appendToPane(textArea, lastMessages.get(lastMessages.size() - 1) + " \n", Color.black,
						StyleConstants.ALIGN_RIGHT);
			} else {
				appendToPane(textArea, "\n", Color.black, StyleConstants.ALIGN_LEFT);
				appendToPane(textArea, message + "\n", Color.black, StyleConstants.ALIGN_LEFT);
			}
		}
		if (message.contains("La connexion a �t� ferm�e")) {
			logout.setVisible(false);
			loginTextField.setVisible(true);
			login.setVisible(true);
		}

	}

	/**
	 * Test if the message is from the server.
	 * @param message the message received
	 * @return true if the message is from the server
	 */
	public boolean isServerMessage(String message) {
		return message.startsWith(EchoServer.MESSAGE_PREFIX);
	}

	/**
	 * Extract the content of the server message. 
	 * @param message the message received
	 * @return the content of the message without the prefix
	 */
	public String extractServerMessage(String message) {
		return message.substring(EchoServer.MESSAGE_PREFIX.length());
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
	/**
	 * Execute the action triggered by the command received.
	 * @param e the action triggering
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
		case "send":
			if (!messageTextField.isInitialMessage()) {
				if (!messageTextField.getText().equals("")) {
					lastMessages.add(messageTextField.getText());
					client.handleMessageFromClientUI(messageTextField.getText());
					messageTextField.setText("");
				}
				 else {
						messageTextField.setBackground(Color.red);
					}
			} else {
				messageTextField.setBackground(Color.red);
			}
			break;
		case "getport":
			client.handleMessageFromClientUI("#getport");
			break;
		case "setport":
			Object tmp = JOptionPane.showInputDialog(this, "Entrez le port", "Changement de port",
					JOptionPane.PLAIN_MESSAGE, null, null, this.port);
			if (tmp != null) {
				client.handleMessageFromClientUI("#setport " + tmp.toString());
				this.port = Integer.parseInt((String) tmp);
			}
			break;
		case "gethost":
			client.handleMessageFromClientUI("#gethost");
			break;
		case "sethost":
			Object tmp2 = JOptionPane.showInputDialog(this, "Entrez le port", "Changement de port",
					JOptionPane.PLAIN_MESSAGE, null, null, this.host);
			if (tmp2 != null) {
				client.handleMessageFromClientUI("#sethost " + tmp2.toString());
				this.host = (String) tmp2;
			}
			break;
		case "login":
			if (!loginTextField.getText().equals("") && !loginTextField.isInitialMessage()) {
				client.handleMessageFromClientUI("#login " + loginTextField.getText());
				if (!client.getPseudo().equals(DEFAULT_PSEUDO)) {
					logout.setVisible(true);
					loginTextField.setVisible(false);
					login.setVisible(false);
				}
			}
			break;
		case "logout":
			client.handleMessageFromClientUI("#logoff");
			logout.setVisible(false);
			loginTextField.setVisible(true);
			login.setVisible(true);
			break;
		default:
			break;
		}
	}

	@Override
	/**
	 * Execute the action triggered by a key pressed.
	 * @param k the key pressed by the user
	 */
	public void keyPressed(KeyEvent k) {
		if (k.getKeyCode() == KeyEvent.VK_ENTER) {
			actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "send"));
		} else if (k.getKeyCode() == KeyEvent.VK_UP) {
			if (currentMessageIndex < lastMessages.size()) {
				messageTextField.setText(lastMessages.get((lastMessages.size() - 1) - currentMessageIndex));
				currentMessageIndex++;
				if (currentMessageIndex == lastMessages.size()) {
					currentMessageIndex--;
				}
			}
		} else if (k.getKeyCode() == KeyEvent.VK_DOWN) {
			if (currentMessageIndex > 0) {
				currentMessageIndex--;
				if (currentMessageIndex < lastMessages.size()) {
					messageTextField.setText(lastMessages.get((lastMessages.size() - 1) - currentMessageIndex));
				}
			} else {
				messageTextField.setText("");
			}
		}
	}

	@Override
	/**
	 * Execute the action triggered by a key released.
	 * @param arg0 the key pressed by the user
	 */
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	/**
	 * Execute the action triggered by a key typed.
	 * @param arg0 the key pressed by the user
	 */
	public void keyTyped(KeyEvent arg0) {
	}

	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		String host = DEFAULT_HOST;
		int port = DEFAULT_PORT;
		new ClientGUI(host, port, new MyFrame(0.5f, 0.5f));
	}

}
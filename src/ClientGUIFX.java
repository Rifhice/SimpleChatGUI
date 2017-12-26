import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

import javax.swing.JOptionPane;

import client.ChatClient;
import common.ChatIF;
import componentJavaFX.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;	
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClientGUIFX extends Application implements ChatIF{
    
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
	
	private	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private	float widthRatio = 0.5f;
	private	float heightRatio = 0.5f;
	
	private int width;
	private int height;
	
	private int port;
	private String host;
	
	private MyButtonFX login;
	private MyButtonFX logout;
	private MyTextFieldFX messageTextField;
	private MyTextFieldFX loginTextField;
	
	private ChatClient client;
	private TextArea textarea; 
	
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
	
    @Override
    public void start(Stage primaryStage) {
    	
    	this.host = DEFAULT_HOST;
    	this.port = DEFAULT_PORT;
    	
    	client = new ChatClient(host, port, this, DEFAULT_PSEUDO);
    	
        primaryStage.setTitle(TITLE);
        Group root = new Group();
		this.width = (int)(screenSize.getWidth()* widthRatio);
		this.height = (int)(screenSize.getHeight()* heightRatio);
        Scene scene = new Scene(root, this.width, this.height, Color.LIGHTGREEN);
        
        root.getChildren().add(new MyButtonFX("Envoyer", sendBounds, this.width, this.height, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				client.handleMessageFromClientUI(messageTextField.getText() );
				messageTextField.setText("");
			}
		}));
        
        
        root.getChildren().add(new MyButtonFX("SetHost", setHostButtonBounds, this.width, this.height, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog(host);
				dialog.setTitle("Change host");
				dialog.setContentText("Entrez le nouveau host :");

				// Traditional way to get the response value.
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()){
					client.handleMessageFromClientUI("#sethost " + result.get());
					host = (String) result.get();
				}
			}
		}));
        
        
        root.getChildren().add(new MyButtonFX("GetHost", getHostButtonBounds, this.width, this.height, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				client.handleMessageFromClientUI("#gethost");
			}
		}));
        
        
        root.getChildren().add(new MyButtonFX("SetPort", setPortButtonBounds, this.width, this.height, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog(port + "");
				dialog.setTitle("Change port");
				dialog.setContentText("Entrez le nouveau port :");

				// Traditional way to get the response value.
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()){
					client.handleMessageFromClientUI("#setport " + Integer.parseInt(result.get()));
					port = Integer.parseInt(result.get());
				}
			}
		}));
        
        
        root.getChildren().add(new MyButtonFX("GetPort", getPortButtonBounds, this.width, this.height, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				client.handleMessageFromClientUI("#getport");
			}
		}));
        
        
		login = new MyButtonFX("Connexion", loginButtonBounds, this.width, this.height, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if (!loginTextField.getText().equals("") && !loginTextField.isInitialMessage()) {
					client.handleMessageFromClientUI("#login " + loginTextField.getText());
					if (!client.getPseudo().equals(DEFAULT_PSEUDO)) {
						logout.setVisible(true);
						loginTextField.setVisible(false);
						login.setVisible(false);
					}
				}
			}
		});
		
		
		logout = new MyButtonFX("Déconnexion", logoutButtonBounds, this.width, this.height, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				client.handleMessageFromClientUI("#logoff");
				logout.setVisible(false);
				loginTextField.setVisible(true);
				login.setVisible(true);
			}
		});
		
		
		logout.setVisible(false);
		root.getChildren().add(login);
		root.getChildren().add(logout);
		
		messageTextField = new MyTextFieldFX("Ecrivez un message...", messageAreaBounds, width, height);
		root.getChildren().add(messageTextField);		
		
		loginTextField = new MyTextFieldFX("login", pseudoBounds, width, height);
		root.getChildren().add(loginTextField);

		textarea = new TextArea();
		textarea.setEditable(false);
		textarea.setLayoutX((int)(chatBounds.getX() * width));
		textarea.setLayoutY((int)(chatBounds.getY() * height));
		textarea.setMinWidth((int)(chatBounds.getWidth() * width));
		textarea.setMinHeight((int)(chatBounds.getHeight() * height));
		textarea.setOnInputMethodTextChanged(new EventHandler() {

			@Override
			public void handle(Event event) {
				textarea.setScrollTop(Double.MAX_VALUE); 
			}
		});
		root.getChildren().add(textarea);		

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
	@Override
	public void display(String message) {
		addToPane(message);
		if (message.contains("La connexion a été fermée")) {
			logout.setVisible(false);
			loginTextField.setVisible(true);
			login.setVisible(true);
		}
	}
	
	private void addToPane(String message) {
		textarea.appendText(message + "\n");
	}
	
    public static void main(String[] args) {
		String host = DEFAULT_HOST;
		int port = DEFAULT_PORT;
        Application.launch(ClientGUIFX.class, args);
    }

}
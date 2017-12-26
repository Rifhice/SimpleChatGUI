// This file contains material supporting section 3.7 of the textbook:// "Object Oriented Software Engineering" and is issued under the open-source// license found at www.lloseng.com package client;import client.*;import common.*;import ocsf.client.*;import java.io.*;import java.lang.reflect.Array;import java.util.ArrayList;import java.util.Arrays;import java.util.Observable;import java.util.Observer;/** * This class overrides some of the methods defined in the abstract * superclass in order to give more functionality to the client. * * @author Dr Timothy C. Lethbridge * @author Dr Robert Lagani&egrave; * @author Fran&ccedil;ois B&eacute;langer * @version July 2000 */public class ChatClient implements Observer{  //Instance variables **********************************************    /**   * The interface type variable.  It allows the implementation of    * the display method in the client.   */  ChatIF clientUI;   String pseudo = ChatIF.DEFAULT_PSEUDO;  ObservableClient client;    //Constructors ****************************************************    /**   * Constructs an instance of the chat client.   *   * @param host The server to connect to.   * @param port The port number to connect on.   * @param clientUI The interface type variable.   * @throws IOException   */    public ChatClient(String host, int port, ChatIF clientUI)     throws IOException   {	  new ChatClient(host,port,clientUI,ChatIF.DEFAULT_PSEUDO);  }  public ChatClient(String host, int port, ChatIF clientUI, String pseudo){	  		client = new ObservableClient(host, port); //Call the superclass constructor	  		this.clientUI = clientUI;		    this.pseudo = pseudo;		    client.addObserver(this);		  }    //Instance methods ************************************************      /**   * This method handles all data that comes in from the server.   *   * @param msg The message from the server.   */  public void handleMessageFromServer(Object msg)   {	  if(msg.toString().startsWith("#") ){		  if(msg.toString().substring(1, msg.toString().length()).equals("logoff")){			  try {				client.closeConnection();				clientUI.display("Le serveur a tu� la connexion.");			} catch (IOException e) {				e.printStackTrace();			}		  }	  }	  else{		  clientUI.display(msg.toString());	  }  }    public Boolean isCommand(String mess){	  return mess.substring(0, 1).equals("#");  }    /**   * This method handles all data coming from the UI               *   * @param message The message from the UI.       */  public void handleMessageFromClientUI(String message)  {	String[] tab = message.split(" ");	String command = tab[0];	if(isCommand(command)){		command = command.substring(1, command.length());		switch (command) {			case "quit":			    System.exit(0);				break;			case "logoff":			    try			    {			    	if(client.isConnected()){					    ArrayList<String> tmp = new ArrayList<String>();					    tmp.add("logoff");			    		client.sendToServer(tmp);			    		client.closeConnection();			    	}			    }			    catch(IOException e) {				    	clientUI.display("Impossible de se d�connecter !");			    }				break;			case "sethost":				try {					client.setHost(tab[1]);				} catch (Exception e) {					clientUI.display("Vous n'avez pas pr�cis� d'h�te !");				}				break;			case "setport":				try {					client.setPort(Integer.parseInt(tab[1]));				} catch (Exception e) {					clientUI.display("Vous n'avez pas pr�cis� de port !");				}				break;			case "login":				String temp = null;				try{					temp = tab[1];				}			    catch(ArrayIndexOutOfBoundsException e){			    	clientUI.display("Pas de pseudo donn� !");			    }				 if(temp == null){			    	  clientUI.display("Pr�ciser un pseudo !");			      }			      else{					if(!client.isConnected()){						try {							client.openConnection();						    ArrayList<String> tmp = new ArrayList<String>();						    tmp.add("login");						    tmp.add(temp);						    client.sendToServer(tmp);						    pseudo = temp;						} catch (IOException e1) {							clientUI.display("Impossible de se connecter au serveur !");						}					}			      }				break;			case "gethost":		      clientUI.display(client.getHost()+"");				break;			case "getport":		      clientUI.display(client.getPort()+"");				break;		}	}	else{	    try	    {	    	client.sendToServer(message);	    }	    catch(IOException e)	    {	      clientUI.display	        ("Ne peut pas envoyer de message au serveur.");	    }	}  }    /**   * This method overrides the one in the superclass.   * Called when a connection is closed.   */   protected void connectionClosed()   { 	  clientUI.display       ("La connexion a �t� ferm�e.");   }      /**    * This method overrides the one in the superclass.    *    * @param exception the exception raised.    */   protected void connectionException(Exception exception) {	   clientUI.display("Erreur de connexion !");   }     protected void connectionEstablished(){	   clientUI.display("Connexion �tablie.");   }     /**   * This method terminates the client.   */  public void quit()  {    try    {    	client.closeConnection();    }    catch(IOException e) {	    }        System.exit(0);  }    public void setPseudo(String pseudo){	  this.pseudo = pseudo;  }    public String getPseudo(){	  return this.pseudo;  }@Overridepublic void update(Observable o, Object arg) {	if(arg instanceof Exception){		connectionException((Exception)(arg));	}	else{		String argument = (String)arg;		if(argument.equals(ObservableClient.CONNECTION_ESTABLISHED)){			connectionEstablished();		}		else if(argument.equals(ObservableClient.CONNECTION_CLOSED)){			connectionClosed();		}		else{			handleMessageFromServer(argument);		}	}	}}//End of ChatClient class
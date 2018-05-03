package sudoku.dialog;// $Id: CS3331.Chat.ChatServer.java,v 1.4 2018/04/06 21:35:43 cheon Exp $

// $Id: ChatServer.java,v 1.4 2018/04/06 21:35:43 cheon Exp $

import java.io.*;
import java.net.*;
import java.util.*;

import edu.utep.cs.cs3331.sudoku.*;

/**
 * A simple chat server implemented using TCP/IP sockets. A client can
 * connect to this server and send messages to other clients. The chat
 * server receives messages from clients and broadcast them to all the
 * connected clients. A message is an arbitrary text and is also
 * printed on stdout. The default port number is 8008.
 *
 * <pre>
 *  Usage: java ChatServer
 * </pre>
 *
 * @author Yoonsik Cheon
 */
public class ChatServer {

    private static final String USAGE = "Usage: java ChatServer";

    /** Default port number on which this server to be run. */
    private static final int PORT_NUMBER = 8008;
    private Board table;
    /** List of print writers associated with current clients,
     * one for each. */
    private List<PrintWriter> clients;

    /** Create a new server. */
    public ChatServer() {
        clients = new LinkedList<PrintWriter>();
        
    }
    
    public ChatServer(Board x){
    	
    	clients = new LinkedList<PrintWriter>();
    	this.table=x;
    }
    

    /** Start the server. */
    public void start() {
        System.out.println("SUDOKU server started on port "
                + PORT_NUMBER + "!");
        try {
        	int clientNum = 1;
            ServerSocket s = new ServerSocket(PORT_NUMBER);
            for (;;) {
                Socket incoming = s.accept();
               
                new ClientHandler(incoming,clientNum++).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("SUDOKU server stopped.");
    }

    /** Add a new client identified by the given print writer. */
    private void addClient(PrintWriter out) {
        synchronized(clients) {
            clients.add(out);
        }
    }

    /** Add the client with given print writer. */
    private void removeClient(PrintWriter out) {
        synchronized(clients) {
            clients.remove(out);
        }
    }

    /** Broadcast the given text to all clients. */
    private void broadcast(String msg) {
        for (PrintWriter out: clients) {
        	out.println(msg );
        	 
            out.flush();
        }
    }
    
   private void broadcastBoard(Board table){
	   for (PrintWriter out: clients) {
       		out.println(table );
       	 
           out.flush();
       }
   }
    public static void main(String[] args) {
    	
        if (args.length > 0) {
            System.out.println(USAGE);
            System.exit(-1);
        }
        new ChatServer().start();
    }

    private void log(String message){
    	System.out.println(message);
    }
    /** A thread to serve a client. This class receive messages from a
     * client and broadcasts them to all clients including the message
     * sender. */
    private class ClientHandler extends Thread {

        /** Socket to read client messages. */
        private Socket incoming;
        private int clientNum;
        /** Create a hander to serve the client on the given socket. */
        public ClientHandler(Socket incoming, int clientNum) {
            this.incoming = incoming;
            this.clientNum = clientNum;
            log("New connection with client # "+ clientNum);
        }

        /** Start receiving and broadcasting messages. */
        public void run() {
            PrintWriter out = null;
          //  table = Board.numTable();
            try {
                out = new PrintWriter(
                        new OutputStreamWriter(incoming.getOutputStream()));

                // inform the server of this new client
                ChatServer.this.addClient(out);
                //addClient(out);
                addClient(out);

                out.print("Welcome to SUDOKU! ");
                out.println("Enter BYE to exit.");
                out.flush();

                BufferedReader in
                        = new BufferedReader(
                        new InputStreamReader(incoming.getInputStream()));
                for (;;) {
                    String msg = in.readLine();
                    
                    if (msg == null) {
                        break;
                    } else {
                        if (msg.trim().equals("BYE")){
                            break;
                        }
                        // broadcast the receive message
                        System.out.println("Player "+clientNum+": "+ msg );
                       // ChatServer.this.broadcast(msg);
                        broadcast(msg);
                        broadcastBoard(table);
                        out.flush();
                    }
                }
                incoming.close();
                ChatServer.this.removeClient(out);
            } catch (Exception e) {
                if (out != null) {
                    ChatServer.this.removeClient(out);
                }
                e.printStackTrace();
            }
        }
    }
}

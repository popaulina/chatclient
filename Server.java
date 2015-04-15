import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
    //List of all clients to send message to
    public static List<ClientSocket> clients = new ArrayList<ClientSocket>();

    public static void main(String[] args) throws IOException {
        //Open server socket
        ServerSocket server = new ServerSocket(5190);
        while (true) {
            try {
                //Connect to clients
                Socket c = server.accept();
                new ClientSocket(c).start();
            }
            catch (IOException e) { }
        }
    }
}

class ClientSocket extends Thread {
    Socket connection;
    String username;
    ClientSocket(Socket c) {connection = c    ;}

    public void run() {
        try {
            Scanner input = new Scanner(connection.getInputStream());
            //Store username as first input
            username = input.nextLine();
            Server.clients.add(this);
            while (input.hasNextLine()) {
                String message = username + ": " + input.nextLine();
                //Send messages to all clients
                for (ClientSocket c : Server.clients) {
                    try {
                        PrintWriter printwriter = new PrintWriter(c.connection.getOutputStream(), true);
                        printwriter.println(message);
                        printwriter.close();
                    } catch (IOException e) {}
                }
            }
        } catch (Exception e) {System.out.println(e.getMessage());}
    }
}
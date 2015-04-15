import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class Client {

    public static void main(String[] args) {
        //Start screen to enter username/choose server
        final JFrame frame = new JFrame("Chat Window");
        frame.setVisible(true);
        frame.setSize(600, 80);
        frame.setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout());
        frame.setContentPane(contentPane);

        final TextField user = new TextField("Username", 10);
        frame.add(user);
        final TextField IP = new TextField("Host IP", 10);
        frame.add(IP);
        JButton start = new JButton("Open Chat");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = user.getText();
                String hostIP = IP.getText();
                try {
                    chat(username, hostIP, frame);
                } catch (IOException i) {}
            }
        });
        frame.add(start);
    }

    public static void chat (String username, String host, JFrame frame) throws IOException {
        try {
            Socket socketOpen = new Socket(InetAddress.getByName(host), 5190);

            //create new screen
            frame.setVisible(false);
            JFrame newFrame = new JFrame("Hello, " + username);
            newFrame.setVisible(true);
            newFrame.setLocationRelativeTo(null);

            //message area
            final TextArea messages = new TextArea(10, 40);
            messages.setEditable(false);
            JScrollPane scroll = new JScrollPane(messages);
            JPanel messagePanel = new JPanel();
            messagePanel.add(scroll);
            Scanner input = new Scanner(socketOpen.getInputStream());
            //incoming messages must be on thread to prevent i/o blocking
            Output info = new Output(messages, input);
            info.start();

            //input area
            final PrintWriter printwriter = new PrintWriter(socketOpen.getOutputStream(), true);
            printwriter.println(username);
            final TextArea writing = new TextArea(1, 35);
            JButton send = new JButton("send");
            send.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    printwriter.println(writing.getText());
                    writing.setText("");
                }
            });
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
            inputPanel.add(writing);
            inputPanel.add(send);

            //entire page
            JPanel page = new JPanel();
            page.setLayout(new BoxLayout(page, BoxLayout.PAGE_AXIS));
            page.add(messagePanel);
            page.add(inputPanel);
            newFrame.add(page);
            newFrame.pack();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class Output extends Thread {
    Scanner in;
    TextArea text;
    Output(TextArea messages, Scanner input) {
        text = messages;
        in = input;
    }
    public void run(){
        while (true){
            if (in.hasNextLine()){
                //newline character must go with append to get a newline
                text.append(in.nextLine() + '\n');
            }
        }
    }
}
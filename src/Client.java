import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class Client extends JFrame implements ActionListener, KeyListener {

    public static Socket socClient;
    public static ObjectInputStream ClientInput;
    public static ObjectOutputStream ClientOutput;

    public String SelectedText;
    public String ClientIDToShare;

    // Text component
    JTextArea t;

    // Frame
    JFrame f;
    
    JMenu m3;

    JMenuItem mi13;
    JMenuItem mi14;
    JMenuItem mi15;
    JMenuItem mi16;
    
    boolean ready;
    boolean isdark;
    
    int fs;
    
    public static void main(String[] args) {
        try {
            socClient = new Socket("localhost", 9998); // named argument
            System.out.println("Connected!");

            Client c1 = new Client();
            Scanner scn = new Scanner(System.in);
            ClientOutput = new ObjectOutputStream(socClient.getOutputStream());
            ClientInput = new ObjectInputStream(socClient.getInputStream());

//            System.out.print("Write your ID : ");
//            String id = scn.nextLine();
            String id = JOptionPane.showInputDialog("ENTER YOUR NAME ");
            ClientOutput.writeUTF(id);
            ClientOutput.flush();

//            System.out.println("Write the name for your frame");
//            String filename = scn.nextLine();
            c1.ClientGUI(id);

            System.out.print("Now You Start your Real Connection");
            while (true) {
                String NewDataInTextArea = ClientInput.readUTF();
                String received = NewDataInTextArea;
//                System.out.println(received);
                if (received.equals("filesave"))
                {
                	String fname = ClientInput.readUTF();
                	File file = new File(fname);
                	FileWriter wr = new FileWriter(file, false);
                    BufferedWriter w = new BufferedWriter(wr);
                    w.write(ClientInput.readUTF());
                    w.flush();
                    w.close();
//                    System.out.println("filesaved");
                }
                else if(received.equals("busy"))
                {
                	new JOptionPane().showMessageDialog(null,"USERBUSY","ERROR",JOptionPane.WARNING_MESSAGE);
                }
                else if(received.equals("done"))
                {
                	
                }
                else
                c1.ChangeText(NewDataInTextArea);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
            e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        }
    }
    
    @SuppressWarnings("deprecation")
	public void ClientGUI(String str) {
        // Create a frame
        f = new JFrame(str);
        ready = false;
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        // Text component
        t = new JTextArea();
        t.setLineWrap(true);
        t.setFont(new Font("Roboto" ,Font.PLAIN,16));
        fs = 16;
        t.setBackground(Color.WHITE);
        t.setForeground(Color.BLACK);
        isdark = false;

        // Create a menu bar
        JMenuBar mb = new JMenuBar();

        // Create a menu for menu
        JMenu m1 = new JMenu("File");
        m3 = new JMenu("Share");
        //230, 30, 63 155, 245, 66

        // Create menu items
        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");
        JMenuItem mi10 = new JMenuItem("Share");
        JMenuItem mi11 = new JMenuItem("unshare");
        JMenuItem mi12 = new JMenuItem("exitsharing");
        mi13 = new JMenuItem("notready");
        mi13.setOpaque(true);
        mi13.setBackground(new Color(230, 30, 63));
        mi13.setSize(mi12.getWidth(),mi12.getHeight());

        // Add action listener
        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);
        
        mi10.addActionListener(this);
        mi11.addActionListener(this);
        mi12.addActionListener(this);
        mi13.addActionListener(this);

        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);
        
//        m3.add(mi13);
        m3.add(mi10);
        m3.add(mi11);
        m3.add(mi12);


        // Create a menu for menu
        JMenu m2 = new JMenu("Edit");

        // Create menu items
        JMenuItem mi4 = new JMenuItem("cut");
        JMenuItem mi5 = new JMenuItem("copy");
        JMenuItem mi6 = new JMenuItem("paste");

        // Add action listener
        mi4.addActionListener(this);
        mi5.addActionListener(this);
        mi6.addActionListener(this);

        m2.add(mi4);
        m2.add(mi5);
        m2.add(mi6);
        
        JMenu m4 = new JMenu("VIEW");
        
        mi14 = new JMenuItem("dark");
        mi14.addActionListener(this);
        mi15 = new JMenuItem("fontsize++");
        mi15.addActionListener(this);
        mi16 = new JMenuItem("fontsize--");
        mi16.addActionListener(this);
        
        m4.add(mi14);
        m4.add(mi15);
        m4.add(mi16);
        
        JMenuItem mc = new JMenuItem("close");

        mc.addActionListener(this);

        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        mb.add(m4);
        mb.add(mi13);
        mb.add(mc);

        f.setJMenuBar(mb);
        f.add(t);
        f.setSize(500, 500);
        f.setVisible(true);
        
        m3.setEnabled(ready);

        t.addKeyListener(this);
    }

    public void ChangeText(String str) {
        t.setText(str);
    }
    
    // If a button is pressed
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.equals("cut")) {
            t.cut();
        }
        else if (s.equals("copy")) {
            t.copy();
        }
        else if (s.equals("paste")) {
            t.paste();
        }
        else if(e.getSource()==mi13)
        {
        	if(!ready)
        	{
        		ready = true;
        		m3.setEnabled(ready);
            	mi13.setBackground(new Color(155, 245, 66));
            	mi13.setText("Ready");
            	try {
					ClientOutput.writeUTF("ready");
					ClientOutput.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
        	}
        	else
        	{
        		ready = false;
        		m3.setEnabled(ready);
        		mi13.setBackground(new Color(230, 30, 63));
        		mi13.setText("notready");
        		try {
					ClientOutput.writeUTF("ready");
					ClientOutput.flush();
					ClientOutput.writeUTF("exitsharing");
	                ClientOutput.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        }
        else if(e.getSource()==mi14)
        {
        	if(!isdark)
        	{
        		t.setBackground(Color.BLACK);
        		t.setForeground(Color.WHITE);
        		isdark = !isdark;
        		mi14.setText("light");
        	}
        	else
        	{
        		t.setBackground(Color.WHITE);
        		t.setForeground(Color.BLACK);
        		isdark = !isdark;
        		mi14.setText("dark");
        	}
        }
        else if(e.getSource()==mi15)
        {
        	fs=fs+3;
        	if(fs<52)
        	t.setFont(new Font("Roboto" ,Font.PLAIN,fs));
        }
        else if(e.getSource()==mi16)
        {
        	fs=fs-3;
        	if(fs>9)
        	t.setFont(new Font("Roboto" ,Font.PLAIN,fs));
        }
        else if (s.equals("Save")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsSaveDialog function to show the save dialog
            int r;
            r = j.showSaveDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {

                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    // Create a file writer that doesn't append
                    FileWriter wr = new FileWriter(fi, false);
                    
                    // Create buffered writer to write
                    BufferedWriter w = new BufferedWriter(wr);
                    
                    // Write
                    ClientOutput.writeUTF("filesave");
//                    System.out.println(fi.getAbsolutePath());
                    String filenametoshare = fi.getAbsolutePath().substring(fi.getAbsolutePath().lastIndexOf('\\')+1);
//                    System.out.println(temp);
                    ClientOutput.writeUTF(filenametoshare);
                    ClientOutput.flush();
                    ClientOutput.writeUTF(t.getText());
                    ClientOutput.flush();
                    w.write(t.getText());

                    w.flush();
                    w.close();
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }
        else if (s.equals("Open")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsOpenDialog function to show the save dialog
            int r;
            r = j.showOpenDialog(null);

            // If the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    // String
                    String s2;

                    // File reader
                    FileReader fr = new FileReader(fi);

                    // Buffered reader
                    BufferedReader br = new BufferedReader(fr);

                    // Initailise sl
                    String sl;
                    sl = br.readLine();
                    
                    // Take the input from the file
                    while ((s2 = br.readLine()) != null) {
                        sl = sl + "\n" + s2;
                    }

                    // Set the text
                    t.setText(sl);
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }
        else if (s.equals("New")) {
        	//warn user of overwriting
            t.setText("");
        }
        else if (s.equals("close")) {
            f.setVisible(false);
            try {
                ClientInput.close();
                socClient.close();
                ClientOutput.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (s.equals("Share")) {
            try {
                ClientOutput.writeUTF("Share");
                ClientOutput.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ClientIDToShare = JOptionPane.showInputDialog("Enter the ID of the Client to send this text..");
            try {
                ClientOutput.writeUTF(ClientIDToShare);
                ClientOutput.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        else if(s.equals("unshare")) {
            try {
                ClientOutput.writeUTF("unshare");
                ClientOutput.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ClientIDToShare = JOptionPane.showInputDialog("Enter the ID's of the Client to unshare..");
            try {
                ClientOutput.writeUTF(ClientIDToShare);
                ClientOutput.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        else if(s.equals("exitsharing")) {
            try {
                ClientOutput.writeUTF("exitsharing");
                ClientOutput.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {
        SelectedText = t.getText();
        try {
            ClientOutput.writeUTF(SelectedText);
            ClientOutput.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
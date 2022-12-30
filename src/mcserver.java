import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

//import tt.ClientHandler;

//import ClientHandler;

public class mcserver {
    static Vector<ClientHandler> ClientsVec = new Vector<>();
    
    static int nClients = 0;

    @SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9998);
        System.out.println("The Server is waiting for the client...");

        // running infinite loop for getting
        // client request
        while (true) {
                // socket object to receive incoming client requests
                Socket SocServer = server.accept();
                System.out.println("Start Connection");

                System.out.println("The client with port number = " + SocServer.getPort() + " Added to the server");

                // obtaining input and out streams
                System.out.println("Waiting for the ID of the Client");

                ObjectInputStream ServerInput = new ObjectInputStream(SocServer.getInputStream());
                ObjectOutputStream ServerOutput = new ObjectOutputStream(SocServer.getOutputStream());
                
                //BufferedReader ClientMessage = new BufferedReader(new InputStreamReader(ServerInput));
                String id = ServerInput.readUTF();

                System.out.println("Client ID : " + id);
                ClientHandler ClientsHand = new ClientHandler(SocServer, id, ServerInput, ServerOutput);

                // Create a new Thread with this object.
                Thread thread = new Thread(ClientsHand);

                System.out.println("Adding this client to active client list");

                // add this client to active clients list
                ClientsVec.add(ClientsHand);

                // start the thread.
                thread.start();

                nClients++;

        }
    }
}

class ClientHandler implements Runnable {
    Vector<ClientHandler> NEWClientsVec = new Vector<>();
    final ObjectInputStream input;
    final ObjectOutputStream output;
    public String ID;
    Socket s;
    boolean ready;
    boolean busy;

    // Constructor
    public ClientHandler(Socket s, String ID, ObjectInputStream input, ObjectOutputStream output) {
        this.ID = ID;
        this.s = s;
        this.input = input;
        this.output = output;
        ready = false;
        busy = false;
    }

    public void run() {
        try 
        {
            while (true) 
            {
                System.out.println("The Client Sent this Text to share..");

                // receive "share"
                String received = input.readUTF();
                if (received.equals("Share")) 
                {
                    received = input.readUTF();
                    this.busy=true;
                    String[] IDs = received.split("\\-");
                    boolean f=false;
                    for (String s : IDs) 
                    {
                        for (ClientHandler mc : mcserver.ClientsVec) 
                        {
                            if (s.equals(mc.ID) && !mc.ID.equals(this.ID)) 
                            {
                            	f=true;
                            	if(!mc.busy && mc.ready)
                            	{
                            		 NEWClientsVec.add(mc);
                                     mc.NEWClientsVec.add(this);
                                     mc.busy=true;
                                     output.writeUTF("done");
                                     output.flush();
                                     break;
                            	}
                            	else
                            	{
                            		output.writeUTF("busy");
                            		output.flush();
                            		break;
                            	}
                            }
                        }
                    }
                    if(!f)
                    {
                    	output.writeUTF("done");
                    	output.flush();
                    }
                    if(this.NEWClientsVec.isEmpty())
                    {
                    	this.busy=false;
                    }
                }
                else if (received.equals("unshare")) 
                {
                    received = input.readUTF();

                    String[] IDs = received.split("\\-");
                    for (String s : IDs) 
                    {
                        for (ClientHandler mc : mcserver.ClientsVec) 
                        {
                            if (s.equals(mc.ID) && !mc.ID.equals(this.ID)) 
                            {
                                NEWClientsVec.remove(mc);
                                mc.NEWClientsVec.remove(this);
                                mc.busy = false;
                                break;
                            }
                        }
                    }
                    if(this.NEWClientsVec.isEmpty())
                    {
                    	this.busy=false;
                    }
                }
                else if (received.equals("exitsharing")) 
                {
                	this.busy=false;
                    for (ClientHandler mc : mcserver.ClientsVec) 
                    {
                        if (NEWClientsVec.contains(mc)) 
                        {
                            NEWClientsVec.remove(mc);
                            mc.NEWClientsVec.remove(this);
                            mc.busy=false;
                        }
                    }
                }
//				      received = input.readUTF();
                else if (received.equals("ready")) 
                {
                	ready = !ready;
                }
                else 
                {
                	for (ClientHandler mc : NEWClientsVec)
                    {
                        if (!ID.equals(mc.ID)) 
                        {
                            mc.output.writeUTF(received);
                            mc.output.flush();
                        }
                    }
                }
            }

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            e.getMessage();
        }

    }
}
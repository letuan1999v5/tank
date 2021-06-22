
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Random;

public class ClientGUI extends JFrame implements WindowListener
{
    private  static JLabel nameLabel;
    private static JLabel scoreLabel;
    public static JPanel gameStatusPanel;
    private Client client;
    private Tank clientTank;
    private Wall clientWall1, clientWall2, clientWall3;

    private static int score;
    
    int width=720,height=600;
    boolean isRunning=true;
    private GameBoardPanel boardPanel;
    private UserPanel userPanel;
    JFrame loginGUI;

    String ipaddressText;
    String portText;
    String nameText;
    String teamText;
    Random randomGenerator = new Random();

    public ClientGUI(JFrame loginGUI, String ipaddressText, String portText, String nameText, String teamText)
    {

        this.loginGUI = loginGUI;
        this.ipaddressText = ipaddressText;
        this.portText = portText;
        this.nameText = nameText;
        this.teamText = teamText;
        score=0;

        nameLabel = new JLabel(nameText);
        nameLabel.setBounds(100,100,100,25);

        gameStatusPanel=new JPanel();
        gameStatusPanel.setBackground(new Color(179,226,131));
        gameStatusPanel.setSize(200,300);
        gameStatusPanel.setBounds(530,180,200,311);
        gameStatusPanel.setLayout(null);
        
        scoreLabel=new JLabel("Score : 0");
        scoreLabel.setBounds(10,90,100,25);
        client=Client.getGameClient();

//        clientWall1=new Wall(new Random().nextInt(50) +150,new Random().nextInt(150)+170);
//        clientWall2=new Wall(new Random().nextInt(50)+275, new Random().nextInt(150)+170);
//        clientWall3=new Wall(new Random().nextInt(50)+400, new Random().nextInt(150)+170);
        clientWall1 = new Wall(165,190);
        clientWall2 = new Wall(340,180);
        clientWall3 = new Wall(245,325);
        clientTank=new Tank(teamText, clientWall1, clientWall2, clientWall3);
        boardPanel=new GameBoardPanel(clientTank,client, clientWall1, clientWall2, clientWall3, false);
        userPanel=new UserPanel(nameText,scoreLabel,loginGUI, this);
        gameStatusPanel.add(scoreLabel);

        initClient();
        setTitle("Tanks Game");
        setSize(width,height);
        setLocation(60,100);
        getContentPane().setBackground(new Color(179,226,131));
        System.out.println("HERE 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        addWindowListener(this);

        getContentPane().add(boardPanel);
        getContentPane().add(userPanel);
        getContentPane().add(gameStatusPanel);
    }
    
    public static int getScore()
    {
        return score;
    }
    
    public static void setScore(int scoreParametar)
    {
        score+=scoreParametar;
        scoreLabel.setText("Score : "+score);
    }
    
    public void initClient()
    {
            try
            {
                 client.register(nameText,ipaddressText,Integer.parseInt(portText),clientTank.getXposition(),clientTank.getYposition(),teamText);
                 boardPanel.setGameStatus(true);
                 boardPanel.repaint();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                 new ClientRecivingThread(client.getSocket()).start();
                 boardPanel.setFocusable(true);
                 this.setVisible(true);
            } catch (IOException ex)
            {
                this.setVisible(false);
                new LoginGUI();
                JOptionPane.showMessageDialog(this,"The Server is not running, try again later!","Tanks 2D Game",JOptionPane.INFORMATION_MESSAGE);
                System.out.println("The Server is not running!");
            }
    }

    public void windowOpened(WindowEvent e) 
    {

    }

    public void windowClosing(WindowEvent e) 
    {
     Client.getGameClient().sendToServer(new Protocol().ExitMessagePacket(clientTank.getTankID()));

    }
    public void windowClosed(WindowEvent e) {
        
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
    
    public class ClientRecivingThread extends Thread
    {
        Socket clientSocket;
        DataInputStream reader;
        public ClientRecivingThread(Socket clientSocket)
        {
            this.clientSocket=clientSocket;
            try {
                reader=new DataInputStream(clientSocket.getInputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
        public void run()
        {
            while(isRunning) 
            {
                String sentence="";
                try {
                    sentence=reader.readUTF();                
                } catch (IOException ex) {
                    ex.printStackTrace();
                }                
               if(sentence.startsWith("ID"))
               {
                    int id=Integer.parseInt(sentence.substring(2));
                    clientTank.setTankID(id);
                    System.out.println("My ID= "+id);
                    
               }
               else if(sentence.startsWith("NewClient"))
               {
                    int pos1=sentence.indexOf(',');
                    int pos2=sentence.indexOf('-');
                    int pos3=sentence.indexOf('|');
                    int pos4=sentence.indexOf('/');
                    int x=Integer.parseInt(sentence.substring(9,pos1));
                    int y=Integer.parseInt(sentence.substring(pos1+1,pos2));
                    int dir=Integer.parseInt(sentence.substring(pos2+1,pos3));
                    int id=Integer.parseInt(sentence.substring(pos3+1,pos4));
                    String team =sentence.substring(pos4+1,sentence.length());
                    if(id!=clientTank.getTankID())
                        boardPanel.registerNewTank(new Tank(x,y,dir,id,0,team));
               }   
               else if(sentence.startsWith("Update"))
               {
                    int pos1=sentence.indexOf(',');
                    int pos2=sentence.indexOf('-');
                    int pos3=sentence.indexOf('|');
                    int x=Integer.parseInt(sentence.substring(6,pos1));
                    int y=Integer.parseInt(sentence.substring(pos1+1,pos2));
                    int dir=Integer.parseInt(sentence.substring(pos2+1,pos3));
                    int id=Integer.parseInt(sentence.substring(pos3+1,sentence.length()));
                
                    if(id!=clientTank.getTankID())
                    {
                        boardPanel.getTank(id).setXpoistion(x);
                        boardPanel.getTank(id).setYposition(y);
                        boardPanel.getTank(id).setDirection(dir);
                        boardPanel.repaint();
                    }
                    
               }
               else if(sentence.startsWith("Shot"))
               {
                    int id=Integer.parseInt(sentence.substring(4));
                
                    if(id!=clientTank.getTankID())
                    {
                        boardPanel.getTank(id).Shot();
                    }
                    
               }
               else if(sentence.startsWith("Remove"))
               {
                  int id=Integer.parseInt(sentence.substring(6));
                  
                  if(id==clientTank.getTankID())
                  {
                        int response=JOptionPane.showConfirmDialog(null,"Sorry, You are loss. Do you want to try again ?","Tanks Game",JOptionPane.OK_CANCEL_OPTION);
                        if(response==JOptionPane.OK_OPTION)
                        {
                            //client.closeAll();
                            setVisible(false);
                            dispose();
                            new ClientGUI(loginGUI, ipaddressText, portText, nameText, teamText);
                        }
                        else
                        {
                            loginGUI.setVisible(true);
                            setVisible(false);
                        }
                  }
                  else
                  {
                      boardPanel.removeTank(id);
                  }
               }
               else if(sentence.startsWith("Exit"))
               {
                   int id=Integer.parseInt(sentence.substring(4));
                  
                  if(id!=clientTank.getTankID())
                  {
                      boardPanel.removeTank(id);
                  }
               }
                      
            }
           
            try {
                reader.close();
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
    }
    
}

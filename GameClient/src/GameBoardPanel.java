import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import javax.swing.*;

/*
 * GameBoardPanel.java
 *
 * Created on 25 ����, 2008, 09:21 �
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Mohamed Talaat Saad
 */
public class GameBoardPanel extends JPanel {
    
    /** Creates a new instance of GameBoardPanel */
    private Tank tank;
    private int width=580;
    private int height=523;
    private static ArrayList<Tank> tanks;
    private boolean gameStatus;
    private Wall wall1, wall2, wall3;


    public GameBoardPanel(Tank tank,Client client,  Wall wall1, Wall wall2, Wall wall3, boolean gameStatus)
    {
        this.tank=tank;
        this.wall1 = wall1;
        this.wall2 = wall2;
        this.wall3 = wall3;
        this.gameStatus=gameStatus;
        setSize(width,height);
        setBounds(-50,0,width,height);
        addKeyListener(new InputManager(tank));
        setFocusable(true);

        tanks=new ArrayList<Tank>(100);
        
        for(int i=0;i<100;i++)
        {
            tanks.add(null);
        }
   
    }
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g=(Graphics2D)gr;

 
        g.setColor(new Color(179,226,131));
        g.fillRect(0,0, getWidth(),getHeight());

        g.setColor(Color.GREEN);
        g.fillRect(70,50, getWidth()-100,getHeight());
        g.drawImage(new ImageIcon("Images/bg.jpg").getImage(),70,50,null);
        g.setColor(new Color(0,0,0));
        g.setFont(new Font("Comic Sans MS",Font.BOLD,25));
        g.drawString("Tanks Game",255,30);
        if(gameStatus) 
        {
            g.drawImage(tank.getBuffImage(),tank.getXposition(),tank.getYposition(),this);
            g.drawImage(wall1.getBuffImage(), wall1.getXposition(), wall1.getYposition(), this);
            g.drawImage(wall2.getBuffImage(), wall2.getXposition(), wall2.getYposition(), this);
            g.drawImage(wall3.getBuffImage(), wall3.getXposition(), wall3.getYposition(), this);
            for(int j=0;j<1000;j++)
            {
                if(tank.getBomb()[j]!=null) 
                {
                    if(tank.getBomb()[j].stop==false){
                        g.drawImage(tank.getBomb()[j].getBomBufferdImg(),tank.getBomb()[j].getPosiX(),tank.getBomb()[j].getPosiY(),this);
                    }
                }
            }
            for(int i=1;i<tanks.size();i++) 
            {
                if(tanks.get(i)!=null)
                    g.drawImage(tanks.get(i).getBuffImage(),tanks.get(i).getXposition(),tanks.get(i).getYposition(),this);
                
                for(int j=0;j<1000;j++)
                {
                    if(tanks.get(i)!=null)
                    {
                        if(tanks.get(i).getBomb()[j]!=null) 
                        {
                            if(tanks.get(i).getBomb()[j].stop==false){
                            g.drawImage(tanks.get(i).getBomb()[j].getBomBufferdImg(),tanks.get(i).getBomb()[j].getPosiX(),tanks.get(i).getBomb()[j].getPosiY(),this);
                            }
                        }
                    }
                }
            }

        }
        
        repaint();
    }

    public void registerNewTank(Tank newTank)
    {
        tanks.set(newTank.getTankID(),newTank);
    }
    public void removeTank(int tankID)
    {
        tanks.set(tankID,null);
    }
    public Tank getTank(int id)
    {
        return tanks.get(id);
    }
    public void setGameStatus(boolean status)
    {
        gameStatus=status;
    }
  
    public static ArrayList<Tank> getClients()
    {
        return tanks;
    }
}

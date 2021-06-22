import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.util.Random;
public class Wall {
    private Image wallImg;
    private BufferedImage ImageBuff;
    private int posiX;
    private int posiY;
    private Random generator = new Random();
    private ArrayList<Integer> listXPosition;
    private ArrayList<Integer> listYPosition;
    private Bomb bomb[]=new Bomb[1000];

    public Wall(int positionX, int positionY){
        loadImage();
        this.posiX = positionX;
        this.posiY = positionY;
    }

    public void loadImage(){
        wallImg = new ImageIcon("Images/wall.jpg").getImage();
        ImageBuff = new BufferedImage(wallImg.getWidth(null), wallImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        ImageBuff.createGraphics().drawImage(wallImg, 0, 0, null);
    }

    public BufferedImage getBuffImage()
    {
        return ImageBuff;
    }

    public int getXposition()
    {
        return posiX;
    }
    public int getYposition()
    {
        return posiY;
    }
    public void setXpoistion(int x)
    {
        posiX=x;
    }
    public void setYposition(int y)
    {
        posiY=y;
    }

    public Bomb[] getBomb()
    {
        return bomb;
    }



}
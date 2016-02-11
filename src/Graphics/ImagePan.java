package Graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.*;
import javax.swing.*;

public class ImagePan extends JLabel {
		
	private float alpha = 1;
    private BufferedImage image;
    
    private int line = 0;
    private int col = 0;
    private int mincol = 0;
    private int maxcol = 0;
	
	public ImagePan(String path){

		try
        {
			image = ImageIO.read(new File("assets/"+path) );
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
	}
	
	@Override
    public Dimension getPreferredSize()
    {
        return new Dimension(image.getWidth(), image.getHeight());
    }
	
	public void zindex(int z){
		this.getParent().setComponentZOrder(this,Math.min(this.getParent().getComponentCount()-1,z));
	}
	
	public void setAlpha(float alpha)
    {
        this.alpha = alpha;
        repaint();
    }
	
	@Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
 
        //  Paint background image
 
        Graphics2D g2 = (Graphics2D) g;  
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        int x = (getWidth() - image.getWidth())/2;
        int y = (getHeight()- image.getHeight())/2;
        g2.drawRenderedImage(image, AffineTransform.getTranslateInstance(x, y));
        g2.setComposite(old);
    }
	
	
	private void nextFrame(){
		
		System.out.println(this.getWidth());
		
	}
	
	
}

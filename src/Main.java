import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

import Graphics.*;
import Graphics.Image;
import Graphics.Window;

public class Main  {
	
	static Window game;

	public static void main(String[] args) throws InterruptedException {
		
		//Créer la fenetre
		game = new Window();
		
		
		Image image = new Image("test.png");
		game.add(image);
		
		Image image2 = new Image("test2.png");
		game.add(image2);
		image2.setSize(100,100);
		image2.setLocation(100,0);
		image2.zindex(0);
		image.zindex(12);
		image2.setAlpha(0.9f);
	
		
		/*
		image.addMouseListener(new MouseAdapter() {
		        public void mouseClicked(MouseEvent e) {
		           System.out.println("Test");
		        }
		    });
		
		float x = 0;
		
		for(int i=0; i<200; i++){
			
			image.setBackground(Color.RED);
			image.setLocation((int) x,0);
			image.setSize(100,100);
			
			x++;
			
			game.fenetre.show();
			
			Thread.sleep(10);
		
		}
		
		game.clear();
		*/
		
	}
	


}

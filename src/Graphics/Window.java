package Graphics;
import java.awt.*;
import javax.swing.*;

public class Window {

	public JFrame fenetre;
	public JPanel panel;
	
	public Window(){
		
		fenetre = new JFrame();
		 fenetre.setTitle("TowerDefense");
		 fenetre.setSize(800,600);
		 fenetre.setLocationRelativeTo(null);
		 fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		 panel = new JPanel();
		 panel.setBackground(Color.WHITE);
		 panel.setLayout(null);
		 fenetre.setContentPane(panel);
		 
		 fenetre.show();
		
	}
	
	public void add(Component comp){
		
		panel.add(comp);
		panel.setOpaque(true);
		fenetre.show();
				
	}
	
	public void clear(){
		panel.removeAll();
		panel.revalidate();
		panel.repaint();
		fenetre.show();
	}
	


	
	public void background(){
		
	}
	
	
}

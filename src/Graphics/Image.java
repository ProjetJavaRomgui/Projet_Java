package Graphics;

import java.awt.*;

import javax.swing.*;

public class Image extends JLabel {
		
	public Image(String path){
		this.setIcon(new ImageIcon("assets/"+path));
		this.setSize(this.getSize());
	}
	
	public void zindex(int z){
		this.getParent().setComponentZOrder(this,Math.min(this.getParent().getComponentCount()-1,z));
	}

	
}

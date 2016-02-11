package Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Text  extends JLabel {
		
	public Text(){
		this.setText("<b>Test</b>test");
		
	}
	
	public void zindex(int z){
		this.getParent().setComponentZOrder(this,Math.min(this.getParent().getComponentCount()-1,z));
	}

	
}
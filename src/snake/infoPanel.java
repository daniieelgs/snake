package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class infoPanel extends JPanel implements Runnable{
	
	private static final long serialVersionUID = 1L;
	private snakePanel game;
	private JPanel info;
	private JLabel num_apple;
	private int cont_apple;
	private Thread t;
	
	public infoPanel(snakePanel _game) {
		
		game=_game;
				
		setBackground(new Color(57, 138, 52));
		
		info=new JPanel(new GridLayout(1, 2));
				
		info.setOpaque(false);
		
		JLabel apples=new JLabel();
		BufferedImage icon=null;
				
		try {
			icon=ImageIO.read(snake.class.getResource("Images/apple.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		apples.setIcon(new ImageIcon(icon.getScaledInstance(32, 32, Image.SCALE_DEFAULT)));
		
		info.add(apples);
		
		add(info);
		
		cont_apple=0;
		
		num_apple=new JLabel("" + cont_apple);
		
		num_apple.setFont(new Font("Verdana", Font.BOLD, 17));
		
		add(num_apple);
		
	}
	
	public void listen() {
		t=new Thread(this);
		t.start();
	}
	
	public void run() {
		
		while(game.isPlaying()) {
			
			try {
				Thread.sleep(game.getTiming());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(cont_apple!=game.getNumApple()) {
				
				cont_apple=game.getNumApple();
				
				num_apple.setText("" + cont_apple);
				
				info.updateUI();
				
				updateUI();
				
			}
			
		}
		
	}
	
}
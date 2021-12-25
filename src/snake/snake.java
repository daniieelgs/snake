package snake;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class snake{

	static final int LANDSCAPE=0; //width > height
	static final int PORTRAIT=1; //height > width
	private static int orientation;
	private static infoPanel info_panel;
	private static snakePanel snake_panel;
	private static JFrame frame;
	
	public static void main(String[] args) {

		frame=new JFrame("Snake");
		
		int ancho=Toolkit.getDefaultToolkit().getScreenSize().width;
		int alto=Toolkit.getDefaultToolkit().getScreenSize().height;	
		
		frame.setMinimumSize(new Dimension(100, 100));
		
		frame.setBounds(ancho/4, alto/4, ancho/2, alto/2);
		
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		Image icon;

		try {
			
			icon = new ImageIcon(snake.class.getResource("Images/icon.png")).getImage();
			
	        frame.setIconImage(icon);

		} catch (Exception e1) {
			
			e1.printStackTrace();
			
		}
		
		orientation=ancho > alto ? LANDSCAPE : PORTRAIT;
		
		snake_panel=new snakePanel(ancho, alto-25); //25px --> barra superior
		
		info_panel=snake_panel.getInfoPanel();
		
		frame.add(snake_panel, BorderLayout.CENTER);
		
		if(orientation==LANDSCAPE) frame.add(info_panel, BorderLayout.WEST);
		else frame.add(info_panel, BorderLayout.NORTH);
				
		frame.setVisible(true);
						
		snake_panel.resize(ancho - (orientation==LANDSCAPE ? info_panel.getWidth() : 0), alto-25-(orientation==PORTRAIT ? info_panel.getHeight() : 0)); //Recoloca el snake_panel después de añadir el info_panel
		
		snake_panel.startMenu(); //abrir menu de juego
		
		frame.addKeyListener(new KeyAdapter() {
			
			//38 up 87
			//40 down 83
			//39 right 68
			//37 left 65
			
			public void keyPressed(KeyEvent e) {
				
				switch (e.getKeyCode()) {
					case 87:
					case 38:
						snake_panel.setDirection(snakePanel.DIRECTION_UP);
						break;
					case 68:
					case 39:
						snake_panel.setDirection(snakePanel.DIRECTION_RIGHT);
						break;
					case 83:
					case 40:
						snake_panel.setDirection(snakePanel.DIRECTION_DOWN);
						break;
					case 65:
					case 37:
						snake_panel.setDirection(snakePanel.DIRECTION_LEFT);
						break;
				}
			}
			
		});
		
		frame.addComponentListener(new ComponentAdapter() {
			
			public void componentResized(ComponentEvent e) {
				
				resize();
				
			}
		});
		
		frame.addWindowStateListener(new WindowStateListener() {
			
			public void windowStateChanged(WindowEvent e) {
				
				resize();
				
			}
		});
	}
	
	private static void resize() {
		
		int _orientation=frame.getWidth() > frame.getHeight() ? LANDSCAPE : PORTRAIT;
		
		if(orientation!=_orientation) { //Cambia la posición del info_panel segun la resolución
			
			orientation=_orientation;
			
			frame.remove(info_panel);
			
			if(orientation==LANDSCAPE) frame.add(info_panel, BorderLayout.WEST);
			else frame.add(info_panel, BorderLayout.NORTH);
			
		}
		
		snake_panel.resize(frame.getWidth() - (orientation==LANDSCAPE ? info_panel.getWidth() : 0), frame.getHeight() - (orientation==PORTRAIT ? info_panel.getHeight() : 0));

		
	}
	
}
package snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayDeque;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class snakePanel extends JPanel implements Runnable{

	private static final long serialVersionUID = 1L;
	private int with, heigh;
	private int dimension;
	private boolean light=true;
	private celda[][] cells; // y / x
	private int snakeX, snakeY, direction, appleX, appleY;
	private boolean apple;
	private int num_apple, lastNumApple;
	
	private int d;
	private boolean playing;
	private Thread move;
		
	private double timing;
	private boolean animation;
	
	private ArrayDeque<celda> snaking;
	
	private JDialog menu;
	private JPanel dialog_panel;
	
	private infoPanel info_panel;
	
	static final int DIRECTION_UP=0, DIRECTION_DOWN=1, DIRECTION_RIGHT=2, DIRECTION_LEFT=3;
	
	final Color BC_COLOR=new Color(57, 138, 52), CELL_COLOR_LIGHT=new Color(170, 215, 81), CELL_COLOR=new Color(162, 209, 73), COLOR_SNAKE=Color.RED;
	
	public snakePanel(int _dimension, int _with, int _heigh, infoPanel _info_panel) {
		
		setLayout(new GridBagLayout());
		
		setBackground(BC_COLOR);
		
		dimension=_dimension;
		
		cells=new celda[dimension][dimension];
		
		with=_with;
		heigh=_heigh;
		
		if(_info_panel == null) {
			
			info_panel=new infoPanel(this);
			info_panel.setBackground(BC_COLOR);
			
		}else info_panel=_info_panel;
		
		//info_panel=( (_info_panel == null) ? new infoPanel(this) : _info_panel); --> error
				
		d=((with<heigh ? with : heigh)-50)/dimension;
		
		for(int x=0; x<dimension; x++) {
			
			for(int y=0; y<dimension; y++) {
				
				createCell(x, y, d);
				
			}
			
		}
				
		snaking=new ArrayDeque<celda>();
							
		menu=new JDialog();
		
		Image icon;

		try {
			
			icon = new ImageIcon(snake.class.getResource("Images/icon.png")).getImage();
			
	        menu.setIconImage(icon);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		menu.setBounds(with*3/8, heigh/4, with/4, heigh/2);
				
		menu.setModal(true);
		
		menu.setResizable(false);
				
		menu.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
								
				if(!playing) System.exit(0);
				
			}
			
		});
		
		restart();
		
	}
	
	public snakePanel(int with, int heigh) {
		
		this(17, with, heigh, null);
		
	}
	
	public void restart() {	//INICIALIZA VALORES POR DEFECTO AL INICIO DE CADA PARTIDA
				
		playing=false;
		
		snakeX=8;
		snakeY=8;
				
		direction=DIRECTION_UP;
		
		appleX=-1;
		appleY=-1;
		
		d=((with<heigh ? with : heigh)-50)/dimension;
		
		for(int x=0; x<dimension; x++) {
			
			for(int y=0; y<dimension; y++) {
				
				cells[y][x].removeAll();
				cells[y][x].passed=false;
				cells[y][x].body=null;
				cells[y][x].direction=0;
				
			}
			
		}

		apple=false;
		num_apple=0;
		timing=400;
		
		animation=true;
						
		snaking=new ArrayDeque<celda>();
		
		cells[snakeY][snakeX].add(makeSnakeHead(DIRECTION_UP, d));
		cells[snakeY][snakeX].passed=true;
		
		snaking.addFirst(cells[snakeY][snakeX]);
		
	}
	
	public void startMenu() {
						
		if(dialog_panel!=null) menu.remove(dialog_panel);
		
		dialog_panel=new startMenuPanel();
		
		menu.add(dialog_panel);
		
		menu.setVisible(true);
		
	}
	
	public void startPlay() {
		
		playing=true;
		
		info_panel.listen(); //INICIALIZA EL CONTADOR
		
		move=new Thread(this);
		
		move.start(); //EMPIEZA A MOVER EL SNAKE
		
	}
	
	private void createCell(int x, int y, int d) {
				
		celda cell=new celda(null);
		
		cell.setBackground(light ? CELL_COLOR_LIGHT : CELL_COLOR);
		cell.light=light;
		
		light=!light;

		createCell(x, y, d, cell);
	}
	
	private void createCell(int x, int y, int d, celda cell) {
		
		GridBagConstraints constraint=new GridBagConstraints();
		
		constraint.fill=GridBagConstraints.BOTH;
		
		constraint.gridx=x;
		constraint.gridy=y;
		
		constraint.ipadx=d;
		constraint.ipady=d;		
		
		add(cell, constraint);
		
		cells[y][x]=cell;
		
	}
	
	private JPanel makeSnakeHead(int direction, int d) { //GENERA LA CABEZA DEL SNAKE
			
		JPanel snake=null;
		JPanel eye1;
		JPanel eye2;
		
		Color color_head=COLOR_SNAKE;
		Color color_eye=Color.WHITE;
		
		int dimension_eyes=(int) (d*0.25);
		int space_eyesX=(d-dimension_eyes*2)/3;
		int space_eyesY=(d/2-dimension_eyes)/2;
						
		switch(direction) {
		
			case DIRECTION_UP:
				snake=new JPanel(null);
				snake.setBackground(color_head);
												
				eye1=new JPanel();
				eye1.setBackground(color_eye);
				eye1.setBounds(space_eyesX, space_eyesY, dimension_eyes, dimension_eyes);
				
				eye2=new JPanel();
				eye2.setBackground(color_eye);
				eye2.setBounds(d-space_eyesX-dimension_eyes, space_eyesY, dimension_eyes, dimension_eyes); //d - espacio lateral del ojo con la cabeza - ancho del ojo - espacio lateral de la cabeza con la casilla
				
				snake.add(eye1);
				snake.add(eye2);
				break;
				
			case DIRECTION_DOWN:
				snake=new JPanel(null);
				snake.setBackground(color_head);
				
				eye1=new JPanel();
				eye1.setBackground(color_eye);
				eye1.setBounds(space_eyesX, d-space_eyesY-dimension_eyes, dimension_eyes, dimension_eyes);
				
				eye2=new JPanel();
				eye2.setBackground(color_eye);
				eye2.setBounds(d-space_eyesX-dimension_eyes, d-space_eyesY-dimension_eyes, dimension_eyes, dimension_eyes); //d -espacio lateral del ojo con la cabeza - ancho del ojo - espacio lateral de la cabeza con la casilla

				
				snake.add(eye1);
				snake.add(eye2);
				break;
				
			case DIRECTION_RIGHT:
				snake=new JPanel(null);
				snake.setBackground(color_head);
								
				eye1=new JPanel();
				eye1.setBackground(color_eye);
				eye1.setBounds(d-space_eyesY-dimension_eyes, space_eyesX, dimension_eyes, dimension_eyes);
				
				eye2=new JPanel();
				eye2.setBackground(color_eye);
				eye2.setBounds(d-space_eyesY-dimension_eyes, d-space_eyesX-dimension_eyes, dimension_eyes, dimension_eyes);

				
				snake.add(eye1);
				snake.add(eye2);
				break;
				
			case DIRECTION_LEFT:
				snake=new JPanel(null);
				snake.setBackground(color_head);
								
				eye1=new JPanel();
				eye1.setBackground(color_eye);
				eye1.setBounds(space_eyesY, space_eyesX, dimension_eyes, dimension_eyes);
				
				eye2=new JPanel();
				eye2.setBackground(color_eye);
				eye2.setBounds(space_eyesY, d-space_eyesX-dimension_eyes, dimension_eyes, dimension_eyes);
				
				snake.add(eye1);
				snake.add(eye2);
				break;
					
		}
		
		snake.setBounds(0, 0, d+1, d+1);
		
		return snake;
		
	}
	
	public void setDirection(int _direction) {
				
		if((direction==DIRECTION_UP && _direction!=DIRECTION_DOWN) || (direction==DIRECTION_LEFT && _direction!=DIRECTION_RIGHT) || (direction==DIRECTION_DOWN && _direction!=DIRECTION_UP) || (direction==DIRECTION_RIGHT && _direction!=DIRECTION_LEFT)) {
			direction=_direction;
			cells[snakeY][snakeX].direction=direction;
			animation=false; //CANCELA LA ANIMACION PARA UNA REACCION RAPIDA
		}
						
		//while(move!=null && move.getState().equals(Thread.State.TIMED_WAITING)) {} 

	}
	
	public void up() {
			
		if(snakeY-1>=0 && !cells[snakeY-1][snakeX].passed) {
			changeBody();
			snakeY--;
			moveSnake();
		}else {
			playing=false;
		}

	}
	
	public void right() {
	
		if(snakeX+1<cells[0].length && !cells[snakeY][snakeX+1].passed) {
			changeBody();
			snakeX++;
			moveSnake();

		}else {
			playing=false;
		}
		
	}
	
	public void down() {
		
		if(snakeY+1<cells.length && !cells[snakeY+1][snakeX].passed) {
			changeBody();
			snakeY++;
			moveSnake();

		}else {
			playing=false;
		}

		
	}
	
	public void left() {
		
		
		if(snakeX-1>=0 && !cells[snakeY][snakeX-1].passed) {
			changeBody();
			snakeX--;
			moveSnake();
		}else {
			playing=false;
		}
		
	}
	
	public void moveSnake() {
				
		if(snakeY==appleY && snakeX==appleX) {
			
			addBody();
			cells[appleY][appleX].removeAll();
			
			num_apple++;
			apple=false;
			
			timing-=timing*0.05;
						
		}
				
		animate(); //ANIMATION
				
		cells[snakeY][snakeX].passed=true;
		cells[snakeY][snakeX].direction=direction;
			
		snaking.addFirst(cells[snakeY][snakeX]);
		
		updateUI();
		
	}
	
	private void animate() { //ANIMATION
		
		JPanel snakeHead=makeSnakeHead(direction, d);
		JPanel lastBody=(JPanel) snaking.getLast().body;
		
		cells[snakeY][snakeX].add(snakeHead);
		
		int locX=0, locY=0, locLastX=0, locLastY=0, directionFirst=snaking.getFirst().direction, directionLast=snaking.getLast().direction;
		snakeHead.setLocation(locX, locY);
		lastBody.setLocation(locLastX, locLastY);
		
		
		for(int i=0, j=d; i<=d && j>=0; i++, j--) {
			
			try {
				Thread.sleep(animation ? (long)(timing/d) : 0, (int)(timing/d)==0 ? (int)(((double)timing/(double)d)*1000000) : 0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			switch(directionFirst) {
			
				case DIRECTION_UP:
					locY=j;
					locX=0;
					break;
			
				case DIRECTION_DOWN:
					locY=i-d;
					locX=0;
					break;
					
				case DIRECTION_LEFT:
					locY=0;
					locX=j;
					break;
					
				case DIRECTION_RIGHT:
					locY=0;
					locX=i-d;
					break;
					
			}
			
			switch(directionLast) {
			
				case DIRECTION_UP:
					locLastY=j-d;
					locLastX=0;
					break;
			
				case DIRECTION_DOWN:
					locLastY=i;
					locLastX=0;
					break;
					
				case DIRECTION_LEFT:
					locLastY=0;
					locLastX=j-d;
					break;
					
				case DIRECTION_RIGHT:
					locLastY=0;
					locLastX=i;
					break;
				
		}
			
			snakeHead.setLocation(locX, locY);
			lastBody.setLocation(locLastX, locLastY);
			
			updateUI();
			
		}
		
		animation=true;
		
	}
	
	public void changeBody() { //CAMBIA LA CABEZA DE SNAKE POR EL CUERPO

		cells[snakeY][snakeX].removeAll();
		
		JPanel body=new JPanel(null);
		body.setBackground(COLOR_SNAKE);
		body.setBounds(0, 0, d+1, d+1);

		cells[snakeY][snakeX].add(body);
		
		
	}
	
	public void addBody() { //AÑADE AL FINAL DE LA COLA UN COMPONENTE TEMPORAL CON LA FUNCION DE QUE SEA ESTE REMOVIDO Y NO LA COLA DEL SNAKE
		
		celda tmp=new celda(null);
		tmp.passed=true;
		tmp.body=new JPanel();
			
		snaking.addLast(tmp);		
	}
	
	public void run() { //MUEVE EL SNAKE
		
		while(playing) {
							
			try {
				
				Thread.sleep(10);
				
				if(direction==DIRECTION_UP) up();
				else if(direction==DIRECTION_DOWN) down();
				else if(direction==DIRECTION_RIGHT) right();
				else if(direction==DIRECTION_LEFT) left();
				
				snaking.getLast().removeAll();
				snaking.getLast().setBackground(snaking.getLast().light ? CELL_COLOR_LIGHT : CELL_COLOR);
				snaking.getLast().passed=false;
				snaking.removeLast();

				
				if(!apple) {
					
					do {
						
						appleX=(int) (Math.random()*dimension);
						appleY=(int) (Math.random()*dimension);
						
					}while(cells[appleY][appleX].passed);
					
					JPanel imagen=new JPanel(null) {

						private static final long serialVersionUID = 1L;

						public void paint(Graphics g) {
										
							Image image=new ImageIcon(getClass().getResource("Images/apple.png")).getImage();
							
							g.drawImage(image, 0, 0, d, d, this);
							
						}
						
					};
					
					
					imagen.setBounds(0, 0, d, d);
					cells[appleY][appleX].add(imagen); //AGREGA LA MANZANA
					
					apple=true;
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}

		lastNumApple=num_apple; //GUARDA EL NUMERO DE MANZANAS DE LA PARTIDA ANTERIOR
		
		restart();
		startMenu();
		
	}

	public int getNumApple() {
		return num_apple;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public int getTiming() {
		return (int) timing;
	}
	
	public infoPanel getInfoPanel() {
		return info_panel;
	}
	
	public void setInfoPanel(infoPanel _info_panel) {
		info_panel=_info_panel;
	}
	
	public void resize(int _with, int _heigh) { //REDIMENSIONA LA CUADRICULA
		
		with=_with;
		heigh=_heigh;
		
		d=((with<heigh ? with : heigh)-50)/dimension;
		
		
		for(int x=0; x<dimension; x++) {
			
			for(int y=0; y<dimension; y++) {
				
				createCell(x, y, d, cells[y][x]);
				
				if(cells[y][x].body!=null) cells[y][x].body.setSize(d+1, d+1);
				
				if(y==appleY && x==appleX && cells[y][x].getComponentAt(1, 1)!=null) cells[y][x].getComponentAt(1, 1).setSize(d, d);
				else if(y==appleY && x==appleX) {
					
					cells[appleY][appleX].removeAll();
					
					JPanel imagen=new JPanel(null) {

						private static final long serialVersionUID = 1L;

						public void paint(Graphics g) {
										
							Image image=new ImageIcon(getClass().getResource("/Images/apple.png")).getImage();
							
							g.drawImage(image, 0, 0, d, d, this);
							
						}
						
					};
					
					
					imagen.setBounds(0, 0, d, d);
					cells[appleY][appleX].add(imagen);
					
				}
				
			}
			
		}
		
		updateUI();
		
	}
	
	private class celda extends JPanel{
		
		private static final long serialVersionUID = 1L;
		boolean passed=false;
		boolean light=false;
		int direction;
		Component body;
		
		public celda(LayoutManager l) {
			super(l);
		}
		
		public Component add(Component c) {
			super.add(c);
			body=c;
			return c;
			
		}
		
	}
	
	private class startMenuPanel extends JPanel{

		private static final long serialVersionUID = 1L;
				
		public startMenuPanel() {
			
			setLayout(new BorderLayout());
			
			add(panelSuperior(), BorderLayout.NORTH);
			add(panelCentral(), BorderLayout.CENTER);
			add(panelInferior(), BorderLayout.SOUTH);
			
			setBackground(BC_COLOR);
			
		}
		
		private JPanel panelSuperior() {
			
			JPanel superior=new JPanel();
			
			superior.setOpaque(false);
			
			JLabel apples=new JLabel();
			BufferedImage icon=null;
					
			try {
				icon=ImageIO.read(snake.class.getResource("Images/apple.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			apples.setIcon(new ImageIcon(icon.getScaledInstance(32, 32, Image.SCALE_DEFAULT)));
			
			superior.add(apples);
			
			JLabel num_apples=new JLabel("" + lastNumApple);
			
			num_apples.setFont(new Font("Verdana", Font.BOLD, 15));
			
			superior.add(num_apples);
			
			return superior;
			
		}
		
		private JPanel panelCentral() {
			
			JPanel central=new JPanel(new GridLayout(5, 5));
			
			central.setOpaque(false);
			
			boolean light=true;
			
			for(int y=0; y<5; y++) {
				
				for(int x=0; x<5; x++) {
					
					if(y==3 && (x>=0 && x<=2)) {
						
						light=!light;
						
						if(x==0 || x==1) {
							
							JPanel body=new JPanel();
							
							body.setBackground(COLOR_SNAKE);
							
							central.add(body);
							
						}else if(x==2) {
							
							central.add(makeSnakeHead(DIRECTION_RIGHT, (with/4)/7));
							
						}
						
					}else {
						
						celda cell=new celda(null);
						
						cell.setBackground(light ? CELL_COLOR_LIGHT : CELL_COLOR);
						cell.light=light;
						
						light=!light;
						
						central.add(cell);
					}
					
				}
				
			}
			
			return central;
			
		}
		
		private JPanel panelInferior() {
			
			JPanel inferior=new JPanel();
			
			inferior.setOpaque(false);
			
			JButton jugar=new JButton("Jugar");
		
			jugar.setFocusPainted(false);
			jugar.setBorderPainted(false); 
			jugar.setBackground(Color.CYAN.brighter());
			
			jugar.setFont(new Font("Verdana", Font.PLAIN, 20));
			
			inferior.add(jugar);
			
			jugar.setCursor(new Cursor(Cursor.HAND_CURSOR));
			
			jugar.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					restart();
					startPlay();
					menu.dispose();
				}
			});
			
			return inferior;
			
		}
		
	}
	
}

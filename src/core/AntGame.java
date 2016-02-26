package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import system.PointValue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import core.Ant;
import ants.NinjaThrowerAnt;
import ants.QueenAnt;
import ants.ThrowerAnt;
import system.Audio;

/**
 * A class that controls the graphical game of Ants vs. Some-Bees. Game simulation system and GUI interaction are intermixed.
 *
 * @author Joel
 * @version Fa2014
 */
@SuppressWarnings("serial")
public class AntGame extends JPanel implements ActionListener, MouseListener {

	/**
	 *
	 */
	private static final boolean DEBUG = false;
	
	private static final long serialVersionUID = 1L;
	// game models
	private AntColony colony;
	private Hive hive;
	private static final String ANT_FILE = "antlist.properties";
	private static final String ANT_PKG = "ants";

	// game clock & speed
	public static final int FPS = 30; // target frames per second
	public static final int TURN_SECONDS = 1; // seconds per turn
	public static final double LEAF_SPEED = .3; // in seconds
	private int turn; // current game turn
	private int frame; // time elapsed since last turn
	private int counter;
	private int counterExt;
	private Timer clock;
	private int STARTTIME = 10; //Set to 10
	private int STARTED = FPS*STARTTIME;
	private boolean DEAD = false;

	// ant properties (laoded from external files, stored as member variables)
	private final ArrayList<String> ANT_TYPES;
	private final Map<String, Image> ANT_IMAGES;// = new HashMap<String,Image>();
	private final Map<String, Color> LEAF_COLORS;// = new HashMap<String, Color>();

	// other images (stored as member variables)
	private final Image TUNNEL_IMAGE = ImageUtils.loadImage("img/tunnel.gif");
	private final Image TUNNEL_CLOSED_IMAGE = ImageUtils.loadImage("img/tunnelclosed.gif");
	private final Image WATER_IMAGE = ImageUtils.loadImage("img/water.png");
	private final Image TUNNEL_SELECT_IMAGE = ImageUtils.loadImage("img/tun_select.png");
	private final Image TUNNEL_SELECTED_IMAGE = ImageUtils.loadImage("img/tun_selected.png");
	private final Image FOOD = ImageUtils.loadImage("img/food.png");
	private final Image BEE_IMAGE[] = new Image[10];
	private final Image BEE_IMAGE2[] = new Image[10];
	private final Image BEEBAD_IMAGE[] = new Image[10];
	private final Image BEEATTACK_IMAGE[] = new Image[10];
	private final Image EXPLOSION[] = new Image[7];
	private final Image REMOVER_IMAGE = ImageUtils.loadImage("img/remover.gif");
	private final Image BACK = ImageUtils.loadImage("assets/preback.png");
	private final Image START = ImageUtils.loadImage("assets/start.png");
	private final Image STARTCLICK = ImageUtils.loadImage("assets/start_button.png");
	private final Image MENU = ImageUtils.loadImage("assets/menutop.png");
	private final Image MENUFRONT = ImageUtils.loadImage("assets/menutop_front.png");
	private final Image PAUSE_IMG = ImageUtils.loadImage("assets/pause.png");
	private final Image PLAY_IMG = ImageUtils.loadImage("assets/play.png");
	private final Image HOVERBLACK = ImageUtils.loadImage("assets/hoverblack.png");
	
	private final Font TITLE = new Font("Helvetica", Font.BOLD, 20);
	private final Font FONT = new Font("Helvetica", Font.BOLD, 15);
	private final Font LITTLE = new Font("Helvetica", Font.ITALIC, 15);
	private final Font LITTLEMAP = new Font("Helvetica", Font.ITALIC, 10);

	public String[] randomText = "Hey !|Hello ?|I don't want to die !|Help me !|Who are you ?".split("\\|");
	// positioning constants
	public static final Dimension FRAME_SIZE = new Dimension(1024, 700);
	public static final Dimension ANT_IMAGE_SIZE = new Dimension(66, 71); // assumed size; may be greater than actual image size
	public static final int BEE_IMAGE_WIDTH = 58;
	public static final Point PANEL_POS = new Point(20, 40);
	public static final Dimension PANEL_PADDING = new Dimension(2, 4);
	public static final Point PLACE_POS = new Point(40, 180);
	public static final Dimension PLACE_PADDING = new Dimension(10, 10);
	public static final int PLACE_MARGIN = 10;
	public static final Point HIVE_POS = new Point(875, 300);
	public static final int CRYPT_HEIGHT = 650;
	public static final Point MESSAGE_POS = new Point(120, 20);
	public static final Dimension LEAF_START_OFFSET = new Dimension(30, 30);
	public static final Dimension LEAF_END_OFFSET = new Dimension(50, 30);
	public static final int LEAF_SIZE = 40;
	
	//Sounds
	public static Audio food_earn = new Audio("food_earn.wav");
	public static Audio Sou_select = new Audio("select.wav");
	public static Audio Sou_place = new Audio("place.wav");
	public static Audio Sou_delete = new Audio("delete.wav");
	public static Audio Sou_explosion = new Audio("explosion.wav");
	public static Audio Sou_leaf = new Audio("throw.wav");

	public static Audio[] add = new Audio[4];

	public Point[] Food = new Point[100];
	public int LEVEL = 0;
	public boolean PAUSE = false;
	public static boolean FIN = false;
	public int DOCLICK = 0;
	public String[] stats = new String[10];

	// areas that can be clicked
	private Map<Rectangle, Place> colonyAreas; // maps from a clickable area to a Place
	private static Map<Place, Rectangle> colonyRects; // maps from a Place to its clickable rectangle (reverse lookup!)
	private Map<Rectangle, Ant> antSelectorAreas; // maps from a clickable area to an Ant that can be deployed
	private Rectangle removerArea; // click to remove an ant
	private Place tunnelEnd; // a Place representing the end of the tunnels (for drawing)
	private Ant selectedAnt; // which ant is currently selected
	
	//Start with only one tunel, center one
	public static int minTunnel = 2;
	public static int maxTunnel = 2;

	// variables tracking animations
	private Map<Bee, AnimPosition> allBeePositions; // maps from Bee to an object storing animation status
	private Map<Bee, PointValue> futureBees; // maps from Bee to an object storing animation status
	private ArrayList<AnimPosition> leaves; // leaves we're animating
	public static PointValue[] explosions = new PointValue[100];
	
	private int mouseX = 0;
	private int mouseY = 0;
	private boolean mousePressed = false;
	private Point dragStart = new Point(0,0);
	private Point scrollPos = new Point(0,0);
	
	public static int XP = 0;
	public int FOODCREATED = 0;
	public static int DEADANT = 0;
	public static int DEADBEES = 0;
	private int XP_RECORD = 0;
	private boolean HASQUEEN = false;
	private int LASTLEVELCHANGE = 1000;

	
	/**
	 * Creates a new game of Ants vs. Some-Bees, with the given colony and hive setup
	 *
	 * @param colony
	 *            The ant colony for the game
	 * @param hive
	 *            The hive (and attack plan) for the game
	 */
	public AntGame (AntColony colony) {

		
		
		//Mise en place de la musique
		Audio music = new Audio("music.wav");
		music.play();
		music.gain(-10);
		music.loop(true);
		//Sons
		add[0] = new Audio("add1.wav");
		add[1] = new Audio("add2.wav");
		add[2] = new Audio("add3.wav");
		add[3] = new Audio("add4.wav");
		add[0].megain = -15f;
		add[1].megain = -15f;
		add[2].megain = -15f;
		add[3].megain = -15f;
		
		food_earn.megain = -15f;
		
		//Get record
		try {
			Scanner sc = new Scanner(new File("stats.propert"));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				XP_RECORD = (int) Float.parseFloat(line);
			}
			sc.close();
		}
		catch (IOException e) { 
			System.out.println("No stat file, creating it...");
			try {
	    		 
			      File file = new File("stats.propert");
			      
			      if (file.createNewFile()){
			        System.out.println("File was created!");
			      }
			      
		    	} catch (IOException e1) {
					System.out.println(" /!\\ File not created !");
			}
		}
		
		
		//Get Bees
		for(int i=0; i<BEE_IMAGE.length;i++){
			
			BEE_IMAGE[i] = ImageUtils.loadImage("img/bees/"+i+"/bee_image.gif");
			BEE_IMAGE2[i] = ImageUtils.loadImage("img/bees/"+i+"/bee_image2.gif");
			BEEBAD_IMAGE[i] = ImageUtils.loadImage("img/bees/"+i+"/beebad_image.gif");
			BEEATTACK_IMAGE[i] = ImageUtils.loadImage("img/bees/"+i+"/beeattack_image.gif");

		}
		
		
		for(int i=0; i<EXPLOSION.length;i++){
			EXPLOSION[i] = ImageUtils.loadImage("img/explosion/"+i+".png");
		}
		
		addMouseMotionListener(new MouseAdapter() {
		     public void mouseMoved(MouseEvent me) {
		    	 if(STARTED==0){
			    	 mouseX = me.getX();
			    	 mouseY = me.getY();
		    	 }else{
		    		 mouseX = -1;
		    		 mouseY = -1;
		    	 }
		     }
	        public void mouseDragged(MouseEvent me) {
	        	if(STARTED==0){
			    	 mouseX = me.getX();
			    	 mouseY = me.getY();
		    	 }else{
		    		 mouseX = -1;
		    		 mouseY = -1;
		    	 }
	        }
		});
		// game init stuff
		this.colony = colony;
		
		this.hive = new Hive();


		// game clock tracking
		frame = 0;
		counter = 0;
		turn = 0;
		clock = new Timer(1000 / FPS, this);

		// member ant property storage variables
		ANT_TYPES = new ArrayList<String>();
		ANT_IMAGES = new HashMap<String, Image>();
		LEAF_COLORS = new HashMap<String, Color>();
		initializeAnts();

		// tracking bee animations
		allBeePositions = new HashMap<Bee, AnimPosition>();
		futureBees = new HashMap<Bee, PointValue>();
		initializeBees();
		leaves = new ArrayList<AnimPosition>();
		
		

		// map clickable areas to what they refer to. Might be more efficient to use separate components, but this keeps everything together
		antSelectorAreas = new HashMap<Rectangle, Ant>();
		colonyAreas = new HashMap<Rectangle, Place>();
		colonyRects = new HashMap<Place, Rectangle>();
		initializeAntSelector();
		initializeColony();

		// adding interaction
		addMouseListener(this);

		// basic appearance
		setPreferredSize(FRAME_SIZE);
		setBackground(Color.WHITE);

		// make and show the frame!
		JFrame frame = new JFrame("Ants vs SomeBees");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);

	}
	
	private void restartGame(){
		colony = null;
		colony = new AntColony(5, 8, 2, 20, 10, 1); // specify the colony ]tunnels, length, moats, food, life, difficulty (1-10)]
		
		//Initialisation des variables statiques
		minTunnel = 2;
		maxTunnel = 2;
		DEADBEES = 0;
		DEADANT = 0;
		XP = 0;
		FOODCREATED = 0;
		STARTED = FPS*STARTTIME;
		HASQUEEN = false;
		FIN = false;
		PAUSE = false;
		counterExt = 0;
		counter = 0;
		DEAD = false;
		turn = 0;
		LEVEL = 0;
		clock.restart();
		this.hive = new Hive();


		// game clock tracking
		frame = 0;
		counter = 0;
		turn = 0;
		clock = new Timer(1000 / FPS, this);


		initializeAnts();

		// tracking bee animations
		allBeePositions = new HashMap<Bee, AnimPosition>();
		futureBees = new HashMap<Bee, PointValue>();
		initializeBees();
		leaves = new ArrayList<AnimPosition>();
		
		LASTLEVELCHANGE = 1000;

		// map clickable areas to what they refer to. Might be more efficient to use separate components, but this keeps everything together
		antSelectorAreas = new HashMap<Rectangle, Ant>();
		colonyAreas = new HashMap<Rectangle, Place>();
		colonyRects = new HashMap<Place, Rectangle>();
		initializeAntSelector();
		initializeColony();
		
	}
	
	//Return smooth animation position a, position b, temps actuel, duree
	public float smooth(int a,int t,int d){
		if(t<d/2){
			return (float) (a*Math.pow(((float)t/d)*2, 2)/2);
		}else{
			return (float) (a*(2-Math.pow(((float)t/d)*2-2, 2))/2);
		}
	}

	@Override
	public void paintComponent (Graphics g) {
		super.paintComponent(g); // take care of anything else
		Graphics2D g2d = (Graphics2D) g;
		g2d.clearRect(0, 0, FRAME_SIZE.width, FRAME_SIZE.height); // clear to background color
		
		g2d.setFont(FONT);
		
		int mov = (int) smooth(-(BACK.getWidth(getParent())-FRAME_SIZE.width),(FPS*STARTTIME-STARTED),FPS*STARTTIME);
		int decalage = (BACK.getWidth(getParent())-FRAME_SIZE.width)+mov;

		
		g2d.drawImage(BACK, mov, 0, null); // draw a bee at that position!
		
		
		g2d.drawImage(MENU, 0, -decalage/6, null); // draw a bee at that position!
		
		drawAntSelector(g2d,decalage/6);

		if(STARTED==0){

			// text displays
			String antString = "none";
			if (selectedAnt != null) {
				antString = selectedAnt.getClass().getName();
				antString = antString.substring(0, antString.length() - 3); // remove the word "ant"
			}
			
		
			g2d.drawString("Life: "+ Math.max(colony.life,0) +" Food: " + colony.getFood(), 18, 140);
			
			g2d.drawString("XP: "+ String.format("%,d",XP), FRAME_SIZE.width-160, 40);
			g2d.drawString("Best: "+ String.format("%,d", Math.max(XP,XP_RECORD)), FRAME_SIZE.width-150, 69);


		}else{
			
			g2d.drawImage(START, FRAME_SIZE.width/2-300 +mov/3, 100, null);
			
		}
		
		drawColony(g2d,decalage);
		drawBees(g2d);
		drawLeaves(g2d);
		
		
		float angle = 0;
		//Afficher la nourriture
		for(int i=0; i<Food.length;i++){
			if(Food[i]!=null){
				
				angle = 3.1415f+(float)Math.atan((float)(Food[i].y)/(float)(Food[i].x));
				Food[i].x = Food[i].x + (int)(6*Math.cos(angle));
				Food[i].y = Food[i].y + (int)(6*Math.sin(angle));

				if(Food[i].y<160-Math.random()*20){
					Food[i]=null;
					colony.increaseFood(1);	
					FOODCREATED++;
					addXP(1);
					food_earn.play();
				}else{
					g2d.drawImage(FOOD, Food[i].x, Food[i].y, null);
				}
			}
		}
		
		doExplosion(g2d);
		if(!PAUSE && !FIN){
			drawHoverText(g2d);
		}
		if (!clock.isRunning()) { // start text
			
			if(counter%2==0){
				g2d.drawImage(STARTCLICK, FRAME_SIZE.width/2-150, FRAME_SIZE.height-200, null);
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.nextFrame();
		}
		

		if(PAUSE || FIN){
			g2d.drawImage(HOVERBLACK,0, 0, FRAME_SIZE.width, FRAME_SIZE.height, getParent());
		}
		
		if(FIN){
			if((int)(counterExt/FPS)%2==0){
				g2d.setFont(TITLE);
				if(counter/FPS<Integer.parseInt(stats[0])+5){
					drawLongText("Game over !",FRAME_SIZE.width/2-65,FRAME_SIZE.height/2-90,g2d);
				}else{
					drawLongText("Click anywhere to play again !",FRAME_SIZE.width/2-165,FRAME_SIZE.height/2-90,g2d);
				}
			}
			
			if(counter/FPS>Integer.parseInt(stats[0])+1){
				g2d.setFont(LITTLE);
				drawLongText("XP :",FRAME_SIZE.width/2-40,FRAME_SIZE.height/2-40,g2d);
				g2d.setFont(FONT);
				drawLongText(stats[1],FRAME_SIZE.width/2,FRAME_SIZE.height/2-40,g2d);
			}
			
			if(counter/FPS>Integer.parseInt(stats[0])+1){
				g2d.setFont(LITTLE);
				drawLongText("Best :",FRAME_SIZE.width/2-50,FRAME_SIZE.height/2-20,g2d);
				g2d.setFont(FONT);
				drawLongText(""+stats[2],FRAME_SIZE.width/2,FRAME_SIZE.height/2-20,g2d);
			}
			
			if(counter/FPS>Integer.parseInt(stats[0])+2){
				g2d.setFont(LITTLE);
				drawLongText("Total food created :",FRAME_SIZE.width/2-138,FRAME_SIZE.height/2,g2d);
				g2d.setFont(FONT);
				drawLongText(""+stats[3],FRAME_SIZE.width/2,FRAME_SIZE.height/2,g2d);
			}
			
			if(counter/FPS>Integer.parseInt(stats[0])+2){
				g2d.setFont(LITTLE);
				drawLongText("Dead ants :",FRAME_SIZE.width/2-88,FRAME_SIZE.height/2+20,g2d);
				g2d.setFont(FONT);
				drawLongText(""+stats[4],FRAME_SIZE.width/2,FRAME_SIZE.height/2+20,g2d);
			}
			
			if(counter/FPS>Integer.parseInt(stats[0])+2){
				g2d.setFont(LITTLE);
				drawLongText("Killed bees :",FRAME_SIZE.width/2-93,FRAME_SIZE.height/2+40,g2d);
				g2d.setFont(FONT);
				drawLongText(""+stats[5],FRAME_SIZE.width/2,FRAME_SIZE.height/2+40,g2d);
			}
			
			
			if(counter/FPS>Integer.parseInt(stats[0])+3){
				g2d.setFont(LITTLE);
				drawLongText("Total time :",FRAME_SIZE.width/2-85,FRAME_SIZE.height/2+80,g2d);
				g2d.setFont(FONT);
				drawLongText(stats[0]+" s",FRAME_SIZE.width/2,FRAME_SIZE.height/2+80,g2d);
			}

			
		}
		
		if(FIN){
			return;
		}
		if(PAUSE ){
			g2d.drawImage(PLAY_IMG,25, FRAME_SIZE.height-75, 50, 50, getParent());
			
			if((int)(counterExt/FPS)%2==0){
				g2d.setFont(TITLE);
				drawLongText("Pause",FRAME_SIZE.width/2-30,FRAME_SIZE.height/2-20,g2d);
			}
			
		}else{
			g2d.drawImage(PAUSE_IMG,25, FRAME_SIZE.height-75, 50, 50, getParent());
		}
		
		
	}
	
	public static void addXP(int howmany){
		if(!FIN){
			XP+=Math.max(0, howmany);
		}
	}
	
	/**
	 * Runs the actual game, processing what occurs on every frame of the game (including individual turns).
	 * This handles both some game logic (turn order) and animation control
	 */
	private void nextFrame () {
		
		if(!PAUSE){
			counter++;
		}
		counterExt++;
		LASTLEVELCHANGE++;
		
		if(clock.isRunning()){
			
			STARTED+=-1-DOCLICK;
			STARTED = Math.max(0, STARTED);
	
			
			if(STARTED==0){
		
	
				
				
				if (frame == 0 && !PAUSE) // at the start of a turn
				{
					System.out.println("TURN: " + turn);
					
					addXP(10);
					
					
					///////////////////
					//Generation du jeu
					///////////////////
					gestionJeu();
					showBees();
					///////////////////
					///////////////////
					///////////////////
					
					//Changer le niveau de jeu (et les ants disponibles)
					if(turn%50==0){
						int temp = LEVEL;
						LEVEL = turn/50;
						if(temp!=LEVEL){
							LASTLEVELCHANGE = 0;
						}
						//1 level = 50 tours
						initializeAntSelector();
					}
		
					// ants take action!
					for (Ant ant : colony.getAllAnts()) {
						if (ant instanceof QueenAnt) //if we're a queen , let's buff
						{
							System.out.print(colony.queenPlace.getQueenPlace());
							if (colony.queenPlace.getQueenPlace().getExit().getAnt()!=null){
								colony.queenPlace.getQueenPlace().getExit().getAnt().buff = true;
							}
							if (colony.queenPlace.getQueenPlace().getEntrance().getAnt()!=null){
								colony.queenPlace.getQueenPlace().getEntrance().getAnt().buff = true;
							}
						}

						addXP(1);
					}
					
					int pos = 0;
					for(Map.Entry<Place, Rectangle> entry: colonyRects.entrySet()){
						
						pos = 0;
						while(pos<Food.length-1 && Food[pos]!=null){
							pos++;
						}
	
						if(entry.getKey().getAnt()!= null && entry.getKey().getAnt().foodMakePerTurn>0 && (Math.random()>0.5 || entry.getKey().getAnt().buff)){
							Food[pos] = new Point(entry.getValue().x + entry.getValue().width/2,entry.getValue().y + entry.getValue().height/2);
						}
						
					}
			
		
					// bees take action!
					for (Bee bee : colony.getAllBees()) {
						if(bee.place!=null){
							bee.action(colony);
							startAnimation(bee); // start up animation for the bee if needed
						}
					}
		
					// new invaders attack!
					Bee[] invaders = hive.invade(colony, turn); // this moves the bees into the colony
					for (Bee bee : invaders) {
						startAnimation(bee);
					}
		
					// if want to do this to ants as well, will need to start storing dead ones with AnimPositions
				}
				
				//Finish ants (special for exploding ants :p)
				if(frame%(FPS/5)==0 && !PAUSE){
					for (Ant ant : colony.getAllAnts()) 
					{
						if(ant.armor<=0){
							ant.reduceArmor(1);
						}
						ant.lastAttacked++;
						ant.lastAttack++;
					}
				}
				
				
				if (frame == (int) (LEAF_SPEED * FPS) && !PAUSE) // after leaves animate
				{
					for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) // remove dead bees
					{
						AnimPosition pos = entry.getValue();
	
						if (entry.getKey().getArmor() <= 0 && entry.getKey().place!=null) { // if dead bee
							if(entry.getKey().place.toString()!="AntQueen"){
								pos.animateTo((int) (FRAME_SIZE.getWidth()+200), (int) pos.y, FPS * TURN_SECONDS);
							}else{
								pos.animateTo((int) (-200), (int) pos.y, FPS * TURN_SECONDS);
							}
						}
						if (entry.getKey().place==null){
							pos.animateTo((int) (FRAME_SIZE.getWidth()+200), (int) pos.y, FPS * TURN_SECONDS);
						}
					}
				}
		
				// every frame
				for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) // apply animations to all the bees
				{
					if (entry.getValue().framesLeft > 0) {
						entry.getValue().step();
					}
					entry.getKey().lastAttacked++;
					entry.getKey().lastAttack++;
				}
				if (colony.queenHasBees()) { // more than 1 life
					System.out.println("Queen Has Bees !!!");
					for (Bee bee: colony.queenPlace.getBees())
					{
						if(bee != null){
							System.out.println("OUUUUCH");
							if(bee.armor>0){
								bee.reduceArmor(bee.getArmor());
								colony.life += -bee.colonyDegat; // Big bees can destroy all the colony
							}
						}
					}
				}
				for (Ant ant : colony.getAllAnts()) // apply time
				{
					//Change Queen armor
					if(ant instanceof QueenAnt){
						HASQUEEN = true;
						ant.armor = colony.life;
						if(ant.armor<=0){
							addExplosion(ant.getPlace());
							addBigExplosion(FRAME_SIZE.width/2,FRAME_SIZE.height/2,FRAME_SIZE.width/3,50);
						}
					}
					
					
					if(ant.lastAttack>FPS + (int)((0.5-Math.random())*FPS*0.2)){
						if (ant instanceof ThrowerAnt) // if we're a thrower, might need to make a leaf!
						{
							Bee target = ((ThrowerAnt) ant).getTarget(); // who we'll throw at (really which square, but works out the same)
							if (target != null) {
								createLeaf(ant, target);
								addXP(1);
							}
						}
						if(ant instanceof NinjaThrowerAnt){
							createNinjaLeaf(ant);
							addXP(2);
						}
						ant.action(colony); // take the action (actually completes the throw now)
						ant.lastAttack = 0;
					}
					
					ant.lastAttacked++;
					ant.lastAttack++;
				}
				Iterator<AnimPosition> iter = leaves.iterator(); // apply animations ot all the leaves
				while (iter.hasNext()) { // iterator so we can remove when finished
					AnimPosition leaf = iter.next();
					if (leaf.framesLeft > 0) {
						leaf.step();
					}
					else {
						iter.remove(); // remove the leaf if done animating
					}
				}
			
				// ADVANCE THE CLOCK COUNTERS
				frame++; // count the frame
				// System.out.println("frame: "+frame);
				if (frame == FPS * TURN_SECONDS) { // if TURN seconds worth of frames
					turn++; // next turn
					frame = 0; // reset frame
				}
		
				if (frame == TURN_SECONDS * FPS / 2) // wait half a turn (1.5 sec) before ending
				{
					// check for end condition before proceeding
					
					if(colony.life<1){
						DEAD = true;
					}
				}
			}else{
				frame = 0;
			}
			
			if(DEAD == true && frame==0){
				
				//Ecrire le nouveau record si il y en a !
				if(XP>XP_RECORD){
					File record = new File("stats.propert");
					FileOutputStream fooStream;
					try {
						fooStream = new FileOutputStream(record, false);
						byte[] myBytes = (""+XP).getBytes();
						fooStream.write(myBytes);
						fooStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(FIN==false){
					FIN = true;
					stats[0] = ""+counter/FPS;
					stats[1] = String.format("%,d",XP) + " xp";
					stats[2] = String.format("%,d",XP_RECORD) + " xp";
					stats[3] = String.format("%,d",FOODCREATED) + "";
					stats[4] = String.format("%,d",DEADANT) + " ant(s)";
					stats[5] = String.format("%,d",DEADBEES) + " bee(s)";
				}
				
			}
		}
		
		this.repaint(); // request an update per frame!

		
	}
	
	private void gestionJeu(){
		
		//Ajout des abeilles
		if(Math.random()>0.6){
			
			if(turn<80){
				
				int nb = 1;
				
				if(Math.random()>0.96){
					nb = nb*2;
				}
				
				if(turn>65){
					nb = nb*2;
				}
				
				for(int i=0;i<nb;i++){
					addBee(3,1);
				
				}
				
			}
			//20 turn pause
			
			if(turn<180 && turn>100){
				
				int nb = 1;
				int max = 0;
				
				if(Math.random()>0.96){
					nb = nb*2;
				}
				
				if(turn>165){
					nb = nb*2;
					max = 5;
				}
				
				for(int i=0;i<nb;i++){
					addBee(Math.max(3, (int)((9+max)*Math.random())),1);
				}
				
			}
			
			//40 turn pause
			
			if(turn<360 && turn>220){
				
				int nb = 2;
				int max = 0;
				
				if(Math.random()>0.96){
					nb = nb*2;
				}
				
				if(turn>265){
					nb = nb*2;
					max = 5;
				}
				
				for(int i=0;i<nb;i++){
					addBee(Math.max(5, (int)((14+max)*Math.random())),1);
				}
				
			}
			
			//Then die !
			
			if(turn>380 || FIN){
				int nb = turn/70+1;
				
				if(Math.random()>0.96){
					nb = nb*3;
				}
				
				for(int i=0;i<nb;i++){
				
					int life = Math.max(3, Math.min(100,(int)(Math.random()*(turn/30+1))));
					addBee(life,Math.max(life/10,1));
				
				}
			}
			
		}
		
		
		//Ajout des tunnels
		if(turn==100){
			minTunnel = 1;
		}
		if(turn==150){
			maxTunnel = 3;
		}
		if(turn==250){
			minTunnel = 0;
		}
		if(turn==300){
			maxTunnel = 4;
		}
	}
	
	private void addBee(int armor, int degatColony){
		
		Bee bee = new Bee(armor);
		bee.colonyDegat = degatColony;
		
		PointValue pt = new PointValue(20-(int)(40*Math.random()),(int)(20*Math.random()));
		pt.value = counter+20*FPS;
		
		futureBees.put(bee, pt);
		
	}
	
	private void showBees(){
		
		ArrayList<Bee> listKey = new ArrayList<Bee>();
		
		for(Entry<Bee, PointValue> entry: futureBees.entrySet()){
			
			if(entry.getValue().value<=counter && entry.getValue().value2==0){
				
				entry.getValue().value2 = 1; // Empecher de mettre plusieurs fois la m��me abeille
				
				Bee bee = new Bee(entry.getKey().armor/2); ///2 because armore is alway multiplies by 2
				bee.colonyDegat = entry.getKey().colonyDegat;
				
				hive.addInsect(bee); // put the bee in Place
				Place[] exits = colony.getBeeEntrances();
				
				for(Bee b: hive.getAllBees()){
					if(b!=null){
						int randExit = (int) (Math.random() * exits.length);
						
						allBeePositions.put(b, new AnimPosition((int) FRAME_SIZE.getWidth()+200, (int) (HIVE_POS.y + (100 * Math.random() - 50))));
						b.moveTo(exits[randExit]);
					}
				}
				
			}
			
		}
		
		for(Entry<Bee, PointValue> entry: futureBees.entrySet()){
			
			if(entry.getValue().value<=counter-FPS){
				
				listKey.add(entry.getKey());	
			}
			
		}
		
		
		//Removing added bees
		for(Bee b: listKey){
			futureBees.remove(b);
		}
		
	}

	//
	/**
	 * Handles clicking on the screen (used for selecting and deploying ants).
	 * Synchronized method so we don't create conflicts in amount of food remaining.
	 *
	 * @param e
	 *            The mouse event representing the click
	 */
	private synchronized void handleClick (MouseEvent e) {
		Point pt = e.getPoint();
		
		if(FIN){
			restartGame();
		}
		
		if(pt.getX()<100 && pt.getY()>FRAME_SIZE.getHeight()-100){
			Sou_select.play();
			PAUSE = !PAUSE;
			return;
		}
		if(PAUSE){
			return;
		}
		// check if deploying an ant
		for (Rectangle rect : colonyAreas.keySet()) {
			if (rect.contains(pt)) {
				if (selectedAnt == null) {
					if(colonyAreas.get(rect).getAnt()!=null && !(colonyAreas.get(rect).getAnt() instanceof QueenAnt)){
						Sou_delete.play();
						colony.increaseFood((colonyAreas.get(rect).getAnt().foodCost+1)/2);
					}
					colony.removeAnt(colonyAreas.get(rect));
					return; // stop searching
				}
				else {
					if(colonyAreas.get(rect).tunnel<minTunnel || colonyAreas.get(rect).tunnel>maxTunnel){
						return;
					}
					if(colonyAreas.get(rect).getAnt()==null){
						Sou_place.play();
						add[(int)(Math.random()*4)].play();
					}
					Ant deployable = buildAnt(selectedAnt.getClass().getName()); // make a new ant of the appropriate type
					colony.deployAnt(colonyAreas.get(rect), deployable);
					
					if(deployable instanceof QueenAnt){
						HASQUEEN = true;
						initializeAntSelector();
					}

					
					return; // stop searching
				}
			}
		}
		
		// check if remover
		if (removerArea.contains(pt)) {
			Sou_select.play();
			selectedAnt = null; // mark as such
			return; // stop searching
		}

		// check if selecting an ant
		for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
			if (entry.getKey().contains(pt)) {
				Sou_select.play();
				selectedAnt = entry.getValue();
				return; // stop searching
			}
		}


	}
	
	private void doExplosion(Graphics2D g2d){
		
		for(int i=0; i<explosions.length; i++){
			if(explosions[i]!=null){
				
				if(explosions[i].value>=0){
					g2d.drawImage(EXPLOSION[explosions[i].value],explosions[i].x,explosions[i].y,null);
				}
				
				if(explosions[i].value==0){
					Sou_explosion.play();
				}
				
				explosions[i].value++;
				
				if(explosions[i].value>6){
					explosions[i] = null;
				}
			}
		}
		
	}
	
	public static void addBigExplosion(int x, int y, int radius, int nb){

		for(int i=0; i<nb; i++){
			addExplosion((int)(x + (0.5-Math.random())*radius*2),
					(int)(y + (0.5-Math.random())*radius*2),
					(int)(FPS*(float)nb/40*Math.random())
					);
		}
		
	}
	
	public static void addBigExplosion(Place pl, int radius, int nb){
		
		for(Entry<Place, Rectangle> entry: colonyRects.entrySet()){
			
			if(entry.getKey()!=null && pl!=null){
				if(entry.getKey().left==pl.left && entry.getKey().tunnel==pl.tunnel){
					addBigExplosion(entry.getValue().x+entry.getValue().width/2, entry.getValue().y+entry.getValue().height/2, radius, nb);
					return;
				}
			}
			
		}
		
		
	}
	
	public static void addExplosion(int x, int y, int delay){
		
		int i = 0;
		while(i<explosions.length && explosions[i]!=null){
			i++;
		}
		if(i==explosions.length){
			i = 0;
		}
		
		explosions[i] = new PointValue(x-96,y-96);
		explosions[i].value = 0-delay;
		
	}
	
	public static void addExplosion(Place pl){
		
		for(Entry<Place, Rectangle> entry: colonyRects.entrySet()){
			
			if(entry.getKey()!=null && pl!=null){
				if(entry.getKey().left==pl.left && entry.getKey().tunnel==pl.tunnel){
					addExplosion(entry.getValue().x+entry.getValue().width/2, entry.getValue().y+entry.getValue().height/2, 0);
					return;
				}
			}
			
		}
		
		
	}

	// Creates a new leaf (animated) from the Ant source to the Bee target.
	// Note that really only cares about the target's Place (Ant can target other Bees in same Place)
	private void createLeaf (Ant source, Bee target) {
		
		Sou_leaf.play();
		
		Rectangle antRect = colonyRects.get(source.getPlace());
		Rectangle beeRect = colonyRects.get(target.getPlace());
		if(beeRect==null || antRect==null){ //��viter les probl��mes
			return;
		}
		int startX = antRect.x + LEAF_START_OFFSET.width;
		int startY = antRect.y + LEAF_START_OFFSET.height;
		int endX = beeRect.x + LEAF_END_OFFSET.height;
		int endY = beeRect.y + LEAF_END_OFFSET.height;
		
		AnimPosition leaf = new AnimPosition(startX, startY);
		leaf.animateTo(endX, endY, (int) (LEAF_SPEED * FPS));
		leaf.color = LEAF_COLORS.get(source.getClass().getName());

		leaves.add(leaf);
	}
	
	// Creates a new leaf (animated) from the Ant source to the Bee target.
	// Note that really only cares about the target's Place (Ant can target other Bees in same Place)
	private void createNinjaLeaf (Ant source) {
		Sou_leaf.play();

		Rectangle antRect = colonyRects.get(source.getPlace());
		int startX = antRect.x + LEAF_START_OFFSET.width;
		int startY = antRect.y + LEAF_START_OFFSET.height;
		int endX = FRAME_SIZE.width+200;
		int endY = startY;

		AnimPosition leaf = new AnimPosition(startX, startY);
		leaf.animateTo(endX, endY, (int) (LEAF_SPEED * FPS));
		leaf.color = LEAF_COLORS.get(source.getClass().getName());

		leaves.add(leaf);
	}
	
	
	private void drawLongText(String str, int x, int y, Graphics2D g2d){
		
		int espace = 0;
		
		for(String s: str.split("\n")){

			espace += g2d.getFont().getSize();
			
			g2d.setColor(Color.BLACK);
			g2d.drawString(s, x-2, y-2+ espace);
			g2d.setColor(Color.WHITE);
			g2d.drawString(s, x, y + espace);
			
		}
		
	}
	
	private void drawHoverText(Graphics2D g2d){
		for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
			
			if (entry.getKey().contains(mouseX, mouseY)){
				
				g2d.setFont(FONT);
				drawLongText(entry.getValue().name,mouseX + 3, mouseY+3, g2d);
				g2d.setFont(LITTLE);
				drawLongText(entry.getValue().description,mouseX + 3, mouseY+3+20, g2d);
			}
			
		}
		
		boolean found = false;
		
		for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) {
			
			
			Rectangle rect = new Rectangle();
			rect.setBounds((int)entry.getValue().x,(int)entry.getValue().y,60,60);
			
			if (rect.contains(mouseX, mouseY)){
				
				g2d.setFont(FONT);
				drawLongText(entry.getKey().name,mouseX + 3, mouseY+3, g2d);
				g2d.setFont(LITTLE);
				drawLongText(entry.getKey().description,mouseX + 3, mouseY+3+20, g2d);
				found = true;
			}
			
		}
		
		if(!found){
		
			for (Map.Entry<Rectangle, Place> entry : colonyAreas.entrySet()) {
				
				if (entry.getKey().contains(mouseX, mouseY) && entry.getValue().tunnel>=minTunnel && entry.getValue().tunnel<=maxTunnel){
					if(entry.getValue().getAnt() == null){
						g2d.setFont(FONT);
						drawLongText(entry.getValue().name,mouseX + 3, mouseY+3, g2d);
						g2d.setFont(LITTLE);
						drawLongText(entry.getValue().description,mouseX + 3, mouseY+3+20, g2d);
					}else{
						g2d.setFont(FONT);
						drawLongText("\""+randomText[(int) ((Math.pow((int)(turn/4),2))%randomText.length)]+"\"",mouseX + 3, mouseY+3, g2d);
						g2d.setFont(LITTLE);
						drawLongText("This ant have "+(entry.getValue().getAnt().armor*100/entry.getValue().getAnt().initArmor)+"% left",mouseX + 3, mouseY+3+20, g2d);
					}
				}
				
			}
		}
		
		
		
		
	}

	// Draws all the places for the Colony on the given Graphics2D
	// Includes drawing the Ants deployed to the Colony (but not the Bees moving through it)
	private void drawColony (Graphics2D g2d, int decalage) {
		
		for (Map.Entry<Rectangle, Place> entry : colonyAreas.entrySet()) {
			Rectangle rect = entry.getKey(); // rectangle area for this place
			Place place = entry.getValue(); // place to draw

			g2d.setColor(Color.BLACK);
			//g2d.draw(rect); // border box (where to click)

			if (place != tunnelEnd && place instanceof Water) {
				g2d.drawImage(WATER_IMAGE, rect.x+decalage, rect.y, null); // decorative image
			} 
			else if (place != tunnelEnd) {
				g2d.drawImage(TUNNEL_IMAGE, rect.x+decalage, rect.y, null); //water image
			}
			
			
			boolean transparent=false;
			
			if((counter/4)%2==0){
				if(turn>95 && place.tunnel==1){
					transparent = true;
				}
				if(turn>145 && place.tunnel==3){
					transparent = true;
				}
				if(turn>245 && place.tunnel==0){
					transparent = true;
				}
				if(turn>295 && place.tunnel==4){
					transparent = true;
				}
			}
			
			if (!transparent && place != tunnelEnd && (place.tunnel<minTunnel || place.tunnel>maxTunnel) ) {
				g2d.drawImage(TUNNEL_CLOSED_IMAGE, rect.x+decalage, rect.y, null); // decorative image
			} 
			
			if (rect.contains(mouseX, mouseY)  && !PAUSE && !FIN && entry.getValue().tunnel>=minTunnel && entry.getValue().tunnel<=maxTunnel){
				g2d.drawImage(TUNNEL_SELECT_IMAGE, rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height, null);
			}
			
			int total_life = 0;
			int total_life_start = 0;
			
			Ant ant = place.getAnt();
			if (ant != null) { // draw the ant if we have one
				if(ant.buff){
					Image img = ANT_IMAGES.get(ant.getClass().getName()+"buffed");
					g2d.drawImage(img, rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height, null);
				}else{
					Image img = ANT_IMAGES.get(ant.getClass().getName());
					g2d.drawImage(img, rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height, null);
				}
				total_life+=ant.armor;
				total_life_start+=ant.initArmor;
			}
			
			int barsize = Math.min(60,Math.max(total_life_start*5,15));

			if(total_life>0){
				g2d.setColor(Color.GRAY);
				g2d.fillRect(rect.x + PLACE_PADDING.width+30-barsize/2, rect.y + PLACE_PADDING.height + 10, barsize,5);
				
				g2d.setColor(Color.GREEN);
				if((float)total_life/total_life_start<=0.7){
					g2d.setColor(Color.ORANGE);
				}else if((float)total_life/total_life_start<=0.4){
					g2d.setColor(Color.RED);
				}
				g2d.fillRect(rect.x + PLACE_PADDING.width+30-barsize/2+1, rect.y + PLACE_PADDING.height + 11, (int)((barsize-2)*((float)total_life/total_life_start)),3);

			}
			
			total_life = 0;
			total_life_start = 0;
			ant = place.getContainingAnt();
			if (ant != null) { // draw the containing ant if we have one
				Image img = ANT_IMAGES.get(ant.getClass().getName());
				g2d.drawImage(img, rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height, null);
				total_life+=ant.armor;
				total_life_start+=ant.initArmor;
			}
			
			
			barsize = Math.min(60,Math.max(total_life_start*5,15));

			if(total_life>0){
				g2d.setColor(Color.GRAY);
				g2d.fillRect(rect.x + PLACE_PADDING.width+30-barsize/2, rect.y + PLACE_PADDING.height + 10 - 10, barsize,5);
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect(rect.x + PLACE_PADDING.width+30-barsize/2+1, rect.y + PLACE_PADDING.height + 11 - 10, (int)((barsize-2)*((float)total_life/total_life_start)),3);

			}


		}
	}

	// Draws all the Bees (included deceased) in their current locations
	private void drawBees (Graphics2D g2d) {
		for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) // go through all the Bee positions
		{
			AnimPosition pos = entry.getValue();
			Bee bee = entry.getKey();
			
			Image image;

			//Lors du retour !
			Boolean flip = false;
			int level = Math.min(9, Math.max(0,bee.level));
			
			image = BEE_IMAGE[level];
			if((counter/3)%2==0){
				image = BEE_IMAGE2[level];
			}
			
			if(entry.getKey().place!=null){
				
				if(bee.lastAttacked==1){
					addXP(bee.initArmor-bee.armor);
				}
	
				if(bee.lastAttacked<FPS/4){ //Change l'image pour un quart de seconde
					image = BEEBAD_IMAGE[level];
				}else{
					image = BEE_IMAGE[level];
					if((counter/3)%2==0){
						image = BEE_IMAGE2[level];
					}
				}
				if(bee.lastAttack<FPS/4){ //Change l'image pour un quart de seconde
					image = BEEATTACK_IMAGE[level];
				}
			
				if(bee.armor<=0 && entry.getKey().place.toString()!="AntQueen"){ //Change l'image
					flip = true;
				}
			}else{
				flip = true;
			}
			
			int mx = (int)(Math.cos((float)counter/(20+bee.randomDecalage))*5);
			int my = (int)(Math.cos((float)counter/(20+bee.randomDecalage))*10)+5;
			
			if(!bee.invisible){
				
				if(flip){
					g2d.drawImage(image,
						(int) pos.x + mx+image.getWidth(getParent()),
						(int) pos.y + my,
						-image.getWidth(getParent()),
						image.getHeight(getParent()),
						null); // draw a bee at that position!
				}else{
					g2d.drawImage(image,
							(int) pos.x + mx,
							(int) pos.y + my,
							null); // draw a bee at that position!
				}
			
			}
			
			
			int barsize = Math.min(100,bee.initArmor*3);
			
			if(bee.armor>0){
				g2d.setColor(Color.GRAY);
				g2d.fillRect((int)(pos.x + PLACE_PADDING.width + 30-barsize/2 + mx), (int)(pos.y + PLACE_PADDING.height + 10 + my), barsize,5);
				
				g2d.setColor(Color.RED);
				g2d.fillRect((int)(pos.x + PLACE_PADDING.width + 30-barsize/2 +mx + 1), (int)(pos.y + PLACE_PADDING.height + 10 + my + 1),
						(int)((barsize-2)*((float)bee.armor/bee.initArmor)),3);


			}
			
		}
	}

	// Draws all the leaves (animation elements) at their current location
	private void drawLeaves (Graphics2D g2d) {
		for (AnimPosition leafPos : leaves) {
			double angle = leafPos.framesLeft * Math.PI / 8; // spin PI/8 per frame (magic variable)
			Shape leaf = leafShape((int) leafPos.x, (int) leafPos.y, angle, LEAF_SIZE);
			g2d.setColor(leafPos.color);
			g2d.fill(leaf);
		}
	}

	/**
	 * Generates the geometric shape to draw for a leaf
	 *
	 * @param x
	 *            starting point (center) x
	 * @param y
	 *            starting point (center) y
	 * @param angle
	 *            current angle the leaf is pointing
	 * @param length
	 *            length of the leaf
	 * @return a new leaf shape
	 */
	private Shape leafShape (int x, int y, double angle, int length) {
		// calculate angles and distances to move
		double[] a = { angle - Math.PI, angle - 3 * Math.PI / 4, angle - Math.PI / 2, angle - Math.PI / 4, angle, angle + Math.PI / 4, angle + Math.PI / 2, angle + 3 * Math.PI / 4 };
		double[] d = { length / 3, length / 2.5, length / 2, length / 1.5, length, length / 1.5, length / 2, length / 2.5 };

		// build a shape that is vaguely leaf-like
		Path2D.Double curve = new Path2D.Double();
		curve.moveTo(x + Math.cos(a[0]) * d[0], y + Math.sin(a[0]) * d[0]); // mathematical magic (just moving from start by given angle and distance, in order)
		curve.quadTo(x + Math.cos(a[1]) * d[1], y + Math.sin(a[1]) * d[1], x + Math.cos(a[2]) * d[2], y + Math.sin(a[2]) * d[2]);
		curve.quadTo(x + Math.cos(a[3]) * d[3], y + Math.sin(a[3]) * d[3], x + Math.cos(a[4]) * d[4], y + Math.sin(a[4]) * d[4]);
		curve.quadTo(x + Math.cos(a[5]) * d[5], y + Math.sin(a[5]) * d[5], x + Math.cos(a[6]) * d[6], y + Math.sin(a[6]) * d[6]);
		curve.quadTo(x + Math.cos(a[7]) * d[7], y + Math.sin(a[7]) * d[7], x + Math.cos(a[0]) * d[0], y + Math.sin(a[0]) * d[0]);

		return curve;
	}
	
	//Afficher les vagues d'ennemis en cours d'arrivee
	private void drawIncomingWaves (Graphics2D g2d, int decalageY) {
		
		int x = 0;
		int y = 0;
		int level = 0;
		Image img;
		
		for(Entry<Bee, PointValue> entry: futureBees.entrySet()){
			
			x = 230-(counter-entry.getValue().value)*30/FPS + entry.getValue().x;
			y = 125 + entry.getValue().y;
			level = Math.min(9, Math.max(0,entry.getKey().level));
			
			img = BEE_IMAGE[level];
			if((counter/3)%2==0){
				img = BEE_IMAGE2[level];
			}
			
			g2d.drawImage(img,x-10,y-10 + (int)(5*Math.cos((float)(3*counter)/FPS)),20,20,this);
			g2d.setFont(LITTLEMAP);
			drawLongText("lv."+(level+1),x-10,y + 10+ (int)(5*Math.cos((float)(3*counter)/FPS)),g2d);
			g2d.setFont(FONT);

		}
	
	}

	// Draws the ant selector area
	private void drawAntSelector (Graphics2D g2d, int decalageY) {
		
		scrollSelector(g2d);
		
		if(STARTED<=0 && (counter*6/FPS)%2==0 && LASTLEVELCHANGE<FPS*2){
			g2d.setColor(Color.WHITE);
			g2d.fillRect(2, 2, FRAME_SIZE.width, 119);
		}
		
		// go through each selector area
		for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
			
			Rectangle rect = entry.getKey(); // selected area
			Ant ant = entry.getValue(); // ant to select
			
			// box status
			
			if (rect.contains(mouseX, mouseY) && !PAUSE && !FIN){
				g2d.drawImage(TUNNEL_SELECT_IMAGE, rect.x + PANEL_PADDING.width, rect.y + PANEL_PADDING.height -decalageY, null);
			}
			else if (ant == selectedAnt) {
				g2d.drawImage(TUNNEL_SELECTED_IMAGE, rect.x + PANEL_PADDING.width, rect.y + PANEL_PADDING.height -decalageY, null);
			}

			// ant image
			Image img = ANT_IMAGES.get(ant.getClass().getName());
			if (ant.getFoodCost() > colony.getFood()) {
				img = ANT_IMAGES.get(ant.getClass().getName()+"disabled");
			}
			g2d.drawImage(img, rect.x + PANEL_PADDING.width, rect.y + PANEL_PADDING.height -decalageY, null);

			// food cost
			g2d.setColor(Color.WHITE);
			g2d.drawString("" + ant.getFoodCost(), rect.x + (rect.width / 2), rect.y + ANT_IMAGE_SIZE.height + 4 + PANEL_PADDING.height -decalageY);

		
		} 
		
		drawIncomingWaves(g2d,decalageY);
		
		g2d.drawImage(MENUFRONT, 0, -decalageY, null); // draw a bee at that position!

		// box status
		if (removerArea.contains(mouseX, mouseY)  && !PAUSE && !FIN){
			g2d.drawImage(TUNNEL_SELECT_IMAGE, removerArea.x + PANEL_PADDING.width, removerArea.y + PANEL_PADDING.height -decalageY, null);
		}
		else if (selectedAnt == null) {
			g2d.drawImage(TUNNEL_SELECTED_IMAGE, removerArea.x + PANEL_PADDING.width, removerArea.y + PANEL_PADDING.height -decalageY, null);
		}
		g2d.setColor(Color.WHITE);

		g2d.drawImage(REMOVER_IMAGE, removerArea.x + PANEL_PADDING.width, removerArea.y + PANEL_PADDING.height -decalageY, null);
	

		
	}
	
	private void scrollSelector(Graphics2D g2d){
		
		if(dragStart.y < 140 && mousePressed && antSelectorAreas.size()>9){
			
			for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
				entry.getKey().x += mouseX-scrollPos.x;
			}
						
			//Block scroll
			int mini = 1000;
			int max = 0;
			for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
				if(entry.getKey().x<mini){
					mini = entry.getKey().x;
				}
				if(entry.getKey().x>max){
					max = entry.getKey().x;
				}
			}
			
			if(mini>=80){
				
				for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
					entry.getKey().x += 80 - mini;
				}
				
			}else if(max<=620){
				
				for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
					entry.getKey().x += 620 - max;
				}
				
			}
			//End
			
			
			scrollPos.x = mouseX;
		}
		
	}

	/**
	 * Initializes the Ant graphics for the game. This method loads Ant details from an external file.
	 * Note that this method MUST be called before others (since they rely on the Ant details!)
	 */
	private void initializeAnts () {
		// load ant properties from external file
		try {
			Scanner sc = new Scanner(new File(ANT_FILE));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.matches("\\w.*")) { // not a comment
					String[] parts = line.split(","); // get the entry parts
					String antType = ANT_PKG + "." + parts[0].trim(); // prepend package name
					try {
						Class.forName(antType); // make sure the class is implemented and we can load it
						ANT_TYPES.add(antType);
						ANT_IMAGES.put(antType, ImageUtils.loadImage(parts[1].trim()));
						
						if (parts.length > 2) {
							LEAF_COLORS.put(antType, new Color(Integer.parseInt(parts[2].trim())));
						}
					}
					catch (ClassNotFoundException e) {
					} // if class isn't found, will continue (reading next line)
					ANT_IMAGES.put(antType, ImageUtils.loadImage(parts[1].trim()));

				}
			}
			sc.close();
		}
		catch (IOException e) { // for IOException, NumberFormatException, ArrayIndex exception... basically if anything goes wrong, don't crash
			System.out.println("Error loading insect gui properties: " + e);
		}
		

	}

	/**
	 * Initializes the Bee graphics for the game. Sets up positions for animations
	 */
	private void initializeBees () {
		Bee[] bees = hive.getBees();
		for (int i = 0; i < bees.length; i++) {
			allBeePositions.put(bees[i], new AnimPosition((int) FRAME_SIZE.getWidth()+200, (int) (HIVE_POS.y + (100 * Math.random() - 50))));
		}
	}

	/**
	 * Initializes the Colony graphics for the game.
	 * Assumes that the AntColony.getPlaces() method returns places in order by row
	 */
	private void initializeColony () {
		Point pos = new Point(PLACE_POS); // start point of the places
		int width = BEE_IMAGE_WIDTH + 2 * PLACE_PADDING.width;
		int height = ANT_IMAGE_SIZE.height + 2 * PLACE_PADDING.height;
		int row = 0;
		pos.translate((width + PLACE_MARGIN) / 2, 0); // extra shift to make room for queen
		for (Place place : colony.getPlaces()) {
			if (place.getExit() == colony.getQueenPlace()) // if this place leads to the queen (the end)
			{
				pos.setLocation(PLACE_POS.x, PLACE_POS.y + row * (height + PLACE_MARGIN)); // move down to beginning of next row
				pos.translate((width + PLACE_MARGIN) / 2, 0); // extra shift to make room for queen
				row++; // increase row number
			}

			Rectangle clickable = new Rectangle(pos.x, pos.y, width, height);
			if(place.getEntrance()!=null){
				colonyAreas.put(clickable, place);
				colonyRects.put(place, clickable);
			}

			pos.translate(width + PLACE_MARGIN, 0); // shift rectangle position for next run
		}

		// make queen location
		pos.setLocation(0, PLACE_POS.y + (row - 1) * (height + PLACE_MARGIN) / 2); // middle of the tunnels (about)
		Rectangle queenRect = new Rectangle(pos.x, pos.y, 0, 0); // no size, will not be drawn
		tunnelEnd = colony.getQueenPlace();
		colonyAreas.put(queenRect, tunnelEnd);
		colonyRects.put(tunnelEnd, queenRect);
	}

	/**
	 * Initializes the graphical Ant Selector area.
	 * Assumes that the Ants have already been initialized (and have established image resources)
	 */
	private void initializeAntSelector () {
		
		//In case we change it all
		antSelectorAreas = new HashMap<Rectangle, Ant>();
		
		Point pos = new Point(PANEL_POS); // starting point of the panel
		int width = ANT_IMAGE_SIZE.width + 2 * PANEL_PADDING.width;
		int height = ANT_IMAGE_SIZE.height + 2 * PANEL_PADDING.height;

		removerArea = new Rectangle(pos.x, pos.y, width, height);
		pos.translate(width + 2, 0);

		for (String antType : ANT_TYPES) // go through the ants in the types; in order
		{
			Rectangle clickable = new Rectangle(pos.x, pos.y, width, height); // where to put the selector
			Ant ant = buildAnt(antType); // the ant that gets deployed from that selector
			if(ant!= null && !(ant instanceof QueenAnt && HASQUEEN)){
				if(ant.level <= LEVEL || DEBUG){ //Only our level of ants

					antSelectorAreas.put(clickable, ant); // register the deployable ant so we can select it
	
					pos.translate(width + 2, 0); // shift rectangle position for next run
				}
			}
		}
	}

	/**
	 * Returns a new instance of an Ant object of the given subclass
	 *
	 * @param antType
	 *            The name of an Ant subclass (e.g., "HarvesterAnt")
	 * @return An instance of that subclass, created using the default constructor
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Ant buildAnt (String antType) {
		Ant ant = null;
		try {
			Class antClass = Class.forName(antType); // what class is this type
			Constructor constructor = antClass.getConstructor(); // find the default constructor (using reflection)
			ant = (Ant) constructor.newInstance(); // call the default constructor to make a new ant
		}
		catch (Exception e) {
		}

		return ant; // return the new ant
	}

	////////////////////
	// Event Handlers //
	////////////////////

	@Override
	public void actionPerformed (ActionEvent e) {
		if (e.getSource() == clock) {
			nextFrame();
		}
	}

	@Override
	public void mousePressed (MouseEvent event) {
		if(STARTED==0){
			handleClick(event); // pass to synchronized method for thread safety!
		}
		this.repaint(); // request a repaint
		if (!clock.isRunning()) {
			clock.start();
		}else if(STARTED!=0){
			DOCLICK = 10;
		}
		
		if(mousePressed == false){
			dragStart.setLocation(mouseX, mouseY);
			scrollPos.setLocation(mouseX,mouseY);
			mousePressed = true;
		}
	}

	@Override
	public void mouseClicked (MouseEvent e) {
	}

	@Override
	public void mouseReleased (MouseEvent e) {
		mousePressed = false;
		dragStart.setLocation(-1, -1);
	}



	// Specifies and starts an animation for a Bee (moving to a particular place)
	private void startAnimation (Bee b) {
		AnimPosition anim = allBeePositions.get(b);
		if (anim.framesLeft == 0) // if not already animating
		{
			Rectangle rect = colonyRects.get(b.getPlace()); // where we want to go to
			if (rect != null && !rect.contains(anim.x, anim.y)) {
				anim.animateTo(rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height, FPS * TURN_SECONDS);
			}
		}
	}

	@Override
	public void mouseExited (MouseEvent e) {
	}

	/**
	 * An inner class that encapsulates location information for animation
	 */
	private static class AnimPosition {

		private double x, y; // current position
		private double dx, dy; // amount to move each frame (double precision)
		private int framesLeft; // frames left in animation
		private Color color; // color of thing we're animating (if relevant)

		/**
		 * Creates a new AnimPosition at the given coordinates
		 *
		 * @param x
		 * @param y
		 */
		public AnimPosition (int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Moves (translates) the animation position by a single frame
		 */
		public void step () {
			x += dx;
			y += dy;
			framesLeft--;
		}

		/**
		 * Calculates the animation movements to get to the given position from the current position in the specified number of frames
		 *
		 * @param nx
		 *            Target x
		 * @param ny
		 *            Target y
		 * @param frames
		 *            Number of frames to move in
		 */
		public void animateTo (int nx, int ny, int frames) {
			framesLeft = frames; // reset number of frames to move
			dx = (nx - x) / framesLeft; // delta is distance between divided by num frames
			dy = (ny - y) / framesLeft;
		}

		@Override
		public String toString () {
			return "AnimPosition[x=" + x + ",y=" + y + ",dx=" + dx + ",dy=" + dy + ",framesLeft=" + framesLeft + "]";
		}
	}

	/**
	 * A utility class for working with external images (placed as inner class so less overwhelming)
	 */
	public static class ImageUtils {

		/**
		 * Loads an image object with the given filename.
		 *
		 * @param filename
		 *            The path and filename of the image to load
		 * @return An Image object representing that image.
		 */
		public static Image loadImage (String filename) {
			Image img = null;

			try {
				img = ImageIO.read(new File(filename)); // read the image from a file
			}
			catch (IOException e) {
				System.err.println("Error loading \'" + filename + "\': " + e.getMessage());
			}
			return img; // return the image
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


}

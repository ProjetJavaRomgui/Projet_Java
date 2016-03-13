package core;

//Importations
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
import ants.HungryAnt;
import ants.NinjaThrowerAnt;
import ants.QueenAnt;
import ants.ThrowerAnt;
import system.AnimPosition;
import system.Audio;
import system.ImageUtils;

/**
 * A class that controls the graphical game of Ants vs. Some-Bees. Game
 * simulation system and GUI interaction are intermixed.
 *
 * @author Mollard Romaric & Ruchot Guillaume on the Joel work
 * @version 2016
 */
@SuppressWarnings("serial")
public class AntGame extends JPanel implements ActionListener, MouseListener {

	
	
	/**
	 *
	 *	Variables
	 *
	 */
	
	private static final long serialVersionUID = 1L;
	// game models
	private AntColony colony;
	private Hive hive;
	private static final String ANT_FILE = "assets/antlist.properties";
	private static final String ANT_PKG = "ants";

	// game clocks & speed
	public static final int FPS = 30; // target frames per second
	public static final int TURN_SECONDS = 1; // seconds per turn
	public static final double LEAF_SPEED = .3; // in seconds
	public static int turn; // current game turn
	private int frame; // time elapsed since last turn
	private int counter; // Sum of all frame counts (never set to 0)
	private int counterExt; // Same as counter but do not stop on pause
	private Timer clock; // Game clock
	private int STARTTIME = 10; // Set to 10, intro duration
	private int STARTED = FPS * STARTTIME;

	// ant properties (laoded from external files, stored as member variables)
	private final ArrayList<String> ANT_TYPES;
	private final Map<String, Image> ANT_IMAGES;// = new HashMap<String,Image>();
	private final Map<String, Color> LEAF_COLORS;// = new HashMap<String, Color>();

	// other images (stored as member variables)
	private final Image TUNNEL_IMAGE = ImageUtils.loadImage("assets/imgs/tunnel.gif");
	private final Image TUNNEL_CLOSED_IMAGE = ImageUtils.loadImage("assets/imgs/tunnelclosed.gif");
	private final Image WATER_IMAGE = ImageUtils.loadImage("assets/imgs/water.png");
	private final Image WATERNOT_IMAGE = ImageUtils.loadImage("assets/imgs/water_not.png");
	private final Image TUNNEL_SELECT_IMAGE = ImageUtils.loadImage("assets/imgs/tun_select.png");
	private final Image TUNNEL_SELECTED_IMAGE = ImageUtils.loadImage("assets/imgs/tun_selected.png");
	private final Image FOOD = ImageUtils.loadImage("assets/imgs/food.png");
	private final Image BEE_IMAGE[] = new Image[10];
	private final Image BEE_IMAGE2[] = new Image[10];
	private final Image BEEBAD_IMAGE[] = new Image[10];
	private final Image BEEATTACK_IMAGE[] = new Image[10];
	private final Image BEESLOW_IMAGE[] = new Image[10];
	private final Image BEESTUN_IMAGE[] = new Image[10];
	private final Image BANG[] = new Image[5];
	private final Image EXPLOSION[] = new Image[7];
	private final Image REMOVER_IMAGE = ImageUtils.loadImage("assets/imgs/remover.gif");
	private final Image BACK = ImageUtils.loadImage("assets/imgs/preback.png");
	private final Image START = ImageUtils.loadImage("assets/imgs/start.png");
	private final Image STARTCLICK = ImageUtils.loadImage("assets/imgs/start_button.png");
	private final Image MENU = ImageUtils.loadImage("assets/imgs/menutop.png");
	private final Image MENUFRONT = ImageUtils.loadImage("assets/imgs/menutop_front.png");
	private final Image PAUSE_IMG = ImageUtils.loadImage("assets/imgs/pause.png");
	private final Image PLAY_IMG = ImageUtils.loadImage("assets/imgs/play.png");
	private final Image HOVERBLACK = ImageUtils.loadImage("assets/imgs/hoverblack.png");
	private final Image BLOOD = ImageUtils.loadImage("assets/imgs/blood.png");
	private final Image BLOOD2 = ImageUtils.loadImage("assets/imgs/blood2.png");
	private final Image BLOOD5 = ImageUtils.loadImage("assets/imgs/blood5.png");
	private final Image TOMB = ImageUtils.loadImage("assets/imgs/tomb.png");
	private final Image LASER_IMG = ImageUtils.loadImage("assets/imgs/laser.png");

	//Fonts
	private final Font TITLE = new Font("Helvetica", Font.BOLD, 20);
	private final Font FONT = new Font("Helvetica", Font.BOLD, 15);
	private final Font LITTLE = new Font("Helvetica", Font.ITALIC, 15);
	private final Font LITTLEMAP = new Font("Helvetica", Font.ITALIC, 10);

	//Random ants text (| separated)
	private final String[] randomText = "Hey !|Hello ?|I don't want to die !|Help me !|Who are you ?|Did you see the marvel ManAnt ?|I am the doctor !|Where is my TARDIS ?|Potatoes.|You're making a big mistake !|I'll kill you !|Who am I ?|Wow it's dirty !"
			.split("\\|");
	
	// positioning constants
	public static final Dimension FRAME_SIZE = new Dimension(1024, 700);
	public static final Dimension ANT_IMAGE_SIZE = new Dimension(66, 71); // avrage size;
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
	public static final int LEAF_SIZE = 28;
	public static final int LEAF_SIZE_BUFF = 50;

	// Sounds
	private static final Audio food_earn = new Audio("food_earn.wav");
	private static final Audio Sou_select = new Audio("select.wav");
	private static final Audio Sou_place = new Audio("place.wav");
	private static final Audio Sou_delete = new Audio("delete.wav");
	private static final Audio Sou_explosion = new Audio("explosion.wav");
	private static final Audio Sou_leaf = new Audio("throw.wav");
	private static final Audio Sou_nleaf = new Audio("laser.wav");
	private static final Audio Sou_slap = new Audio("slap.wav");
	private static final Audio[] add = new Audio[4];
	//Musics
	private static final Audio gameover = new Audio("gameover.wav");
	private static final Audio title = new Audio("title.wav");
	private static final Audio music = new Audio("music.wav");

	// areas that can be clicked
	private Map<Rectangle, Place> colonyAreas; // maps from a clickable area to a Place
	private static Map<Place, Rectangle> colonyRects; // maps from a Place to its clickable rectangle (reverse lookup!)
	private Map<Rectangle, Ant> antSelectorAreas; // maps from a clickable area to an Ant that can be deployed
	private Rectangle removerArea; // click to remove an ant
	private Place tunnelEnd; // a Place representing the end of the tunnels (for drawing)
	private Ant selectedAnt; // which ant is currently selected
	
	// variables tracking and handling animations
	private Map<Bee, AnimPosition> allBeePositions; // maps from Bee to an object storing animation status
	private Map<Bee, PointValue> futureBees; // maps from Bee to an object storing animation status
	private ArrayList<AnimPosition> leaves; // leaves we're animating
	public static PointValue[] explosions = new PointValue[100];
	public static PointValue[] tombstone = new PointValue[1000];
	
	//Game vars
	private Point[] Food = new Point[100];
	private int LEVEL = 0;
	private boolean PAUSE = false;
	public static boolean FIN = false;
	private int DOCLICK = 0; //To make intro faster, its set to 10 if click on start
	private boolean DEAD = false; // If the game is loose

	// Start with only one tunel, the center one
	public static int minTunnel = 2;
	public static int maxTunnel = 2;
	
	//Mouse and scroll vars
	private int mouseX = 0;
	private int mouseY = 0;
	private boolean mousePressed = false;
	private Point dragStart = new Point(0, 0);
	private Point scrollPos = new Point(0, 0);

	//Staztistics
	private String[] stats = new String[10]; // Contain stats
	public static int XP = 0;
	public int FOODCREATED = 0;
	public static int DEADANT = 0;
	public static int DEADBEES = 0;
	private int XP_RECORD = 0;
	private boolean HASQUEEN = false;
	private int LASTLEVELCHANGE = 1000;
	private int ANTSDISCOVERED = 0;

	
	

	
	
	
	
	/**
	 * Creates a new game of Ants vs. Some-Bees, with the given colony and hive
	 * setup
	 *
	 * @param colony
	 *            The ant colony for the game
	 * @param hive
	 *            The hive (and attack plan) for the game
	 */
	public AntGame(AntColony colony) {
		
		//Initialize image and sounds
		initializeVars(colony);

		//Initialize mouse listener
		addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent me) {
				if (STARTED == 0) {
					mouseX = me.getX();
					mouseY = me.getY();
				} else {
					mouseX = -1;
					mouseY = -1;
				}
			}

			public void mouseDragged(MouseEvent me) {
				if (STARTED == 0) {
					mouseX = me.getX();
					mouseY = me.getY();
				} else {
					mouseX = -1;
					mouseY = -1;
				}
			}
		});
		

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

		// map clickable areas to what they refer to. Might be more efficient to
		// use separate components, but this keeps everything together
		antSelectorAreas = new HashMap<Rectangle, Ant>();
		colonyAreas = new HashMap<Rectangle, Place>();
		colonyRects = new HashMap<Place, Rectangle>();
		initializeAntSelector();
		initializeColony();

		// adding interaction
		addMouseListener(this);

		//Make Window
		makeWindow();

	}


	


	/**
	 * Runs the actual game, processing what occurs on every frame of the game
	 * (including individual turns). This handles both some game logic (turn
	 * order) and animation control
	 */
	private void nextFrame() {

		if (!PAUSE) {
			counter++;
			colony.lastAttacked++;
		}
		counterExt++;
		LASTLEVELCHANGE++;

		if (clock.isRunning()) {

			int temps = STARTED;
			STARTED += -1 - DOCLICK;
			STARTED = Math.max(0, STARTED);

			if (STARTED == 0 && temps != 0) {
				music.play();
				music.gain(-40);
				music.loop(true);
			}
			
			try{
				

			if (STARTED == 0) {

				// Fondu des musiques
				if (counter % 4 == 0) {
					if (title.gain() > -70) {
						title.gain(title.gain() - 1);
					}
					if (title.gain() < -10) {
						music.gain(Math.min(-10, music.gain() + 1));
					}
				}

				if (frame == 0 && !PAUSE) // at the start of a turn
				{
					System.out.println("TURN: " + turn);

					system.Utils.addXP(10);

					///////////////////
					// Generation du jeu
					///////////////////
					gestionJeu();
					showBees();
					///////////////////
					///////////////////
					///////////////////

					// Changer le niveau de jeu (et les ants disponibles)
					if (turn % 50 == 0) {
						int temp = LEVEL;
						LEVEL = turn / 50;
						if (temp != LEVEL) {
							LASTLEVELCHANGE = 0;
						}
						// 1 level = 50 tours
						initializeAntSelector();
					}

					// Ajouter une vie !
					if (turn % 100 == 0 && turn>99) {
						colony.life++;
					}

					// ants take action!
					for (Ant ant : colony.getAllAnts()) {
						if (ant instanceof QueenAnt) // if we're a queen , let's
														// buff
						{
							System.out.print(colony.queenPlace.getQueenPlace());
							if (colony.queenPlace.getQueenPlace().getExit().getAnt() != null) {
								colony.queenPlace.getQueenPlace().getExit().getAnt().buff = true;
							}
							if (colony.queenPlace.getQueenPlace().getEntrance().getAnt() != null) {
								colony.queenPlace.getQueenPlace().getEntrance().getAnt().buff = true;
							}
						}

						system.Utils.addXP(1);
					}

					int pos = 0;
					for (Map.Entry<Place, Rectangle> entry : colonyRects.entrySet()) {

						pos = 0;
						while (pos < Food.length - 1 && Food[pos] != null) {
							pos++;
						}

						if (entry.getKey().getAnt() != null && entry.getKey().getAnt().foodMakePerTurn > 0
								&& (Math.random() > 0.5 || entry.getKey().getAnt().buff)) {
							Food[pos] = new Point(entry.getValue().x + entry.getValue().width / 2,
									entry.getValue().y + entry.getValue().height / 2);
						}

					}

					// bees take action!
					for (Bee bee : colony.getAllBees()) {
						if (bee.place != null) {
							bee.action(colony);
							startAnimation(bee); // start up animation for the
													// bee if needed
						}

					}

					if (colony.queenHasBees()) { // more than 1 life
						System.out.println("Queen Has Bees !!!");
						for (Bee bee : colony.queenPlace.getBees()) {
							if (bee != null && bee.damageDone) {
								bee.getPlace().removeInsect(bee);
								bee.invisible = true;
							}
						}
					}

					// new invaders attack!
					Bee[] invaders = hive.invade(colony, turn); // this moves
																// the bees into
																// the colony
					for (Bee bee : invaders) {
						startAnimation(bee);
					}

					// if want to do this to ants as well, will need to start
					// storing dead ones with AnimPositions
				}

				// Finish ants (special for exploding ants :p)
				if (frame % (FPS / 5) == 0 && !PAUSE) {
					for (Ant ant : colony.getAllAnts()) {
						if (ant.armor <= 0) {
							ant.reduceArmor(1);
						}
						ant.lastAttacked++;
						ant.lastAttack++;
					}
				}

				if (frame == (int) (LEAF_SPEED * FPS) && !PAUSE) // after leaves
																	// animate
				{
					for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) // remove
																							// dead
																							// bees
					{
						AnimPosition pos = entry.getValue();

						if (entry.getKey().getArmor() <= 0 && entry.getKey().place != null) { // if
																								// dead
																								// bee
							if (entry.getKey().place.toString() != "AntQueen") {
								pos.animateTo((int) (FRAME_SIZE.getWidth() + 200), (int) pos.getY(), FPS * TURN_SECONDS);
							} else {
								pos.animateTo((int) (-200), (int) pos.getY(), FPS * TURN_SECONDS);
							}
						}
						if (entry.getKey().place == null) {
							pos.animateTo((int) (FRAME_SIZE.getWidth() + 200), (int) pos.getY(), FPS * TURN_SECONDS);
						}
					}
				}

				// every frame
				for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) // apply
																						// animations
																						// to
																						// all
																						// the
																						// bees
				{
					if (entry.getValue().getFramesLeft() > 0) {
						entry.getValue().step();
					}
					entry.getKey().lastAttacked++;
					entry.getKey().lastAttack++;
				}

				if (colony.queenHasBees()) { // more than 1 life
					System.out.println("Queen Has Bees !!!");
					for (Bee bee : colony.queenPlace.getBees()) {
						if (bee != null) {
							System.out.println("OUUUUCH");
							if (bee.armor > 0) {
								if (bee.getPlace() != null && bee.getPlace().left >= 0
										&& bee.getPlace().getAnt() != null) {
									bee.getPlace().getAnt().lastAttacked = 0;
									bee.reduceArmor(bee.getArmor());
								}
								if (bee.damageDone == false) {
									colony.life += -bee.colonyDegat; // Big bees
																		// can
																		// destroy
																		// all
																		// the
																		// colony
									bee.damageDone = true;
									colony.lastAttacked = 0;
								}
							}
						}
					}
				}

				for (Ant ant : colony.getAllAnts()) // apply time
				{
					// Change Queen armor
					if (ant instanceof QueenAnt) {
						HASQUEEN = true;
						ant.armor = colony.life;
						if (ant.armor <= 0) {
							if (!FIN) {
								addExplosion(ant.getPlace());
								addBigExplosion(FRAME_SIZE.width / 2, FRAME_SIZE.height / 2, FRAME_SIZE.width / 3, 50);
							}
						}
					}

					if (!PAUSE && ant.lastAttack > FPS + (int) ((0.5 - Math.random()) * FPS * 0.2)) {
						if (ant instanceof ThrowerAnt) // if we're a thrower,
														// might need to make a
														// leaf!
						{
							Bee target = ((ThrowerAnt) ant).getTarget(); // who
																			// we'll
																			// throw
																			// at
																			// (really
																			// which
																			// square,
																			// but
																			// works
																			// out
																			// the
																			// same)
							if (target != null) {
								createLeaf(ant, target);
								system.Utils.addXP(1);
							}
						}
						if (ant instanceof NinjaThrowerAnt) {
							if(ant.getPlace().getClosestBee(0, 8) != null){
								Sou_nleaf.play();
								ant.lastAttack = 0;
								system.Utils.addXP(2);
							}
						}else{
							ant.lastAttack = 0;
						}
						ant.action(colony); // take the action (actually
											// completes the throw now)
					}

					ant.lastAttacked++;
					ant.lastAttack++;

					if (ant.DEAD && ant.lastAttacked > FPS / 6) {
						addTomb(ant.place);
						ant.remove();
					}
				}
				Iterator<AnimPosition> iter = leaves.iterator(); // apply
																	// animations
																	// ot all
																	// the
																	// leaves
				while (iter.hasNext()) { // iterator so we can remove when
											// finished
					AnimPosition leaf = iter.next();
					if (leaf.getFramesLeft() > 0) {
						leaf.step();
					} else {
						iter.remove(); // remove the leaf if done animating
					}
				}

				// ADVANCE THE CLOCK COUNTERS
				frame++; // count the frame
				// System.out.println("frame: "+frame);
				if (frame == FPS * TURN_SECONDS) { // if TURN seconds worth of
													// frames
					turn++; // next turn
					frame = 0; // reset frame
				}

				if (frame == TURN_SECONDS * FPS / 2) // wait half a turn (1.5
														// sec) before ending
				{
					// check for end condition before proceeding

					if (colony.life < 1) {
						DEAD = true;
					}
				}
			} else {
				frame = 0;
			}

			if (DEAD == true && frame == 0) {

				// Ecrire le nouveau record si il y en a !
				if (XP > XP_RECORD) {
					File record = new File("assets/stats.propert");
					FileOutputStream fooStream;
					try {
						fooStream = new FileOutputStream(record, false);
						byte[] myBytes = ("" + XP).getBytes();
						fooStream.write(myBytes);
						fooStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (FIN == false) {
					FIN = true;

					music.pause();
					gameover.play();
					gameover.gain(-10);

					stats[0] = "" + counter / FPS;
					stats[1] = String.format("%,d", XP) + " xp";
					stats[2] = String.format("%,d", XP_RECORD) + " xp";
					stats[3] = String.format("%,d", FOODCREATED) + "";
					stats[4] = String.format("%,d", DEADANT) + " ant(s)";
					stats[5] = String.format("%,d", DEADBEES) + " bee(s)";
					stats[6] = "" + ANTSDISCOVERED + "/18";
				}

			}
			
			
			}catch(Exception e){
				System.out.println("ERREUR : "+e);
			}
			
		}

		this.repaint(); // request an update per frame!

	}
	
	

	/**
	 * 
	 * Make waves function of game state (arcade)
	 * 
	 */
	private void gestionJeu() {

		// Ajout des abeilles
		if (Math.random() > 0.6) {

			if (turn < 80) {

				int nb = turn % 2;

				if (Math.random() > 0.96) {
					nb = nb * 2;
				}

				if (turn > 65) {
					nb = nb * 2;
				}

				for (int i = 0; i < nb; i++) {
					addBee(3, 1);

				}

			}
			// 20 turn pause

			if (turn < 180 && turn > 100) {

				int nb = turn % 2;
				int max = 0;

				if (Math.random() > 0.96) {
					nb = nb * 2;
				}

				if (turn > 165) {
					nb = nb * 2;
					max = 5;
				}

				for (int i = 0; i < nb; i++) {
					addBee(Math.max(3, (int) ((6 + max) * Math.random())), 1);
				}

			}

			if (turn < 280 && turn > 200) {

				int nb = 1;
				int max = 0;

				if (Math.random() > 0.96) {
					nb = nb * 2;
				}

				if (turn > 265) {
					nb = nb * 2;
					max = 5;
				}

				for (int i = 0; i < nb; i++) {
					addBee(Math.max(3, (int) ((9 + max) * Math.random())), 1);
				}

			}

			// 40 turn pause

			if (turn < 460 && turn > 320) {

				int nb = 1;
				int max = 0;

				if (Math.random() > 0.96) {
					nb = nb * 2;
				}

				if (turn > 365) {
					nb = nb * 2;
					max = 5;
				}

				for (int i = 0; i < nb; i++) {
					addBee(Math.max(3, (int) ((9 + max) * Math.random())), 1);
				}

			}

			// 40 turn pause

			if (turn < 560 && turn > 420) {

				int nb = 1;

				if (Math.random() > 0.96) {
					nb = nb * 2;
				}

				for (int i = 0; i < nb; i++) {
					addBee(Math.max(10, (int) ((20) * Math.random())), 1);
				}

			}

			// Then die !

			if (turn > 580 || FIN) {
				int nb = turn / 70 + 1;

				if (Math.random() > 0.96) {
					nb = nb * 3;
				}

				for (int i = 0; i < nb; i++) {

					int life = Math.max(3, Math.min(100, (int) (Math.random() * (turn / 30 + 1))));
					addBee(life, Math.max(life / 10, 1));

				}
			}

		}

		// Ajout des tunnels
		if (turn >= 100) {
			minTunnel = Math.min(1,minTunnel);
		}
		if (turn >= 150) {
			maxTunnel = Math.max(3,maxTunnel);
		}
		if (turn >= 250) {
			minTunnel = Math.min(0,minTunnel);
		}
		if (turn >= 300) {
			maxTunnel = Math.max(4,maxTunnel);
		}
	}
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////
	///////////////////////////////////////////////////////
	/////////                                      ////////
	/////////            GAME METHODS              ////////
	/////////                                      ////////
	///////////////////////////////////////////////////////
	///////////////////////////////////////////////////////
	
	
	
	

	///////////////////////
	// elements managers //
	///////////////////////

	private void addBee(int armor, int degatColony) {

		Bee bee = new Bee(armor);
		bee.colonyDegat = degatColony;

		PointValue pt = new PointValue(20 - (int) (40 * Math.random()), (int) (20 * Math.random()));
		pt.value = counter + 20 * FPS;

		futureBees.put(bee, pt);

	}

	private void showBees() {

		ArrayList<Bee> listKey = new ArrayList<Bee>();

		for (Entry<Bee, PointValue> entry : futureBees.entrySet()) {

			if (entry.getValue().value <= counter && entry.getValue().value2 == 0) {

				entry.getValue().value2 = 1; // Empecher de mettre plusieurs
												// fois la m��me abeille

				Bee bee = new Bee(entry.getKey().armor / 2); /// 2 because
																/// armore is
																/// alway
																/// multiplies
																/// by 2
				bee.colonyDegat = entry.getKey().colonyDegat;

				hive.addInsect(bee); // put the bee in Place
				Place[] exits = colony.getBeeEntrances();

				for (Bee b : hive.getAllBees()) {
					if (b != null) {
						int randExit = (int) (Math.random() * exits.length);

						allBeePositions.put(b, new AnimPosition((int) FRAME_SIZE.getWidth() + 200,
								(int) (HIVE_POS.y + (100 * Math.random() - 50))));
						b.moveTo(exits[randExit]);
					}
				}

			}

		}

		for (Entry<Bee, PointValue> entry : futureBees.entrySet()) {

			if (entry.getValue().value <= counter - FPS) {

				listKey.add(entry.getKey());
			}

		}

		// Removing added bees
		for (Bee b : listKey) {
			futureBees.remove(b);
		}

	}

	

	private void doExplosion(Graphics2D g2d) {

		for (int i = 0; i < explosions.length; i++) {
			if (explosions[i] != null) {

				if (explosions[i].value >= 0) {
					g2d.drawImage(EXPLOSION[explosions[i].value], explosions[i].x, explosions[i].y, null);
				}

				if (explosions[i].value == 0) {
					Sou_explosion.play();
				}

				explosions[i].value++;

				if (explosions[i].value > 6) {
					explosions[i] = null;
				}
			}
		}

	}

	public static void addBigExplosion(int x, int y, int radius, int nb) {

		for (int i = 0; i < nb; i++) {
			addExplosion((int) (x + (0.5 - Math.random()) * radius * 2), (int) (y + (0.5 - Math.random()) * radius * 2),
					(int) (FPS * (float) nb / 40 * Math.random()));
		}

	}

	public static void addBigExplosion(Place pl, int radius, int nb) {

		for (Entry<Place, Rectangle> entry : colonyRects.entrySet()) {

			if (entry.getKey() != null && pl != null) {
				if (entry.getKey().left == pl.left && entry.getKey().tunnel == pl.tunnel) {
					addBigExplosion(entry.getValue().x + entry.getValue().width / 2,
							entry.getValue().y + entry.getValue().height / 2, radius, nb);
					return;
				}
			}

		}

	}

	public static void addExplosion(int x, int y, int delay) {

		int i = 0;
		while (i < explosions.length && explosions[i] != null) {
			i++;
		}
		if (i == explosions.length) {
			i = 0;
		}

		explosions[i] = new PointValue(x - 96, y - 96);
		explosions[i].value = 0 - delay;

	}

	public static void addExplosion(Place pl) {

		for (Entry<Place, Rectangle> entry : colonyRects.entrySet()) {

			if (entry.getKey() != null && pl != null) {
				if (entry.getKey().left == pl.left && entry.getKey().tunnel == pl.tunnel) {
					addExplosion(entry.getValue().x + entry.getValue().width / 2,
							entry.getValue().y + entry.getValue().height / 2, 0);
					return;
				}
			}

		}

	}

	public static void addTomb(Place pl) {

		for (Entry<Place, Rectangle> entry : colonyRects.entrySet()) {

			if (entry.getKey() != null && pl != null) {
				if (entry.getKey().left == pl.left && entry.getKey().tunnel == pl.tunnel) {

					int x = entry.getValue().x + (int) ((0.5 - Math.random()) * entry.getValue().width / 2)
							+ entry.getValue().width / 2;
					int y = entry.getValue().y + (int) ((0.5 - Math.random()) * entry.getValue().height / 2)
							+ entry.getValue().height / 2;

					int i = 0;
					while (i < tombstone.length && tombstone[i] != null) {
						i++;
					}
					if (i == tombstone.length) {
						i = (int) (Math.random() * tombstone.length);
					}

					tombstone[i] = new PointValue(x - 20, y - 20);

					System.out.println("Added Tombstone");

					return;
				}
			}

		}
	}

	// Creates a new leaf (animated) from the Ant source to the Bee target.
	// Note that really only cares about the target's Place (Ant can target
	// other Bees in same Place)
	private void createLeaf(Ant source, Bee target) {

		Sou_leaf.play();

		Rectangle antRect = colonyRects.get(source.getPlace());
		Rectangle beeRect = colonyRects.get(target.getPlace());
		if (beeRect == null || antRect == null) { // ��viter les probl��mes
			return;
		}
		int startX = antRect.x + LEAF_START_OFFSET.width;
		int startY = antRect.y + LEAF_START_OFFSET.height;
		int endX = beeRect.x + LEAF_END_OFFSET.height;
		int endY = beeRect.y + LEAF_END_OFFSET.height;

		AnimPosition leaf = new AnimPosition(startX, startY);
		leaf.buff = source.buff;
		leaf.animateTo(endX, endY, (int) (LEAF_SPEED * FPS));
		leaf.setColor(LEAF_COLORS.get(source.getClass().getName()));

		leaves.add(leaf);
	}
	
	


	////////////////
	// Drawers    //
	////////////////
	
	
	/**
	 * Main drawer, call other draw methods
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // take care of anything else
		Graphics2D g2d = (Graphics2D) g;
		g2d.clearRect(0, 0, FRAME_SIZE.width, FRAME_SIZE.height); // clear to
																	// background
																	// color

		g2d.setFont(FONT);

		int mov = (int) system.Utils.smooth(-(BACK.getWidth(getParent()) - FRAME_SIZE.width), (FPS * STARTTIME - STARTED),
				FPS * STARTTIME);
		int decalage = (BACK.getWidth(getParent()) - FRAME_SIZE.width) + mov;

		g2d.drawImage(BACK, mov, 0, null); // draw a bee at that position!

		g2d.drawImage(MENU, 0, -decalage / 6, null); // draw a bee at that
														// position!

		drawAntSelector(g2d, decalage / 6);

		if (STARTED == 0) {

			// text displays
			String antString = "none";
			if (selectedAnt != null) {
				antString = selectedAnt.getClass().getName();
				antString = antString.substring(0, antString.length() - 3); // remove
																			// the
																			// word
																			// "ant"
			}

			g2d.drawString("Life: " + Math.max(colony.life, 0) + " Food: " + colony.getFood(), 18, 140);

			g2d.drawString("XP: " + String.format("%,d", XP), FRAME_SIZE.width - 160, 40);
			g2d.drawString("Best: " + String.format("%,d", Math.max(XP, XP_RECORD)), FRAME_SIZE.width - 150, 69);

			g2d.drawString("Time: " + String.format("%,d", turn), FRAME_SIZE.width - 80, 100);

		} else {

			g2d.drawImage(START, FRAME_SIZE.width / 2 - 300 + mov / 3, 100, null);

		}

		drawTomb(g2d);
		drawColony(g2d, decalage);
		drawBees(g2d);
		drawLeaves(g2d);

		float angle = 0;
		// Afficher la nourriture
		for (int i = 0; i < Food.length; i++) {
			if (Food[i] != null) {

				angle = 3.1415f + (float) Math.atan((float) (Food[i].y) / (float) (Food[i].x));
				Food[i].x = Food[i].x + (int) (6 * Math.cos(angle));
				Food[i].y = Food[i].y + (int) (6 * Math.sin(angle));

				if (Food[i].y < 160 - Math.random() * 20) {
					Food[i] = null;
					colony.increaseFood(1);
					FOODCREATED++;
					system.Utils.addXP(1);
					food_earn.play();
				} else {
					g2d.drawImage(FOOD, Food[i].x, Food[i].y, null);
				}
			}
		}

		doExplosion(g2d);
		if (!PAUSE && !FIN) {
			drawHoverText(g2d);
		}
		if (!clock.isRunning()) { // start text

			if (counter % 2 == 0) {
				g2d.drawImage(STARTCLICK, FRAME_SIZE.width / 2 - 150, FRAME_SIZE.height - 200, null);
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.nextFrame();
		}

		if (colony.life <= 5) {
			g2d.drawImage(BLOOD5, 0, 0, getParent());
		}
		if (colony.life <= 2) {
			g2d.drawImage(BLOOD2, 0, 0, getParent());
		}
		float size = 1;
		if (colony.lastAttacked <= 10) {
			size = (float) (1f + colony.lastAttacked * 0.05);
			g2d.drawImage(BLOOD, 0 - (int) ((size - 1) * FRAME_SIZE.width) / 2,
					0 - (int) ((size - 1) * FRAME_SIZE.height) / 2, (int) (size * FRAME_SIZE.width),
					(int) (size * FRAME_SIZE.height), getParent());
		}
		if (colony.lastAttacked == 1 && !FIN) {
			addBigExplosion(0, FRAME_SIZE.height / 2 + 50, 100, 10);
		}

		if (PAUSE || FIN) {
			g2d.drawImage(HOVERBLACK, 0, 0, FRAME_SIZE.width, FRAME_SIZE.height, getParent());
		}

		if (FIN) {
			if ((int) (counterExt / FPS) % 2 == 0) {
				g2d.setFont(TITLE);
				//if (counter / FPS < Integer.parseInt(stats[0]) + 5) {
					system.Utils.drawLongText("Game over !", FRAME_SIZE.width / 2 - 65, FRAME_SIZE.height / 2 - 90, g2d);
				/*} else {
					system.Utils.drawLongText("Click anywhere to play again !", FRAME_SIZE.width / 2 - 165,
							FRAME_SIZE.height / 2 - 90, g2d);
				}*/
			}

			if (counter / FPS > Integer.parseInt(stats[0]) + 1) {
				g2d.setFont(LITTLE);
				system.Utils.drawLongText("XP :", FRAME_SIZE.width / 2 - 40, FRAME_SIZE.height / 2 - 40, g2d);
				g2d.setFont(FONT);
				system.Utils.drawLongText(stats[1], FRAME_SIZE.width / 2, FRAME_SIZE.height / 2 - 40, g2d);
			}

			if (counter / FPS > Integer.parseInt(stats[0]) + 1) {
				g2d.setFont(LITTLE);
				system.Utils.drawLongText("Best :", FRAME_SIZE.width / 2 - 50, FRAME_SIZE.height / 2 - 20, g2d);
				g2d.setFont(FONT);
				system.Utils.drawLongText("" + stats[2], FRAME_SIZE.width / 2, FRAME_SIZE.height / 2 - 20, g2d);
			}

			if (counter / FPS > Integer.parseInt(stats[0]) + 2) {
				g2d.setFont(LITTLE);
				system.Utils.drawLongText("Total food created :", FRAME_SIZE.width / 2 - 138, FRAME_SIZE.height / 2, g2d);
				g2d.setFont(FONT);
				system.Utils.drawLongText("" + stats[3], FRAME_SIZE.width / 2, FRAME_SIZE.height / 2, g2d);
			}

			if (counter / FPS > Integer.parseInt(stats[0]) + 2) {
				g2d.setFont(LITTLE);
				system.Utils.drawLongText("Dead ants :", FRAME_SIZE.width / 2 - 88, FRAME_SIZE.height / 2 + 20, g2d);
				g2d.setFont(FONT);
				system.Utils.drawLongText("" + stats[4], FRAME_SIZE.width / 2, FRAME_SIZE.height / 2 + 20, g2d);
			}

			if (counter / FPS > Integer.parseInt(stats[0]) + 2) {
				g2d.setFont(LITTLE);
				system.Utils.drawLongText("Killed bees :", FRAME_SIZE.width / 2 - 93, FRAME_SIZE.height / 2 + 40, g2d);
				g2d.setFont(FONT);
				system.Utils.drawLongText("" + stats[5], FRAME_SIZE.width / 2, FRAME_SIZE.height / 2 + 40, g2d);
			}

			if (counter / FPS > Integer.parseInt(stats[0]) + 3) {
				g2d.setFont(LITTLE);
				system.Utils.drawLongText("Total time :", FRAME_SIZE.width / 2 - 85, FRAME_SIZE.height / 2 + 70, g2d);
				g2d.setFont(FONT);
				system.Utils.drawLongText(stats[0] + " s", FRAME_SIZE.width / 2, FRAME_SIZE.height / 2 + 70, g2d);
			}

			if (counter / FPS > Integer.parseInt(stats[0]) + 4) {
				g2d.setFont(LITTLE);
				system.Utils.drawLongText("Ants discovered :", FRAME_SIZE.width / 2 - 128, FRAME_SIZE.height / 2 + 100, g2d);
				g2d.setFont(FONT);
				system.Utils.drawLongText("" + stats[6], FRAME_SIZE.width / 2, FRAME_SIZE.height / 2 + 100, g2d);
			}

		}

		if (FIN) {
			return;
		}
		if (PAUSE) {
			g2d.drawImage(PLAY_IMG, 25, FRAME_SIZE.height - 75, 50, 50, getParent());

			if ((int) (counterExt / FPS) % 2 == 0) {
				g2d.setFont(TITLE);
				system.Utils.drawLongText("Pause", FRAME_SIZE.width / 2 - 30, FRAME_SIZE.height / 2 - 20, g2d);
			}

		} else {
			g2d.drawImage(PAUSE_IMG, 25, FRAME_SIZE.height - 75, 50, 50, getParent());
		}

	}
	
	
	
	
	//Draw tombstones
	private void drawTomb(Graphics2D g2d) {

		for (int i = 0; i < tombstone.length; i++) {
			if (tombstone[i] != null) {

				g2d.drawImage(TOMB, tombstone[i].x, tombstone[i].y, null);

			}
		}

	}
	
	//Drawing text on mouse over
	private void drawHoverText(Graphics2D g2d) {
		for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {

			if (entry.getKey().contains(mouseX, mouseY)) {

				g2d.setFont(FONT);
				system.Utils.drawLongText(entry.getValue().name, mouseX + 3, mouseY + 3, g2d);
				g2d.setFont(LITTLE);
				system.Utils.drawLongText(entry.getValue().description, mouseX + 3, mouseY + 3 + 20, g2d);
			}

		}

		boolean found = false;

		for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) {

			Rectangle rect = new Rectangle();
			rect.setBounds((int) entry.getValue().getX(), (int) entry.getValue().getY(), 60, 60);

			if (rect.contains(mouseX, mouseY) && !found) {

				g2d.setFont(FONT);
				system.Utils.drawLongText(entry.getKey().name, mouseX + 3, mouseY + 3, g2d);
				g2d.setFont(LITTLE);
				system.Utils.drawLongText(entry.getKey().description, mouseX + 3, mouseY + 3 + 20, g2d);
				found = true;
			}

		}

		if (!found) {

			for (Map.Entry<Rectangle, Place> entry : colonyAreas.entrySet()) {

				if (entry.getValue().left < 8 && entry.getKey().contains(mouseX, mouseY)
						&& entry.getValue().tunnel >= minTunnel && entry.getValue().tunnel <= maxTunnel) {
					if (entry.getValue().getAnt() == null) {
						g2d.setFont(FONT);
						system.Utils.drawLongText(entry.getValue().name, mouseX + 3, mouseY + 3, g2d);
						g2d.setFont(LITTLE);
						system.Utils.drawLongText(entry.getValue().description, mouseX + 3, mouseY + 3 + 20, g2d);
					} else {
						g2d.setFont(FONT);
						system.Utils.drawLongText("\""
								+ randomText[(int) ((Math.pow((int) (turn / 4 + entry.getValue().left
										+ entry.getValue().tunnel + entry.getValue().getAnt().armor
										+ entry.getValue().getAnt().description.length()), 2)) % randomText.length)]
								+ "\"", mouseX + 3, mouseY + 3, g2d);
						g2d.setFont(LITTLE);
						system.Utils.drawLongText("This ant have "
								+ (entry.getValue().getAnt().armor)
								+ "pv", mouseX + 3, mouseY + 3 + 20, g2d);
						if(entry.getValue().getAnt().damage>0){
							system.Utils.drawLongText("It makes "
								+ (entry.getValue().getAnt().damage)
								+ " damages per attack", mouseX + 3, mouseY + 3 + 35, g2d);
						}
					}
				}

			}
		}

	}

	// Draws all the places for the Colony on the given Graphics2D
	// Includes drawing the Ants deployed to the Colony (but not the Bees moving
	// through it)
	private void drawColony(Graphics2D g2d, int decalage) {

		for (Map.Entry<Rectangle, Place> entry : colonyAreas.entrySet()) {

			if (entry.getValue().left < 8) {

				Rectangle rect = entry.getKey(); // rectangle area for this
													// place
				Place place = entry.getValue(); // place to draw

				g2d.setColor(Color.BLACK);
				// g2d.draw(rect); // border box (where to click)

				if (place != tunnelEnd && place instanceof Water && !place.hasNenuphar) {
					g2d.drawImage(WATER_IMAGE, rect.x + decalage, rect.y, null); // decorative
																					// image
				} else if (place != tunnelEnd && place instanceof Water){
					g2d.drawImage(WATERNOT_IMAGE, rect.x + decalage, rect.y, null); //nénufar !
				} else if (place != tunnelEnd) {
					g2d.drawImage(TUNNEL_IMAGE, rect.x + decalage, rect.y, null); // water
																					// image
				}

				boolean transparent = false;

				if ((counter / 4) % 2 == 0) {
					if (turn > 95 && place.tunnel == 1) {
						transparent = true;
					}
					if (turn > 145 && place.tunnel == 3) {
						transparent = true;
					}
					if (turn > 245 && place.tunnel == 0) {
						transparent = true;
					}
					if (turn > 295 && place.tunnel == 4) {
						transparent = true;
					}
				}

				if (!transparent && place != tunnelEnd && (place.tunnel < minTunnel || place.tunnel > maxTunnel)) {
					g2d.drawImage(TUNNEL_CLOSED_IMAGE, rect.x + decalage, rect.y, null); // decorative
																							// image
				}

				if (rect.contains(mouseX, mouseY) && !PAUSE && !FIN && entry.getValue().tunnel >= minTunnel
						&& entry.getValue().tunnel <= maxTunnel) {
					g2d.drawImage(TUNNEL_SELECT_IMAGE, rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height,
							null);
				}

				int total_life = 0;
				int total_life_start = 0;
				Ant ant = place.getAnt();
				if (ant != null) { // draw the ant if we have one
					
					int h;
					//Afficher le laser arrière
					if(ant instanceof NinjaThrowerAnt){
						if(ant.lastAttack<10){
							h = (10-ant.lastAttack)*3;
							if(ant.buff){
								h = h*3;
							}
							g2d.drawImage(LASER_IMG,rect.x+55, rect.y+40-(int)(3*Math.cos((float) (counter + ant.randomDecalage * 10) * 4f / FPS))-h/2, 1000, h, getParent());
						}
					}
					
					
					Image img;
					if (ant.buff) {
						img = ANT_IMAGES.get(ant.getClass().getName() + "buffed");
						if(ant instanceof HungryAnt){
							if(((HungryAnt) ant).Cooldown!=0){
							img = ANT_IMAGES.get("ants.HungryAntFoodbuffed");
							}
						}
					} else {
						img = ANT_IMAGES.get(ant.getClass().getName());
						if(ant instanceof HungryAnt){
							if(((HungryAnt) ant).Cooldown!=0){
							img = ANT_IMAGES.get("ants.HungryAntFood");
							}
						}
					}
					
					float respiration = (float) (1
							+ 0.05 * Math.cos((float) (counter + ant.randomDecalage * 10) * 4f / FPS));
					g2d.drawImage(img, rect.x + PLACE_PADDING.width,
							rect.y + PLACE_PADDING.height + (int) ((1 - respiration) 
									* img.getHeight(getParent()))
									- (int) (respiration * 10) + 10,
							img.getWidth(getParent()), (int) (respiration * img.getHeight(getParent())), null);

					total_life += ant.armor;
					total_life_start += ant.initArmor;

					int size = 0;
					if (ant.lastAttacked < FPS / 4) {
						size = (130 / (FPS / 4)) * (ant.lastAttacked);
						g2d.drawImage(BANG[(int) (Math.random() * BANG.length)],
								rect.x + 40 + (int) ((1 - Math.random()) * 20) - size / 2,
								rect.y + 50 + (int) ((1 - Math.random()) * 20) - size / 2, size, size, null);
					}
					if (ant.lastAttacked < FPS / 4) {
						Sou_slap.play();
					}
					
					
					//Afficher le laser avant
					if(ant instanceof NinjaThrowerAnt){
						if(ant.lastAttack<10){
							h = (10-ant.lastAttack)*3;
							if(ant.buff){
								h = h*3;
							}
							g2d.drawImage(LASER_IMG,rect.x+55, rect.y+48-(int)(3*Math.cos((float) (counter + ant.randomDecalage * 10) * 4f / FPS))-h/2, 1000, h, getParent());
						}
					}
					
				}

				int barsize = Math.min(60, Math.max(total_life_start * 5, 15));

				if (total_life > 0) {
					g2d.setColor(Color.GRAY);
					g2d.fillRect(rect.x + PLACE_PADDING.width + 30 - barsize / 2, rect.y + PLACE_PADDING.height + 10,
							barsize, 5);

					g2d.setColor(Color.GREEN);
					if ((float) total_life / total_life_start <= 0.7) {
						g2d.setColor(Color.ORANGE);
					} else if ((float) total_life / total_life_start <= 0.4) {
						g2d.setColor(Color.RED);
					}
					g2d.fillRect(rect.x + PLACE_PADDING.width + 30 - barsize / 2 + 1,
							rect.y + PLACE_PADDING.height + 11,
							(int) ((barsize - 2) * ((float) total_life / total_life_start)), 3);

				}

				total_life = 0;
				total_life_start = 0;
				ant = place.getContainingAnt();
				if (ant != null) { // draw the containing ant if we have one
					Image img = ANT_IMAGES.get(ant.getClass().getName());
					g2d.drawImage(img, rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height, null);
					total_life += ant.armor;
					total_life_start += ant.initArmor;
				}

				barsize = Math.min(60, Math.max(total_life_start * 5, 15));

				if (total_life > 0) {
					g2d.setColor(Color.GRAY);
					g2d.fillRect(rect.x + PLACE_PADDING.width + 30 - barsize / 2,
							rect.y + PLACE_PADDING.height + 10 - 10, barsize, 5);

					g2d.setColor(Color.WHITE);
					g2d.fillRect(rect.x + PLACE_PADDING.width + 30 - barsize / 2 + 1,
							rect.y + PLACE_PADDING.height + 11 - 10,
							(int) ((barsize - 2) * ((float) total_life / total_life_start)), 3);

				}

			}

		}
	}

	// Draws all the Bees (included deceased) in their current locations
	private void drawBees(Graphics2D g2d) {
		for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) // go
																				// through
																				// all
																				// the
																				// Bee
																				// positions
		{
			AnimPosition pos = entry.getValue();
			Bee bee = entry.getKey();

			Image image;

			// Lors du retour !
			Boolean flip = false;
			int level = Math.min(9, Math.max(0, bee.level));

			image = BEE_IMAGE[level];
			if ((counter / 3) % 2 == 0) {
				image = BEE_IMAGE2[level];
			}

			if (entry.getKey().place != null) {

				if (bee.lastAttacked == 1) {
					system.Utils.addXP(bee.initArmor - bee.armor);
				}

				if (bee.lastAttacked < FPS / 4) { // Change l'image pour un
													// quart de seconde
					image = BEEBAD_IMAGE[level];
				} else {
					image = BEE_IMAGE[level];
					if ((counter / 3) % 2 == 0) {
						image = BEE_IMAGE2[level];
					}
				}
				if (bee.lastAttack < FPS / 4) { // Change l'image pour un quart
												// de seconde
					image = BEEATTACK_IMAGE[level];
				}
				if (bee.slow >= 0) {
					image = BEESLOW_IMAGE[level];
				}
				if (bee.stun >= 0) {
					image = BEESTUN_IMAGE[level];
				}

				if (bee.armor <= 0 && entry.getKey().place.left != 0) { // Change
																		// l'image
					flip = true;
				}
			} else {
				flip = true;
			}

			int mx = (int) (Math.cos((float) counter / (20 + bee.randomDecalage)) * 5);
			int my = (int) (Math.cos((float) counter / (20 + bee.randomDecalage)) * 10) + 5;

			if (!bee.invisible && image != null) {

				if (flip) {
					g2d.drawImage(image, (int) pos.getX() + mx + image.getWidth(getParent()), (int) pos.getY() + my,
							-image.getWidth(getParent()), image.getHeight(getParent()), null); // draw
																								// a
																								// bee
																								// at
																								// that
																								// position!
				} else {
					g2d.drawImage(image, (int) pos.getX() + mx, (int) pos.getY() + my, null); // draw
																					// a
																					// bee
																					// at
																					// that
																					// position!
				}

				int barsize = Math.min(100, bee.initArmor * 3);

				if (bee.armor > 0) {
					g2d.setColor(Color.GRAY);
					g2d.fillRect((int) (pos.getX() + PLACE_PADDING.width + 30 - barsize / 2 + mx),
							(int) (pos.getY() + PLACE_PADDING.height + 10 + my), barsize, 5);

					g2d.setColor(Color.RED);
					g2d.fillRect((int) (pos.getX() + PLACE_PADDING.width + 30 - barsize / 2 + mx + 1),
							(int) (pos.getY() + PLACE_PADDING.height + 10 + my + 1),
							(int) ((barsize - 2) * ((float) bee.armor / bee.initArmor)), 3);

				}

			}

		}
	}

	// Draws all the leaves (animation elements) at their current location
	private void drawLeaves(Graphics2D g2d) {
		for (AnimPosition leafPos : leaves) {
			double angle = leafPos.getFramesLeft() * Math.PI / 8; // spin PI/8 per
																// frame (magic
																// variable)
			Shape leaf;
			if (!leafPos.buff) {
				leaf = system.Utils.leafShape((int) leafPos.getX(), (int) leafPos.getY(), angle, LEAF_SIZE);
			} else {
				leaf = system.Utils.leafShape((int) leafPos.getX(), (int) leafPos.getY(), angle, LEAF_SIZE_BUFF);
			}
			g2d.setColor(leafPos.getColor());
			g2d.fill(leaf);
		}
	}


	// Afficher les vagues d'ennemis en cours d'arrivee
	private void drawIncomingWaves(Graphics2D g2d, int decalageY) {

		int x = 0;
		int y = 0;
		int level = 0;
		Image img;
		int add;
		for (Entry<Bee, PointValue> entry : futureBees.entrySet()) {

			x = 230 - (counter - entry.getValue().value) * 30 / FPS + entry.getValue().x;
			y = 125 + entry.getValue().y;
			level = Math.min(9, Math.max(0, entry.getKey().level));

			img = BEE_IMAGE[level];
			if ((counter / 3) % 2 == 0) {
				img = BEE_IMAGE2[level];
			}

			add = +20 * entry.getKey().randomDecalage * FPS;

			g2d.drawImage(img, x - 10, y - 10 + (int) (5 * Math.cos((float) (3 * counter + add) / FPS)), 20, 20, this);
			g2d.setFont(LITTLEMAP);
			system.Utils.drawLongText("lv." + (level + 1), x - 10, y + 10 + (int) (5 * Math.cos((float) (3 * counter + add) / FPS)),
					g2d);
			g2d.setFont(FONT);

		}

	}

	// Draws the ant selector area
	private void drawAntSelector(Graphics2D g2d, int decalageY) {

		drawScrollSelector(g2d);

		if (STARTED <= 0 && (counter * 6 / FPS) % 2 == 0 && LASTLEVELCHANGE < FPS) {
			g2d.setColor(Color.WHITE);
			g2d.fillRect(2, 2, FRAME_SIZE.width, 119);
		}

		// go through each selector area
		for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {

			Rectangle rect = entry.getKey(); // selected area
			Ant ant = entry.getValue(); // ant to select

			// box status

			if (rect.contains(mouseX, mouseY) && !PAUSE && !FIN) {
				g2d.drawImage(TUNNEL_SELECT_IMAGE, rect.x + PANEL_PADDING.width,
						rect.y + PANEL_PADDING.height - decalageY, null);
			} else if (ant == selectedAnt) {
				g2d.drawImage(TUNNEL_SELECTED_IMAGE, rect.x + PANEL_PADDING.width,
						rect.y + PANEL_PADDING.height - decalageY, null);
			}

			// ant image
			Image img = ANT_IMAGES.get(ant.getClass().getName());
			if (ant.getFoodCost() > colony.getFood()) {
				img = ANT_IMAGES.get(ant.getClass().getName() + "disabled");
			}
			g2d.drawImage(img, rect.x + PANEL_PADDING.width, rect.y + PANEL_PADDING.height - decalageY, null);

			// food cost
			g2d.setColor(Color.WHITE);
			g2d.drawString("" + ant.getFoodCost(), rect.x + (rect.width / 2),
					rect.y + ANT_IMAGE_SIZE.height + 4 + PANEL_PADDING.height - decalageY);

		}

		drawIncomingWaves(g2d, decalageY);

		g2d.drawImage(MENUFRONT, 0, -decalageY, null); // draw a bee at that
														// position!

		// box status
		if (removerArea.contains(mouseX, mouseY) && !PAUSE && !FIN) {
			g2d.drawImage(TUNNEL_SELECT_IMAGE, removerArea.x + PANEL_PADDING.width,
					removerArea.y + PANEL_PADDING.height - decalageY, null);
		} else if (selectedAnt == null) {
			g2d.drawImage(TUNNEL_SELECTED_IMAGE, removerArea.x + PANEL_PADDING.width,
					removerArea.y + PANEL_PADDING.height - decalageY, null);
		}
		g2d.setColor(Color.WHITE);

		g2d.drawImage(REMOVER_IMAGE, removerArea.x + PANEL_PADDING.width,
				removerArea.y + PANEL_PADDING.height - decalageY, null);

	}

	private void drawScrollSelector(Graphics2D g2d) {

		if (dragStart.y < 140 && mousePressed && antSelectorAreas.size() > 9) {

			for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
				entry.getKey().x += mouseX - scrollPos.x;
			}

			// Block scroll
			int mini = 1000;
			int max = 0;
			for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
				if (entry.getKey().x < mini) {
					mini = entry.getKey().x;
				}
				if (entry.getKey().x > max) {
					max = entry.getKey().x;
				}
			}

			if (mini >= 80) {

				for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
					entry.getKey().x += 80 - mini;
				}

			} else if (max <= 620) {

				for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
					entry.getKey().x += 620 - max;
				}

			}
			// End

			scrollPos.x = mouseX;
		}

	}
	
	
	
	
	
	//////////////////
	// Initializers //
	//////////////////
	

	/**
	 * Initializes the Ant graphics for the game. This method loads Ant details
	 * from an external file. Note that this method MUST be called before others
	 * (since they rely on the Ant details!)
	 */
	private void initializeAnts() {
		// load ant properties from external file
		try {
			Scanner sc = new Scanner(new File(ANT_FILE));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.matches("\\w.*")) { // not a comment
					String[] parts = line.split(","); // get the entry parts
					String antType = ANT_PKG + "." + parts[0].trim(); // prepend
																		// package
																		// name
					try {
						Class.forName(antType); // make sure the class is
												// implemented and we can load
												// it
						ANT_TYPES.add(antType);
						ANT_IMAGES.put(antType, ImageUtils.loadImage(parts[1].trim()));

						if (parts.length > 2) {
							LEAF_COLORS.put(antType, new Color(Integer.parseInt(parts[2].trim())));
						}
					} catch (ClassNotFoundException e) {
					} // if class isn't found, will continue (reading next line)
					ANT_IMAGES.put(antType, ImageUtils.loadImage(parts[1].trim()));

				}
			}
			sc.close();
		} catch (IOException e) { // for IOException, NumberFormatException,
									// ArrayIndex exception... basically if
									// anything goes wrong, don't crash
			System.out.println("Error loading insect gui properties: " + e);
		}

	}

	/**
	 * Initializes the Bee graphics for the game. Sets up positions for
	 * animations
	 */
	private void initializeBees() {
		Bee[] bees = hive.getBees();
		for (int i = 0; i < bees.length; i++) {
			allBeePositions.put(bees[i], new AnimPosition((int) FRAME_SIZE.getWidth() + 200,
					(int) (HIVE_POS.y + (100 * Math.random() - 50))));
		}
	}

	/**
	 * Initializes the Colony graphics for the game. Assumes that the
	 * AntColony.getPlaces() method returns places in order by row
	 */
	private void initializeColony() {
		Point pos = new Point(PLACE_POS); // start point of the places
		int width = BEE_IMAGE_WIDTH + 2 * PLACE_PADDING.width;
		int height = ANT_IMAGE_SIZE.height + 2 * PLACE_PADDING.height;
		int row = 0;
		pos.translate((width + PLACE_MARGIN) / 2, 0); // extra shift to make
														// room for queen
		for (Place place : colony.getPlaces()) {
			if (place.getExit() == colony.getQueenPlace()) // if this place
															// leads to the
															// queen (the end)
			{
				pos.setLocation(PLACE_POS.x, PLACE_POS.y + row * (height + PLACE_MARGIN)); // move
																							// down
																							// to
																							// beginning
																							// of
																							// next
																							// row
				pos.translate((width + PLACE_MARGIN) / 2, 0); // extra shift to
																// make room for
																// queen
				row++; // increase row number
			}

			Rectangle clickable = new Rectangle(pos.x, pos.y, width, height);
			if (place.getEntrance() != null) {
				colonyAreas.put(clickable, place);
				colonyRects.put(place, clickable);
			}

			pos.translate(width + PLACE_MARGIN, 0); // shift rectangle position
													// for next run

		}

		// make queen location
		pos.setLocation(-100, PLACE_POS.y + (row - 1) * (height + PLACE_MARGIN) / 2); // middle
																						// of
																						// the
																						// tunnels
																						// (about)
		Rectangle queenRect = new Rectangle(pos.x, pos.y, 0, 0); // no size,
																	// will not
																	// be drawn
		tunnelEnd = colony.getQueenPlace();
		colonyAreas.put(queenRect, tunnelEnd);
		colonyRects.put(tunnelEnd, queenRect);
	}

	/**
	 * Initializes the graphical Ant Selector area. Assumes that the Ants have
	 * already been initialized (and have established image resources)
	 */
	private void initializeAntSelector() {

		// In case we change it all
		antSelectorAreas = new HashMap<Rectangle, Ant>();

		Point pos = new Point(PANEL_POS); // starting point of the panel
		int width = ANT_IMAGE_SIZE.width + 2 * PANEL_PADDING.width;
		int height = ANT_IMAGE_SIZE.height + 2 * PANEL_PADDING.height;

		removerArea = new Rectangle(pos.x, pos.y, width, height);
		pos.translate(width + 2, 0);

		ANTSDISCOVERED = 0;
		for (String antType : ANT_TYPES) // go through the ants in the types; in
											// order
		{

			Rectangle clickable = new Rectangle(pos.x, pos.y, width, height); // where
																				// to
																				// put
																				// the
																				// selector
			Ant ant = system.Utils.buildAnt(antType); // the ant that gets deployed from that
											// selector
			if (ant != null && ant.level <= LEVEL || AntsVsSomeBees.DEBUG) {
				ANTSDISCOVERED++;
			}
			if (ant != null && !(ant instanceof QueenAnt && HASQUEEN)) {
				if (ant.level <= LEVEL || AntsVsSomeBees.DEBUG) { // Only our level of ants

					antSelectorAreas.put(clickable, ant); // register the
															// deployable ant so
															// we can select it

					pos.translate(width + 2, 0); // shift rectangle position for
													// next run
				}
			}
		}
	}

	
	
	////////////////////
	// Initialization //
	////////////////////
	

	
	public void initializeVars(AntColony colony){

		// Mise en place de la musique
		title.play();
		title.gain(-3);
		title.loop(true);

		// Sons
		add[0] = new Audio("add1.wav");
		add[1] = new Audio("add2.wav");
		add[2] = new Audio("add3.wav");
		add[3] = new Audio("add4.wav");
		add[0].megain = -15f;
		add[1].megain = -15f;
		add[2].megain = -15f;
		add[3].megain = -15f;

		food_earn.megain = -15f;

		// Get best game
		try {
			Scanner sc = new Scanner(new File("assets/stats.propert"));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				XP_RECORD = (int) Float.parseFloat(line);
			}
			sc.close();
		} catch (IOException e) {
			System.out.println("No stat file, creating it...");
			try {

				File file = new File("assets/stats.propert");

				if (file.createNewFile()) {
					System.out.println("File was created!");
				}

			} catch (IOException e1) {
				System.out.println(" /!\\ File not created !");
			}
		}

		// Get Bees
		for (int i = 0; i < 4; i++) {

			BEE_IMAGE[i] = ImageUtils.loadImage("assets/imgs/bees/" + i + "/bee_image.gif");
			BEE_IMAGE2[i] = ImageUtils.loadImage("assets/imgs/bees/" + i + "/bee_image2.gif");
			BEEBAD_IMAGE[i] = ImageUtils.loadImage("assets/imgs/bees/" + i + "/beebad_image.gif");
			BEEATTACK_IMAGE[i] = ImageUtils.loadImage("assets/imgs/bees/" + i + "/beeattack_image.gif");
			BEESTUN_IMAGE[i] = ImageUtils.loadImage("assets/imgs/bees/" + i + "/beestun_image.png");
			BEESLOW_IMAGE[i] = ImageUtils.loadImage("assets/imgs/bees/" + i + "/beeslow_image.gif");

		}

		// Get Bang
		for (int i = 0; i < BANG.length; i++) {

			BANG[i] = ImageUtils.loadImage("assets/imgs/bang/" + i + ".png");

		}

		for (int i = 0; i < EXPLOSION.length; i++) {
			EXPLOSION[i] = ImageUtils.loadImage("assets/imgs/explosion/" + i + ".png");
		}
		
		// game init stuff
		this.colony = colony;
		this.hive = new Hive();

		// game clock tracking
		frame = 0;
		counter = 0;
		turn = 0;

		// DEBUG
		if(AntsVsSomeBees.DEBUG){
			turn = 600; minTunnel = 0; maxTunnel = 4;
			colony.increaseFood(7328192);
		}

		clock = new Timer(1000 / FPS, this);
	}
	
	public void makeWindow(){
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
	
	
	
	
	
	
	
	////////////////////
	// Animations     //
	////////////////////
	
	// Specifies and starts an animation for a Bee (moving to a particular
	// place)
	private void startAnimation(Bee b) {

		AnimPosition anim = allBeePositions.get(b);
		if (anim.getFramesLeft() == 0 && b.damageDone == false) // if not already
															// animating
		{
			Rectangle rect = colonyRects.get(b.getPlace()); // where we want to
															// go to
			if (rect != null && !rect.contains(anim.getX(), anim.getY())) {
				anim.animateTo(rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height, FPS * TURN_SECONDS);
			}
		}
	}
	
	
	
	

	////////////////////
	// Event Handlers //
	////////////////////

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clock) {
			nextFrame();
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (STARTED == 0) {
			handleClick(event); // pass to synchronized method for thread
								// safety!
		}
		this.repaint(); // request a repaint
		if (!clock.isRunning()) {
			clock.start();
		} else if (STARTED != 0) {
			DOCLICK = 10;
		}

		if (mousePressed == false) {
			dragStart.setLocation(mouseX, mouseY);
			scrollPos.setLocation(mouseX, mouseY);
			mousePressed = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		dragStart.setLocation(-1, -1);
	}
	
	//
	/**
	 * Handles clicking on the screen (used for selecting and deploying ants).
	 * Synchronized method so we don't create conflicts in amount of food
	 * remaining.
	 *
	 * @param e
	 *            The mouse event representing the click
	 */
	private synchronized void handleClick(MouseEvent e) {
		Point pt = e.getPoint();

		/*if (FIN) {
			if (counter / FPS > Integer.parseInt(stats[0]) + 2) {
				restartGame();
			}
		}
		*/
		
		if (pt.getX() < 100 && pt.getY() > FRAME_SIZE.getHeight() - 100) {
			Sou_select.play();
			PAUSE = !PAUSE;
			return;
		}
		if (PAUSE) {
			return;
		}
		// check if deploying an ant
		for (Rectangle rect : colonyAreas.keySet()) {
			if (rect.contains(pt) && colonyAreas.get(rect).left < 8) {
				if (selectedAnt == null) {
					if (colonyAreas.get(rect).getAnt() != null
							&& !(colonyAreas.get(rect).getAnt() instanceof QueenAnt)) {
						Sou_delete.play();
						colony.increaseFood((colonyAreas.get(rect).getAnt().foodCost + 1) / 2);
					}
					colony.removeAnt(colonyAreas.get(rect));
					return; // stop searching
				} else {
					if (colonyAreas.get(rect).tunnel < minTunnel || colonyAreas.get(rect).tunnel > maxTunnel) {
						return;
					}
					if (colonyAreas.get(rect).getAnt() == null) {
						Sou_place.play();
						add[(int) (Math.random() * 4)].play();
					}
					Ant deployable = system.Utils.buildAnt(selectedAnt.getClass().getName()); // make
																					// a
																					// new
																					// ant
																					// of
																					// the
																					// appropriate
																					// type
					colony.deployAnt(colonyAreas.get(rect), deployable);

					if (deployable instanceof QueenAnt) {
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
	
	//Auto implemented but unused
	@Override
	public void mouseClicked(MouseEvent e) { }
	@Override
	public void mouseEntered(MouseEvent e) { }
	@Override
	public void mouseExited(MouseEvent e) { }


}

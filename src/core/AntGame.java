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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import ants.ThrowerAnt;
import Graphics.*;

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
	private Timer clock;

	// ant properties (laoded from external files, stored as member variables)
	private final ArrayList<String> ANT_TYPES;
	private final Map<String, Image> ANT_IMAGES;// = new HashMap<String,Image>();
	private final Map<String, Color> LEAF_COLORS;// = new HashMap<String, Color>();

	// other images (stored as member variables)
	private final Image TUNNEL_IMAGE = ImageUtils.loadImage("img/tunnel.gif");
	private final Image BEE_IMAGE = ImageUtils.loadImage("img/bee.gif");
	private final Image BEEBAD_IMAGE = ImageUtils.loadImage("img/bee_bad.gif");
	private final Image BEEATTACK_IMAGE = ImageUtils.loadImage("img/bee_attack.gif");
	private final Image REMOVER_IMAGE = ImageUtils.loadImage("img/remover.gif");
	private final Image BEEDEAD = ImageUtils.loadImage("img/bee_return.gif");
	// positioning constants
	public static final Dimension FRAME_SIZE = new Dimension(1024, 768);
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

	// areas that can be clicked
	private Map<Rectangle, Place> colonyAreas; // maps from a clickable area to a Place
	private Map<Place, Rectangle> colonyRects; // maps from a Place to its clickable rectangle (reverse lookup!)
	private Map<Rectangle, Ant> antSelectorAreas; // maps from a clickable area to an Ant that can be deployed
	private Rectangle removerArea; // click to remove an ant
	private Place tunnelEnd; // a Place representing the end of the tunnels (for drawing)
	private Ant selectedAnt; // which ant is currently selected

	// variables tracking animations
	private Map<Bee, AnimPosition> allBeePositions; // maps from Bee to an object storing animation status
	private ArrayList<AnimPosition> leaves; // leaves we're animating

	/**
	 * Creates a new game of Ants vs. Some-Bees, with the given colony and hive setup
	 *
	 * @param colony
	 *            The ant colony for the game
	 * @param hive
	 *            The hive (and attack plan) for the game
	 */
	public AntGame (AntColony colony, Hive hive) {
		// game init stuff
		this.colony = colony;
		this.hive = hive;

		// game clock tracking
		frame = 0;
		turn = 0;
		clock = new Timer(1000 / FPS, this);

		// member ant property storage variables
		ANT_TYPES = new ArrayList<String>();
		ANT_IMAGES = new HashMap<String, Image>();
		LEAF_COLORS = new HashMap<String, Color>();
		initializeAnts();

		// tracking bee animations
		allBeePositions = new HashMap<Bee, AnimPosition>();
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
		JFrame frame = new JFrame("Ants vs. Some-Bees");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);

	}

	@Override
	public void paintComponent (Graphics g) {
		super.paintComponent(g); // take care of anything else
		Graphics2D g2d = (Graphics2D) g;
		g2d.clearRect(0, 0, FRAME_SIZE.width, FRAME_SIZE.height); // clear to background color

		g2d.drawImage(ImageUtils.loadImage("assets/back.png"), 0, 0, null); // draw a bee at that position!
		g2d.drawImage(ImageUtils.loadImage("assets/test.png"), 0, 0, null); // draw a bee at that position!
		
		drawAntSelector(g2d);

		// text displays
		String antString = "none";
		if (selectedAnt != null) {
			antString = selectedAnt.getClass().getName();
			antString = antString.substring(0, antString.length() - 3); // remove the word "ant"
		}
		g2d.drawString("Ant selected: " + antString, 20, 20); // hard-coded positions, make variable?
		g2d.drawString("Life: "+ colony.life +", Food: " + colony.getFood() + ", Turn: " + turn, 20, 140);

		drawColony(g2d);
		drawBees(g2d);
		drawLeaves(g2d);
		


		if (!clock.isRunning()) { // start text
			g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
			g2d.setColor(Color.RED);
			g2d.drawString("CLICK TO START", 350, 550);
		}
	}

	/**
	 * Runs the actual game, processing what occurs on every frame of the game (including individual turns).
	 * This handles both some game logic (turn order) and animation control
	 */
	private void nextFrame () {
		
		
		//Calculate nb of food produced each turn
		int nbFrameFood = 0;
		for (Ant ant : colony.getAllAnts()) {
			nbFrameFood += ant.foodMakePerTurn;
		}
		if(nbFrameFood>0){
			nbFrameFood = (FPS*TURN_SECONDS)/nbFrameFood;
			//Real time food increase
			if(nbFrameFood>FPS*TURN_SECONDS){
				colony.increaseFood(1);
			}else{
				if (frame%nbFrameFood == 0){
					colony.increaseFood(1);
				}
			}

		}
		
		if (frame == 0) // at the start of a turn
		{
			System.out.println("TURN: " + turn);

			// ants take action!
			for (Ant ant : colony.getAllAnts()) {
				if (ant instanceof ThrowerAnt) // if we're a thrower, might need to make a leaf!
				{
					Bee target = ((ThrowerAnt) ant).getTarget(); // who we'll throw at (really which square, but works out the same)
					if (target != null) {
						createLeaf(ant, target);
					}
				}
				ant.action(colony); // take the action (actually completes the throw now)
			}

			// bees take action!
			for (Bee bee : colony.getAllBees()) {
				bee.action(colony);
				startAnimation(bee); // start up animation for the bee if needed
			}

			// new invaders attack!
			Bee[] invaders = hive.invade(colony, turn); // this moves the bees into the colony
			for (Bee bee : invaders) {
				startAnimation(bee);
			}

			// if want to do this to ants as well, will need to start storing dead ones with AnimPositions
		}
		
		
		if (frame == (int) (LEAF_SPEED * FPS)) // after leaves animate
		{
			for (Map.Entry<Bee, AnimPosition> entry : allBeePositions.entrySet()) // remove dead bees
			{
				if (entry.getKey().getArmor() <= 0) { // if dead bee
					AnimPosition pos = entry.getValue();
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
		for (Ant ant : colony.getAllAnts()) // apply time
		{
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

		this.repaint(); // request an update per frame!

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
			
			
			if (colony.queenHasBees()) { // more than 1 life
				for (Bee bee: colony.queenPlace.getBees())
				{
					if(bee.place.toString()=="AntQueen" && bee.armor>0){
						bee.armor=-1;
						colony.life += -1;
					}
				}
			}
			
			if(colony.life<0){
				JOptionPane.showMessageDialog(this, "The ant queen has perished! Please try again.", "Bzzzzz!", JOptionPane.PLAIN_MESSAGE);
				System.exit(0); // quit
			}
			if (hive.getBees().length + colony.getAllBees().size() == 0) { // no more bees--we won!
				JOptionPane.showMessageDialog(this, "All bees are vanquished. You win!", "Yaaaay!", JOptionPane.PLAIN_MESSAGE);
				System.exit(0); // quit
			}
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

		// check if deploying an ant
		for (Rectangle rect : colonyAreas.keySet()) {
			if (rect.contains(pt)) {
				if (selectedAnt == null) {
					colony.removeAnt(colonyAreas.get(rect));
					return; // stop searching
				}
				else {
					Ant deployable = buildAnt(selectedAnt.getClass().getName()); // make a new ant of the appropriate type
					colony.deployAnt(colonyAreas.get(rect), deployable);
					return; // stop searching
				}
			}
		}

		// check if selecting an ant
		for (Rectangle rect : antSelectorAreas.keySet()) {
			if (rect.contains(pt)) {
				selectedAnt = antSelectorAreas.get(rect);
				return; // stop searching
			}
		}

		// check if remover
		if (removerArea.contains(pt)) {
			selectedAnt = null; // mark as such
			return; // stop searching
		}
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

	// Creates a new leaf (animated) from the Ant source to the Bee target.
	// Note that really only cares about the target's Place (Ant can target other Bees in same Place)
	private void createLeaf (Ant source, Bee target) {
		Rectangle antRect = colonyRects.get(source.getPlace());
		Rectangle beeRect = colonyRects.get(target.getPlace());
		int startX = antRect.x + LEAF_START_OFFSET.width;
		int startY = antRect.y + LEAF_START_OFFSET.height;
		int endX = beeRect.x + LEAF_END_OFFSET.height;
		int endY = beeRect.y + LEAF_END_OFFSET.height;

		AnimPosition leaf = new AnimPosition(startX, startY);
		leaf.animateTo(endX, endY, (int) (LEAF_SPEED * FPS));
		leaf.color = LEAF_COLORS.get(source.getClass().getName());

		leaves.add(leaf);
	}

	// Draws all the places for the Colony on the given Graphics2D
	// Includes drawing the Ants deployed to the Colony (but not the Bees moving through it)
	private void drawColony (Graphics2D g2d) {
		for (Map.Entry<Rectangle, Place> entry : colonyAreas.entrySet()) {
			Rectangle rect = entry.getKey(); // rectangle area for this place
			Place place = entry.getValue(); // place to draw

			g2d.setColor(Color.BLACK);
			//g2d.draw(rect); // border box (where to click)

			if (place != tunnelEnd) {
				g2d.drawImage(TUNNEL_IMAGE, rect.x, rect.y, null); // decorative image
			}

			Ant ant = place.getAnt();
			if (ant != null) { // draw the ant if we have one
				Image img = ANT_IMAGES.get(ant.getClass().getName());
				g2d.drawImage(img, rect.x + PLACE_PADDING.width, rect.y + PLACE_PADDING.height, null);
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
			if(bee.lastAttacked<FPS/4){ //Change l'image pour un quart de seconde
				image = BEEBAD_IMAGE;
			}else{
				image = BEE_IMAGE;
			}
			if(bee.lastAttack<FPS/4){ //Change l'image pour un quart de seconde
				image = BEEATTACK_IMAGE;
			}
			
			if(bee.armor<=0){ //Change l'image
				image = BEEDEAD;
			}
			
			g2d.drawImage(image,
					(int) pos.x + (int)(Math.cos((pos.x+pos.y)/(20+bee.randomDecalage))*10),
					(int) pos.y + (int)(Math.cos((pos.x+pos.y)/(20+bee.randomDecalage))*10),
					null); // draw a bee at that position!
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

	// Draws the ant selector area
	private void drawAntSelector (Graphics2D g2d) {
		// go through each selector area
		for (Map.Entry<Rectangle, Ant> entry : antSelectorAreas.entrySet()) {
			Rectangle rect = entry.getKey(); // selected area
			Ant ant = entry.getValue(); // ant to select

			// box status
			g2d.setColor(Color.WHITE);
			if (ant.getFoodCost() > colony.getFood()) {
				g2d.setColor(Color.GRAY);
			}
			else if (ant == selectedAnt) {
				g2d.setColor(Color.BLUE);
			}
			g2d.fill(rect);

			// box outline
			g2d.setColor(Color.BLACK);
			g2d.draw(rect);

			// ant image
			Image img = ANT_IMAGES.get(ant.getClass().getName());
			g2d.drawImage(img, rect.x + PANEL_PADDING.width, rect.y + PANEL_PADDING.height, null);

			// food cost
			g2d.drawString("" + ant.getFoodCost(), rect.x + (rect.width / 2), rect.y + ANT_IMAGE_SIZE.height + 4 + PANEL_PADDING.height);
		}

		// for removing an ant
		if (selectedAnt == null) {
			g2d.setColor(Color.BLUE);
			g2d.fill(removerArea);
		}
		g2d.setColor(Color.BLACK);
		g2d.draw(removerArea);
		g2d.drawImage(REMOVER_IMAGE, removerArea.x + PANEL_PADDING.width, removerArea.y + PANEL_PADDING.height, null);
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
			colonyAreas.put(clickable, place);
			colonyRects.put(place, clickable);

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
		Point pos = new Point(PANEL_POS); // starting point of the panel
		int width = ANT_IMAGE_SIZE.width + 2 * PANEL_PADDING.width;
		int height = ANT_IMAGE_SIZE.height + 2 * PANEL_PADDING.height;

		removerArea = new Rectangle(pos.x, pos.y, width, height);
		pos.translate(width + 2, 0);

		for (String antType : ANT_TYPES) // go through the ants in the types; in order
		{
			Rectangle clickable = new Rectangle(pos.x, pos.y, width, height); // where to put the selector
			Ant ant = buildAnt(antType); // the ant that gets deployed from that selector
			antSelectorAreas.put(clickable, ant); // register the deployable ant so we can select it

			pos.translate(width + 2, 0); // shift rectangle position for next run
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
		handleClick(event); // pass to synchronized method for thread safety!
		this.repaint(); // request a repaint
		if (!clock.isRunning()) {
			clock.start();
		}
	}

	@Override
	public void mouseClicked (MouseEvent e) {
	}

	@Override
	public void mouseReleased (MouseEvent e) {
	}

	@Override
	public void mouseEntered (MouseEvent e) {
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

}

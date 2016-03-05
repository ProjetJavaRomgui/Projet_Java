package system;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.lang.reflect.Constructor;

import core.Ant;

public class Utils {

	

	// Return smooth animation position a, position b, temps actuel, duree
	public static float smooth(int a, int t, int d) {
		if (t < d / 2) {
			return (float) (a * Math.pow(((float) t / d) * 2, 2) / 2);
		} else {
			return (float) (a * (2 - Math.pow(((float) t / d) * 2 - 2, 2)) / 2);
		}
	}
	
	/**
	 * Add XP to the game mana
	 * @param howmany (XP you want to add)
	 */
	public static void addXP(int howmany) {
		if (!core.AntGame.FIN) {
			core.AntGame.XP += Math.max(0, (int)(howmany*((float)core.AntGame.turn/50+1)));
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
	public static Shape leafShape(int x, int y, double angle, int length) {
		// calculate angles and distances to move
		double[] a = { angle - Math.PI, angle - 3 * Math.PI / 4, angle - Math.PI / 2, angle - Math.PI / 4, angle,
				angle + Math.PI / 4, angle + Math.PI / 2, angle + 3 * Math.PI / 4 };
		double[] d = { length / 3, length / 2.5, length / 2, length / 1.5, length, length / 1.5, length / 2,
				length / 2.5 };

		// build a shape that is vaguely leaf-like
		Path2D.Double curve = new Path2D.Double();
		curve.moveTo(x + Math.cos(a[0]) * d[0], y + Math.sin(a[0]) * d[0]); // mathematical
																			// magic
																			// (just
																			// moving
																			// from
																			// start
																			// by
																			// given
																			// angle
																			// and
																			// distance,
																			// in
																			// order)
		curve.quadTo(x + Math.cos(a[1]) * d[1], y + Math.sin(a[1]) * d[1], x + Math.cos(a[2]) * d[2],
				y + Math.sin(a[2]) * d[2]);
		curve.quadTo(x + Math.cos(a[3]) * d[3], y + Math.sin(a[3]) * d[3], x + Math.cos(a[4]) * d[4],
				y + Math.sin(a[4]) * d[4]);
		curve.quadTo(x + Math.cos(a[5]) * d[5], y + Math.sin(a[5]) * d[5], x + Math.cos(a[6]) * d[6],
				y + Math.sin(a[6]) * d[6]);
		curve.quadTo(x + Math.cos(a[7]) * d[7], y + Math.sin(a[7]) * d[7], x + Math.cos(a[0]) * d[0],
				y + Math.sin(a[0]) * d[0]);

		return curve;
	}
	
	/**
	 * Returns a new instance of an Ant object of the given subclass
	 *
	 * @param antType
	 *            The name of an Ant subclass (e.g., "HarvesterAnt")
	 * @return An instance of that subclass, created using the default
	 *         constructor
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Ant buildAnt(String antType) {
		Ant ant = null;
		try {
			Class antClass = Class.forName(antType); // what class is this type
			Constructor constructor = antClass.getConstructor(); // find the
																	// default
																	// constructor
																	// (using
																	// reflection)
			ant = (Ant) constructor.newInstance(); // call the default
													// constructor to make a new
													// ant
		} catch (Exception e) {
		}

		return ant; // return the new ant
	}
	
	/**
	 * Generate multi line text (\n separator)
	 * @param str (your text)
	 * @param x (position)
	 * @param y
	 * @param g2d (graphics2D to modify)
	 */
	public static void drawLongText(String str, int x, int y, Graphics2D g2d) {

		int espace = 0;

		for (String s : str.split("\n")) {

			espace += g2d.getFont().getSize();

			g2d.setColor(Color.BLACK);
			g2d.drawString(s, x - 2, y - 2 + espace);
			g2d.setColor(Color.WHITE);
			g2d.drawString(s, x, y + espace);

		}

	}
	
}

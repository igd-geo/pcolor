
package de.fhg.igd.pcolor.examples.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.List;

import javax.swing.JPanel;

/**
 * Displays a series of colors in a color wheel
 * @author Martin Steiger
 */
public final class JColorWheel extends JPanel
{
	private static final long serialVersionUID = 6680246951351244766L;

	private List<Color> colors;
	private int inner = 25;
	
	/**
	 * @param colors to colors to show in the wheel
	 */
	public JColorWheel(List<Color> colors) 
	{
		this.colors = colors;
		setBackground(Color.WHITE);
	}
	
	/**
	 * @return the inner radius of the wheel
	 */
	public int getInner() 
	{
		return inner;
	}

	/**
	 * @param inner the inner radius of the wheel
	 */
	public void setInner(int inner)	
	{
		this.inner = inner;
	}

	@Override
	protected void paintComponent(Graphics g1) {
	    super.paintComponent(g1);
	    int dia = Math.min(getWidth(), getHeight());

	    Graphics2D g = (Graphics2D) g1;
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    int dx = (getWidth() - dia) / 2;
	    int dy = (getHeight() - dia) / 2;
	    g.translate(dx, dy);
	    
	    if (inner > 0) 
	    {
		    Area a = new Area(new Rectangle(0, 0, getWidth(), getHeight()));
		    Ellipse2D clip = new Ellipse2D.Double(dia / 2 - inner, dia / 2 - inner, inner * 2, inner * 2);
		    a.subtract(new Area(clip));
		    g.setClip(a);
	    }

	    int size = colors.size();
	    for (int i = 0; i < size; i++) 
	    {
	        g.setColor(colors.get(i));
	        int st = i * 360 / size;
	        int end = 1 * 360 / size + 2;
	        // we use the integer version of arc to avoid aliasing effects
	        g.fillArc(0, 0, dia, dia, st, end);
	    }
	}

	public void setColors(List<Color> colors)
	{
		this.colors = colors;
		repaint();
	}
}
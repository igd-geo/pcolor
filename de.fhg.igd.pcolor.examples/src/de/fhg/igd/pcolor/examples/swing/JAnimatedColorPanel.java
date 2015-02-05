
package de.fhg.igd.pcolor.examples.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * An animated panel showing some color information charts
 * @author Martin Steiger
 */
public class JAnimatedColorPanel extends JPanel
{
	private static final long serialVersionUID = -2728145046907929077L;
	private final Timer timer;
	private List<Color> colors;

	private int index;

	/**
	 * Uses a default animation delay of 20ms
	 * @param colors the list of colors to iterate through
	 */
	public JAnimatedColorPanel(List<Color> colors)
	{
		this(colors, 20);
	}

	/**
	 * @param delay the animation delay in ms
	 * @param cols the list of colors to iterate through
	 */
	public JAnimatedColorPanel(final List<Color> cols, int delay)
	{
		this.colors = cols;
		timer = new Timer(delay, new ActionListener()
		{
			private boolean visibleBefore = false;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!isDisplayable() && visibleBefore)
					timer.stop(); 
				
				if (isDisplayable())
					visibleBefore = true;
				
				setBackground(colors.get(index));
				index++;
				if (index >= colors.size())
					index = 0;
			}
		});
		timer.start();
	}
	
	@Override
	protected void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;

		int barWidth = 200;
		int barDist = 35;
		int barHeight = 25;
		Color color = getBackground();
		
		drawBars(g, color, 10, 40, barWidth, barDist, barHeight);
		
		g.drawString(color.toString(), 10, 20);
		g.drawString("Colors: " + colors.size(), 10, 160);

		int chartLeft = 300;
		int chartTop = 10;
		int chartWidth = 300;
		int chartHeight = 200;
		
		drawChart(g, chartLeft, chartTop, chartWidth, chartHeight);
	}

	private void drawChart(Graphics2D g, int chartLeft, int chartTop, int chartWidth, int chartHeight)
	{
		g.translate(chartLeft, chartTop);

		Object oldAA = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.BLACK);
		GeneralPath[] paths = new GeneralPath[3];
		Color firstColor = colors.get(0);
		int[] colorComps = new int[] { firstColor.getRed(), firstColor.getGreen(), firstColor.getBlue() };
		for (int i = 0; i < 3; i++)
		{
			float y = chartHeight - colorComps[i] * chartHeight / 255f;

			paths[i] = new GeneralPath();
			paths[i].moveTo(0, y);
		}

		for (int idx = 1; idx < colors.size(); idx++)
		{
			Color c2 = colors.get(idx);
			int[] vals = new int[] { c2.getRed(), c2.getGreen(), c2.getBlue() };
			for (int i = 0; i < 3; i++)
			{
				float y = chartHeight - vals[i] * chartHeight / 255f;
				float x = idx * chartWidth / colors.size();
				paths[i].lineTo(x, y);
			}
		}
		
		g.setStroke(new BasicStroke(0.8f));
		g.draw(paths[0]);
		g.draw(paths[1]);
		g.draw(paths[2]);
		g.setStroke(new BasicStroke());

		// axis arrow top
		g.drawLine(-3, +3, 0, 0);
		g.drawLine(+3, +3, 0, 0);
		
		// chart axes
		g.drawLine(0, 0, 0, chartHeight);
		g.drawLine(0, chartHeight, chartWidth, chartHeight);
		
		// axis arrow right
		g.drawLine(chartWidth - 3, chartHeight - 3, chartWidth, chartHeight);
		g.drawLine(chartWidth - 3, chartHeight + 3, chartWidth, chartHeight);
		
		// moving circles on the chart
		Color idxColor = colors.get(index);
		colorComps = new int[] { idxColor.getRed(), idxColor.getGreen(), idxColor.getBlue() };
		Color[] baseColors = new Color[] { Color.RED, Color.GREEN, Color.BLUE };
		int diam = 8;
		for (int i = 0; i < 3; i++)
		{
			int x = index * chartWidth / colors.size();
			int y = chartHeight - colorComps[i] * chartHeight / 255;
			g.setColor(baseColors[i]);
			g.fillOval(x - diam / 2, y - diam / 2, diam, diam);
			g.setColor(Color.BLACK);
			g.drawOval(x - diam / 2, y - diam / 2, diam, diam);
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
		
		g.translate(-chartLeft, -chartTop);
	}

	private void drawBars(Graphics2D g, Color color, int x, int y, int maxWidth, int barDist, int barHeight)
	{
		int[] vals = new int[] { color.getRed(), color.getGreen(), color.getBlue() };
		for (int i = 0; i < 3; i++)
		{
			int w = vals[i] * maxWidth / 255;
			g.setColor(Color.DARK_GRAY);
			g.fillRect(x, y + i * barDist, w, barHeight);
			g.setColor(Color.BLACK);
			g.drawRect(x, y + i * barDist, maxWidth, barHeight);
		}

	}

	public void setColors(List<Color> colors2)
	{
		colors = colors2;
		if (index >= colors.size())
			index = colors.size() - 1;
	}
}


package de.fhg.igd.pcolor.examples.swing;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Shows the color samples in two swing windows
 * @author Martin Steiger
 */
public class SwingColorViewer
{
    /**
	 * @param args ignored
	 */
	public static void main(String[] args)
	{
		final JFrame frameAnim = new JFrame();
		frameAnim.setLayout(new BorderLayout());

		List<Color> initColors = Collections.singletonList(Color.GRAY);
		final JAnimatedColorPanel comp = new JAnimatedColorPanel(initColors);
        final JColorWheel bigWheel = new JColorWheel(initColors);

		JPanel controls = new JPanel(new FlowLayout());
    	final JSpinner lumSpinner = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
    	final JSpinner chromaSpinner = new JSpinner(new SpinnerNumberModel(60, 0, 100, 1));
    	controls.add(new JLabel("Luminance"));
		controls.add(lumSpinner);
		controls.add(new JLabel("Chroma"));
		controls.add(chromaSpinner);
		JButton dumpButton = new JButton("Dump Java constants");
		dumpButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				float lum = ((Number)lumSpinner.getValue()).floatValue();
				float chroma = ((Number)chromaSpinner.getValue()).floatValue();
				List<Color> colors = MyColorFinder.findCAMColors(lum, chroma, 1f);

				int cols = 4;
				int idx = 0;
				for (Color color : colors)
				{
					System.out.print(String.format("new Color(0x%06X)", color.getRGB() & 0xFFFFFF));
					idx++;
					if (idx < colors.size())
						System.out.print(",");
					if (idx % cols == 0)
						System.out.println(); else
						System.out.print(" ");
				}
			}
		});
		controls.add(dumpButton);
		ChangeListener changeListener = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				float lum = ((Number)lumSpinner.getValue()).floatValue();
				float chroma = ((Number)chromaSpinner.getValue()).floatValue();
				List<Color> colors = MyColorFinder.findCAMColors(lum, chroma, 1f);
				comp.setColors(colors);
				bigWheel.setColors(colors);
			}
		};
		lumSpinner.addChangeListener(changeListener);
		chromaSpinner.addChangeListener(changeListener);
		
		frameAnim.add(controls, BorderLayout.NORTH);
		
        final JPanel frameWheel = new JPanel(new GridLayout(1, 0, 5, 5));
        frameWheel.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
        frameWheel.setBackground(Color.WHITE);
        frameWheel.setPreferredSize(new Dimension(1, 150));
        
        float[] lums = { 40, 45, 50, 55, 60 };
        float[] chroms = { 50, 55, 60, 55, 50 };
		for (int i = 0; i < 5; i++)
		{
			JColorWheel wheel = new JColorWheel(MyColorFinder.findCAMColors(lums[i], chroms[i], 1f));
			wheel.setToolTipText(String.format("Lum %.0f - Chroma %.0f", lums[i], chroms[i]));
			frameWheel.add(wheel);
		}
		frameAnim.add(frameWheel, BorderLayout.SOUTH);

        final JPanel centerPanel = new JPanel(new BorderLayout(5, 0));
        centerPanel.add(comp, BorderLayout.CENTER);
        bigWheel.setPreferredSize(new Dimension(200, 200));
		centerPanel.add(bigWheel, BorderLayout.EAST);
		frameAnim.add(centerPanel, BorderLayout.CENTER);
		
		changeListener.stateChanged(null);

		frameAnim.setTitle("Color Viewer");
		frameAnim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameAnim.setMinimumSize(new Dimension(850, 500));
		frameAnim.setSize(850, 500);
		frameAnim.setLocationRelativeTo(null);
		frameAnim.setVisible(true);
    }
}

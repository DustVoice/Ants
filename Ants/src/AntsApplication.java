import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point for displaying Ants as an Application.
 * Use this for both Java web start and executable jar.
 * 
 * @author David Holland
 */
public class AntsApplication
{
	
	public static void main(String[] args)
	{
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				
				JFrame frame = new JFrame("Ants"); //Create the main containing Frame
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				final Ants ants = new Ants(); // Creates the main JPanel, containing cells with ants, etc.
				frame.add(ants); // Add the JPanel to the JFrame
				
				AdvancedControlPanel advancedPanel = new AdvancedControlPanel(
						ants);
				
				final AntsControlPanel antsPanel = new AntsControlPanel(ants,
						advancedPanel);
				frame.add(antsPanel.getPanel(), BorderLayout.EAST);
				
				frame.add(advancedPanel.getPanel(), BorderLayout.SOUTH);
				
				frame.setSize(600, 600);
				
				frame.setIconImage(new ImageIcon(getClass().getResource(
						"icon.png")).getImage());
				
				frame.setVisible(true);
				
			}
		});
		
	}
}

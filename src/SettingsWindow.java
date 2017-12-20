import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class SettingsWindow extends JFrame {

	private JPanel contentPane;
	private JTextField txtNumParticles;
	private JTextField txtParticleSize;
	private static SettingsWindow frame;
	private JTextField txtColorScrollDelay;
	private JLabel lblError;
	private JCheckBox cbShowCursor;
	private JCheckBox cbParticleAttraction;
	private JCheckBox cbConnectParticles;
	private JCheckBox cbLoopIfOffScreen;
	private JCheckBox cbTripMode;
	private JCheckBox cbTrails;

	public static void showSettings() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new SettingsWindow();
					frame.setAlwaysOnTop(true);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	public SettingsWindow() {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		setTitle("Jake Garza's Particle Program");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 381);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNumberOfParticles = new JLabel("Number of Particles: ");
		lblNumberOfParticles.setBounds(10, 11, 111, 14);
		contentPane.add(lblNumberOfParticles);
		
		txtNumParticles = new JTextField();
		txtNumParticles.setText(Main.particles.size() + "");
		txtNumParticles.setBounds(125, 8, 105, 20);
		contentPane.add(txtNumParticles);
		txtNumParticles.setColumns(10);
		
		JLabel lblParticleSize = new JLabel("Particle size: ");
		lblParticleSize.setBounds(10, 36, 111, 14);
		contentPane.add(lblParticleSize);
		
		txtParticleSize = new JTextField();
		txtParticleSize.setText(Particle.getSize() + "");
		txtParticleSize.setColumns(10);
		txtParticleSize.setBounds(125, 33, 105, 20);
		contentPane.add(txtParticleSize);
		
		JButton btnStart = new JButton("Set");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int particleNumber = Integer.parseInt(txtNumParticles.getText());
					int particleSize = Integer.parseInt(txtParticleSize.getText());
					int colorScrollDelay = Integer.parseInt(txtColorScrollDelay.getText());
					boolean showCursor = cbShowCursor.isSelected();
					boolean particleAttraction = cbParticleAttraction.isSelected();
					boolean connectParticles = cbConnectParticles.isSelected();
					boolean loopIfOffScreen = cbLoopIfOffScreen.isSelected();
					boolean tripMode = cbTripMode.isSelected();
					boolean particleTrails = cbTrails.isSelected();
					
					//Set number of particles
					if (Main.particles.size() != particleNumber)
						Main.setParticles(particleNumber);
					
					//Set size of all particles
					Particle.setSize(particleSize);
					
					//Set color scroll delay
					//Main.colorScrollDelay = colorScrollDelay;
					ParticleSettings.setColorScrollDelay(colorScrollDelay);
					
					//Set cursor to showing or not
					//Main.showCursor = showCursor;
					ParticleSettings.setCursorShowing(showCursor);
					
					//Set particle attraction or not
					//Main.attraction = particleAttraction;
					ParticleSettings.setAttraction(particleAttraction);
					
					//Draw line between particles or not
					//Main.connectParticles = connectParticles;
					ParticleSettings.setConnectParticles(connectParticles);
					
					//Main.loopIfOffScreen = loopIfOffScreen;
					ParticleSettings.setLoopIfOffScreen(loopIfOffScreen);
					
					//Main.tripMode = tripMode;
					ParticleSettings.setTripMode(tripMode);
					
					//Main.particleTrails = particleTrails;
					ParticleSettings.setParticleTrails(particleTrails);
					
					//Close this window without closing program
					frame.dispose();
					
				} catch (Exception e) {
					lblError.setText(e.getMessage());
					//JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		btnStart.setBounds(6, 284, 89, 23);
		contentPane.add(btnStart);
		
		JLabel lblColorScrollDelay = new JLabel("Color Scroll Delay: ");
		lblColorScrollDelay.setBounds(10, 61, 111, 14);
		contentPane.add(lblColorScrollDelay);
		
		txtColorScrollDelay = new JTextField();
		txtColorScrollDelay.setText("15");
		txtColorScrollDelay.setColumns(10);
		txtColorScrollDelay.setBounds(125, 58, 105, 20);
		contentPane.add(txtColorScrollDelay);
		
		cbShowCursor = new JCheckBox("Show Cursor");
		cbShowCursor.setBounds(6, 82, 115, 23);
		contentPane.add(cbShowCursor);
		
		txtNumParticles.setText(Main.particles.size() + "");
		txtParticleSize.setText(Particle.getSize() + "");
		//txtColorScrollDelay.setText(Main.colorScrollDelay + "");
		cbShowCursor.setSelected(ParticleSettings.isCursorShowing());
		
		lblError = new JLabel("");
		lblError.setBounds(10, 318, 414, 14);
		contentPane.add(lblError);
		
		cbParticleAttraction = new JCheckBox("Particle Attraction");
		cbParticleAttraction.setSelected(ParticleSettings.isAttraction());
		cbParticleAttraction.setBounds(6, 108, 115, 23);
		contentPane.add(cbParticleAttraction);
		
		cbConnectParticles = new JCheckBox("Connect Particles");
		cbConnectParticles.setSelected(ParticleSettings.connectingParticles());
		cbConnectParticles.setBounds(6, 134, 115, 23);
		contentPane.add(cbConnectParticles);
		
		cbLoopIfOffScreen = new JCheckBox("Loop if off screen");
		cbLoopIfOffScreen.setSelected(ParticleSettings.loopIfOffScreen());
		cbLoopIfOffScreen.setBounds(6, 158, 115, 23);
		contentPane.add(cbLoopIfOffScreen);
		
		cbTripMode = new JCheckBox("Trip mode");
		cbTripMode.setSelected(ParticleSettings.isTripMode());
		cbTripMode.setBounds(6, 184, 115, 23);
		contentPane.add(cbTripMode);
		
		cbTrails = new JCheckBox("Particle Trails");
		cbTrails.setSelected(ParticleSettings.particleTrails());
		cbTrails.setBounds(6, 210, 115, 23);
		contentPane.add(cbTrails);
	}
}

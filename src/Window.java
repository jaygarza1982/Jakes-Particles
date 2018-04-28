import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class Window extends JFrame {

	private JPanel contentPane;
	private JTextField txtNumParticles;
	private JTextField txtParticleSize;
	private JTextField txtTargetFPS;
	private JCheckBox cbUseScreenBG;
	private JCheckBox cbFullScreen;
	private JButton btnStart;
	private static Window frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
                    System.out.println(System.getProperty("java.version"));
					frame = new Window();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * Create the frame.
	 */
	public Window() {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		setTitle("Jake Garza's Particle Program");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 215);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNumberOfParticles = new JLabel("Number of Particles: ");
		lblNumberOfParticles.setBounds(10, 11, 111, 14);
		contentPane.add(lblNumberOfParticles);
		
		txtNumParticles = new JTextField();
		txtNumParticles.setText("10000");
		txtNumParticles.setBounds(125, 8, 105, 20);
		contentPane.add(txtNumParticles);
		txtNumParticles.setColumns(10);
		
		JLabel lblParticleSize = new JLabel("Particle size: ");
		lblParticleSize.setBounds(10, 36, 111, 14);
		contentPane.add(lblParticleSize);
		
		txtParticleSize = new JTextField();
		txtParticleSize.setText("3");
		txtParticleSize.setColumns(10);
		txtParticleSize.setBounds(125, 33, 105, 20);
		contentPane.add(txtParticleSize);
		
		JLabel lblTargetFps = new JLabel("Target FPS: ");
		lblTargetFps.setBounds(10, 61, 111, 14);
		contentPane.add(lblTargetFps);
		
		cbUseScreenBG = new JCheckBox("Use screen as background");
		cbUseScreenBG.setBounds(6, 82, 224, 23);
		contentPane.add(cbUseScreenBG);

		cbFullScreen = new JCheckBox("Fullscreen window");
		cbFullScreen.setSelected(true);
		cbFullScreen.setBounds(6, 107, 224, 23);
		contentPane.add(cbFullScreen);
		
		txtTargetFPS = new JTextField();
		txtTargetFPS.setText("60");
		txtTargetFPS.setColumns(10);
		txtTargetFPS.setBounds(125, 58, 105, 20);
		contentPane.add(txtTargetFPS);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int particleNumber = Integer.parseInt(txtNumParticles.getText());
					int particleSize = Integer.parseInt(txtParticleSize.getText());
					long targetFPS = Long.parseLong(txtTargetFPS.getText());
					boolean screenBackground = cbUseScreenBG.isSelected();
					Main.fullScreen = cbFullScreen.isSelected();
					
					frame.dispose(); //Close this window without exiting program
					
					Main.startParticles(particleNumber, particleSize, targetFPS, screenBackground);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		btnStart.setBounds(10, 142, 135, 23);
		contentPane.add(btnStart);
		
		JButton btnCheckForUpdates = new JButton("Check for updates");
		btnCheckForUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Updater.isUpdate()) {
						//File chooser object with jar files as default file to save to
						JFileChooser jfc = new JFileChooser();
						jfc.setDialogTitle("Select a save loction for the updated version.");
						jfc.setFileFilter(new FileNameExtensionFilter("JAR Files", "jar"));
						jfc.showOpenDialog(null);
						
						File toSave = jfc.getSelectedFile();
						
						//If the name of the file dosen't have .jar at the end, add it to the file name
						if (!toSave.getName().toLowerCase().endsWith(".jar"))
							toSave = new File(toSave.getAbsolutePath() + ".jar");
						
						Updater.downloadFile("http://www.motths.net/Particles.jar", toSave.getAbsolutePath());
					}
					else {
						JOptionPane.showMessageDialog(null, "You are running the latest version.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		btnCheckForUpdates.setBounds(289, 142, 135, 23);
		contentPane.add(btnCheckForUpdates);
		
		JButton btnDemoMode = new JButton("Demo mode");
		btnDemoMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Turn demo mode on
				//Main.demoMode = true;
				ParticleSettings.setDemoMode(true);
				
				//Click the start button
				btnStart.doClick();
			}
		});
		btnDemoMode.setBounds(289, 7, 135, 23);
		contentPane.add(btnDemoMode);
	}
}

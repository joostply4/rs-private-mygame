import java.applet.Applet;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class Loader extends Applet implements ActionListener {
	public static Properties properties = new Properties();
	public JFrame appletFrame;
	public static JPanel appletPanel = new JPanel();
	public static final int SUB_BUILD = 7;
	public static final int PORT_ID = 43594;
	public static final String SERVER_IP = "127.0.0.1";
	public static final boolean DISABLE_USELESS_PACKETS = true;
	public static boolean webclient = true;
	public static String webpage = "matrixrsps.com";

	private LayoutManager Layout; 
	private JButton Button1, Button2, Button3, Button4, Button5;

	public static String getWebClientCachePath() {
		return getWebClientCachePath(SUB_BUILD);
	}
	
	public static boolean newInterface(int id) {
		if(id > 1144) 
			return true;
		switch(id) {
		case 320:
		case 751:
			return true;
		default:
			return false;
		}
	}
	
	public static String getWebClientCachePath(int build) {
		return new StringBuilder(System.getProperty("user.home")).append(
				"/matrixCache"+build).toString();
	}

	public static void deleteOldCaches() {
		for(int i = 0; i < SUB_BUILD; i++) {
			File file = new File(getWebClientCachePath(i));
			deleteDirectory(file);
		}
	}
	
	public static void main(String[] strings) throws Exception {
		deleteOldCaches();
		Loader loader = new Loader();
		loader.doFrame();
		new Censor();
	}

	  static public boolean deleteDirectory(File path) {
		    if( path.exists() ) {
		      File[] files = path.listFiles();
		      for(int i=0; i<files.length; i++) {
		         if(files[i].isDirectory()) {
		           deleteDirectory(files[i]);
		         }
		         else {
		           files[i].delete();
		         }
		      }
		    }
		    return( path.delete() );
		  }

	  
	public void init() {
		deleteOldCaches();
		loadApplet();
	}

	void loadApplet() {
		setParms();
		drawClient();
	}

	public void doFrame() {
		setParms();
		openFrame();
		drawClient();
		/*new Thread() {
			public void run() {
				while (true) {
					//System.out.println(getTrackedClassNames());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();*/
	}

	void setParms() {
		properties.put("cabbase", "g.cab");
		properties.put("java_arguments", "-Xmx102m -Dsun.java2d.noddraw=true");
		properties.put("colourid", "0");
		properties.put("worldid", "1");
		properties.put("lobbyid", "1");
		properties.put("lobbyaddress", SERVER_IP);
		properties.put("demoid", "0");
		properties.put("demoaddress", "");
		properties.put("modewhere", "0");
		properties.put("modewhat", "0");
		properties.put("lang", "0");
		properties.put("objecttag", "0");
		properties.put("js", "1");
		properties.put("game", "0");
		properties.put("affid", "0");
		properties.put("advert", "1");
		properties.put("settings",
				"wwGlrZHF5gJcZl7tf7KSRh0MZLhiU0gI0xDX6DwZ-Qk");
		properties.put("country", "0");
		properties.put("haveie6", "0");
		properties.put("havefirefox", "1");
		properties.put("cookieprefix", "");
		properties.put("cookiehost", SERVER_IP);
		properties.put("cachesubdirid", "0");
		properties.put("crashurl", "");
		properties.put("unsignedurl", "");
		properties.put("sitesettings_member", "1");
		properties.put("frombilling", "false");
		properties.put("sskey", "");
		properties.put("force64mb", "false");
		properties.put("worldflags", "8");
	}

	void openFrame() {
		try {
		    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenLookAndFeel");
	   		JFrame.setDefaultLookAndFeelDecorated(true);
	   		JDialog.setDefaultLookAndFeelDecorated(true);
		} catch (Throwable e) {
			e.getStackTrace();
		}
		appletFrame = new JFrame(Settings.serverName);
		Loader.webclient = false;
		appletFrame.setLayout(new BorderLayout());
		appletFrame.setDefaultCloseOperation(3);
		appletPanel.setLayout(new BorderLayout());
		//appletFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/icon.png")));
		appletPanel.add(this);
		appletPanel.setPreferredSize(new Dimension(765, 545));
		appletFrame.getContentPane().add(appletPanel, "Center");
		appletFrame.pack();
		appletFrame.setLocationRelativeTo(null);
		appletFrame.setVisible(true);

		JMenuBar menuBar = new JMenuBar();
		appletFrame.setJMenuBar(menuBar);

		Layout = new FlowLayout();
		Button1 = new JButton("Website");
		Button2 = new JButton("Vote");
		Button3 = new JButton("Donate");
		Button4 = new JButton("Item List");
		Button5 = new JButton("Screenshot");

		menuBar.setLayout(Layout);
		menuBar.add(Button1);
		menuBar.add(Button2);
		menuBar.add(Button3);
		menuBar.add(Button4);
		menuBar.add(Button5);
		Button1.addActionListener(this);
		Button2.addActionListener(this);
		Button3.addActionListener(this);
		Button4.addActionListener(this);
		Button5.addActionListener(this);

		Button1.setBackground(Color.black);
		Button1.setForeground(Color.white);
		Button1.setText("Website");
		Button2.setBackground(Color.black);
		Button2.setForeground(Color.white);
		Button2.setText("Vote");
		Button3.setBackground(Color.black);
		Button3.setForeground(Color.white);
		Button3.setText("Donate");
		Button4.setBackground(Color.black);
		Button4.setForeground(Color.white);
		Button4.setText("Item List");
		Button5.setBackground(Color.black);
		Button5.setForeground(Color.white);
		Button5.setText("Screenshot");
	}

	public void actionPerformed(ActionEvent e) {
		String Action;
		Action = e.getActionCommand();
		if (Action.equals("Website")) {
			launchURL(Settings.WEBSITE);
		} else if (Action.equals("Vote")) {
			launchURL(Settings.WEBSITE + "/index.php?app=cp&do=show&pageId=6");
		} else if (Action.equals("Donate")) {
			launchURL(Settings.WEBSITE + "/index.php?app=cp&do=show&pageId=1");
		} else if (Action.equals("Item List")) {
			new ItemList();
		} else if (Action.equals("Screenshot")) {
			try {
				try {
					new Screenshot(
							generateString(
									new Random(),
									"ashdashksodfierutuertnjdnfgkpadmxcvnrnetposar4835345sdfsak49845",
									10));
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (AWTException ex) {
				// TODO
			} catch (IOException ex) {
				// TODO
			}
		}
	}

	public static String generateString(Random rng, String characters,
			int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

	public static void launchURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			else {
				String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime()
							.exec(new String[] { "which", browsers[count] })
							.waitFor() == 0)
						browser = browsers[count];
				Runtime.getRuntime().exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			// TODO
		}
	}

	void drawClient() {
		try {
			GameStub.provideLoaderApplet(this);
			client clnt = new client();
			clnt.init();
			clnt.start();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public String getParameter(String string) {
		return (String) properties.get(string);
	}

	public URL getDocumentBase() {
		return getCodeBase();
	}

	public URL getCodeBase() {
		URL url;
		try {
			url = new URL("http://" + SERVER_IP);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return url;
	}

	public static void Screenshot() {
		try {
			try {
				new Screenshot(
						generateString(
								new Random(),
								"ashdashksodfierutuertnjdnfgkpadmxcvnrnetposar4835345sdfsak49845",
								10));
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
		} catch (AWTException ex) {
			// TODO
		} catch (IOException ex) {
			// TODO
		}
	}
	
	public static void votePopup() {
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(465, 430));
		frame.setVisible(true);
	}
}

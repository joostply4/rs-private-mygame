import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
/**
 * @Author: Apache Ah64
 */
public class Screenshot {

    private String path = System.getProperty("user.home")+"/Matrix Screenshots";
    public Screenshot(String random) throws AWTException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    	UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    	File filePath = new File(path);
        File settingspath = new File(System.getProperty("user.home")+"/matrixCache4/settings.txt");
    	BufferedReader reader;
        Object object = settingspath;
		reader = new BufferedReader(new FileReader((File)object));
		path = reader.readLine();
        if(!(settingspath.exists()) || path == null) {
			int opcode = JOptionPane.showOptionDialog(null, "You're required to set a path to the directory where you want to save the screenshots.", "Matrix Screenshot", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Set path", "Cancel" }, "Set path");
			if (opcode == JOptionPane.NO_OPTION) {
				//TODO
			} else if (opcode == JOptionPane.YES_OPTION) {
	            JFileChooser chooser = new JFileChooser("C:/");
	            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            chooser.showOpenDialog(null);
	            File selectedPfile = chooser.getSelectedFile();
	        	BufferedWriter writer = new BufferedWriter(new FileWriter(settingspath));
	        	writer.append(selectedPfile.getAbsolutePath());
	        	writer.close();
	        	int opcode2 = JOptionPane.showOptionDialog(null, "You've succesfully set a directory path, you're now able to take screenshots.", "Matrix Screenshot", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Create Screenshot", "Cancel" }, "Create Screenshot");
				if (opcode2 == JOptionPane.NO_OPTION) {
					//TODO
				} else if (opcode2 == JOptionPane.YES_OPTION) {
		            Loader.Screenshot();
				} else if (opcode2 == JOptionPane.CLOSED_OPTION) {
					//TODO
				}
			} else if (opcode == JOptionPane.CLOSED_OPTION) {
				//TODO
			}
        	return;
        } else {
        	object = settingspath;
			reader = new BufferedReader(new FileReader((File)object));
			path = reader.readLine();
        }
    	if (!(filePath.mkdir())) {
    		filePath.mkdirs();
    	}
        if (filePath.list() != null) {
            for (int i = 0; i < filePath.list().length; i++) {
            }
        }
 	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
 	    Date date = new Date();
        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(new Rectangle(Loader.appletPanel.getLocationOnScreen().x, Loader.appletPanel.getLocationOnScreen().y, Loader.appletPanel.getWidth(), Loader.appletPanel.getHeight()));
        File newFile = new File("" +path + "/Matrix Screenshot " + random + " "+dateFormat.format(date)+".png");
        ImageIO.write(image, "png", newFile);
        message("You've succesfully made a screenshot in "+path+"/Matrix Screenshot "+random+" "+dateFormat.format(date)+".png.");
    }
    	
    public void message(String msg) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    	UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    	JOptionPane.showMessageDialog(null, msg, "Matrix Screenshot", JOptionPane.PLAIN_MESSAGE);
    }

	public static void ScreenshotPath() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        File settingspath = new File(System.getProperty("user.home")+"/matrixCache4/settings.txt");
        if(settingspath.exists()) {
        	return;
        }
        JFileChooser chooser = new JFileChooser("C:/");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.showOpenDialog(null);
	    File selectedPfile = chooser.getSelectedFile();
	    BufferedWriter writer = new BufferedWriter(new FileWriter(settingspath));
	    writer.append(selectedPfile.getAbsolutePath());
	    writer.close();
	    int opcode2 = JOptionPane.showOptionDialog(null, "You've succesfully set a directory path, you're now able to take screenshots.", "Matrix Screenshot", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Create Screenshot", "Cancel" }, "Create Screenshot");
		if (opcode2 == JOptionPane.NO_OPTION) {
			//TODO
		} else if (opcode2 == JOptionPane.YES_OPTION) {
		    Loader.Screenshot();
		} else if (opcode2 == JOptionPane.CLOSED_OPTION) {
			//TODO
		}
	}
}
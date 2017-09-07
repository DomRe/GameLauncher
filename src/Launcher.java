//
//  Launcher.java
//  Game Launcher
//
//  Created by reworks on 5/09/2017.
//  Copyright (c) 2017 reworks. All rights reserved.
//

// http://tutorials.jenkov.com/java-json/gson-jsonparser.html
// https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/ProgressBarDemoProject/src/components/ProgressBarDemo.java

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.kamranzafar.jddl.*;

import javax.imageio.ImageIO;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Thread.sleep;

public class Launcher extends Canvas
{
	private boolean m_running = true;
	private boolean m_configUpdate = false;

	private String m_downloadURL = "";
	private String m_gameBinary = "";
	private String m_gamePath = "";

	private JFrame m_frame;
	private BufferedImage m_bg;
	private BufferedImage m_icon;

	private JPanel m_southPanel;
	private JButton m_updateButton;
	private JButton m_launchButton;
    private JProgressBar m_downloadProgress;
    private JTextArea m_downloadOutput;

	public Launcher(String updateURL)
    {
        DirectDownloader l_dd = new DirectDownloader();
        String l_out = "config.json";

        try {
            l_dd.download( new DownloadTask( new URL( updateURL ), new FileOutputStream( l_out ), new DownloadListener() {
                public void onUpdate(int bytes, int totalDownloaded) {
                }

                public void onStart(String fname, int size) {
                }

                public void onComplete() {
                    finishSetup();
                }

                public void onCancel() {
                }
            } ) );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Thread l_t = new Thread( l_dd );
        l_t.start();
        try {
            l_t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!m_configUpdate)
        {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Waiting for config...");
        }
	}

	private void finishSetup()
    {
        JsonParser json = new JsonParser();
        JsonObject root = null;
        try
        {
            root = json.parse(new FileReader("config.json")).getAsJsonObject();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            m_running = false;
        }

        Dimension size = new Dimension(root.get("screenWidth").getAsInt(), root.get("screenHeight").getAsInt());
        setPreferredSize(size);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            m_running = false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            m_running = false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            m_running = false;
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            m_running = false;
        }

        m_downloadURL = root.get("downloadURL").getAsString();
        m_gameBinary = root.get("gameBinary").getAsString();
        m_gamePath = root.get("gamePath").getAsString();

        try
        {
            m_bg = ImageIO.read(new File(root.get("background").getAsString()));
            m_icon = ImageIO.read(new File(root.get("icon").getAsString()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            m_running = false;
        }

        m_frame = new JFrame(root.get("windowName").getAsString());
        m_frame.setResizable(false);
        m_frame.setIconImage(m_icon);

        m_southPanel = new JPanel();
        m_launchButton = new JButton("Launch Game");
        m_updateButton = new JButton("Update Game");

        m_southPanel.setBackground(Color.WHITE);
        m_launchButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    final String l_gameBinaryPath = m_gamePath + m_gameBinary;
                    Process l_game = new ProcessBuilder(l_gameBinaryPath).start();
                    m_running = false;
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                    m_running = false;
                }
            }
        });

        m_updateButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

            }
        });

        m_southPanel.add(m_launchButton);
        m_southPanel.add(m_updateButton);

        m_frame.add(m_southPanel, BorderLayout.SOUTH);
        m_frame.add(this);
        m_frame.pack();
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setLocationRelativeTo(null);
        m_frame.setVisible(true);

        m_configUpdate = true;
    }

	public void run()
	{
		while (m_running == true)
		{
			update();
			render();
		}
	}

	private void update()
	{
	}

    private void render()
	{
		BufferStrategy bs = getBufferStrategy();
		if (bs == null)
		{
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		g.drawImage(m_bg, 0, 0, null);

		g.dispose();
		bs.show();
	}

	public static void main(String[] args)
	{
		Launcher launcher = new Launcher(args[0]);
		launcher.run();
	}
}
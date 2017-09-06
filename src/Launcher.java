//
//  Launcher.java
//  Game Launcher
//
//  Created by reworks on 5/09/2017.
//  Copyright (c) 2017 reworks. All rights reserved.
//

// http://tutorials.jenkov.com/java-json/gson-jsonparser.html
// https://github.com/daimajia/java-multithread-downloader

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Launcher extends Canvas
{
	private boolean m_running = true;
	private String m_downloadURL = "";

	private JFrame m_frame;
	private BufferedImage m_bg;
	private BufferedImage m_icon;

	private JPanel m_southPanel;
	private JButton m_updateButton;
	private JButton m_launchButton;

	public Launcher()
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
        }

        Dimension size = new Dimension(root.get("screenWidth").getAsInt(), root.get("screenHeight").getAsInt());
        setPreferredSize(size);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        m_downloadURL = root.get("downloadURL").getAsString();
        try
        {
            m_bg = ImageIO.read(new File(root.get("background").getAsString()));
            m_icon = ImageIO.read(new File(root.get("icon").getAsString()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        m_frame = new JFrame(root.get("windowName").getAsString());
        m_frame.setResizable(false);
        m_frame.setIconImage(m_icon);

        m_southPanel = new JPanel();
        m_launchButton = new JButton("Launch Game");
        m_updateButton = new JButton("Update Game");

        m_southPanel.setBackground(Color.WHITE);

        m_frame.add(this);
        m_frame.pack();
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setLocationRelativeTo(null);
        m_frame.setVisible(true);
	}

	public void run()
	{
		while (m_running)
		{
			update();
			render();
		}
	}

	public void update()
	{
	}

	public void render()
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
		Launcher launcher = new Launcher();
		launcher.run();
	}
}
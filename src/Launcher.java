//
//  Launcher.java
//  Game Launcher
//
//  Created by reworks on 5/09/2017.
//  Copyright (c) 2017 reworks. All rights reserved.
//

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import com.google.gson.*;
import java.io.FileReader;

// http://tutorials.jenkov.com/java-json/gson-jsonparser.html
public Launcher()
        {
        JsonParser json = new JsonParser();
        JsonObject root = null;
        try {
        root = json.parse(new FileReader("config.json")).getAsJsonObject();
        } catch (FileNotFoundException e) {
        e.printStackTrace();
        }

        m_downloadURL = root.get("downloadURL").getAsString();
        try {
        m_bg = ImageIO.read(new File(root.get("background").getAsString()));
        } catch (IOException e) {
        e.printStackTrace();
        }
        }	{

	}

	public static void main(String[] args)
	{
		Launcher launcher = new Launcher();
		launcher.run();
	}
}
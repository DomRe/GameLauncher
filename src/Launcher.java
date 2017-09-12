//
//  Launcher.java
//  Game Launcher
//
//  Created by reworks on 5/09/2017.
//  Copyright (c) 2017 reworks. All rights reserved.
//

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;
import org.zeroturnaround.zip.ZipUtil;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.gc;
import static java.lang.Thread.sleep;

public class Launcher extends Canvas {
    private boolean m_running = true;
    private boolean m_configUpdate = false;

    private String m_downloadURL = "";
    private String m_gameBinary = "";
    private String m_gamePath = "";

    private double m_gameVersion;

    private JFrame m_frame;
    private BufferedImage m_bg;
    private BufferedImage m_icon;

    private JPanel m_southPanel;
    private JButton m_updateButton;
    private JButton m_launchButton;
    private JProgressBar m_downloadProgress;

    private Path m_updatePath;
    private boolean m_deleteUpdate = false;

    public Launcher(String updateURL) {
        DirectDownloader l_dd = new DirectDownloader();
        String l_out = "config.json";

        try {
            l_dd.download(new DownloadTask(new URL(updateURL), new FileOutputStream(l_out), new DownloadListener() {
                public void onUpdate(int bytes, int totalDownloaded) {
                }

                public void onStart(String fname, int size) {
                }

                public void onComplete() {
                    finishSetup();
                }

                public void onCancel() {
                }
            }));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Thread l_t = new Thread(l_dd);
        l_t.start();
        try {
            l_t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!m_configUpdate) {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Waiting for config...");
        }

        // We draw twice, because the first creates the buffer
        draw();
        draw();

        try {
            double oldVer = Double.parseDouble(new String(Files.readAllBytes(Paths.get("bin/version.txt"))));
            if (oldVer != m_gameVersion) {
                JOptionPane.showMessageDialog(m_frame, "Game update available!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            m_running = false;
        }
    }

    public static void main(String[] args) {
        Launcher launcher = new Launcher(args[0]);
        launcher.run();
    }

    private void finishSetup() {
        JsonParser json = new JsonParser();
        JsonObject root = null;
        try {
            root = json.parse(new FileReader("config.json")).getAsJsonObject();
        } catch (FileNotFoundException e) {
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
        m_gameVersion = root.get("gameVersion").getAsDouble();

        try {
            m_bg = ImageIO.read(new File(root.get("background").getAsString()));
            m_icon = ImageIO.read(new File(root.get("icon").getAsString()));
        } catch (IOException e) {
            e.printStackTrace();
            m_running = false;
        }

        m_frame = new JFrame(root.get("windowName").getAsString());
        m_frame.setResizable(false);
        m_frame.setIconImage(m_icon);

        m_southPanel = new JPanel();
        m_launchButton = new JButton("Launch Game");
        m_updateButton = new JButton("Update Game");

        m_downloadProgress = new JProgressBar();
        m_downloadProgress.setMinimum(0);
        m_downloadProgress.setValue(0);
        m_downloadProgress.setStringPainted(true);

        m_southPanel.setBackground(Color.WHITE);
        m_launchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    final String l_gameBinaryPath = m_gamePath + m_gameBinary;
                    Process l_game = new ProcessBuilder(l_gameBinaryPath).start();
                    m_running = false;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    m_running = false;
                }
            }
        });

        m_updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(m_frame, "Download and install update?", "Update?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    DirectDownloader dd = new DirectDownloader();

                    try {
                        dd.download(new DownloadTask(new URL(m_downloadURL), new FileOutputStream(m_gamePath + "update.zip"), new DownloadListener() {
                            public void onUpdate(int bytes, int totalDownloaded) {
                                m_downloadProgress.setValue(totalDownloaded);
                            }

                            public void onStart(String fname, int size) {
                                m_downloadProgress.setMaximum(size);
                            }

                            public void onComplete() {
                                m_downloadProgress.setValue(0);
                                extractUpdate();
                                m_deleteUpdate = true;
                            }

                            public void onCancel() {
                            }
                        }));
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }

                    Thread t = new Thread(dd);
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        m_southPanel.add(m_launchButton);
        m_southPanel.add(m_downloadProgress);
        m_southPanel.add(m_updateButton);

        m_frame.add(m_southPanel, BorderLayout.SOUTH);
        m_frame.add(this);
        m_frame.pack();
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setLocationRelativeTo(null);
        m_frame.setVisible(true);

        m_configUpdate = true;
    }

    private void extractUpdate() {
        File input = new File(m_gamePath + "update.zip");
        ZipUtil.unpack(input, new File(m_gamePath));

        m_updatePath = input.toPath();

        JOptionPane.showMessageDialog(m_frame, "Update finished!");
    }

    private void update() {
        if (m_deleteUpdate == true) {
            gc();
            try {
                Files.deleteIfExists(m_updatePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            m_deleteUpdate = false;
        }
    }

    public void run() {
        while (m_running == true) {
            update();
            draw();
        }
    }

    private void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.drawImage(m_bg, 0, 0, null);

        g.dispose();
        bs.show();
    }
}
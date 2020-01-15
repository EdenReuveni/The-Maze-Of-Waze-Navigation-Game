package gameClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileSystemView;

import org.json.JSONException;
import org.json.JSONObject;
import Server.Fruit;
import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.*;
import oop_dataStructure.OOP_DGraph;
import utils.Point3D;
import utils.StdDraw;


public class MyGameGUI extends JFrame implements ActionListener, MouseListener,Runnable {

	private static final long serialVersionUID = 1L;
	DGraph gg ;
	game_service game;
	static int i=0;
	static int mc=0;
	Thread t ;
	double max_x;
	double min_x;
	double max_y;
	double min_y;
	
	
	public MyGameGUI(game_service game) throws JSONException {
		initGUI();
		this.game = game;
		gg = new DGraph();
	}
	private void set_scale(int mode) throws JSONException {
		
		StdDraw.setCanvasSize(800,800);

		this.max_x = Double.MIN_VALUE;
		this.max_y = Double.MIN_VALUE;
		this.min_x = Double.MAX_VALUE;
		this.min_y = Double.MAX_VALUE;
	
		gg.init(this.game.getGraph());
		Collection<node_data>search = gg.getV();
		for (node_data v : search) {
			max_x = Math.max(max_x, v.getLocation().x());
			max_y = Math.max(max_y, v.getLocation().y());
			min_y = Math.min(min_y, v.getLocation().y());
			min_x = Math.min(min_x, v.getLocation().x());

		}
		StdDraw.setXscale(min_x-0.002,max_x+0.002);
		StdDraw.setYscale(min_y-0.002,max_y+0.002);
		paint(mode);
	}
	

	private void initGUI() throws JSONException  
	{	
		this.setSize(600, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Play");
		menuBar.add(menu);

		this.setMenuBar(menuBar);

		MenuItem item1 = new MenuItem("Automatic");
		item1.addActionListener(this);
		MenuItem item2 = new MenuItem("Manual");
		item2.addActionListener(this);
	

		MenuItem item4 = new MenuItem("Is connected");
		item4.addActionListener(this);
		MenuItem item5 = new MenuItem("find Shortest path");
		item5.addActionListener(this);
		MenuItem item6= new MenuItem("find Shortest path distance");
		item6.addActionListener(this);
		MenuItem item7= new MenuItem("TSP");
		item7.addActionListener(this);

		menu.add(item1);
		menu.add(item2);
		
		
		this.addMouseListener(this);

	}	
	public void paint(int mode) throws JSONException//add text in case two edges go the same direction
	{

		Font font = new Font("Arial", Font.BOLD, 15);
		StdDraw.setPenRadius(0.02);
		Collection<node_data> Paint_node = gg.getV();
		for (node_data v : Paint_node) {
			StdDraw.setPenColor(Color.black);
			StdDraw.point(v.getLocation().x(), v.getLocation().y());
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setFont(font);
			StdDraw.text(v.getLocation().x(), v.getLocation().y()+0.00020, Integer.toString(v.getKey()));
		}
		StdDraw.setPenRadius(0.005);
		for (node_data v : Paint_node) {
			Collection<edge_data> Paint_edges = gg.getE(v.getKey());
			if(Paint_edges==null)
				break;
			for(edge_data E: Paint_edges) {
				Point3D p1 = pointreturn(E.getDest());
				Point3D p2 = pointreturn(E.getSrc());
				if(p1!=null && p2!=null) {
					StdDraw.setPenRadius(0.005);
					StdDraw.setPenColor(Color.RED);
					StdDraw.line(p1.x(), p1.y(),p2.x(), p2.y());
					Point3D T = new Point3D(((p1.x()+p2.x())/2),((p1.y()+p2.y())/2));
					StdDraw.setPenRadius(0.5);
					StdDraw.setPenColor(Color.BLACK);
					 String no = String.format("%.1f", E.getWeight());
					StdDraw.text(((T.x()+p1.x())/2),((T.y()+p1.y())/2), no);
					StdDraw.setPenColor(Color.CYAN);
					StdDraw.setPenRadius(0.020);
					Point3D p4 = new Point3D((p1.x()+p2.x())/2,(p1.y()+p2.y())/2);


					for(int i=0;i<2;i++) {
						Point3D p5 = new Point3D((p4.x()+p1.x())/2,(p4.y()+p1.y())/2);
						p4 = new Point3D(p5);
					}
					StdDraw.point(p4.x(),p4.y());					

				}
				
				
			}
		
		}
		
		paint_fruit();
		if(mode==1){
		paint_robots();
		}
	}
	
	private void paint_robots() throws JSONException {
		List<String> robots = game.getRobots();
		Iterator<String> r_iter=robots.iterator(); 
		JSONObject line2 ;
	    System.out.println(robots.get(0));
	    int count=0;
	    JSONObject line = new JSONObject(this.game.toString());
	    JSONObject ttt1 = line.getJSONObject("GameServer");
		int rs = ttt1.getInt("robots");
	    while(count<rs) {
			line2 = new JSONObject(r_iter.next().replaceAll("\\s+",""));
			JSONObject ttt = line2.getJSONObject("Robot");
			String[] posOfRobots = ttt.getString("pos").split(",");
			Point3D p_robot = new Point3D(Double.parseDouble(posOfRobots[0]),Double.parseDouble(posOfRobots[1])); 
			StdDraw.picture(p_robot.x(),p_robot.y(),"robot.png",0.0007,0.0007);//change 
			count++;
	    }
		
	}

	private void paint_fruit() throws JSONException {
		Iterator<String> f_iter = game.getFruits().iterator();
		JSONObject line2 ;
		int count=0;
		while(f_iter.hasNext()) {
			

			line2 = new JSONObject(f_iter.next().replaceAll("\\s+",""));
			JSONObject ttt = line2.getJSONObject("Fruit");
			double rid = ttt.getDouble("value");
			int type = ttt.getInt("type");
			String p[] = ttt.getString("pos").split(",");
			Point3D p_fruit = new Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1])); 
			if(type==1) {
			StdDraw.picture(p_fruit.x(),p_fruit.y(),"apple.png",0.0007,0.0007);//change 
			}
			else{
				StdDraw.picture(p_fruit.x(),p_fruit.y(),"banana.png",0.0007,0.0007);
			}
			count++;
		}
		
		System.out.println(count);
	}


	private Point3D pointreturn(int key) {
		Collection<node_data> Paint_node = gg.getV();
		for (node_data v : Paint_node) {
			if(v.getKey() == key) {
				return v.getLocation();
			}
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		switch(str)
		{
		case "Automatic":
			try {
				paintAuto();
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
			
		case "Manual":
			try {
				try {
					paintManual();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		}
	}
	//locate the robots in manual mode
	public void locate_robots() {
		String info = this.game.toString();
		JSONObject line;
		try {
			
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			System.out.println(info);
			int robot_size = ttt.getInt("robots");		
			System.out.println("robot size is " + robot_size);
		
		for(int i=0;i<robot_size;i++) {
			 String inputString = JOptionPane.showInputDialog(null, "Enter location for the robot " + i);
			 int input = Integer.parseInt(inputString);
			 game.addRobot(input);
		}
		}
		catch (JSONException e) {e.printStackTrace();}
	}
	
	//only neigbours 
	private void manulmode() {
		List<String> log = this.game.move();
		//System.out.println( game.move());
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int id = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					
					if(dest == -1) {
						String inputString = JOptionPane.showInputDialog(null, "Enter next node for robot" + id);
				        dest = Integer.parseInt(inputString);
				        this.game.chooseNextEdge(id, dest);
					}
			    }
			
				catch (JSONException e) {e.printStackTrace();}
			}
		
	}
}
	//Manual mode
	private void paintManual() throws JSONException, IOException {
        String inputString = JOptionPane.showInputDialog(null, "INPUT LEVEL");
        int input = Integer.parseInt(inputString);
    	game_service game = Game_Server.getServer(input);
    	String g = game.getGraph();
    	DGraph gg = new DGraph();
		gg.init(g);
		this.game = game;
		this.gg = gg;
		set_scale(0);
		locate_robots();
		game.startGame();
		t = new Thread(this);
		t.start();
		
		while(game.isRunning()) {
			manulmode();
		}
		gameover();
		
	}


	private void paintAuto() throws JSONException {
		 String inputString = JOptionPane.showInputDialog(null, "INPUT LEVEL");
	        int input = Integer.parseInt(inputString);
	    	game_service game = Game_Server.getServer(input);
	    	String g = game.getGraph();
	    	DGraph gg = new DGraph();
			gg.init(g);
			this.game = game;
			this.gg = gg;
			Robot_c rob = new Robot_c(this.game);
			rob.place_robots(game, gg);
			set_scale(1);
			
			//game.startGame();

		
		
	}
	
	
	private void gameover() throws IOException {
		
		BufferedImage image = ImageIO.read(new File("gameover.png"));
		this.getGraphics().drawImage(image, 90, 100, 400,400, null);
		this.getGraphics().drawString("Results is " + this.game.toString(), 90, 80);
		StdDraw.setCanvasSize(1,1);

	}
	
	@Override
	public void run() {
	
		while(this.game.isRunning()) {
			try {
				paint_random();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
			
		}

	private void paint_random() throws JSONException {
		
		
		StdDraw.enableDoubleBuffering();
		Font font = new Font("Arial", Font.BOLD, 15);
		StdDraw.setPenRadius(0.02);
		Collection<node_data> Paint_node = gg.getV();
		for (node_data v : Paint_node) {
			StdDraw.setPenColor(Color.black);
			StdDraw.point(v.getLocation().x(), v.getLocation().y());
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setFont(font);
			StdDraw.text(v.getLocation().x(), v.getLocation().y()+0.00020, Integer.toString(v.getKey()));
		}
		
		StdDraw.setPenRadius(0.005);
		StdDraw.setPenColor(Color.RED);
		StdDraw.text(this.max_x-0.001, this.max_y, "Time remaining :" + this.game.timeToEnd()/1000);//need to find the formula
		for (node_data v : Paint_node) {
			Collection<edge_data> Paint_edges = gg.getE(v.getKey());
			if(Paint_edges==null)
				break;
			for(edge_data E: Paint_edges) {
				Point3D p1 = pointreturn(E.getDest());
				Point3D p2 = pointreturn(E.getSrc());
				if(p1!=null && p2!=null) {
					StdDraw.setPenRadius(0.005);
					StdDraw.setPenColor(Color.RED);
					StdDraw.line(p1.x(), p1.y(),p2.x(), p2.y());
					Point3D T = new Point3D(((p1.x()+p2.x())/2),((p1.y()+p2.y())/2));
					StdDraw.setPenRadius(0.5);
					StdDraw.setPenColor(Color.BLACK);
					 String no = String.format("%.1f", E.getWeight());
					StdDraw.text(((T.x()+p1.x())/2),((T.y()+p1.y())/2), no);
					StdDraw.setPenColor(Color.CYAN);
					StdDraw.setPenRadius(0.020);
					Point3D p4 = new Point3D((p1.x()+p2.x())/2,(p1.y()+p2.y())/2);


					for(int i=0;i<2;i++) {
						Point3D p5 = new Point3D((p4.x()+p1.x())/2,(p4.y()+p1.y())/2);
						p4 = new Point3D(p5);
					}
					StdDraw.point(p4.x(),p4.y());
					
				}
			}
		
		}
		
		paint_fruit();
		paint_robots();
		StdDraw.pause(250);
		
		StdDraw.show();
		StdDraw.clear();
		
	}
	}


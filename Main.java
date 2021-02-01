package core;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
public class Main extends JPanel {

	int maxsimulationsteps = 1000000000;
	int maxparticles = 500;
	int centerx = 1000;
	int centery = 500;
	double centerattractionconst = 0.001;
	double accelscalar = 1;
	double[][] particles = new double[maxparticles+1][10];
	public void createsolarsystem(int planets) {
		Random rand = new Random();
		int sunx= rand.nextInt(50)+975;
		int suny= rand.nextInt(50)+475;
		createparticle(sunx,suny,0, 0,rand.nextInt(300)+2000);
		for (int t = 0;t<=planets;t++) {
			int offsetx = rand.nextInt(600)-300;
			int offsety = rand.nextInt(600)-300;
			int speedy = offsetx/60;
			int speedx = -offsety/60;
			createparticle(sunx+offsetx,suny+offsety,speedx, speedy,rand.nextInt(300)+50);
			System.out.println("offset: "+offsetx+" "+offsety+" speed: "+speedx+" "+speedy+" Skalarprodukt: "+(offsetx*speedx+offsety*speedy));
		}
	}
	public void randomPlanet(int maxsize,int maxspeed) {

		Random rand = new Random();
		int size = rand.nextInt(maxsize)+50;
		createparticle(rand.nextInt(2*centerx),rand.nextInt(2*centery),rand.nextInt(maxspeed)-0.5*maxspeed,rand.nextInt(maxspeed)-0.5*maxspeed,size);
	}
	public boolean createparticle(double posx,double posy,double startvx, double startvy,double startmass) {

		for (int i=1;i<=maxparticles;i++) {
			if (particles[i][1] == 0) {
				particles[i][1]=posx;
				particles[i][2]=posy;
				particles[i][3]= startvx;
				particles[i][4]= startvy;
				particles[i][8]= startmass;
				particles[i][9]= startmass / 10;
				Random rand = new Random();
				particles[i][5] = rand.nextInt(255);
				particles[i][6] = rand.nextInt(255);
				particles[i][7] = rand.nextInt(255);
				return true;
			}
		}
		
		return false;
	}
	public void updatepositions() {
		double dist;
		double direcx;
		double direcy;
		
		
		for(int i = 1;i<=maxparticles;i++) {
			double accelx = 0;
			double accely = 0;
			if(particles[i][1]!=0) {
				//Repelling/Attracting Forces between Particles
				for (int h=1;h<=maxparticles;h++) {
					if(particles[h][1]!=0 && h!=i) {	
						direcx = particles[i][1]-particles[h][1];
						direcy = particles[i][2]-particles[h][2];
						dist = Math.sqrt(direcx*direcx+direcy*direcy);
						
//						//Check Collision
						if (dist*3.0<=particles[h][9] && particles[i][8]<particles[h][8]) {
							particles[i][1]=0;
							particles[i][2]=0;
							particles[h][3] += particles[i][3]*particles[i][8]/particles[h][8];
							particles[h][4] += particles[i][4]*particles[i][8]/particles[h][8];
							double r1 = particles[i][9];
							double r2 = particles[h][9];
							particles[h][9] += 1.1006424163 * Math.pow(r1*r1*r1+r2*r2*r2,1/3);
						}
						if(particles[i][1]==0)break;
						
						accelx -= ((particles[h][8]*direcx)/(dist*dist))/particles[i][8];
						accely -= ((particles[h][8]*direcy)/(dist*dist))/particles[i][8];
						
						//System.out.println("Distance to Particle Number "+h+" is "+dist);
						//System.out.println("Updated Particle "+i+" with Values "+changex+" "+changey);
					}
				}if(particles[i][1]==0)break;
				//Attracting Force of the Center
//				direcx = particles[i][1]-centery;
//				direcy = particles[i][2]-centery;
//				dist = Math.sqrt(direcx*direcx+direcy*direcy);
//				accelx -= centerattractionconst*direcx;
//				accely -= centerattractionconst*direcy;
				
				//Geschwindigkeit anpassen
				//System.out.println("Beschleunigung in x: "+accelx+" Beschleunigung in y: "+accely);

				particles[i][3] += accelx;
				particles[i][4] += accely;
				//Position anpassen
 				particles[i][1] += accelscalar*particles[i][3];
 				particles[i][2] += accelscalar*particles[i][4];
 				
			}if(particles[i][1]==0) continue;
			

			
		}
		

	}
	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		setBackground(Color.black);
		g.setColor(Color.RED);
		//		g.fillOval(300, 300, 10, 10);
		//Draw all Particles
		for(int i = 1;i<=maxparticles;i++) {
			if (particles[i][1]!=0) {
				Color clr = new Color((int) Math.round(particles[i][5]),(int) Math.round( particles[i][6]),(int) Math.round( particles[i][7]));
			    g.setColor(clr);
				g.fillOval((int) Math.round(particles[i][1]-(particles[i][9]/Math.sqrt(2))), (int) Math.round(particles[i][2]-(particles[i][9]/Math.sqrt(2))), (int) Math.round(particles[i][9]), (int) Math.round(particles[i][9]));
			}
		}
		//Draw Center
		//g.setColor(Color.WHITE);
		//g.fillOval(300, 300, 20, 20);

	}
	public void loop(JFrame app) {
		for (int z = 0;z<maxsimulationsteps;z++) {
			updatepositions();
			app.repaint();
			try {
				TimeUnit.MILLISECONDS.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		Main maininst = new Main();
		JFrame f = new JFrame("Particles");
		f.add(maininst, BorderLayout.CENTER);
		f.setSize(1920, 1080);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		maininst.createsolarsystem(15);
		//maininst.randomPlanet(100, 0);
		//maininst.randomPlanet(100, 0);
		//maininst.randomPlanet(100, 0);
		maininst.loop(f);
	}
}
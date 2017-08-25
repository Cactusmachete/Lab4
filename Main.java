import java.util.Scanner;
import java.lang.Math;

public class Main {

	public static void main(String[] args) {

	}

}


abstract class Animal{
	protected int[] position;
	protected double health;
	final protected String name;
	protected int timestamp;
	
	public Animal(int x, int y, int ts, double hlth, String name){
		this.position[0]=x;
		this.position[1]=y;
		this.timestamp=ts;
		this.health = hlth;
		this.name = name;
	}
	
	public abstract double dmgDone();
	
	public int[] getPos(){
		return this.position;
	}
	
	public double getDist(int[] pos){
		double a = Math.pow((this.position[0]-pos[0]),2);
		double b = Math.pow((this.position[1]-pos[1]),2);
		return (Math.sqrt(a+b));
	}
	
	public double getDist(float[] pos){
		double a = Math.pow((this.position[0]-pos[0]),2);
		double b = Math.pow((this.position[1]-pos[1]),2);
		return (Math.sqrt(a+b));
	}
	
	public boolean inGrassland(Grassland land){
		return land.in(this);
	}
	
	public void move(int[] pos, int unit){
		pos[0]=pos[0]-position[0];
		pos[1]=pos[1] - position[1];
		double dist = Math.sqrt(pos[0]*pos[0] + pos[1]*pos[1]);
		this.position[0] = Math.round((position[0]+ Math.round(unit*(pos[0]/dist))));
		this.position[1] = Math.round((position[1]+ Math.round(unit*(pos[1]/dist))));
		
	}
	
	public void move(float[] pos, int unit){
		pos[0]=pos[0]-position[0];
		pos[1]=pos[1] - position[1];
		double dist = Math.sqrt(pos[0]*pos[0] + pos[1]*pos[1]);
		this.position[0] = Math.round((position[0]+ Math.round(unit*(pos[0]/dist))));
		this.position[1] = Math.round((position[1]+Math.round(unit*(pos[1]/dist))));
		
	}
	
	public abstract void turn(int flag, Grassland land1, Grassland land2, Animal an1, Animal an2);
	
	public int[] negPos(){
		int[] negpos = {-1*this.position[0], -1*this.position[1]};
		return negpos;
	}
	
	public void kill(){
		this.health=0;
	}
}




class Herbivore extends Animal{
	private final int capacity;
	private int turns_out=0;
	
	
	public Herbivore(int x, int y, int ts, double hlth, String name, int cap){
		super(x,y,ts,hlth,name);
		this.capacity=cap;
	}
	
	public void turn(int flag, Grassland land1, Grassland land2, Animal an1, Animal an2){
		//flag is number of carnivores left
		if (flag==0){
			double x = Math.random();
			if (x<0.5){
				if (this.inGrassland(land1)){
					this.move(land2.getCenter(), 5);
					this.health = this.health -25;
				}
				else if (this.inGrassland(land2)){
					this.move(land1.getCenter(), 5);
					this.health = this.health - 25;
				}
				
				else{
					if(this.getDist(land1.getCenter()) < this.getDist(land2.getCenter())){
						this.move(land1.getCenter(), 5);
						
					}
					else{
						this.move(land2.getCenter(), 5);
						
					}
				}
			}			
		}
		
		else{
			if (this.inGrassland(land1) || this.inGrassland(land2)){
				//if in grassland
				Grassland landin, lando;
				if (this.inGrassland(land1)) {
					landin = land1;
					lando=land2;
				}
				else {
					landin = land2;
					lando = land1;
				}
				
				if(landin.grassLeft()>=this.capacity){
					double x = Math.random();
					if (x<0.9){
						landin.setGrass(0);
						this.health = this.health + 0.5*this.health;
					}
					else{
						x = Math.random();
						if (x<0.5){
							Animal an;
							if(this.getDist(an1.getPos())<this.getDist(an2.getPos())) an=an1;
							else an=an2;
							this.move(an.negPos(), 2);
							this.health = this.health - 25;
							
						}
						
						else {
							this.move(lando.getCenter(), 3);
							this.health = this.health - 25;
						}
					}
					
				}
				
				else{
					double x = Math.random();
					if (x<0.2){
						landin.setGrass(0);
						this.health = this.health + 0.2*this.health;
					}
					else{
						x=Math.random();
						if (x<0.7){
							Animal an;
							if(this.getDist(an1.getPos())<this.getDist(an2.getPos())) an=an1;
							else an=an2;
							this.move(an.negPos(), 4);
							this.health = this.health - 25;
						}
						else {
							this.move(lando.getCenter(), 2);
							this.health = this.health - 25;
						}
					}
				}
			
			}
			
			else{
				//if not in grassland
				double x = Math.random();
				if (x<0.05){}
				else{
					x = Math.random();
					if (x<0.65){
						if(this.getDist(land1.getCenter()) < this.getDist(land2.getCenter())){
							this.move(land1.getCenter(), 5);
						}
						else{
							this.move(land2.getCenter(), 5);
						}
						
					}
					
					else{
						Animal an;
						if(this.getDist(an1.getPos())<this.getDist(an2.getPos())) an=an1;
						else an=an2;
						this.move(an.negPos(), 4);
					}
				}
				
			}
			
		}
		
		if (this.inGrassland(land1)|| this.inGrassland(land2)){
			this.turns_out = 0;
		}
		
		else{
			this.turns_out++;
			if (this.turns_out>7){
				this.health = this.health - 5;
			}
		}
	}
	
	
	
	public double dmgDone(){
		return 0;
	}
}


class Carnivore extends Animal{
	
	public Carnivore(int x, int y, int ts, double hlth, String name){
		super(x,y,ts,hlth,name);
	}
	
	public void turn(int flag, Grassland land1, Grassland land2, Animal an1, Animal an2){
		//flag is number of herbivores left
		if (flag==0){}
		
		else{
			
			if(this.getDist(an1.getPos())<=1 || this.getDist(an2.getPos())<=1){
				if(this.getDist(an1.getPos())<this.getDist(an2.getPos())){
					an1.kill();
				}
				else an2.kill();
			}
			
			else{
				if (this.inGrassland(land1) || this.inGrassland(land2)){
					double x = Math.random();
					if (x<0.25){}
					else{
						Animal an;
						if(this.getDist(an1.getPos())<this.getDist(an2.getPos())) an=an1;
						else an=an2;
						this.move(an.getPos(), 2);
					}
				}
				else{
					double x = Math.random();
					if (x<0.92){
						Animal an;
						if(this.getDist(an1.getPos())<this.getDist(an2.getPos())) an=an1;
						else an=an2;
						this.move(an.getPos(), 4);
					}
				}
				
			}
			
		}
	}
	
	
	public double dmgDone(){
		return 0;
	}
}



class Grassland{
	private final float[] center;
	private final float radius;
	private int grass;
	
	public Grassland(int x, int y, int r, int g){
		float[] c = {x, y};
		this.center = c;
		this.radius = r;
		this.grass = g;
	}
	
	public boolean in(Animal animal){
		if (animal.getDist(this.center)<radius){
			return true;
		}
		return false;
	}
	
	public void setGrass(int a){
		this.grass = a;
	}
	
	
	
	public int grassLeft(){
		return this.grass;
	}
	
	public float[] getCenter(){
		return this.center;
	}
	
}





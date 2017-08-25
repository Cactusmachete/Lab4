import java.util.Scanner;
import java.lang.Math;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		Comparator<Animal> compare = new AnimalComparer();
		PriorityQueue<Animal> queue= new PriorityQueue<Animal>(4, compare);
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter Total Final Time for Simulation:");
		int final_time = scan.nextInt();
		System.out.println("Enter x, y centre, radius and Grass Available for First Grassland:");
		Grassland land1 = new Grassland(scan.nextFloat(), scan.nextFloat(), scan.nextFloat(), scan.nextFloat());
		System.out.println("Enter x, y centre, radius and Grass Available for Second Grassland:");
		Grassland land2 = new Grassland(scan.nextFloat(), scan.nextFloat(), scan.nextFloat(), scan.nextFloat());
		System.out.println("Enter Health and Grass Capacity for Herbivores:");
		float health = scan.nextFloat();
		float grass_cap = scan.nextFloat();
		System.out.println("Enter x, y position and timestamp for First Herbivore:");
		Animal one = new Herbivore(scan.nextInt(), scan.nextInt(), scan.nextInt(), health, "First Herbivore", grass_cap);
		System.out.println("Enter x, y position and timestamp for Second Herbivore:");
		Animal two = new Herbivore(scan.nextInt(), scan.nextInt(), scan.nextInt(), health, "Second Herbivore", grass_cap);
		System.out.println("Enter Health for Carnivores:");
		health = scan.nextFloat();
		System.out.println("Enter x, y position and timestamp for First Carnivore");
		Animal three = new Carnivore(scan.nextInt(), scan.nextInt(), scan.nextInt(), health, "First Carnivore");
		System.out.println("Enter x, y position and timestamp for Second Carnivore:");
		Animal four = new Carnivore(scan.nextInt(), scan.nextInt(), scan.nextInt(), health, "Second Carnivore");
		
		System.out.println("The Simulation Begins -");
		queue.add(one);
		queue.add(two);
		queue.add(three);
		queue.add(four);
		
		int max_TS=0;
		if (one.getTS()>max_TS) max_TS=one.getTS();
		if (two.getTS()>max_TS) max_TS=two.getTS();
		if (three.getTS()>max_TS) max_TS=three.getTS();
		if (four.getTS()>max_TS) max_TS=four.getTS();
		int num_carn=2, num_herb=2;
		int turn=0;
		Random rand = new Random();
		
		
		while(turn<final_time && (num_herb>0 || num_carn>0)){
			Animal current = queue.remove();
			if (current instanceof Herbivore){
				current.turn(num_carn, land1, land2, three, four);
			}
			else current.turn(num_herb, land1, land2, one, two);
			
			current.setTS(rand.nextInt((final_time - (max_TS+1))) + max_TS);
			if (current.getTS()==final_time-1){
				current.kill();
			}
			else{
				if (current.getTS()>max_TS) max_TS=current.getTS();
			}
			
			System.out.println("It is " + current.getName());
			
			if (current.getHealth()>0){
				System.out.println("It's health is " + current.getHealth());
			}
			else{
				System.out.println("It is dead");
				if (current instanceof Herbivore){
					num_herb--;
				}
				else num_carn--;
			}
			queue.add(current);
			turn++;
			
			System.out.println();
		}
		
		
		
		
		
		
		
		
	}

}

class AnimalComparer implements Comparator<Animal>{
	int[] origin = {0,0};

	@Override
	public int compare(Animal o1, Animal o2) {
		if(o1.getTS()<o2.getTS()){
			return -1;
		}
		else if (o1.getTS()>o2.getTS()){
			return 1;
		}
		
		else{
			if (o1.getHealth()>o2.getHealth()){
				return -1;
			}
			
			else if(o1.getHealth()<o2.getHealth()){
				return 1;
			}
			
			else{
				if ((o1 instanceof Herbivore) && (o2 instanceof Carnivore )){
					return -1;
				}
				else if ((o1 instanceof Carnivore) && (o2 instanceof Herbivore )){
					return 1;
				}
				
				else{
					if (o1.getDist(origin) <o2.getDist(origin)){
						return -1;
					}
					else return 1;
				}
				
			}
		}
		
	}
	
	
}


abstract class Animal{
	protected int[] position;
	protected float health;
	final protected String name;
	protected int timestamp;
	
	public Animal(int x, int y, int ts, float hlth, String name){
		int[] pos = {x,y};
		this.position = pos;
		this.timestamp=ts;
		this.health = hlth;
		this.name = name;
	}
	
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
		this.position[0]=1000000000;
		this.position[1] = 1000000000;
		this.setTS(1000000000);
	}
	
	public int getTS(){
		return this.timestamp;
	}

	public float getHealth(){
		return this.health;
	}
	
	public String getName(){
		return this.name;
	}
	public void setTS(int a){
		this.timestamp = a;
	}
}




class Herbivore extends Animal{
	private final float capacity;
	private int turns_out=0;
	
	
	public Herbivore(int x, int y, int ts, float hlth, String name, float cap){
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
						landin.setGrass(landin.grassLeft()-this.capacity);
						this.health = (float) (this.health + 0.5*this.health);
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
						this.health = (float) (this.health + 0.2*this.health);
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
		
		if (this.health<=0){
			this.kill();
		}
	}

	
}


class Carnivore extends Animal{
	private int turns_away=0;
	
	public Carnivore(int x, int y, int ts, float hlth, String name){
		super(x,y,ts,hlth,name);
	}
	
	public void turn(int flag, Grassland land1, Grassland land2, Animal an1, Animal an2){
		//flag is number of herbivores left
		if (flag==0){
			if  (!(this.inGrassland(land1) || this.inGrassland(land2))){
				this.health = this.health - 60;
			}
			else{
				this.health = this.health - 30;
			}
		}
		
		else{
			
			if(this.getDist(an1.getPos())<=1 || this.getDist(an2.getPos())<=1){
				if(this.getDist(an1.getPos())<this.getDist(an2.getPos())){
					an1.kill();
					this.health = this.health + (2/3)*an1.health;
		
				}
				else {
					an2.kill();
					this.health = this.health + (2/3)*an2.health;
				}
				
				
			}
			
			else{
				if (this.inGrassland(land1) || this.inGrassland(land2)){
					double x = Math.random();
					if (x<0.25){
						this.health = this.health - 25;
					}
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
					else{
						this.health = this.health - 60;
					}
				}
				
			}
			
		}
		
		if(this.getDist(an1.getPos())<=5|| this.getDist(an2.getPos())<=5){
			this.turns_away =0;
		}
		else{
			this.turns_away++;
			if (this.turns_away>7){
				this.health = this.health-6;
			}
		}
		
		if (this.health<=0){
			this.kill();
		}
	}

	
}



class Grassland{
	private final float[] center;
	private final float radius;
	private float grass;
	
	
	public Grassland(float x, float y, float r, float g){
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
	
	public void setGrass(float a){
		this.grass = a;
	}
	
	
	
	public float grassLeft(){
		return this.grass;
	}
	
	public float[] getCenter(){
		return this.center;
	}
	
}





package marcovian;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class BMuller{
	private Random rand = new Random();
	public float Z0,Z1;
	BMuller(){
		float  U1 = rand.nextFloat(), U2 = rand.nextFloat();
		float R= (float) Math.sqrt((-2)*Math.log(U1)),
		O= (float) (2*Math.PI*U2);
		Z0 = (float) (R*Math.cos(O));
		Z1 = (float) (R*Math.sin(O));
	}
}
public class Main {
	static Logger  log;
	static{
		log = Logger.getLogger("");
		log.setLevel(Level.DEBUG);
		BasicConfigurator.configure();
	}
	static int[] hist(ArrayList<Double> rands){
		Collections.sort(rands);
		rands.get(rands.size()-1);
		int numberOfValuesAboveZero =  10*(int)Math.floor(rands.get(rands.size()-1));
		int numberOfValuesUnderZero = 10*(int)Math.abs(Math.floor(rands.get(0)));
		log.debug(numberOfValuesUnderZero +"<- 0 ->"+numberOfValuesAboveZero);
		//make a hist array to rettain the frequencies of breakdowns
		int[] x = new int[numberOfValuesAboveZero
		                  +numberOfValuesUnderZero+1];

		//populate vector
		for(double i:rands)
			x[(int)(Math.floor(i)*10+numberOfValuesUnderZero)]++;

		//print histogram
		for(int i:x)
			System.out.print(i+"\t");
		System.out.println("\n");

		return x;
	}

	static int[] mixhist(double[] rands){
		//print header
		for(int i=-19;i<120;i++)
			System.out.print(i+"\t");
		System.out.println("");

		int [] x = new int[140];
		//initialize
		for(int i=0;i<140;i++)
			x[i]=0;

		//populate vector
		for(double i:rands){
			try{ x[((int)(i*10))+69]++;}
			catch(Exception e){/*exceptional value, ignore*/}
		}

		//print histogram
		for(int i:x)
			System.out.print(i+"\t");
		System.out.println("\n");

		return x;
	}
	static float N(int u, double s){
		Random rand = new Random();
		return (float) (u+s*rand.nextGaussian());
	}
	static double[] genManyN(int howMany, int u, double s){
		Random rand = new Random();
		double []gauss = new double[howMany];
		for(int i=0;i<howMany;i++)
			gauss[i]=u+s*rand.nextGaussian();
		return gauss;
	}
	static HashMap<Float,Integer> mixgenN(int howMany,
			double a1,int u1, double s1,
			double a2, int u2, double s2,
			double a3, int u3, double s3, int breakdown){
		Random rand = new Random();
		HashMap<Float,Integer> gauss = new HashMap<Float,Integer>();

		for(int i=0;i<howMany;i++){
			double r = rand.nextDouble();
			if(r<a1){
				float z= (float) ((Math.floor(N(u1,s1)*breakdown))/breakdown);
				gauss.put(z,
						gauss.get(z)==null? 1 : gauss.get(z)+1);
				}
			else if(r<a2){
				float z= (float) ((Math.floor(N(u2,s2)*breakdown))/breakdown);
				gauss.put(z,
						gauss.get(z)==null? 1 : gauss.get(z)+1);
				}
			else {
				float z= (float) ((Math.floor(N(u3,s3)*breakdown))/breakdown);
				gauss.put(z,
						gauss.get(z)==null? 1 : gauss.get(z)+1);
				}
		}
		return gauss;
	}
	public static void main(String[] args) {
		int howMany=3000;
		int breakdown = 10;
		HistApplet.create("Mixed",mixgenN(
				howMany,
				0.3,5,1.2,	//N(pondere, medie, variatie)
				0.5,10,3,
				0.2,30,2.5,
				breakdown
		),breakdown);

		//generate BMuller numbers
		HashMap<Float,Integer> rands=new HashMap<Float,Integer>();
		for(int i=0;i<howMany/2;i++){
			BMuller bm = new BMuller();
			//System.out.println("Pair1: Z0="+ bm.Z0+ ", Z1="+bm.Z1);
			float z = (float) ((Math.floor(bm.Z0*breakdown))/breakdown);
			rands.put(
					z,
					rands.get(z)==null? 1 : rands.get(z)+1
			);
			
			z = (float) ((Math.floor(bm.Z1*breakdown))/breakdown);
			rands.put(
					z,
					rands.get(z)==null? 1 : rands.get(z)+1
			);
		}

		//HistApplet.create("BMuller gen", rands, breakdown);
		//generate shifted Gauss numbers
		for(int i=0;i<howMany;i++){
			float z = (float) ((Math.floor(N(1,0.2)*breakdown))/breakdown);
			rands.put(
					z,
					rands.get(z)==null? 1 : rands.get(z)+1
			);
		}
	}
}
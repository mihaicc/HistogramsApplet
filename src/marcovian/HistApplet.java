package marcovian;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JApplet;
import javax.swing.JFrame;



public class HistApplet extends JApplet {

	private static final long serialVersionUID = 8444545330101719000L;
	static int yUpperMargin,yLowerMargin=0,breakdown;
	static float xLeftMargin,xRightMargin,xLength,yLength;
	static HashMap<Float,Integer> histo;
	/**
	 * Launches an applet window with a histographical representation of the passed map
	 * @param name applet title
	 * @param histo mapping between x-value and y-value
	 */

	static void create(String name, HashMap<Float,Integer> h,int b){

		//sort histo by value, and get the hight of the Yaxis
		yUpperMargin = JavaUtils.sortByValue(h).entrySet().iterator().next().getValue();

		//sort histo by key, to get the width of the Xaxis and to properly iterate at printing
		histo = (HashMap<Float, Integer>) JavaUtils.sortByKey(h);
		Iterator<Entry<Float, Integer>> it = histo.entrySet().iterator();

		//set the max value with the first value, and iterate until the last value for the minvalue
		xRightMargin = it.next().getKey();
		xLeftMargin = xRightMargin;
		breakdown = b;
		while(it.hasNext()){
			xLeftMargin = it.next().getKey();
		}
		xLength = getLength(xLeftMargin,xRightMargin);
		yLength = getLength(yLowerMargin,yUpperMargin);
		JFrame f = new JFrame(name);
		f.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		JApplet histApplet = new HistApplet();
		f.getContentPane().add("Center", histApplet);
		histApplet.init();
		f.pack();
		f.setSize(new Dimension(1120, 600));
		f.show();
	}

	private static float getLength(float start, float stop) {
		if(start<0 && stop>0)
			return Math.abs(start)+1f/breakdown+ Math.abs(stop);
		else 
			return stop + start*-1;
	}

	boolean a=false;
	int offset=550;
	double t=1;
	double tn=2;
	double h=0.1;
	double y=0.75;

	public void init() {
		setBackground(Color.white);
		setForeground(Color.white);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if(!a){
			a=true;
			g2.setPaint(Color.gray);
			
			//			g2.setPaint(Color.red);g2.drawString("Euler ", 800, 100+56);
			//			g2.setPaint(Color.green);g2.drawString("Runge-Kutta ", 800, 100+44);

			int xDashes = 12,xScale=80;
			int yDashes = 10, yScale = 40;
			float xGraphStep = xScale*xDashes/(xLength*breakdown);
			float yGraphStep = yScale*yDashes/yLength;

			DecimalFormat df = new DecimalFormat("#.##");
			
			// X axis
			g2.draw(new Line2D.Double(30, offset-50, 1020, offset-50));
			
			//paint X dashes and grades
			for(int j=1;j<xDashes;j++){
				//grade
				g2.drawString( df.format((((float)xLength)/xDashes)*j +xLeftMargin)+"",  33 + j * xScale,  offset- 33);
				//dashe
				g2.draw(new Line2D.Double(50 + j * xScale,offset- 48, 50 + j * xScale, offset-52));
			}

			//find offset (where on the X axis should the Y axis be printed
			int xOffset=40;
			if(xLeftMargin<0 && xRightMargin>0) 	xOffset+= Math.abs(xLeftMargin)*breakdown * xGraphStep;

			// Y axis
			g2.draw(new Line2D.Double(10+xOffset,offset- 0, 10+ xOffset,offset- 450));
			
			//paint Y dashes and grades
			for (int j = 1; j < 10; j++) {
				//grade
				g2.drawString( (int)(((10-j)*(yUpperMargin/10f)))+"",  xOffset-17,  offset- 450 + j * yScale);
				//dashe
				g2.draw(new Line2D.Double(xOffset+8,offset- 450 + j * yScale, xOffset+12, offset-450 + j *yScale));
			}

			

			//Draw graph
			g2.setPaint(Color.red);
			
			int prevY = histo.get(xLeftMargin);
			for(float step=0;step<xLength*breakdown;){
				
				//round the float division result
				float round =  new BigDecimal(xLeftMargin+step/breakdown).setScale(1, RoundingMode.HALF_EVEN).floatValue();
				
				int nowY = histo.get(round)==null? 0:histo.get(round);
				g2.draw(new Line2D.Double(
						50+ step++*xGraphStep, offset - 50 - prevY*yGraphStep,
						50+ step*xGraphStep, offset - 50 - nowY*yGraphStep
				));
				prevY=nowY;
			}
		}
	}

}


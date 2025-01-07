package patientCare;

import sim.engine.*;
import sim.field.network.Edge;
import sim.portrayal.Inspector;
import sim.display.*;
import sim.portrayal.*;

import sim.portrayal.continuous.*;
import sim.portrayal.simple.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.*;
import sim.portrayal.network.*;

public class CareWithUI extends GUIState {
	
	public Display2D display;
	public JFrame displayFrame;
	ContinuousPortrayal2D centerPortrayal = new ContinuousPortrayal2D();
	NetworkPortrayal2D interactionsPortrayal = new NetworkPortrayal2D();

	public static void main(String[] args) {
		CareWithUI vid = new CareWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}

	public CareWithUI() {super(new Care(System.currentTimeMillis()));}
	public CareWithUI(SimState state) {super(state);}
	public static String getName() {return "Patient - doctor interactions";}

	private Care care = (Care) state; //do I still need this?

	public Object getSimulationInspectedObject() {
		return state;
	}

	public Inspector getInspector() {
		Inspector i = super.getInspector();
		// i.setVolatile(true);
		return i;
	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void setupPortrayals(){
	    Care care = (Care) state;
	    // tell the portrayals what to portray and how to portray them
	    centerPortrayal.setField( care.center );
	    
	    interactionsPortrayal.setField( new SpatialNetwork2D( care.center, care.visualPatients ) );
		
	    	//color to the edges
		  interactionsPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D() { public
		  void draw(Object object, Graphics2D graphics, DrawInfo2D info) { //Edge color
		  Edge estaLinea = (Edge)object; 
		  Patient patient = (Patient)estaLinea.getTo();
		  int visits = patient.getTotalCare();
		  int lineIntensity = 30;
		  int col = Math.min(Math.max(255-visits*lineIntensity, 0),255);
		  toPaint = new Color(col, col,col); 
		  fromPaint = new Color(col,col,col); 
		  super.draw(object, graphics, info); } });

		  //color to the patients
	    for(int i = 0; i < care.numPatients; i++) {
	    	Patient currentPatient = (Patient)care.patients.objs[i];
	    	centerPortrayal.setPortrayalForObject(currentPatient, new OvalPortrayal2D() {
	    		 public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
	    			 Patient patient = (Patient)object; 
	    			 double healthStatus = patient.H[(int)state.schedule.getSteps()]; 
	    			 int colIntensity = 25;
	    			 int miColor = (int)Math.min(Math.max(0, (healthStatus*colIntensity+100)), 250); 
	    			 paint = new Color(miColor, 250-miColor,60); 
	    			 //System.out.println(miColor);
	       			 scale = 2;
	    			 super.draw(object, graphics, info); } 	
	    	});
	    }
	    //make doctor bigger
	    centerPortrayal.setPortrayalForObject(care.doctor, new OvalPortrayal2D() {
   		 public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
   			 //Doctor doctor = (Doctor)object;
   			 scale = 2;
   			 paint = new Color(0,0,0);
   			 super.draw(object, graphics, info);
   		 }

	    });
		
	    // reschedule the displayer
	    display.reset();
	    display.setBackdrop(Color.white);
	// redraw the display display.repaint();
	    display.repaint();
	}
	
	public void init(Controller c) {
		super.init(c);
		
		display = new Display2D(600,600,this);
	    display.setClipping(false);
	    
	    displayFrame = display.createFrame();
	    displayFrame.setTitle("Un doctor y 30 pacientes");
	    c.registerFrame(displayFrame); // so the frame appears in the "Display" list displayFrame.setVisible(true);
	    displayFrame.setVisible(true);
	    display.attach( centerPortrayal, "Center" );
	    display.attach( interactionsPortrayal, "Interactions" );
	}

	public void quit() {
		super.quit();
		if (displayFrame!=null) displayFrame.dispose(); displayFrame = null;
		display = null;
	}

	public void finish() {
		super.quit();
		care.patients.clear();
		System.out.println("Total needs: "+care.totalProgress);
		System.out.println("Total interactions: "+care.totalInteractions);
		//System.out.println("Capacity usage " + care.capacity*care.weeks/care.totalInteractions);
		care.totalProgress = 0;
		care.totalInteractions = 0;
		System.out.println("Stopping simmulation");
	}
	
	public void load(SimState state)
	{
	super.load(state); setupPortrayals(); }

}

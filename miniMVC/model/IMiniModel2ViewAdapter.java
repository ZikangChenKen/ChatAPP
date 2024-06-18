package yc138_zc45.miniMVC.model;

import java.util.function.Supplier;

import javax.swing.JComponent;

/**
 * Mini model to view Adapter.
 */
public interface IMiniModel2ViewAdapter {
	
	/**
	 * Display the message
	 * @param msg the message we want to display
	 */
	public void displayMsg(String msg);
	
	
	public void displayNewMember(String member);
	
	/**
	 * Add component to the current view
	 * @param componentSupplier The component factory
	 * @param label A label to be displayed with the component
	 * @return 
	 */
	public Runnable addComponent(Supplier<JComponent> componentSupplier, String label);

}

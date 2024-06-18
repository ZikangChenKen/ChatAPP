package yc138_zc45.miniMVC.model;

import java.util.function.Supplier;

import javax.swing.JComponent;

/**
 * Adapter from mini to micro
 */
public interface IMicroAdapter {
	
	/**
	 * Add the component to the micro view.
	 * @param supplier
	 */
	void addToMicroView(Supplier<JComponent> supplier);
	
	/**
	 * Display message on the remote micro room screen
	 * @param msg
	 */
	void displayMsg(String msg);
}

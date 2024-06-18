package yc138_zc45.mainMVC.control;


import java.rmi.RemoteException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import common.network.messageReceiver.IInitialConnection;
import common.network.messageReceiver.INamedNetworkMessageReceiver;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIPortConfigWithBoundName;
import yc138_zc45.mainMVC.model.IMainModel2ViewAdapter;
import yc138_zc45.mainMVC.model.MainModel;
import yc138_zc45.mainMVC.view.IMainView2ModelAdapter;
import yc138_zc45.mainMVC.view.MainView;
import yc138_zc45.miniMVC.model.NameIDDyad;
import provided.config.impl.AppConfigChooser;
import provided.discovery.IEndPointData;
import provided.discovery.impl.model.DiscoveryModel;
import provided.discovery.impl.model.IDiscoveryModelToViewAdapter;
import provided.discovery.impl.view.DiscoveryPanel;
import provided.discovery.impl.view.IDiscoveryPanelAdapter;
//import provided.rmiUtils.examples.hello_discovery.client.model.ClientModel;
//import provided.rmiUtils.examples.hello_discovery.client.model.IModel2ViewAdapterClient;
//import yc138_cp43.client.model.ClientModel;
//import yc138_cp43.client.model.IModel2ViewAdapterClient;
//import provided.rmiUtils.examples.hello_discovery.client.view.HelloViewClient;
//import provided.rmiUtils.examples.hello_discovery.client.view.IView2ModelAdapterClient;
//import yc138_cp43.client.view.ClientView;
//import yc138_cp43.client.view.IView2ModelAdapterClient;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
//import provided.remoteCompute.client.model.taskUtils.ITaskFactory;
//import provided.remoteCompute.compute.ICompute;


/**
 * @author swong
 *
 */
public class MainController{
	/**
	 * The system logger to use. Change and/or customize this logger as desired.
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();
	
	/**
	 * The model in use
	 */
	private MainModel model;
	
	/**
	 * The view in use
	 */
	private MainView<NameIDDyad, INamedNetworkMessageReceiver> view;

	/**
	 * The Discovery server UI panel for the view
	 */
	private DiscoveryPanel<IEndPointData> discPnl;
	
	/**
	 * A self-contained model to handle the discovery server.   MUST be started AFTER the main model as it needs the IRMIUtils from the main model! 
	 */
	private DiscoveryModel<IInitialConnection> discModel;  // Replace "IRemoteStubType" with the appropriate for the application, i.e. the Remote type of stub in Registry)  

	/**
	 * The selected app configuration holding the configuration-dependent information.
	 * Using the simpler RMIPortConfig because this client is watch-only and not binding 
	 * anything into the local Registry and thus doesn't need a bound name to be defined.
	 * Technically, this client doesn't need the stub port either.
	 */
	private RMIPortConfigWithBoundName currentConfig;
	
	/**
	 * 3 possible app configs with different config names and port numbers.
	 */
	AppConfigChooser<RMIPortConfigWithBoundName> appChooser =  new AppConfigChooser<RMIPortConfigWithBoundName>( // Can add default choice index parameter here if desired
			new RMIPortConfigWithBoundName("yc", IRMI_Defs.STUB_PORT_SERVER, IRMI_Defs.CLASS_SERVER_PORT_SERVER, "Client-Server_port"),	
			new RMIPortConfigWithBoundName("ken", IRMI_Defs.STUB_PORT_CLIENT, IRMI_Defs.CLASS_SERVER_PORT_CLIENT, "Client-Client_port"),
			new RMIPortConfigWithBoundName("BoyNextDoor", IRMI_Defs.STUB_PORT_EXTRA, IRMI_Defs.CLASS_SERVER_PORT_EXTRA, "Client-Extra_port")
	);	
	
	/**
	 * Constructor of the class.   Instantiates and connects the model and the view plus the discovery panel and model.
	 */
	public MainController() {
		
		sysLogger.setLogLevel(LogLevel.DEBUG);  // For debugging purposes.   Default is LogLevel.INFO
		
		// Select the desired app configuration early so that any configuration-dependent
		// construction processes can use it.
		currentConfig = (RMIPortConfigWithBoundName) appChooser.choose(); // Have the user select a configuration.
		sysLogger.log(LogLevel.INFO, "Selected app config: "+currentConfig);
		
		discPnl = new DiscoveryPanel<IEndPointData>( new IDiscoveryPanelAdapter<IEndPointData>() {

			/**
			 * watchOnly is ignored b/c discovery model configured for watchOnly = true
			 */
			@Override
			public void connectToDiscoveryServer(String category, boolean watchOnly, Consumer<Iterable<IEndPointData>> endPtsUpdateFn) {
				// Ask the discovery model to connect to the discovery server on the given category and use the given updateFn to update the endpoints list in the discovery panel.
				discModel.connectToDiscoveryServer(category, endPtsUpdateFn);
			}

			@Override
			public void connectToEndPoint(IEndPointData selectedEndPt) {
				// Ask the discovery model to obtain a stub from a remote Registry using the info from the given endpoint 
				discModel.connectToEndPoint(selectedEndPt);
			}
			
		}, false, true);  // "Client" usage mode
		
		discModel = new DiscoveryModel<IInitialConnection>(sysLogger, new IDiscoveryModelToViewAdapter<IInitialConnection>() {

			@Override
			public void addStub(IInitialConnection stub) {
				sysLogger.log(LogLevel.INFO, "CONNECTED TO SOME STUB");
				model.connectToStub(stub);   // Give the stub obtained from a remote Registry to the model to process
			}
			
		});

		model = new MainModel(sysLogger, currentConfig, new IMainModel2ViewAdapter() {

			@Override
			public void displayMsg(String msg) {
				view.append(msg);
			}

			@Override
			public Runnable addComponent(String name, JComponent component) {
				return view.addComponentFac(name, new Supplier<JComponent>() {
					@Override
					public JComponent get() {
						return component;
					}
				});
			}

			@Override
			public void addConnection(INamedNetworkMessageReceiver con) {
				view.addUser(con);
			}

			@Override
			public void addRoom(NameIDDyad room) {
				view.addRoom(room);
			}

			@Override
			public void removeRoom(NameIDDyad roomInfo) {
				view.removeRoom(roomInfo);
			}

			@Override
			public void removeConnection(INamedNetworkMessageReceiver con) {
				view.removeUser(con);
			}
			
		});
		
		view = new MainView<NameIDDyad, INamedNetworkMessageReceiver>(new IMainView2ModelAdapter<NameIDDyad, INamedNetworkMessageReceiver>() {

			

			@Override
			public void login(String userName) {
				model.login(userName);
			}

			@Override
			public void createRoom(String name) {
				model.createRoom(name);
			}

			@Override
			public void invite(NameIDDyad room, INamedNetworkMessageReceiver userName) throws RemoteException {
				model.invite(room, userName);
			}

			@Override
			public void quit() {
				model.quit(0);
			}
			
		});
		
	}

	/**
	 * Starts the view then the model plus the discovery panel and model.  The view needs to be started first so that it can display 
	 * the model status updates as it starts.   The discovery panel is added to the main view after the discovery model starts. 
	 */
	public void start() {
		// start the main model.  THE MODEL MUST BE STARTED _BEFORE_  model.getRMIUtils() IS CALLED!!
		model.start();   // starts the internal IRMIUtils instance too.
		
		discPnl.start();  // start the discovery panel
		discModel.start(model.getRMIUtils(), currentConfig.name, currentConfig.boundName);
		//discModel.start(model.getRMIUtils());   // start the discovery model using the already started IRMIUtils instance.
		view.addCtrlComponent(discPnl);  // Add the discovery panel to the view's "control" panel.

		// start the main view.  Starting the view here will keep the view from showing before the discovery panel is installed.
		view.start();

	}

	/**
	 * Run the app.
	 * @param args Not used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//(new HelloAppClient()).start();
				try {
					(new MainController()).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}

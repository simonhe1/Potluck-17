	import java.awt.BorderLayout;
	import java.awt.Dimension; 
	import java.awt.Event;
	import java.awt.GridBagConstraints;
	import java.awt.GridBagLayout;
	import java.awt.GridLayout;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.awt.event.KeyEvent;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.IOException;
	import java.util.Scanner;

	import javax.swing.JButton;
	import javax.swing.JFileChooser;
	import javax.swing.JFrame;
	import javax.swing.JLabel;
	import javax.swing.JMenu;
	import javax.swing.JMenuBar;
	import javax.swing.JMenuItem;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;
	import javax.swing.JTextPane;
	import javax.swing.KeyStroke;

	public class MainMenu implements ActionListener,Observer{
		
		JFileChooser jfc;
		private JButton[][] _questions; 
		private JPanel _boardPanel,_teamPanel;
		private JFrame jfrm;
		private Model _model;
		private static int teamNumber = 0;
		
		public MainMenu(){
		//Back-end model where all the calculations are done
		_model = new Model();
		//Makes an observer for model which calls the update method every time turns is reset in the model 
		_model.setObserver(this);
		//Creates a 5x5 array of JButtons for the questions
		_questions = new JButton[5][5];
		// Create a frame
	    jfrm = new JFrame("A.S.I.A Jeopardy");
	    //FIle chooser
	    jfc = new JFileChooser();
	    //Set to exit
	    jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    //Size of frame
	    jfrm.setSize(1650, 1080);
	    jfrm.setLocationRelativeTo(null);
	    //Sets layout to allow for adding panels on the top,bottom,left,right, and center
	    jfrm.setLayout(new BorderLayout());
	    
		//MenuBar and choices
		JMenuBar menuBar = new JMenuBar();
	    JMenu FileMenu = new JMenu("File");
	    JMenu Help = new JMenu("Help");
	    JMenuItem HelpItem =new JMenuItem("About");
	    JMenuItem QuitItem = new JMenuItem("Quit");
	    JMenuItem OpenItem = new JMenuItem("Open");
	    JMenuItem TeamItem = new JMenuItem("Add Team");

	    //Adding File and Help    
	    menuBar.add(FileMenu);
	    //Alt+F: File
	    FileMenu.setMnemonic(KeyEvent.VK_F);
	    menuBar.add(Help);
	    //Alt+H: Help
	    Help.setMnemonic(KeyEvent.VK_H);
	    jfrm.add(menuBar);
	    jfrm.setJMenuBar(menuBar);
	   
	    //Setting hotkeys to the items
	    FileMenu.add(OpenItem);
	    OpenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Event.CTRL_MASK));
	    FileMenu.add(QuitItem);
	    QuitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,Event.CTRL_MASK));
	    FileMenu.add(TeamItem);
	    TeamItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,Event.CTRL_MASK));
	    Help.add(HelpItem);
	    HelpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.CTRL_MASK));
	    
	    //Calling Action Listener
	    QuitItem.addActionListener(this);
	    HelpItem.addActionListener(this);
	    TeamItem.addActionListener(this);
	    OpenItem.addActionListener(this);
	    
	    // Display the frame
	    jfrm.setVisible(true);		 
	 }
	
	//About action listener
	protected void AboutEvent()
	{	
		JTextPane panel = new JTextPane();
		panel.setPreferredSize(new Dimension(300,150));
		panel.setText( "This is for A.S.I.A's potluck event.");
		panel.setEditable(false);
		JOptionPane.showMessageDialog(null, panel,"About this application",JOptionPane.INFORMATION_MESSAGE);	
	}
	
	//Happens after file is selected
	protected void createAndPopulate(Scanner data)
	{
		//Using JPanel and grid layout to create the game frame
		_boardPanel = new JPanel();
		_boardPanel.setLayout(new GridLayout(6,5));
		//Category placement
		for(int category=0;category<5;category++)
		{ 
			_boardPanel.add(new JLabel(data.nextLine()));
		}
		//Placing row by column placement
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				//Point value text for button
				_questions[i][j] = new JButton("$"+(i+1)+"00");
				//Getting question from the file and adding it into Questions
				_model.getQuestions().add(new Questions(data.nextLine()));
				_boardPanel.add(_questions[i][j]);
				//Setting name of button equal to value for easier calling
				_questions[i][j].setName(Integer.toString(5*i+j));
				//Adding action listener for the buttons
				_questions[i][j].addActionListener(new PopupHandler(
				_model.getQuestions().get((i*5)+j).getQuestion(),_model,i,j));	
			}
		}
		//adds the 5x5 board to the center of the frame
		jfrm.add(_boardPanel,BorderLayout.CENTER);
		//Makes a team panel to store the team names and score
		_teamPanel = new JPanel();
		//Sets the layout so that when teams are added, they are centered right away
		_teamPanel.setLayout(new GridBagLayout());
		//adds the teams to the bottom of the jfrm
		jfrm.add(_teamPanel,BorderLayout.PAGE_END);
		
		//updates visually the frame
		jfrm.revalidate();
		jfrm.repaint();
	}
	
	//Opens a text file to read from
	protected void OpenEvent()
	{
		jfc.setDialogTitle("Choose a file to read: ");
		int jfcResult = jfc.showOpenDialog(null); //Pass null to center the dialog
		if(jfcResult == JFileChooser.APPROVE_OPTION)
		{
			try
			{			
				Scanner data = new Scanner(new FileReader(jfc.getSelectedFile()));
				createAndPopulate(data);
				data.close();
			}
			catch (FileNotFoundException ex)
			{
				JOptionPane.showMessageDialog(null,"File is not found.");
			} 
			catch (@SuppressWarnings("hiding") IOException e1) {
				JOptionPane.showMessageDialog(null, "Text file not chosen.");
				e1.printStackTrace();
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "No file chosen.");
		}
			
	}
	
	//Adds teams to the _teamPanel
	protected void addTeam(){
		String input = JOptionPane.showInputDialog(null,"What's your team name?");
		_model.addTeam(input);
		JLabel label = new JLabel("<html>"+input + "<br>Score: "+_model.getTeams().get(teamNumber).getScore()+"</html>");
		label.setPreferredSize(new Dimension(200,100));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = teamNumber;
		c.gridy = 0;
		_teamPanel.add(label,c);
		teamNumber++;
		jfrm.revalidate();
		jfrm.repaint();
	}
	
	//Updates the board visually
	public void update(){
		for(int i=0;i<5;i++){
			for(int j=0;j<5;j++){
				if(_model.getQuestions().get((i*5)+j).isEmpty()){_questions[i][j].setVisible(false);}
			}
		}
		
		for(int k=0;k<_model.getTeamSize();k++){
			JLabel temp = (JLabel)_teamPanel.getComponent(k);
			temp.setText("<html>"+_model.getTeams().get(k).getName()+"<br>Score: "+_model.getTeams().get(k).getScore()*100+"</html>");
		}

		jfrm.revalidate();
		jfrm.repaint();
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		switch(e.getActionCommand())
		{
			case "Quit":
				System.exit(0);
			break;
			case "About":
				AboutEvent();
			break; 
			case "Open":
				OpenEvent();
			break;
			case "Add Team":
				addTeam();
			break;
		}
	}		
}

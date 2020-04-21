	import javax.swing.JFrame;

public class GameRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int width = 800;
		int height = 975;
		
		JFrame g = new JFrame ("Welcome to connect 4");
		
		g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		g.setSize(width, height);
		
		Board gb = new Board(width, height-22);
		g.add(gb);
		g.setVisible(true);
	}

}

import java.util.Scanner;
public class game
{
	// three values represented in squares
	private char empty = ' ';
	private char p1 = 'X';
	private char p2 = 'O';
	private int dep; // gobal depth count

	// define the game board
	board gm_board;

	// Heuristic Array determining h value
	// based on possible wins/2
	private int[][] h_array = {
		{ 0, -10, -1000, -10000, -100000 }, // Minimize 'O', to improve cpu chance to win
		{ 10, 0, 0, 0, 0 }, //Maximize 'X'
		{ 100, 0, 0, 0, 0 }, //increase h as X has more oppurtunities towin
		{ 1000, 0, 0, 0, 0 },
		{ 10000, 0, 0, 0, 0 },
		{ 100000, 0, 0, 0, 0 }
	};
	
	// variables to hold heurtic values and respective square
	private int h_h[];
	private int h_sq[];
	
	// count number of nodes checked
	private int total_nodes;


	// construtor, reset grid
	public game( )
	{
		//create new instance of board
		gm_board = new board();
		for( int x = 0; x < 25; x++ )
			gm_board.setSquare( x, empty );

		h_h = new int[25];
		h_sq = new int[25];
	}

	// function to switch player
	private char other_player( char pl )
	{ return pl == p1 ? p2 : p1; }

	// function to mark a players move
	private void play( int sq, char player )
	{ gm_board.setSquare( sq, player ); }

	// function to initailize cpus turn or get players move
	//  also stores depth value
	private void move(  char Player, int move_num, int d )
	{
		Scanner sc = new Scanner( System.in );
		int square = 0; //initailize square value
		
		//determine best move for X( the cpu )
		if( Player == p1 )
		{
			Pair p = MinMax( Player, square, move_num, -25, 25,d );
			// make cpus move
			System.out.println( total_nodes + " nodes examined." );
			play( p.getFirst(), Player );
			System.out.println( "Move # " + move_num + " - X moves to " + p.getFirst());
		} else {
			//get the human players move
			do {
				System.out.println( "which square would you like to move: " );
				square = sc.nextInt();
				square--;
			} while( gm_board.getSquare(square) != empty );
			play(  square, Player );
		}
	}

	// Calculate the heuristic value for each state
	private int  Evaluate( char Player )
	{
		int x;
		int Heuristic = 0;
		// outer loop is based on # of possible wins
		for( x = 0; x < 12; x++ )
		{
			int y;
			// Get # of wins for player and oppenent to determine heuristic
			int Players = 0, Others = 0;
			// inner loop based on # needed to get in a row
			for( y = 0; y < 5; y++ )
			{
				char move = gm_board.eval( gm_board, x, y );
				if( move == Player )
					Players++;
				else if( move == other_player( Player ))
					Others++;
			}
			// Get the heuristic value from maxtrix
			Heuristic += h_array[Players][Others];
		}
		
		return Heuristic;
	}


	// Function to perform min-max with alpha beta pruning
	Pair MinMax( char Player, int Square, int move_num, int Alpha, int Beta, int depth )
	{
		int Best_Square = -1;
		int Moves = 0;
		int x;

		// Increment node count
		total_nodes++;

		// create a state space
		for( x = 0; x < 25; x++ )
		{
			
			if( depth <= 0 ) continue;
			if( gm_board.getSquare(x) == empty )
			{
				int Heuristic;
				int y;
				play( x, Player );
				Heuristic = Evaluate( Player );
				// remove the move after h-value is calculated
				play( x, empty );
				for( y = Moves-1; y>=0 &&	
					h_h[y] < Heuristic; y-- )
				{
					h_h[y+1] = h_h[y];
					h_sq[y+1] = h_sq[y];
				}

				h_h[y+1] = Heuristic;
				h_sq[y+1] = x;
				Moves++;
			}
		}

		// Evalulate heuristic values to determine best move
		//  also prune the tree
		for( x = 0; x < Moves; x++ )
		{
			// enforce depth policy ( do not go lower than the depth)
			int Score;
			int Sq = h_sq[x];
			char Winner;

			play( Sq, Player );

			Winner = gm_board.winner(gm_board);
			if( Winner == p1 )
				// subtrace the move_number by maxium moves
				Score = 25 - move_num;
			else if( Winner == p2 )
				Score = move_num - 25;
			else if(Winner == 'T' )
				Score = 0;
			else
				Score = MinMax( other_player( Player ), Square, move_num+1, Alpha, Beta, depth-1 ).getSecond();

			// Clear the move after it is evaluated
			play( Sq, empty );

			// start pruning tree
			if( Player == p1 ) {
				if( Score >= Beta ) {
					Pair p = new Pair( Sq, Score );
					return p;
				} else if( Score > Alpha ) {
					Alpha = Score;
					Best_Square = Sq;
				}
			} else {
				if( Score <= Alpha ) {
					Pair p = new Pair( Sq, Score );
					return p;

				} else if( Score < Beta ) {
					Beta = Score;
					Best_Square = Sq;
				}
			}
		}


		if( Player == p1 ) {
			Pair p = new Pair( Best_Square, Alpha );
			return p;
		} else {
			Pair p = new Pair( Best_Square, Beta );
			return p;
		}
	}

	// Function to display the current board
	private void display(board gm_board )
	{
		for( int x = 0; x < 25; x+= 5 )
		{
			if( x > 0 )
				System.out.println( "---+---+---+---+---\n" );
				System.out.println(" " + gm_board.getSquare(x) +" | " + gm_board.getSquare(x+1) + " | " + gm_board.getSquare(x+2) + " | " + gm_board.getSquare(x+3) + " | " + gm_board.getSquare(x+4));
		}

		System.out.println();
		System.out.println("----------------------");
	}

	// begin the game
	public void start()
	{
		char player = p1; //Set first player to X (cpu)
		int move_num = 1; //counts number of moves
		dep = 5; //start at a depth of 1
		// Loop until there is a winner or a tie
		System.out.println( "Pick a number in respect to square number, squares are valued like english left to right then down, in a 5x5 matrix" );


		while( gm_board.winner(gm_board) == empty )
		{
			display(gm_board);
			move( player, move_num, dep );
			player = other_player(player);
			move_num++;
			dep++;	
		}
		display(gm_board);
		System.out.println( gm_board.winner(gm_board)=='T' ? "game is a tie" : "The winner is " + gm_board.winner(gm_board) ); 
	}
	
	
}

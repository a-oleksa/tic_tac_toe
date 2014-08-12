public class board
{
	// set up the board as a single character array
	private char square[] = new char[25];

	// modify a square value
	public void setSquare( int sq, char player )
	{
		square[sq] = player;
	}

	// return value of a square
	public char getSquare( int sq ) { return square[sq]; }

	// define array which indicates squares nessacary to win
	int[][] five_row = {
		{ 0, 1, 2, 3, 4 },
		{ 5, 6, 7, 8, 9 },
		{ 10, 11, 12, 13, 14 },
		{ 15, 16, 17, 18, 19 },
		{ 20, 21, 22, 23, 24 },
		{ 0, 6, 12, 18, 24 },
		{ 4, 8, 12, 16, 20 },
		{ 0, 5, 10, 15, 20 },
		{ 1, 6, 11, 16, 21 },
		{ 2, 7, 12, 17, 22 },
		{ 3, 8, 13, 18, 23 },
		{ 4, 9, 14, 19, 24 }
	};

	// determine winner
	public char winner( board bd )
	{
		for( int x = 0; x < 12; x++ )
		{
			char poss_win = bd.square[five_row[x][0]];
			if( poss_win != ' ' &&
				poss_win == bd.square[five_row[x][1]] &&
				poss_win == bd.square[five_row[x][2]] &&
				poss_win == bd.square[five_row[x][3]] &&
				poss_win == bd.square[five_row[x][4]] )
			return poss_win;
		}
		for( int y = 0; y < 25; y++ )
		{
			if( bd.getSquare(y) == ' ' )
				return ' ';
		} 

		return 'T';
	}

	// function for evaluation of heuristic in game
	public char eval( board bd, int x, int y )
	{
		return bd.square[five_row[x][y]];
	}
}

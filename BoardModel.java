import java.util.Observable;

/**
 * A Board model holds the game itself and checks if a move is legal or not.
 * A board consists of a specific number of Squares defined in the constructor...
 * It extends Observable to make it visible for the boardView class.
 */
class BoardModel extends Observable
{
    private final int cols;
    private final int rows;
    private final ColorSt[][] aboard;

    private String result;
    private ColorSt turn = ColorSt.WHITE;

    private ColorSt player1 = null;
    private ColorSt player2 = null;

    private int empty = 0;

    public BoardModel()
    {
        cols = 8;
        rows = 8;
        aboard = new ColorSt[cols][rows];
        empty = 8 * 8;

        for (int y = 0; y < cols; y++)
        {
            for (int x = 0; x < rows; x++)
            {
                aboard[x][y] = null;
            }
        }

    }

    /**
     * Adds a piece to the board
     *
     * @param x     the x-coordinate. Between 0-7.
     * @param y     the y-coordinate. Between 0-7.
     * @param color the color.
     */
    void addPiece(int x, int y, ColorSt color)
    {

        if (x >= rows || x < 0 || y >= cols || y < 0)
        {
            System.out.println("Move out of range, try again!");
        }

        aboard[x][y] = color;
        setChanged();
        notifyObservers();
    }

    /**
     * Flips a piece
     *
     * @param x the x-coordinate. Between 0-7.
     * @param y the y-coordinate. Between 0-7.
     */
    void flipPiece(int x, int y)
    {

        if (aboard[x][y] == ColorSt.BLACK)
        {
            aboard[x][y] = ColorSt.WHITE;
        } else
        {
            aboard[x][y] = ColorSt.BLACK;
        }
    }

    /**
     * Counts the number of pieces with the specified color on the board.
     *
     * @param color what color to count.
     * @return the number of the colored pieces.
     */
    public int count(ColorSt color)
    {
        int num = 0;

        for (int y = 0; y < cols; y++)
        {
            for (int x = 0; x < rows; x++)
            {
                if (aboard[x][y] != null && aboard[x][y] == color)
                {
                    num++;
                }
            }
        }

        return num;
    }

    /**
     * Get the piece at the specified coordinate.
     *
     * @param x the x-coordinate. Between 0-7.
     * @param y the y-coordinate. Between 0-7.
     * @return returns the piece at the given position.
     */
    ColorSt getPieceAt(int x, int y)
    {
        if (x >= rows || x < 0 || y >= cols || y < 0)
        {
            return null;
        }
        return aboard[x][y];
    }

    /**
     * Checks if a given move is legal or not.
     *
     * @param x     the x-coordinate. Between 0-7.
     * @param y     the y-coordinate. Between 0-7.
     * @param color the specified color.
     * @return returns true or false.
     */
    public boolean isLegalMove(int x, int y, ColorSt color)
    {
        if (this.getPieceAt(x, y) != null)
        {
            return false;
        }

        ColorSt intSquare;
        int a, b, i, j, num;

        //Check in all four directions
        for (i = -1; i <= 1; i++) //Goes from -1 to 1. Two directions. (0 not included)
        {
            for (j = -1; j <= 1; j++) //Goes from -1 to 1. Two directions. (0 not included)
            {
                if (!(j == 0 && i == 0)) //Can't check in the 0 direction. If j and i is NOT 0 then...
                {
                    a = x;
                    b = y;
                    num = 0;

                    do
                    {
                        a = a + i; //a = a+i; a+(-1), a+0, a+1 (a = row)
                        b = b + j; //b = b+j; b+(-1), b+0, b+1 (a = column)


                        intSquare = this.getPieceAt(a, b); //returns the status of this piece

                        if (intSquare != null && intSquare != color) //
                        {
                            num++; //Add one to num.
                        }

                        if (intSquare != null && intSquare == color) //If there's a piece and its WHITE...
                        {
                            if (num > 0) //
                            {
                                return true; // This is the only thing that returns true
                            } else
                            {
                                intSquare = null; //Else set temp to null
                            }
                        }
                    }
                    while (intSquare != null); //Do this while initSquare is not EMPTY...
                }
            }
        }

        return false;  //Return false if direction is equal to 0.

    }

    /**
     * Checks if the given color has any valid moves left.
     *
     * @param color what color to search after.
     * @return return the number of valid moves
     */
    int validMoves(ColorSt color)
    {
        int i;
        int j;
        int num = 0;

        for (i = 0; i < 8; i++)
        {
            for (j = 0; j < 8; j++)
            {
                if (isLegalMove(i, j, color))
                {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Resembles the isLegalMove but this actually makes a move
     *
     * @param x     the x-coordinate. Between 0-7.
     * @param y     the y-coordinate. Between 0-7.
     * @param color the color to check for.
     */
    public void doMove(int x, int y, ColorSt color)
    {
        int a;
        int b;
        int i;
        int j;
        int num;
        ColorSt intSquare;

        this.addPiece(x, y, color);

        //Check in all four directions
        for (i = -1; i <= 1; i++) //Goes from -1 to 1. Two directions. (0 not included)
        {
            for (j = -1; j <= 1; j++) //Goes from -1 to 1. Two directions. (0 not included)
            {
                if (!(j == 0 && i == 0)) //Can't check in the 0 direction. If j and i is NOT 0 then...
                {
                    a = x;
                    b = y;
                    num = 0;

                    do
                    {
                        a = a + i; //Adds one to the coordinate/direction a
                        b = b + j; //Adds one to the coordinate/direction b

                        intSquare = this.getPieceAt(a, b);

                        if (intSquare != null) //If temp is either black or white then....
                        {
                            if (intSquare != color)
                            {
                                num++;
                            } else
                            {
                                if (num > 0)
                                {
                                    a = x + i;    //Adds one to the x coordinate(direction)
                                    b = y + j;    //Adds one to the y coordinate(direction)

                                    intSquare = this.getPieceAt(a, b);

                                    while (intSquare != color)
                                    {
                                        flipPiece(a, b);

                                        a = a + i;
                                        b = b + j;
                                        intSquare = this.getPieceAt(a, b);
                                    }
                                }
                                intSquare = null;
                            }
                        }
                    }

                    while (intSquare != null);
                }
            }
        }

        if (color == ColorSt.WHITE)
        {
            if (validMoves(ColorSt.BLACK) > 0)
            {
                turn = ColorSt.BLACK;
            }
        }

        if (color == ColorSt.BLACK)
        {
            if (validMoves(ColorSt.WHITE) > 0)
            {
                turn = ColorSt.WHITE;
            }
        }

        if (isEndOfGame())
        {
            int countBlack;
            int countWhite;

            if ((countWhite = count(ColorSt.WHITE)) > (countBlack = count(ColorSt.BLACK)))  //If there are more white pieces...
            {
                result = "White wins!";
                setChanged();
                notifyObservers();
            } else if (countWhite < countBlack)
            {
                result = "Black wins!";
                setChanged();
                notifyObservers();
            } else if (countBlack == countWhite)
            {
                result = "It's a tie";
                setChanged();
                notifyObservers();
            }
        }

        setChanged();
        notifyObservers();
    }


    /**
     * Checks if the game has reached the end.
     *
     * @return returns true or false.
     */
    boolean isEndOfGame()
    {
        if (empty == 0)
        {
            return true;
        } else if (this.count(ColorSt.BLACK) == 0 || this.count(ColorSt.WHITE) == 0)
        {
            return true;
        } else if (this.validMoves(ColorSt.BLACK) == 0 && this.validMoves(ColorSt.WHITE) == 0)
        {
            return true;
        }

        return false;
    }

    /**
     * Gets the current result of the game.
     *
     * @return a result
     */
    public String getResult()
    {
        return result;
    }

    /**
     * Gets the current turn.
     *
     * @return the color that has the turn.
     */
    public ColorSt getTurn()
    {
        return turn;
    }

    /**
     * Gets the board.
     *
     * @return the current board.
     */
    public ColorSt[][] getBoard()
    {
        return aboard;
    }

    /**
     * Sets the color
     *
     * @param aColor a color
     */
    public void setColor(ColorSt aColor)
    {
        if (aColor == ColorSt.WHITE)
        {
            player1 = aColor;
        }

        if (aColor == ColorSt.BLACK)
        {
            player2 = aColor;
        }
    }

    /**
     * Gets the color.
     *
     * @return a color.
     */
    public ColorSt getColor()
    {
        if (player1 == null)
        {
            return player2;
        } else if (player2 == null)
        {
            return player1;
        } else
        {
            return null;
        }
    }

    /**
     * Sets the winner
     *
     * @param winner the winner of the game.
     */
    public void setWinner(String winner)
    {
        result = winner;

        setChanged();
        notifyObservers();
    }

    /**
     * Initializes the game. Notifies the update method of the observer.
     */
    public void initGame()
    {
        addPiece(3, 3, ColorSt.WHITE);
        addPiece(3, 4, ColorSt.BLACK);
        addPiece(4, 3, ColorSt.BLACK);
        addPiece(4, 4, ColorSt.WHITE);

        setChanged();
        notifyObservers();
    }

    /**
     * Clears or resets the board. Notifies the update method of the observer.
     */
    public void clearBoard()
    {
        for (int y = 0; y < cols; y++)
        {
            for (int x = 0; x < rows; x++)
            {
                aboard[x][y] = null;
            }
        }

        turn = ColorSt.WHITE;
        result = null;

        setChanged();
        notifyObservers();
    }

}






package edu.utep.cs.cs3331.sudoku;

import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JLabel;

import edu.utep.cs.cs3331.GUI.dialog.SudokuDialog;

/** An abstraction of Sudoku puzzle. */
public class Board {
    /**
     *
     */
    private PrintStream out = System.out;
    public int[][] table;
    public int[][] completeBoard;
    private static int No_Input = 0;
    private boolean [][] truthTable;
    private boolean solvable = false;
    private int counter =0;
    private int inserted = 0;
    int counterN = 0;
    int counterF =0;
    //private int caseNum = 0;
    /** Size of this board (number of columns/rows). */
    public int size;

    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;
        this.table = new int [size][size];
        this.truthTable = new boolean[size][size];

    }


    /** Return the size of this board. */

    public int size() {
        return size;
    }

    /**
     *
     * @return
     */
    public int [][] getTable(){
        return table;
    }
    //Partially fill a board of size 4 with 4 digits
    // Partially fill a board of size 9 with 17 digits

 public void partialFill(){
    	
    	if(size == 9){
    		partialFillN();
    	}
    	else{
    		System.out.println("FOUR FILL");
    		int min =0;int max =size-1; //input limits
    		int mink=1; int maxk = size;
    		 int inserted = 4;
    		Random r = new Random();
    		int randomX = r.nextInt(max-min+1)+min;
    		System.out.println("X "+randomX);
    		int randomY = r.nextInt(max-min+1)+min;
    		System.out.println("Y "+randomY);
    		int randomK = r.nextInt(maxk-mink+1)+mink;
    		System.out.println("K "+randomK);


    		if (table[randomX][randomY] == No_Input){//if position is empty
    			if(sudokuLaw(randomX,randomY,randomK)){//if input is sudokulaw
    				///////if leads to a sudoku solution
    				table[randomX][randomY]= randomK;
    				counterF = counterF+1;
    				System.out.println(counterF);
    				
    				if(counterF != inserted){
    					partialFill();
    				}
    			}
    			//empty but not sudoku law
    			if(counterF != inserted){
    				System.out.println(counterF +"second");
    				partialFill();
    				counterF = counterF+1;
    			}
    		}
    		//Not an empty positon, DO NOTHING
    		if(table[randomX][randomY] != No_Input){
        		System.out.println("already placed here");
        		if(counterF <=inserted){
        			
        		}
        	}
    	}
    }
    public void partialFillN(){
    	
    	System.out.println("NINE FILL");
    	int min =0;int max =size-1; //input limits
    	int mink=1; int maxk = size;
    	 int insertedN = 17;

    	Random r = new Random();
    	int randomX = r.nextInt(max-min+1)+min;
    	System.out.println("X "+randomX);
    	int randomY = r.nextInt(max-min+1)+min;
    	System.out.println("Y "+randomY);
    	int randomK = r.nextInt(maxk-mink+1)+mink;
    	System.out.println("K "+randomK);


    	if (table[randomX][randomY] == No_Input){//if position is empty
    		if(sudokuLaw(randomX,randomY,randomK)){//if input is sudokulaw
    			///////if leads to a sudoku solution
    		//	if(counterN <= insertedN){
    				System.out.println("counter for "+counterN+ "is less than "+insertedN);
    				table[randomX][randomY]= randomK;
    				System.out.println("inserted"+randomK);

    				counterN= counterN+1;
    				
    			//}

    				if(counterN != insertedN){
    					partialFill();
    				}
    			

    		}
    		//empty but not sudoku law
    		if(counterN != insertedN){
    			partialFillN();
    			counterN = counterN+1;
    		}
    	}
    	if(table[randomX][randomY] != No_Input){
    		System.out.println("already placed here");
    		if(counterN <=insertedN){
    			
    		}
    	}
    	
    	//Not an empty positon, DO NOTHING
    }

    public boolean check(){
        if(isSolved()){
            return true;
        }for(int row = 1;row<size;row++){
            for(int col = 1; col<size;col++){
                if(table[row][col]==0) {
                    return true;
                }
                for(int k=1;k<=size;k++) {
                    if(table[row][col] !=0 && sudokuLaw(row,col,k)) {
                        return true;
                    }
                    if(!sudokuLaw(row,col,k)&&!isSolved()) {
                        return true;
                    }
                }
                return false;
            }
        }

        return false;
    }
//------------------------------------
    public boolean solve() {

        if(isSolved()){
            return true;
        }
        for(int row = 0;row<size;row++){
            for(int col = 0; col<size;col++){
                if(table[row][col] != No_Input){
                    continue;
                }
                for(int k = 1;k<=size;k++){

                    if(sudokuLaw(row,col,k)){
                        table[row][col] = k;
                        if(solve()) {
                            return true;
                        }else {
                            table[row][col] = No_Input;
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }
    //-----------------------------
    /**
     *
     * @param x
     * @param y
     * @param v
     */
    public void insert(int x, int y, int v){
        if(x>size || y > size || v > size || x < 0 || y < 0 ) {

            return;
        }
        if(v==No_Input || sudokuLaw(x,y,v)){
            this.table[x][y] = v;
            this.truthTable[x][y] = true;

        }
    }
    /**
     *
     * @param x
     * @param y
     * @param v
     * @return
     */
    public boolean sudokuLaw(int x, int y, int v){


        for (int jj = 0; jj < size; jj++) {
            if (table[x][jj] == v) {

                return false;
            }
        }
        for (int ii = 0; ii < size; ii++) {
            if (table[ii][y] == v) {

                return false;
            }
        }
        int boxRow = x - x % (int)Math.sqrt(size);
        int boxColumn = y - y % (int)Math.sqrt(size);

        for (int ii = 0; ii < (int)Math.sqrt(size); ii++) {
            for (int jj = 0; jj < (int)Math.sqrt(size); jj++) {
                if (table[boxRow + ii][boxColumn + jj] == v) {

                    return false;
                }
            }
        }
        return true;
    }
    /**
     *
     * @return
     */
    public boolean isSolved() {
        for(int i = 0; i<truthTable.length; i++) {
            for(int k = 0; k<truthTable.length; k++){
                if(!truthTable[i][k]) {
                    return truthTable[i][k];
                }
            }
        }
        return true;
    }


}

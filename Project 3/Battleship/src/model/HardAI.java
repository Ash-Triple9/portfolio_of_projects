package model;

import java.util.Random;

public class HardAI {
    private Random random = new Random();
    private int lastHitX = -1;
    private int lastHitY = -1;
    private boolean lastHitSuccessful = false;
    private boolean searching = false;
    private int searchDirection = 0; // 0 - no direction, 1 - up, 2 - down, 3 - left, 4 - right
    private int[][] board = new int[10][10];
    private boolean vertical;
    
    private int xDir = 4;
    private int yDir = 4;
    public HardAI() {
        // Initialize the board array with 0s
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = 0;
            }
        }
    }
   
    public int[] getXYCoords() {
    	int[] result = new int[2];
    	if(lastHitX == -1) {
    		result[0]=random.nextInt(10);
    		result[1]=random.nextInt(10);
    		return result;
    	}
    
        if (searching) {
        	if(board[lastHitX][lastHitY] == 1) {
        		int[] x = checkForEmpty(lastHitX,lastHitY);
        		result[0] = x[0];
        		result[1] = x[1];
        		return result;
        	
        	}
        	
                	
        if(board[lastHitX][lastHitY] == 2) {
        	
        	if(lastHitX ==9) {
				int x, y;
    			 do {
    		        x = random.nextInt(10);
    		        y = random.nextInt(10);
    		    } while (board[x][y] != 0); // Check if the square is already targeted
    		    // Set the result to the valid random coordinates
    		    result[0] = x;
    		    result[1] = y;
    		 
    		    return result;
			}
     		
        	if(lastHitX !=9 && board[lastHitX+1][lastHitY] !=2&& board[lastHitX+1][lastHitY] !=9) {
  
    			result[0] = lastHitX+1;
    			result[1] = lastHitY;
    			return result;
    		}else if(lastHitX !=0 && board[lastHitX-1][lastHitY] != 2&& board[lastHitX-1][lastHitY] !=9) {
    		
    			result[0] = lastHitX-1;
    			result[1] = lastHitY;
    			return result;
    		}
    		else {
    			if(board[lastHitX][lastHitY] == 2) {
    			    if(lastHitX != 0 && board[lastHitX - 1][lastHitY] == 0) {
    			        result[0] = lastHitX - 1;
    			        result[1] = lastHitY;
    			        return result;
    			    } else if(lastHitX != 9 && board[lastHitX + 1][lastHitY] == 0) {
    			        result[0] = lastHitX + 1;
    			        result[1] = lastHitY;
    			        return result;
    			    } else {
    			        while(lastHitX != 0 && board[lastHitX - 1][lastHitY] == 2) { // move left until not 2
    			            lastHitX = lastHitX - 1;
    			        }
    			        if(lastHitX == 0 || board[lastHitX - 1][lastHitY] == 9) { // if leftmost == 9
    			            while(lastHitX != 9 && board[lastHitX + 1][lastHitY] == 2) { // move right
    			                lastHitX = lastHitX + 1;
    			            }
    			            if(lastHitX != 9 && board[lastHitX + 1][lastHitY] != 9) { // if rightmost != 9
    			                result[0] = lastHitX + 1;
    			                result[1] = lastHitY;
    			                return result;
    			            } else {
    			                int x, y;
    			                do {
    			                    x = random.nextInt(10);
    			                    y = random.nextInt(10);
    			                } while (board[x][y] != 0); // Check if the square is already targeted
    			                
    			                // Set the result to the valid random coordinates
    			                result[0] = x;
    			                result[1] = y;
    			                
    			                return result;
    			            }
    			        } else {
    			            result[0] = lastHitX - 1;
    			            result[1] = lastHitY;
    			            return result;
    			        }
    			    }
    			}

   
    		}
    		
        }
     	
             if(board[lastHitX][lastHitY] == 3) {
            	 if(lastHitY != 0 && board[lastHitX][lastHitY-1] == 0) {
            		 
            		 result[0] = lastHitX;
            		 result[1] = lastHitY -1;
            		 return result;
            	 }else if(lastHitY != 9 && board[lastHitX][lastHitY+1] == 0) {
            		 result[0] = lastHitX;
            		 result[1] = lastHitY +1;
            		 return result;
            	 }else {
            	 
                		
              while(lastHitY != 0 && board[lastHitX][lastHitY-1] == 3) { // move up until not 3 
            	  lastHitY = lastHitY-1;
              }
              if(lastHitY==0 || board[lastHitX][lastHitY-1]==9) { // if highest == 9
            	  while(lastHitY!=9 && board[lastHitX][lastHitY+1] == 3) {// move down 
            		  lastHitY = lastHitY+1;
            	  }
            	  if(lastHitY != 9 && board[lastHitX][lastHitY+1] != 9) { // if lowest != 9
            		  
            		  result[0] = lastHitX;
            		  result[1] = lastHitY+1;
            		  return result;
            	  }else {
            		  int x, y;
  	     			 do {
  	     		        x = random.nextInt(10);
  	     		        y = random.nextInt(10);
  	     		    } while (board[x][y] != 0); // Check if the square is already targeted
  	     		
  	     		    // Set the result to the valid random coordinates
  	     		    result[0] = x;
  	     		    result[1] = y;
  	     		 
  	     		    return result;
            	  }
              }else {
            	  result[0] = lastHitX;
            	  result[1] = lastHitY - 1;
            	  return result;
              }
             }
             }
        
        } 	
             int x, y; // all failed
  			 do {
  		        x = random.nextInt(10);
  		        y = random.nextInt(10);
  		    } while (board[x][y] != 0); // Check if the square is already targeted
  			
  		    // Set the result to the valid random coordinates
  		    result[0] = x;
  		    result[1] = y;
  		 
  		    return result;      		
             
    }


 private int[] checkForEmpty(int i, int j) {
int[] check = new int[2];
check[0] =0;
check[1] =0;

	  if(i !=0 && board[i-1][j] != 9 ) {
		  check[0] = i-1;
		  check[1] = j;

		  
		  return check;
	  }
	  else if(j !=0 && board[i][j-1] != 9) {
		  check[0] = i;
		  check[1] = j-1;

		  return check;
	  }
	  else if(i !=9 && board[i+1][j] != 9) {
		  check[0] = i+1;
		  check[1] = j;

		  return check;
	  }
		
	  else if(j !=9 && board[i][j+1] != 9) {
		  check[0] = i;
		  check[1] = j+1;

		  return check;
	  }
	return check;
	 

 }


    public void reportHit(int x, int y) {
    	searching = true;

    	board[x][y] = 1;
    	if((lastHitY -1== y || lastHitY +1 == y) && lastHitX == x) {
    		
    		board[x][y]=3;
    		board[x][lastHitY]=3;
    	}else if((lastHitX -1 == x || lastHitX +1 == x ) && lastHitY == y) {
    		//x != lastHitX&& lastHitX !=-1 && lastHitY == lastHitY
       		board[x][y] =2;
    		board[lastHitX][y]= 2;
    	}

    
        lastHitX = x;
 
        lastHitY = y;
    	
        lastHitSuccessful = true;
        if (!searching) {
            searching = true;
            // init search direction randomly
           // searchDirection = random.nextInt(4) + 1; // 1 to 4
        }
    }

    public void reportMiss(int x, int y) {

    	board[x][y] = 9;// hit but a miss
        lastHitSuccessful = false;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
            	
            	}
            	
            }

       
        
    }
}




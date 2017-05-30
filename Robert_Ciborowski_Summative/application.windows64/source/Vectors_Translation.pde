/*
 * Name: Translation Vector
 * Date: 22/05/2016
 * Author: Michael Petres and Robert Ciborowski
 * Description: Implements a translation vector.
*/

class Vector_Translate extends Vector {
 

  // This is the constructor.
  Vector_Translate() {
    
  }

  // The following 2 methods compute the next set of coordinates during a move.
  int getHorizontalMovement(int timing) {
    float vectDisplacement = timing * speed;
    float displacement = cos(radians(getDirection())) * vectDisplacement;
    return (int) displacement;
  }
  
  int getVerticalMovement(int timing) {
    float vectDisplacement = timing * speed;
    float displacement = sin(radians(getDirection())) * vectDisplacement;
    return (int) displacement;
  }
}
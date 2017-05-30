/*
 * Name: Scaling Vector
 * Date: 22/05/2016
 * Author: Mr. Petres & Robert Ciborowski
 * Description: Implements a vector.
*/

// The evctor class.
class Vector_Scale extends Vector {
  // The only property (magnitude as a float).
  float scaleMagnitude;
  
  // This is the constructor.
  Vector_Scale(float setScaleMagnitude) {
    super();
    scaleMagnitude = setScaleMagnitude;
  }

  // This returns scaling based on time.
  float getScaling(int timing) {
    return scaleMagnitude * timing;
  }
  
  // This modifies the scaling.
  void increaseMagnitude(float incr) {
    scaleMagnitude += incr;
  }

  // returns 'true' if "pix" is less than the current magnitude
  boolean moving(int pix) {
    if (magnitude == -1 || pix < magnitude) {
      return false;
    }
    return true;
  }
}
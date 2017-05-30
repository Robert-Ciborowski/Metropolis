/*
 * Name: Vector
 * Date: 22/05/2016
 * Author: Michael Petres and Robert Ciborowski
 * Description: Implements a vector.
*/

class Vector {

  // This is the direction (uses degrees). 0 would be 3 o'clock on an analog clock.
  float direction;
  // This is the magnitude.
  float magnitude;
  // This is the speed (meant to be used as pixels per millisecond).
  float speed;

  // The constructor.
  Vector() {

  }
  
  // This sets details.
  void setDetails(float setDirection, float setMagnitude, float setSpeed) {
    direction = setDirection;
    magnitude = setMagnitude;
    speed = setSpeed;
  }

  // This returns properties.
  float getMagnitude() {
    return magnitude;
  }

  float getDirection() {
    return direction;
  }

  int getHorizontalMovement(int timing) {
    throw new RuntimeException("Use a tranlsation vector and not a regular vector!");
  }

  int getVerticalMovement(int timing) {
    throw new RuntimeException("Use a tranlsation vector and not a regular vector!");
  }

  // returns 'true' if "pix" is less than the current magnitude
  boolean moving(int pix) {
    if (magnitude == -1 || pix < magnitude) {
      return false;
    }
    return true;
  }
}
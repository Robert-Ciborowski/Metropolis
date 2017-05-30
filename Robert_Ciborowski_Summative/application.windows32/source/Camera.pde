/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: A class for a camera.
*
*/

// This setsthe widths used for edge scrolling for all cameras.
int globalWidthOfLeftScrollingBar = 40;
int globalWidthOfRightScrollingBar = 40;
int globalWidthOfTopScrollingBar = 75;
int globalWidthOfBottomScrollingBar = 90;

// This is the camera class.
class Camera {
  // These are the properties of the camera.
  // This is a translation vector and is used for moving the camera.
  Vector_Translate translationVector = new Vector_Translate();
  // This stores the coordinates, animate time, speed and other properties.
  int coordinatesOfTopLeft[];
  boolean animating;
  int coordinatesOfOldMouse[];
  int timeOfAnimationStart;
  float cameraSpeed;
  boolean rightClick;

  // This is the only constructor.
  Camera(int setX, int setY, float setCameraSpeed) {
    // This sets the properties.
    coordinatesOfTopLeft = new int[2];
    coordinatesOfTopLeft[0] = setX;
    coordinatesOfTopLeft[1] = setY;
    coordinatesOfOldMouse = new int[2];
    animating = false;
    rightClick = false;
    timeOfAnimationStart = 0;
    cameraSpeed = setCameraSpeed;
  }

  // This returns positioning.
  int xPosition() {
    return coordinatesOfTopLeft[0];
  }

  int yPosition() {
    return coordinatesOfTopLeft[1];
  }

  // This fixes the camera position to the map.
  void fixCameraPosition() {
    if (coordinatesOfTopLeft[0] < widthOfTiles / 2) {
     coordinatesOfTopLeft[0] = widthOfTiles / 2; 
    }
    if (coordinatesOfTopLeft[1] < heightOfTiles / 2) {
     coordinatesOfTopLeft[1] = heightOfTiles / 2; 
    }
    if (coordinatesOfTopLeft[0] > (currentMap.getWidth() / 2) * widthOfTiles - width) {
     coordinatesOfTopLeft[0] = (currentMap.getWidth() / 2) * widthOfTiles - width; 
    }
    if (coordinatesOfTopLeft[1] > (currentMap.getHeight() / 2) * heightOfTiles - height) {
     coordinatesOfTopLeft[1] = (currentMap.getHeight() / 2) * heightOfTiles - height; 
    }
  }

 // This updates the camera.
  void update() {
    // This is used for right-click camera movement.
    if (mousePressed == true && mouseButton == RIGHT && rightClick == false) {
      // This triggers right-click movement.
      rightClick = true;
      coordinatesOfOldMouse[0] = mouseX;
      coordinatesOfOldMouse[1] = mouseY;
    } else if (rightClick == true) {
      // This updates the camera's position based on right-click movement.
      coordinatesOfTopLeft[0] += (coordinatesOfOldMouse[0] - mouseX);
      coordinatesOfTopLeft[1] += (coordinatesOfOldMouse[1] - mouseY);
      coordinatesOfOldMouse[0] = mouseX;
      coordinatesOfOldMouse[1] = mouseY;
      fixCameraPosition();
      currentMap.updateTileVertexes();
      if ((mousePressed == false || mouseButton != RIGHT)) {
        rightClick = false;
      }
    } else if (screens[currentScreen].type.equals("gameplay") && screens[currentScreen].ifMouseIsOnAMenu() == false) {
      // This is used for edge-scrolling and checks to see if the camera should scroll based on if the cursor is within an area.
      if (mouseX < globalWidthOfLeftScrollingBar) {
        if (mouseY < globalWidthOfTopScrollingBar) {
          translationVector.setDetails(225, 1, cameraSpeed);
        } else if (mouseY > height - globalWidthOfBottomScrollingBar) {
          translationVector.setDetails(135, 1, cameraSpeed);
        } else {
          translationVector.setDetails(180, 1, cameraSpeed);
        }
        toggleAnimating();
      } else if (mouseX > width - globalWidthOfRightScrollingBar) {
        if (mouseY < globalWidthOfTopScrollingBar) {
          translationVector.setDetails(315, 1, cameraSpeed);
        } else if (mouseY > height - globalWidthOfBottomScrollingBar) {
          translationVector.setDetails(45, 1, cameraSpeed);
        } else {
          translationVector.setDetails(0, 1, cameraSpeed);
        }
        toggleAnimating();
      } else if (mouseY < globalWidthOfTopScrollingBar) {
        translationVector.setDetails(270, 1, cameraSpeed);
        toggleAnimating();
      } else if (mouseY > height - globalWidthOfTopScrollingBar) {
        translationVector.setDetails(90, 1, cameraSpeed);
        toggleAnimating();
      } else {
        animating = false;
      }
      if (animating == true) {
        move();
      }
    } else {
      animating = false;
    }
  }
  
  // This toggles camera movement for edge-scrolling.
  void toggleAnimating() {
    if (animating == false) {
      animating = true;
      timeOfAnimationStart = millis();
    }
  }
  
  // This moves the camera based on edge-scrolling.
  void move() {
    coordinatesOfTopLeft[0] += translationVector.getHorizontalMovement(millis() - timeOfAnimationStart);
    coordinatesOfTopLeft[1] += translationVector.getVerticalMovement(millis() - timeOfAnimationStart);
    fixCameraPosition();
    currentMap.updateTileVertexes();
  }
}
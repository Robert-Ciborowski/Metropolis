/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: A class for screens.
*
*/

// The screen class.
class Screen {
  // These are the properties of the screen, storing its type ("gamplay" or "seperate") and the menus included in teh screen.
  String type;
  int menusOfTheScreen[] = null;

  // This is the constructor.
  Screen(String setType, int setMenus[]) {
    // This sets the properties.
    type = setType;
    if (setMenus != null) {
      menusOfTheScreen = setMenus;
    }
  }

  // This renders the screen.
  void render() {
    if (menusOfTheScreen != null) {
      for (int ii = 0; ii < menusOfTheScreen.length; ii++) {
        menus[menusOfTheScreen[ii]].render();
      }
    }
  }
  
  // This checks to see if the mouse is on a menu (used for edge-scrolling with the camera).
  boolean ifMouseIsOnAMenu() {
   boolean isTrue = false;
   for (int ii = 0; ii < menusOfTheScreen.length; ii++) {
    if (mouseX > menus[menusOfTheScreen[ii]].imageCoordinates[0] &&  mouseX < menus[menusOfTheScreen[ii]].imageCoordinates[0] + menus[menusOfTheScreen[ii]].image.width) {
      if (mouseY > menus[menusOfTheScreen[ii]].imageCoordinates[1] &&  mouseY < menus[menusOfTheScreen[ii]].imageCoordinates[1] + menus[menusOfTheScreen[ii]].image.height) {
       isTrue = true; 
      }
    }
   }
   return isTrue;
  }
}
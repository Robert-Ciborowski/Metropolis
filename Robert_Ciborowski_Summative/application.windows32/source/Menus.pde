/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: A class for menus.
*
*/

// This is the menu class.
class Menu {
  // These are the menu properties.
  // These are image related properties.
  PImage image;
  int imageCoordinates[];
  // These are text-related properties.
  String texts[];
  color textColours[];
  int textCoordinates[][];
  int textSizes[];
  // This is where the buttons are stored.
  Button buttons[] = null;

  // This is the constructor.
  Menu(String imageSource, int setImageCoordinates[], String setTexts[], color setColours[], int setTextCoordinates[][], int buttonsToLoad[], int setTextSizes[]) {
    // This sets up the main properties of the menu, including buttons by loading them from an external file.
    image = loadImage(imageSource);
    imageCoordinates = setImageCoordinates;
    texts = setTexts;
    textColours = setColours;
    textCoordinates = setTextCoordinates;
    if (buttonsToLoad != null) {
      buttons = new Button[buttonsToLoad.length];
      for (int ii = 0; ii < buttonsToLoad.length; ii++) {
        buttons[ii] = new Button();
        buttons[ii].loadButtonFromFile(buttonFilePath + buttonsToLoad[ii] + customDataFileExtension);
      }
    }
    textSizes = setTextSizes;
  }

  // This sets teh background image source.
  void setImageSource(String imageSource) {
    image = loadImage(imageSource);
  }

  // This renders the menu.
  void render() {
    // This renders the image.
    image(image, imageCoordinates[0], imageCoordinates[1]);
    // This renders text.
    if (texts != null) {
      for (int ii = 0; ii < texts.length; ii++) {
        String text;
        if (texts[ii].equals("@MONEY")) {
         text = String.valueOf(gameplay.getMoney()); 
        } else if (texts[ii].equals("@POPULATION")) {
         text = String.valueOf(gameplay.getPopulation());
        } else {
         text = texts[ii]; 
        }
        textSize(textSizes[ii]);
        fill(textColours[ii]);
        text(text, textCoordinates[ii][0], textCoordinates[ii][1]);
      }
    }
    // This renders buttons.
    if (buttons != null) {
      for (int ii = 0; ii < buttons.length; ii++) {
        buttons[ii].update();
      }
    }
  }
}
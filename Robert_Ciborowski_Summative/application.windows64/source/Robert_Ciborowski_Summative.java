import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Robert_Ciborowski_Summative extends PApplet {

/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: A city-building game.
*
*/

// This sets the background clear colour.
int backgroundClearColour = color(50, 50, 240);

// This sets up screens and menus.
// Screens are only displayed one at a time and contain one or more menus.
// Menus conatin one or more buttons, texts and a single static image.
Screen screens[];
Menu menus[];
// The current screen.
int currentScreen = 0;
// The default camera position used in all gameplay objects that will exist.
int defaultCameraPosition[];
// The current map, camera and gameplay to be used.
Map currentMap;
Camera playerCamera;
Gameplay gameplay;

// This runs at the program's startup.
public void setup() {
  // This makes the window fullscreen and sets the framerate.
  
  frameRate(30);
  // This laods data from external files.
  loadAllDataFromExternalFiles();
  // This sets up the player camera.
  playerCamera = new Camera(defaultCameraPosition[0], defaultCameraPosition[1], 0.03f);
  // This loads some images used by the game.
  loadTileImages();
  loadUnitImages();
  // This loads the map of the game.
  loadMap(0);
  // This sets up gameplay and laods its unit properties from an external file.
  gameplay = new Gameplay();
  loadGameplayUnitProperties();
  gameplay.selectUnitType(0);
  gameplay.selectUnit(0);
}
// This runs every frame.
public void draw() {
  // This clears the background.
  background(backgroundClearColour);
  // This renders and updates the main game components such as the camera, gameplay and screens if they need to be rendered or updated.
  playerCamera.update();
  if (screens[currentScreen].type.equals("gameplay")) {
    currentMap.render();
    gameplay.update();
  }
  screens[currentScreen].render();
}
/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: A class for buttons.
*
*/

// The button class.
class Button {
  // These are the properties of the object.
  // These are the colour properties.
  int mainColour;
  int mainColourWhenHover;
  int mainColourWhenClicked;
  int currentMainColour;
  int outlineColour;
  int textColour;

  // These are text-related properties.
  String text;
  int textDimensions[];
  int textSize;
  
  // These are image properties as well as background management properties.
  PImage image = null;
  PImage backgroundManager = null;
  // This will recreate the image for backgroundManager in the next frame if it is set to true.
  boolean refreshButton;

  // These are the main numerical button properties.
  int buttonDimensions[];
  int borderWidth;
  int roundedCornerRadius;

  // This stores if the button has been set to a click state.
  boolean buttonWasAlreadyClicked;
  // This stores the code that is read when the button is clicked.
  String onClickCode;
  // This is used for button scaling.
  Vector_Scale scale;

  // This is one of the constructors.
  Button() {
  }
  // This is another constructor.
  Button(int setMainColour, int setMainColourWhenHover, int setMainColourWhenClicked, int setOutlineColour, int setTextColour, String setText, int setTextDimensions[], int setTextSize, int setButtonDimensions[], int setBorderWidth, int setRoundedCornerRadius, String onClickCode) {
    // This sets the button's properties.
    mainColour = setMainColour;
    mainColourWhenHover = setMainColourWhenHover;
    mainColourWhenClicked = setMainColourWhenClicked;
    currentMainColour = mainColour;
    outlineColour = setOutlineColour;
    textColour = setTextColour;
    text = setText;
    textDimensions = setTextDimensions;
    textSize = setTextSize;
    buttonDimensions = setButtonDimensions;
    borderWidth = setBorderWidth;
    roundedCornerRadius = setRoundedCornerRadius;
    buttonWasAlreadyClicked = false;
    refreshButton = true;

    scale = new Vector_Scale(1);
  }

  // This loads the button properties from an external file (it's similar to the above constructor, in a way).
  public void loadButtonFromFile(String fileLocation) {
    // This laods the button data from teh external file.
    String buttonLines[] = loadStrings(fileLocation);
    // This is changed if the text positioning are meant to be the same as the button positioning.
    boolean copyTextDimensionsFromButtonDimensions = false;

    // This loops through the external file's data.
    for (int ii = 1; ii < buttonLines.length; ii++) {
      // This sets the properties.
      if (buttonLines[ii - 1].equals("Main Colour:")) {
        String data[] = split(buttonLines[ii], ", ");
        mainColour = color(PApplet.parseInt(data[0]), PApplet.parseInt(data[1]), PApplet.parseInt(data[2]));
      } else if (buttonLines[ii - 1].equals("Main Colour When Hover:")) {
        String data[] = split(buttonLines[ii], ", ");
        mainColourWhenHover = color(PApplet.parseInt(data[0]), PApplet.parseInt(data[1]), PApplet.parseInt(data[2]));
      } else if (buttonLines[ii - 1].equals("Main Colour When Clicked:")) {
        String data[] = split(buttonLines[ii], ", ");
        mainColourWhenClicked = color(PApplet.parseInt(data[0]), PApplet.parseInt(data[1]), PApplet.parseInt(data[2]));
      } else if (buttonLines[ii - 1].equals("Outline Colour:")) {
        String data[] = split(buttonLines[ii], ", ");
        outlineColour = color(PApplet.parseInt(data[0]), PApplet.parseInt(data[1]), PApplet.parseInt(data[2]));
      } else if (buttonLines[ii - 1].equals("Text Colour:")) {
        String data[] = split(buttonLines[ii], ", ");
        textColour = color(PApplet.parseInt(data[0]), PApplet.parseInt(data[1]), PApplet.parseInt(data[2]));
      } else if (buttonLines[ii - 1].equals("Text:")) {
        text =  buttonLines[ii];
      } else if (buttonLines[ii - 1].equals("Text Dimensions:")) {
        // This checks the see if the text positioning should be the same as the button positioning.
        if (buttonLines[ii].equals("BUTTON_DIMENSIONS")) {
          copyTextDimensionsFromButtonDimensions = true;
        } else {
          String data[] = split(buttonLines[ii], ", ");
          textDimensions = new int[2];
          textDimensions[0] = PApplet.parseInt(data[0]);
          // This sets the y position to the height minus a specified value if requested (ex: HEIGHT_MINUS_100).
          if (data[1].charAt(0) == 'H' && data[1].charAt(7) == 'M') {
            String temporary[] = split(data[1], "_");
            textDimensions[1] = height - PApplet.parseInt(temporary[2]);
          } else {
            textDimensions[1] = PApplet.parseInt(data[1]);
          }
        }
      } else if (buttonLines[ii - 1].equals("Button Dimensions:")) {
        String data[] = split(buttonLines[ii], ", ");
        buttonDimensions = new int[4];
        buttonDimensions[0] = PApplet.parseInt(data[0]);
        // This sets the y position to the height minus a specified value if requested (ex: HEIGHT_MINUS_100).
        if (data[1].charAt(0) == 'H' && data[1].charAt(7) == 'M') {
          String temporary[] = split(data[1], "_");
          buttonDimensions[1] = height - PApplet.parseInt(temporary[2]);
        } else {
          buttonDimensions[1] = PApplet.parseInt(data[1]);
        }
        buttonDimensions[2] = PApplet.parseInt(data[2]);
        buttonDimensions[3] = PApplet.parseInt(data[3]);
        if (copyTextDimensionsFromButtonDimensions == true) {
          // This sets the text dimensions to the button dimensions.
          textDimensions = buttonDimensions;
        }
      } else if (buttonLines[ii - 1].equals("Border Width:")) {
        borderWidth = PApplet.parseInt(buttonLines[ii]);
      } else if (buttonLines[ii - 1].equals("Rounded Corner Radius:")) {
        roundedCornerRadius = PApplet.parseInt(buttonLines[ii]);
      } else if (buttonLines[ii - 1].equals("OnClick:")) {
        onClickCode = buttonLines[ii];
      } else if (buttonLines[ii - 1].equals("Text Size:")) {
        textSize = PApplet.parseInt(buttonLines[ii]);
      } else if (buttonLines[ii - 1].equals("Image:")) {
        image = loadImage(buttonLines[ii]);
      }
    }
    // This sets some properties to their default values.
    currentMainColour = mainColour;
    buttonWasAlreadyClicked = false;
    refreshButton = true;
    scale = new Vector_Scale(1);
  }

  // This runs when the button should be rendered.
  public void update() {
    onClick();
    render();
  }

  // This checks mouse-related events.
  public void onClick() {
    // This checks to see if the cursor is within the button.
    if (mouseX >= buttonDimensions[0] && mouseY >= buttonDimensions[1] && mouseX <= buttonDimensions[0] + buttonDimensions[2] && mouseY <= buttonDimensions[1] + buttonDimensions[3]) {
      // This checks to see if the properties of the button need to be set to the "click" properties.
      if (mousePressed == true && mouseButton == LEFT && buttonWasAlreadyClicked == false) {
        // This makes sure that if the button is being clicked for more than one frame, the button's actions are not performed several times.
        // This scales the button to be alrger.
        scale.increaseMagnitude(buttonDimensions[3] / 8);
        buttonWasAlreadyClicked = true;
        currentMainColour = mainColourWhenClicked;
        refreshButton = true;
      } else if (buttonWasAlreadyClicked == false) {
        // Button hovering.
        if (currentMainColour != mainColourWhenHover) {
          currentMainColour = mainColourWhenHover;
        }
        refreshButton = true;
      }
    } else if (currentMainColour == mainColourWhenHover) {
      // This sets the button's colour back to normal (if necessary).
      currentMainColour = mainColour;
      refreshButton = true;
    }
    if (buttonWasAlreadyClicked == true && mousePressed == false && mouseButton == LEFT) {
      // This sets the button's properties back to normal when the mouse button is let go and runs its on-click actions.
      buttonWasAlreadyClicked = false;
      currentMainColour = mainColour;
      refreshButton = true;
      // This scales thebutton to be smaller.
      scale.increaseMagnitude(-buttonDimensions[3] / 8);
      onClickActions();
    }
  }

  // This runs after the button is pressed.
  public void onClickActions() {
    // This checks the button's code and runs an appropriate action.
    String onClickCodeSplit[] = split(onClickCode, ", ");
    if (onClickCodeSplit[0].equals("GO_TO_SCREEN")) {
      int newScreen = PApplet.parseInt(onClickCodeSplit[1]);
      if (newScreen <= screens.length) {
        currentScreen = newScreen;
      }
    } else if (onClickCodeSplit[0].equals("SELECT_UNIT_TYPE")) {
      gameplay.selectUnitType(PApplet.parseInt(onClickCodeSplit[1]));
    } else if (onClickCodeSplit[0].equals("SELECT_UNIT")) {
      gameplay.selectUnit(PApplet.parseInt(onClickCodeSplit[1]));
    } else if (onClickCodeSplit[0].equals("CYCLE_UNIT")) {
      gameplay.cycleUnit(PApplet.parseInt(onClickCodeSplit[1]));
    } else if (onClickCodeSplit[0].equals("TOGGLE_DEMO")) {
      gameplay.toggleDemo();
    }
  }

  // This renders the button.
  public void render() {
    // This will recalculate the look of the button if necessary.
    if (refreshButton == true) {
      if (image == null) {
        // This runs if there is no image for the button and uses rect() to draw the button.
        stroke(outlineColour);
        strokeWeight(borderWidth);
        fill(currentMainColour);
        rect(buttonDimensions[0] - scale.getScaling(1), buttonDimensions[1] - scale.getScaling(1), buttonDimensions[2] + scale.getScaling(1) * 2, buttonDimensions[3] + scale.getScaling(1) * 2, roundedCornerRadius);
        textSize(textSize);
        fill(textColour);
        text(text, textDimensions[0], textDimensions[1]);
      } else {
        // This will draw the button image with appropriate tinting.
        if (currentMainColour == mainColourWhenHover) {
          tint(Math.abs(255 - (red(mainColour) - red(mainColourWhenHover))), Math.abs(255 - (green(mainColour) - green(mainColourWhenHover))), Math.abs(255 - (blue(mainColour) - blue(mainColourWhenHover))));
        } else if (currentMainColour == mainColourWhenClicked) {
          tint(Math.abs(255 - (red(mainColour) - red(mainColourWhenClicked))), Math.abs(255 - (green(mainColour) - green(mainColourWhenClicked))), Math.abs(255 - (blue(mainColour) - blue(mainColourWhenClicked))));
        }
        image(image, buttonDimensions[0], buttonDimensions[1]);
        tint(255);
      }
      // This saves the rendered image for optimization.
      backgroundManager = get(buttonDimensions[0] - PApplet.parseInt(scale.getScaling(1)), buttonDimensions[1] - PApplet.parseInt(scale.getScaling(1)), buttonDimensions[2] + PApplet.parseInt(scale.getScaling(1) * 2), buttonDimensions[3] + PApplet.parseInt(scale.getScaling(1) * 2));
    } else {
      if (backgroundManager != null) {
        // This draws the button using backgroundManager.
        set(buttonDimensions[0] - PApplet.parseInt(scale.getScaling(1)), buttonDimensions[1] - PApplet.parseInt(scale.getScaling(1)), backgroundManager);
      }
    }
  }
}
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
  public int xPosition() {
    return coordinatesOfTopLeft[0];
  }

  public int yPosition() {
    return coordinatesOfTopLeft[1];
  }

  // This fixes the camera position to the map.
  public void fixCameraPosition() {
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
  public void update() {
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
  public void toggleAnimating() {
    if (animating == false) {
      animating = true;
      timeOfAnimationStart = millis();
    }
  }
  
  // This moves the camera based on edge-scrolling.
  public void move() {
    coordinatesOfTopLeft[0] += translationVector.getHorizontalMovement(millis() - timeOfAnimationStart);
    coordinatesOfTopLeft[1] += translationVector.getVerticalMovement(millis() - timeOfAnimationStart);
    fixCameraPosition();
    currentMap.updateTileVertexes();
  }
}
/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: A city-building game.
*
*/

// These store the locations and names of external files.
String mainFilePath = "game_data/main";
String screenFilePath = "game_data/screens_and_menus/screen";
String menuFilePath = "game_data/screens_and_menus/menu";
String buttonFilePath = "game_data/screens_and_menus/button";
String screensAndMenusPath = "game_data/screens_and_menus/";
String mapFilePath = "game_data/maps/map";
String tileFilePath = "game_data/tiles/";
String unitImageFilePath = "game_data/units/unit_";
String unitDataFilePath = "game_data/units/units";
String gameRuleDataFilePath = "game_data/rules";
String imageFileExtension = ".png";
String customDataFileExtension = ".txt";
// This stores how many screens and menus the game is allowed to use (set in main.txt).
int numberOfScreens = 0;
int numberOfMenus = 0;


// This loads all main data from external files.
public void loadAllDataFromExternalFiles() {
  // This uses main.txt to set main game properties.
  String mainLines[] = loadStrings(mainFilePath + customDataFileExtension);
  for (int ii = 0; ii < mainLines.length; ii++) {
    if (ii > 0) {
      // This must use equals() to compare the values inside of the strings since the == operator will see
      // if 2 string values are the same object (which will always return false).
      if (mainLines[ii - 1].equals("Screen Amount:")) {
        numberOfScreens = parseInt(mainLines[ii]);
      } else if (mainLines[ii - 1].equals("Menu Amount:")) {
        numberOfMenus = parseInt(mainLines[ii]);
      } else if (mainLines[ii - 1].equals("Unit Type Amount:")) {
        amountOfUnitTypes = parseInt(mainLines[ii]);
      } else if (mainLines[ii - 1].equals("Buildings Per Unit Type:")) {
        maximumAmountOfBuildings = parseInt(mainLines[ii]);
      } else if (mainLines[ii - 1].equals("Default Camera Position:")) {
        defaultCameraPosition = PApplet.parseInt(split(mainLines[ii], ", "));
      } else if (mainLines[ii - 1].equals("Game Rules Amount:")) {
        globalGameRules = new String[PApplet.parseInt(mainLines[ii])][5];
      }
    }
  }
  // This runs other external file loading.
  loadMenus();
  loadScreens();
  loadGameRules();
}

// This loads data about menus.
public void loadMenus() {
  // This initialises the menu array.
  menus = new Menu[numberOfMenus];
  // This loads a menu file for each menu.
  for (int ii = 0; ii < numberOfMenus; ii++) {
    // These are temporary properties that will eventually be used by the menu.
    String imageSource = "";
    int imageCoords[] = null;
    String menuTexts[] = null;
    int textColours[] = null;
    int textCoordinates[][] = null;
    int buttonsToLoad[] = null;
    int textSizes[] = null;

    // This loads menu data.
    String menuLines[] = loadStrings(menuFilePath + ii + customDataFileExtension);
    for (int jj = 1; jj < menuLines.length; jj++) {
      if (menuLines[jj - 1].equals("Image Source:")) {
        imageSource = menuLines[jj];
      } else if (menuLines[jj - 1].equals("Image Coords:")) {
        String temporary[] = split(menuLines[jj], ", ");
        imageCoords = PApplet.parseInt(split(menuLines[jj], ", "));
        // This sets the y position to the height minus a specified value if requested (ex: HEIGHT_MINUS_100).
        if (temporary[1].charAt(0) == 'H' && temporary[1].charAt(7) == 'M') {
          String temporary2[] = split(temporary[1], "_");
          imageCoords[1] = height - PApplet.parseInt(temporary2[2]);
        }
      } else if (menuLines[jj - 1].equals("Text:")) {
        menuTexts = split(menuLines[jj], ", ");
      } else if (menuLines[jj - 1].equals("Colours:")) {
        String colourData[] = split(menuLines[jj], "; ");
        textColours = new int[colourData.length];
        for (int kk = 0; kk < colourData.length; kk++) {
          int values[] = PApplet.parseInt(split(colourData[kk], ", "));
          textColours[kk] = color(values[0], values[1], values[2]);
        }
      } else if (menuLines[jj - 1].equals("Text Coordinates:")) {
        String textData[] = split(menuLines[jj], "; ");
        textCoordinates = new int[textData.length][2];
        for (int kk = 0; kk < textData.length; kk++) {
          String values[] = split(textData[kk], ", ");
          textCoordinates[kk][0] = PApplet.parseInt(values[0]);
          // This sets the y position to the height minus a specified value if requested (ex: HEIGHT_MINUS_100).
          if (values[1].charAt(0) == 'H' && values[1].charAt(7) == 'M') {
            String temporary2[] = split(values[1], "_");
            textCoordinates[kk][1] = height - PApplet.parseInt(temporary2[2]);
          } else {
            textCoordinates[kk][1] = PApplet.parseInt(values[1]);
          }
        }
      } else if (menuLines[jj - 1].equals("Buttons:")) {
        buttonsToLoad = PApplet.parseInt(split(menuLines[jj], ", "));
      } else if (menuLines[jj - 1].equals("Text Size:")) {
        textSizes = PApplet.parseInt(split(menuLines[jj], "; "));
      }
    }
    // This sets up the menu.
    menus[ii] = new Menu(imageSource, imageCoords, menuTexts, textColours, textCoordinates, buttonsToLoad, textSizes);
  }
}

// This loads data about screens.
public void loadScreens() {
  // This sets up the screen array.
  screens = new Screen[numberOfScreens];
  for (int ii = 0; ii < numberOfScreens; ii++) {
    // This loads each screen's properties from its own file.
    String screenType = "seperate";
    int menusOfTheScreen[] = null;
    String screenLines[] = loadStrings(screenFilePath + ii + customDataFileExtension);
    for (int jj = 1; jj < screenLines.length; jj++) {
      if (screenLines[jj - 1].equals("Type:")) {
        screenType = screenLines[jj];
      } else if (screenLines[jj - 1].equals("Menus:")) {
        menusOfTheScreen = PApplet.parseInt(split(screenLines[jj], ", "));
      }
    }
    // This sets up each screen.
    screens[ii] = new Screen(screenType, menusOfTheScreen);
  }
}

// This loads maps from external files.
public void loadMap(int mapID) {
  String mapLines[] = loadStrings(mapFilePath + mapID + customDataFileExtension);
  int dimensionsOfMap[] = new int[2];
  int tileValues[] = null;
  // This loads the map properties.
  for (int ii = 1; ii < mapLines.length; ii++) {
    if (mapLines[ii - 1].equals("Width:")) {
      dimensionsOfMap[0] = PApplet.parseInt(mapLines[ii]);
    } else if (mapLines[ii - 1].equals("Height:")) {
      dimensionsOfMap[1] = PApplet.parseInt(mapLines[ii]);
    } else if (mapLines[ii - 1].equals("Tile Data:")) {
      String data[] = split(mapLines[ii], ", ");
      tileValues = new int[data.length];
      for (int jj = 0; jj < data.length; jj++) {
        tileValues[jj] = PApplet.parseInt(data[jj]);
      }
    }
  }
  // This sets up the map.
  currentMap = new Map(dimensionsOfMap);
  currentMap.loadTiles(tileValues);
}

// This loads unit properties from external files.
public void loadGameplayUnitProperties() {
  // This loads the external file.
  String mapLines[] = loadStrings(unitDataFilePath + customDataFileExtension);
  // This loads each unit's properties.
  int temporaryCosts[][] = null;
  String temporaryNames[][] = null;
  int temporaryUnitProperties[][] = null;
  for (int ii = 1; ii < mapLines.length; ii++) {
    if (mapLines[ii - 1].equals("Prices:")) {
      String data[] = split(mapLines[ii], "; ");
      temporaryCosts = new int[amountOfUnitTypes][maximumAmountOfBuildings];
      for (int jj = 0; jj < amountOfUnitTypes; jj++) {
        if (jj < data.length) {
          String data2[] = split(data[jj], ", ");
          for (int kk = 0; kk < maximumAmountOfBuildings; kk++) {
            temporaryCosts[jj][kk] = PApplet.parseInt(data2[kk]);
          }
        }
      }
    } else if (mapLines[ii - 1].equals("Names:")) {
      String data[] = split(mapLines[ii], "; ");
      temporaryNames = new String[amountOfUnitTypes][maximumAmountOfBuildings];
      for (int jj = 0; jj < amountOfUnitTypes; jj++) {
        if (jj < data.length) {
          String data2[] = split(data[jj], ", ");
          for (int kk = 0; kk < maximumAmountOfBuildings; kk++) {
            temporaryNames[jj][kk] = data2[kk];
          }
        }
      }
    } else if (mapLines[ii - 1].equals("Properties:")) {
      String data[] = split(mapLines[ii], "; ");
      temporaryUnitProperties = new int[amountOfUnitTypes * maximumAmountOfBuildings][3];
      for (int jj = 0; jj < data.length; jj++) {
        String data2[] = split(data[jj], ", ");
        for (int kk = 0; kk < data2.length; kk++) {
          temporaryUnitProperties[jj][kk] = PApplet.parseInt(data2[kk]);
        }
      }
    }
  }
  // This sets the unit properties.
  if (temporaryCosts != null && temporaryNames != null) {
    gameplay.setProperties(temporaryCosts, temporaryNames);
  }
  if (temporaryUnitProperties != null) {
    unitProperties = temporaryUnitProperties;
  }
}

// This loads game rules.
public void loadGameRules() {
  // This loads the external file.
  String ruleLines[] = loadStrings(gameRuleDataFilePath + customDataFileExtension);
  // This sets the game rules.
  int numberOfRulesRead = 0;
  for (int ii = 1; ii < ruleLines.length; ii++) {
    if (ruleLines[ii - 1].equals("Units can be placed on these tiles:")) {
      String data[] = split(ruleLines[ii], ", ");
      tilesUnitsCanBePlacedOn = new int[data.length];
      for (int jj = 0; jj < data.length; jj++) {
        tilesUnitsCanBePlacedOn[jj] = PApplet.parseInt(data[jj]);
      }
    } else if (ruleLines[ii - 1].equals("If:")) {
      globalGameRules[numberOfRulesRead] = split(ruleLines[ii], " ");
      numberOfRulesRead++;
    } else if (ruleLines[ii - 1].equals("Time Between Tax Updates:")) {
      timeBetweenTaxChecks = PApplet.parseInt(ruleLines[ii]);
    } else if (ruleLines[ii - 1].equals("Default Land Value:")) {
      defaultLandValue = PApplet.parseInt(ruleLines[ii]);
    }
  }
}
/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: The gameplay class in which main gameplay elements take place.
*
*/

// This stores the game rules and default land value.
String globalGameRules[][] = null;
int defaultLandValue;

// This is the gameplay class.
class Gameplay {
  // These are the properties.
  // This stores all units the player owns in an arraylist.
  ArrayList<Unit> units = new ArrayList<Unit>();
  // This stores unit data.
  int selectedUnitType;
  int selectedUnit;
  int selectedUnitTypeOld;
  int selectedUnitOld;
  int unitCosts[][];
  String unitNames[][];
  // This stores whether or not the player is in demolition mode.
  boolean demo;
  
  // This stores statistics about money, population and unit types.
  long money;
  long population;
  int residentialTax;
  int commercialTax;
  int industrialTax;
  float demandForCity;
  
  int electricityAmount;
  int waterAmount;
  int trashDisposalAmount;
  int fireAmount;
  int healthAmount;
  int policeAmount;
  int educationAmount;
  int pollutionAmount;

  // This is the constructor.
  Gameplay() {
    // This sets the properties to their default values.
    selectedUnitType = 1;
    selectedUnit = 0;
    selectedUnitTypeOld = 3;
    selectedUnitOld = 3;
    money = 6000;
    population = 0;
    demo = false;
    electricityAmount = 0;
    waterAmount = 0;
    trashDisposalAmount = 0;
    fireAmount = 0;
    healthAmount = 0;
    policeAmount = 0;
    educationAmount = 0;
    pollutionAmount = 0;
    residentialTax = 9;
    commercialTax = 9;
    industrialTax = 9;
  }
  
  // This sets some gameplay properties.
  public void setProperties(int setCosts[][], String setNames[][]) {
   unitCosts = setCosts;
   unitNames = setNames;
  }
  
  // This returns statistics about the city.
  public long getMoney() {
   return money;
  }

  public long getPopulation() {
   return population;
  }
  
  // This is used to select a unit or a unit type.
  public void selectUnitType(int setType) {
    selectedUnit = 0;
    selectedUnitType = setType;
    updateMenuSelectedText();
    demo = false;
  }
  
  public void selectUnit(int setUnit) {
    selectedUnit = setUnit;
  }
  
  // This toggles demolition mode.
  public void toggleDemo() {
    if (demo == false) {
      demo = true;
    } else {
     demo = false; 
    }
  }
  
  // This returns whether demolition mode is on.
  public boolean demoIsOn() {
    return demo;  
  }
  
  // This updates the "select a unit" menu text.
  public void updateMenuSelectedText() {
    if (selectedUnit >= 0 && selectedUnitType >= 0) {
     menus[3].texts[0] = unitNames[selectedUnitType][selectedUnit];
    } else {
      menus[3].texts[0] = "";
    }
  }
  
  // This switches what unit is being selected via the "select a unit" menu.
  public void cycleUnit(int setUnit) {
    if (selectedUnit + setUnit > -1 && selectedUnit + setUnit < maximumAmountOfBuildings) {
      File imageFile = new File(dataPath(unitImageFilePath + selectedUnitType + "_" + (selectedUnit + 1) + imageFileExtension));
      if (imageFile.exists()) {
        selectedUnit += setUnit;
      }
    }
    updateMenuSelectedText();
  }

  // This adds a unit.
  public void add(int type, int xPosition, int yPosition, int building) {
    int ii = 0;
    // This checks to see if the player can afford a unit (they can go into a negative account balance by a little).
    if (money >= unitCosts[selectedUnitType][selectedUnit] - 1000) {
      // This checks to see where to put the unit in the unit array list.
      if (units.size() == 0) {
        add_unit(type, xPosition, yPosition, building);
      } else {
        int whetherToAdd = 0;
        for (Unit jj : units) {
          if (jj.getXPosition() == xPosition && jj.getYPosition() == yPosition) {
            // If this code is reached, there's already a tile in the space!
            whetherToAdd = 1;
          } else if ((jj.getXPosition() + (jj.getYPosition() * currentMap.getWidth())) > (xPosition + (yPosition * currentMap.getWidth()))) {
            if (whetherToAdd == 0) {
              // This says that the unit could be placed in the currently questioned spot in the arraylist.
             whetherToAdd = 2; 
            }
          } else if (ii + 1 == units.size()) {
            if (whetherToAdd == 0) {
              // This says that the unit could be placed in the currently questioned spot in the arraylist.
             whetherToAdd = 2; 
            }
          } else {
            ii++;
          }
        }
        // If nothing argued against adding the unit, the unit is added into the arraylist.
        if (whetherToAdd == 2) {
         add_unit(type, xPosition, yPosition, building); 
        }
      }
    }
  }

  // This removes a unit.
  public void remove(int xPosition, int yPosition) {
    int ii = 0;
    for (Unit jj : units) {
      if (jj.getXPosition() == xPosition && jj.getYPosition() == yPosition) {
        // If this code is reached, the game found the unit to delete.
        units.remove(ii);
        break;
      } else {
       ii++;
      }
    }
  }
  
  // This adds a unit and specifies its properties.
  public void add_unit(int type, int xPosition, int yPosition, int building) {
    // This reduces the player's money based on the unit cost.
    money -= unitCosts[selectedUnitType][selectedUnit];
    // This adds the appropriate unit.
    if (type == 0) {
       units.add(new Unit_Residential(xPosition, yPosition, building)); 
    } else if (type == 1) {
       units.add(new Unit_Commercial(xPosition, yPosition, building)); 
    } else if (type == 2) {
       units.add(new Unit_Industrial(xPosition, yPosition, building)); 
    } else if (type == 3) {
       units.add(new Unit_Transport(xPosition, yPosition, building)); 
    } else if (type == 4) {
       units.add(new Unit_Electric(xPosition, yPosition, building)); 
    } else if (type == 5) {
       units.add(new Unit_Hydro(xPosition, yPosition, building)); 
    } else if (type == 6) {
       units.add(new Unit_Garbage(xPosition, yPosition, building)); 
    } else if (type == 7) {
       units.add(new Unit_Fire(xPosition, yPosition, building)); 
    } else if (type == 8) {
       units.add(new Unit_Health(xPosition, yPosition, building)); 
    } else if (type == 9) {
       units.add(new Unit_Police(xPosition, yPosition, building)); 
    } else if (type == 10) {
       units.add(new Unit_Education(xPosition, yPosition, building)); 
    }
  }
  
  // This is run when the gamepaly should be updated.
  public void update() {
    render();
  }

  // This renders the gameplay.
  public void render() {
    // This renders units.
    for (int hh = 0; hh < currentMap.getHeight(); hh++) {
      for (Unit ii : units) {
        if (ii.getXPosition() % 2 != 0 && ii.getYPosition() == hh) {
          ii.render();
        }
      }
      for (Unit ii : units) {
        if (ii.getXPosition() % 2 == 0 && ii.getYPosition() == hh) {
          ii.render();
        }
      }
    }
    
    // This changes the image of the selected unit if necessary.
    if (selectedUnitType != selectedUnitTypeOld || selectedUnit != selectedUnitOld) {
      menus[5].setImageSource(unitImageFilePath + selectedUnitType + "_" + selectedUnit + imageFileExtension);
      selectedUnitTypeOld = selectedUnitType;
      selectedUnitOld = selectedUnit;
    }
  }
  
  // This modifies gameplay-related properties of the city.
  public void modifyElectricityAmount(int modifier) {
    electricityAmount += modifier;
    updateDemand();
  }
  
  public void modifyWaterAmount(int modifier) {
    waterAmount += modifier;
    updateDemand();
  }
  
  public void modifyTrashAmount(int modifier) {
   trashDisposalAmount += modifier;
   updateDemand();
  }
  
  public void modifyFireAmount(int modifier) {
   fireAmount += modifier;
   updateDemand();
  }
  
  public void modifyHealthAmount(int modifier) {
   healthAmount += modifier;
   updateDemand();
  }
  
  public void modifyPoliceAmount(int modifier) {
   policeAmount += modifier;
   updateDemand();
  }
  
  public void modifyEducationAmount(int modifier) {
   educationAmount += modifier;
   updateDemand();
  }
  
  public void modifyPollutionAmount(int modifier) {
    pollutionAmount += modifier;
    updateDemand();
  }
  
  // This updates city demand (used when citizens move into the city).
  public void updateDemand() {
   if (population > 0) {
     demandForCity = (electricityAmount + waterAmount + trashDisposalAmount + fireAmount + healthAmount + policeAmount + educationAmount - pollutionAmount) / population;
   }
  }
  
  // This returns city demand.
  public float getDemandForCity() {
   println(demandForCity);
   return demandForCity;
  }
  
  // This modifies city properties.
  public void modifyPopulation(int modifier) {
   population += modifier; 
  }
  
  public void modifyMoney(float modifier) {
   money += modifier; 
  }
  
  // This returns the tax amounts of the city.
  public int getResidentialTax() {
   return residentialTax; 
  }
  
  public int getCommericalTax() {
   return commercialTax; 
  }
  
  public int getIndustrialTax() {
   return industrialTax; 
  }
}
/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: The map class.
*
*/

// This is the class for the map.
class Map {
  // These are the proeperties of the map.
  // This stores the map's width and height.
  int dimensions[];
  // This stores the map's tiles.
  Tile mapTiles[] = null;

// This is the constructor.
  Map(int setDimensions[]) {
    // This sets the dimensions.
    dimensions = setDimensions;
  }

  // This loads tiles.
  public void loadTiles(int tilesTypes[]) {
    // This sets up mapTiles.
    if (mapTiles == null) {
      mapTiles = new Tile[tilesTypes.length];
    }
    // This creates the tiles within mapTiles and sets up their positioning.
    int positions[] = {0, 0};
    for (int ii = 0; ii < tilesTypes.length; ii++) {
      if (mapTiles[ii] == null) {
        mapTiles[ii] = new Tile();
      }
      mapTiles[ii].tileSetup(tilesTypes[ii], positions[0], positions[1]);
      if (positions[0] + 1 == dimensions[0]) {
        positions[0] = 0;
        if (positions[1] + 1 == dimensions[1]) {
          break;
        } else {
          positions[1]++;
        }
      } else {
        positions[0]++;
      }
    }
  }
  
  // This updates the tile's vertexes.
  public void updateTileVertexes() {
   for (int ii = 0; ii < mapTiles.length; ii++) {
     mapTiles[ii].updateVertexCoordinates();
   }
  }
  
  // This returns the map dimensions.
  public int getWidth() {
    return dimensions[0];  
  }
  
  public int getHeight() {
    return dimensions[1];  
  }
  
  // This renders the map.
  public void render() {
   for (int ii = 0; ii < mapTiles.length; ii++) {
     mapTiles[ii].update();
   }
  }
}
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
  int textColours[];
  int textCoordinates[][];
  int textSizes[];
  // This is where the buttons are stored.
  Button buttons[] = null;

  // This is the constructor.
  Menu(String imageSource, int setImageCoordinates[], String setTexts[], int setColours[], int setTextCoordinates[][], int buttonsToLoad[], int setTextSizes[]) {
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
  public void setImageSource(String imageSource) {
    image = loadImage(imageSource);
  }

  // This renders the menu.
  public void render() {
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
  public void render() {
    if (menusOfTheScreen != null) {
      for (int ii = 0; ii < menusOfTheScreen.length; ii++) {
        menus[menusOfTheScreen[ii]].render();
      }
    }
  }
  
  // This checks to see if the mouse is on a menu (used for edge-scrolling with the camera).
  public boolean ifMouseIsOnAMenu() {
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
/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: A class for tiles + other related properties and methods.
*
*/

// These are global values for tiles including tile dimensions, number of tile types, textures and tinting.
int widthOfTiles = 100;
int heightOfTiles = 75;
int numberOfTileTypes = 4;
PImage tiles[] = new PImage[numberOfTileTypes];
int greyTintAmountForTiles = 50;
int demolitionTintForTiles = 150;

// This loads the tile images.
public void loadTileImages() {
  for (int ii = 0; ii < numberOfTileTypes; ii++) {
    tiles[ii] = loadImage(tileFilePath + "tile" + ii + imageFileExtension);
  }
}

// This is the tile class.
class Tile {
  // These are the properties.
  // This stores tile type, coordinates, and whether the mouse is hovering on the tile.
  int type;
  int tileCoordinates[] = new int[2];
  int isometricY;
  int vertexCoordinates[][] = new int[4][2];
  boolean mouseHover;
  
  // This is the constructor.
  Tile() {
  }

  // This sets up the tile properties (run after the constructor).
  public void tileSetup(int setType, int x, int y) {
    type = setType;
    tileCoordinates[0] = x;
    tileCoordinates[1] = y;
    updateVertexCoordinates();
    mouseHover = false;
  }

  // This updates the tile vertex coordinates (based on the pixels on the screen and the camera).
  public void updateVertexCoordinates() {
    // This gets the camera position.
    int camX = playerCamera.xPosition();
    int camY = playerCamera.yPosition();
    // This udpates the vertex coordinates.
    vertexCoordinates[0][0] = ((tileCoordinates[0] * widthOfTiles / 2) + widthOfTiles / 2) - camX;
    vertexCoordinates[1][0] = (tileCoordinates[0] * widthOfTiles / 2) + widthOfTiles - camX;
    vertexCoordinates[2][0] = vertexCoordinates[0][0];
    vertexCoordinates[3][0] = (tileCoordinates[0] * widthOfTiles / 2) - camX;
    vertexCoordinates[0][1] = tileCoordinates[1] * heightOfTiles - camY;
    vertexCoordinates[1][1] = ((tileCoordinates[1] * heightOfTiles) + heightOfTiles / 2) - camY;
    vertexCoordinates[2][1] = (tileCoordinates[1] * heightOfTiles) + heightOfTiles - camY;
    if ((tileCoordinates[0] % 2) != 0) {
      // This moves the tile vertexes depending on if the tile's x position is even or odd (to create an isometric effect).
      vertexCoordinates[0][1] += heightOfTiles / 2;
      vertexCoordinates[1][1] += heightOfTiles / 2;
      vertexCoordinates[2][1] += heightOfTiles / 2;
    }
    vertexCoordinates[3][1] = vertexCoordinates[1][1];
  }

  // This returns mosueHover.
  public boolean mouseIsHovering() {
    return mouseHover;
  }

  // This updates the tile.
  public void update() {
    ontopChecker();
    render();
  }

  // This checks to see if the mosue is ontop of the tile, using slope.
  public void ontopChecker() {
    if (screens[currentScreen].ifMouseIsOnAMenu() == false) {
      float slope;
      float y_int;
      float yPositionIfMouseWereOnTheLine;
      // This must check to see which quarant the mosue is in and then uses the line formula to see if the mouse is within the tile.
      if (mouseX > vertexCoordinates[3][0] && mouseY > vertexCoordinates[0][1] && mouseX < vertexCoordinates[1][0] && mouseY < vertexCoordinates[2][1]) {
        if (mouseX < vertexCoordinates[0][0]) {
          if (mouseY < vertexCoordinates[3][1]) {
            slope = (float) (vertexCoordinates[3][1] - vertexCoordinates[0][1]) / (vertexCoordinates[3][0] - vertexCoordinates[0][0]);
            y_int = vertexCoordinates[3][1] - (slope * vertexCoordinates[3][0]);
            yPositionIfMouseWereOnTheLine = (slope * mouseX) + y_int;
            if (mouseY >= yPositionIfMouseWereOnTheLine) {
              // It is inside.
              ontopActions();
            } else {
              mouseHover = false;
            }
          } else if (mouseY > vertexCoordinates[3][1]) {
            slope = (float) (vertexCoordinates[2][1] - vertexCoordinates[3][1]) / (vertexCoordinates[2][0] - vertexCoordinates[3][0]);
            y_int = vertexCoordinates[3][1] - (slope * vertexCoordinates[3][0]);
            yPositionIfMouseWereOnTheLine = (slope * mouseX) + y_int;
            if (mouseY <= yPositionIfMouseWereOnTheLine) {
              // It is inside.
              ontopActions();
            } else {
              mouseHover = false;
            }
          } else {
            // The mouse must be inside if it's y position is equal to vertexCoordinates[3][1].
            ontopActions();
          }
        } else if (mouseX > vertexCoordinates[0][0]) {
          if (mouseY < vertexCoordinates[1][1]) {
            slope = (float) (vertexCoordinates[1][1] - vertexCoordinates[0][1]) / (vertexCoordinates[1][0] - vertexCoordinates[0][0]);
            y_int = vertexCoordinates[1][1] - (slope * vertexCoordinates[1][0]);
            yPositionIfMouseWereOnTheLine = (slope * mouseX) + y_int;
            if (mouseY >= yPositionIfMouseWereOnTheLine) {
              // It is inside.
              ontopActions();
            } else {
              mouseHover = false;
            }
          } else if (mouseY > vertexCoordinates[1][1]) {
            slope = (float) (vertexCoordinates[1][1] - vertexCoordinates[2][1]) / (vertexCoordinates[1][0] - vertexCoordinates[2][0]);
            y_int = vertexCoordinates[1][1] - (slope * vertexCoordinates[1][0]);
            yPositionIfMouseWereOnTheLine = (slope * mouseX) + y_int;
            if (mouseY <= yPositionIfMouseWereOnTheLine) {
              // It is inside.
              ontopActions();
            } else {
              mouseHover = false;
            }
          } else {
            // The mouse must be inside if it's y position is equal to vertexCoordinates[1][1].
            ontopActions();
          }
        } else {
          // The mouse's x position is the same as vertexCoordinates[0][0]. The tiles are symmetrical along a vertical line
          // from the top and bottom vertexes so the mouse must be inside the shape.
          ontopActions();
        }
      } else {
        mouseHover = false;
      }
    }
  }

  // This runs if the mosue is ontop of the tile.
  public void ontopActions() {
    mouseHover = true;
    if (mousePressed == true) {
      onClickActions();
    }
  }

  // This renders the tile (with tinting, if necessary).
  public void render() {
    if (mouseHover == true) {
      if (gameplay.demoIsOn()) {
        tint(255, 255 - demolitionTintForTiles, 255 - demolitionTintForTiles);
      } else {
        tint(255 - greyTintAmountForTiles);
      }
    }
    image(tiles[type], xRenderPositionWithIsometrics(), yRenderPositionWithIsometrics()); 
    tint (255, 255, 255);
  }

  // This returns positioning on the screen.
  public int xRenderPositionWithIsometrics() {
    return (tileCoordinates[0] * widthOfTiles / 2) - playerCamera.xPosition();
  }
  
  public int yRenderPositionWithIsometrics() {
    // This moves the tile depending on its x position to give an isometric effect.
    if ((tileCoordinates[0] % 2) == 0) {
      isometricY = tileCoordinates[1] * heightOfTiles;
    } else {
      isometricY = (tileCoordinates[1] * heightOfTiles) + (heightOfTiles / 2);
    }
    return isometricY - playerCamera.yPosition();
  }

  // This runs when the mouse button is pressed.
  public void onClickActions() {
    if (mouseButton == LEFT) {
      if (gameplay.demoIsOn()) {
        gameplay.remove(tileCoordinates[0] + 1, tileCoordinates[1] + 1);
      } else {
        boolean place = false;
        for (int ii = 0; ii < tilesUnitsCanBePlacedOn.length; ii++) {
          if (tilesUnitsCanBePlacedOn[ii] == type) {
           place = true;
           break;
          }
        }
        if (place == true) {
          gameplay.add(gameplay.selectedUnitType, tileCoordinates[0] + 1, tileCoordinates[1] + 1, gameplay.selectedUnit);
        }
      }
    }
  }
}
/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: Units and their several types.
*
*/

// This stores global unit data such as how many types there are of them, how many buildings are in each type, tinting and at what interval they modify the city budget.
int amountOfUnitTypes;
int maximumAmountOfBuildings;
PImage unitImages[][];
int greyTintAmountForUnits = 50;
int tilesUnitsCanBePlacedOn[];
int unitProperties[][];
int timeBetweenTaxChecks;

// This laods the unit images.
public void loadUnitImages() {
  unitImages = new PImage[amountOfUnitTypes][maximumAmountOfBuildings];
  for (int ii = 0; ii < amountOfUnitTypes; ii++) {
    for (int jj = 0; jj < maximumAmountOfBuildings; jj++) {
      String path = unitImageFilePath + ii + "_" + jj + imageFileExtension;
      File image = new File(dataPath(path));
      if (image.exists()) {
        unitImages[ii][jj] = loadImage(path);
      }
    }
  }
}

// This is the general unit class.
public abstract class UnitBase {
  // These are the properties, such as type and upgradability.
  int type;
  int building;
  int coordinates[];
  boolean upgradability;
  long timeSinceLastTaxCheck;

  // This sets up the properties.
  public void setupProperties(int xPosition, int yPosition, int setBuilding) {
    coordinates = new int[2];
    coordinates[0] = xPosition;
    coordinates[1] = yPosition;
    building = setBuilding;
    upgradability = false;
    timeSinceLastTaxCheck = millis();
  }

  // This updates the unit,.
  public void update() {
    render();
  }

  // This returns positioning in the map.
  public int getXPosition() {
    return coordinates[0];
  }

  public int getYPosition() {
    return coordinates[1];
  }

  // This returns positioning on the screen based on what tile the unit is on.
  public int getXWithIsometrics() {
    int positionInArray = (coordinates[1] - 1) * currentMap.getWidth() + coordinates[0] - 1;
    return currentMap.mapTiles[positionInArray].xRenderPositionWithIsometrics();
  }

  public int getYWithIsometrics() {
    int positionInArray = (coordinates[1] - 1) * currentMap.getWidth() + coordinates[0] - 1;
    return currentMap.mapTiles[positionInArray].yRenderPositionWithIsometrics();
  }

  // This returns gamplay-related unit properties.
  public int getType() {
    return type;
  }

  public int getBuilding() {
    return building;
  }

  // This renders the unit.
  public void render() {
    if (building >= 0) {
      if (unitImages[type][building] != null) {
        // This renders the unit using an image (with tinting, if necesarry).
        int positionInArray = (coordinates[1] - 1) * currentMap.getWidth() + coordinates[0] - 1;
        if (currentMap.mapTiles[positionInArray].mouseIsHovering()) {
          if (gameplay.demoIsOn()) {
            tint(255, 255 - demolitionTintForTiles, 255 - demolitionTintForTiles);
          } else {
            tint(255 - greyTintAmountForUnits);
          }
        }
        image(unitImages[type][building], getXWithIsometrics(), getYWithIsometrics() - (unitImages[type][building].height - heightOfTiles));
        tint (255, 255, 255);
      }
      // This updates the updraing and budget system.
      upgrading();
      updateBudget();
    }
  }

  // This checks the rules for teh unit, if any exist.
  public void checkRules() {
    for (int ii = 0; ii < globalGameRules.length; ii++) {
      if (globalGameRules[ii][0].equals("unit_" + type + "_" + building)) {
        if (globalGameRules[ii][1].equals("beside")) {
          // This is the rule used for when a unit is beside another.
          for (Unit jj : gameplay.units) {
            if (((jj.getXPosition() == coordinates[0] - 1 || jj.getXPosition() == coordinates[0] + 1) && (jj.getYPosition() == coordinates[1] - 1 || jj.getYPosition() == coordinates[1] + 1 || jj.getYPosition() == coordinates[1])) && globalGameRules[ii][2].equals("unit_" + jj.getType() + "_" + jj.getBuilding())) {
              if (globalGameRules[ii][3].equals("toggle")) {
                // This toggles if the unit can be upgraded (such as when a residential zone gets a house built ontop) or if its even active.
                if (globalGameRules[ii][4].equals("upgradability")) {
                  toggleUpgradability();
                  break;
                } else if (globalGameRules[ii][4].equals("activity")) {
                  toggleActivity(true);
                  break;
                }
              }
            }
          }
        }
      }
      // This is similar to the above but checks to see if the unit is in any other unit's rules.
      if (globalGameRules[ii][2].equals("unit_" + type + "_" + building)) {
        if (globalGameRules[ii][1].equals("beside")) {
          int kk = 0;
          for (Unit jj : gameplay.units) {
            if (((jj.getXPosition() == coordinates[0] - 1 || jj.getXPosition() == coordinates[0] + 1) && (jj.getYPosition() == coordinates[1] - 1 || jj.getYPosition() == coordinates[1] + 1 || jj.getYPosition() == coordinates[1])) && globalGameRules[ii][0].equals("unit_" + jj.getType() + "_" + jj.getBuilding())) {
              if (globalGameRules[ii][3].equals("toggle")) {
                // This toggles if the unit can be upgraded (such as when a residential zone gets a house built ontop) or if its even active.
                if (globalGameRules[ii][4].equals("upgradability")) {
                  gameplay.units.get(kk).toggleUpgradability();
                } else if (globalGameRules[ii][4].equals("activity")) {
                  gameplay.units.get(kk).toggleActivity(true);
                }
              }
            }
            kk++;
          }
        }
      }
    }
  }

  // This toggles upgradability.
  public void toggleUpgradability() {
    if (upgradability == true) {
      upgradability = false;
    } else {
      upgradability = true;
    }
  }

  // These are later defined by each subclass.
  public void toggleActivity(boolean on) {
  }

  public void upgrading() {
  }

  public void updateBudget() {
  }
}

// This is the unit interface.
interface Unit {
  public int getXPosition();
  public int getYPosition();
  public int getXWithIsometrics();
  public int getYWithIsometrics();
  public int getType();
  public int getBuilding();
  public void update();
  public void render();
  public void toggleUpgradability();
  public void toggleActivity(boolean on);
  public void upgrading();
  public void updateBudget();
}

// These are the different unit types. Each type has its own special properties such as its chance of upgrading.
// Each type also has its own constructor that changes some properties in the UnitBase class.
// Finally, each unit may also contains its own function for upgrading, etc.
class Unit_Residential extends UnitBase implements Unit {
  int timeSinceLastUpgradeUpdate;
  int timeBetweenUpgradeUpdates;
  float chanceOfUpgrading;


  Unit_Residential(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 0;
    checkRules();
    timeSinceLastUpgradeUpdate = millis();
    timeBetweenUpgradeUpdates = 4000;
    if (gameplay.getDemandForCity() != 0) {
      chanceOfUpgrading = 0.01f * gameplay.getDemandForCity();
    } else {
      chanceOfUpgrading = 0.01f;
    }
  }

  public void upgrading() {
    if (upgradability == true) {
      if (millis() >= timeSinceLastUpgradeUpdate + timeBetweenUpgradeUpdates) {
        if (random(0, 1) <= chanceOfUpgrading) {
          building = 2;
          gameplay.modifyPopulation((int) random(1, 4));
          timeSinceLastUpgradeUpdate = millis();
          upgradability = false;
        }
      }
    }
  }

  public void updateBudget() {
    if (millis() >= timeSinceLastTaxCheck + timeBetweenTaxChecks) {
      gameplay.modifyMoney((gameplay.getResidentialTax() * defaultLandValue) / 100);
      timeSinceLastTaxCheck = millis();
    }
  }
}

class Unit_Commercial extends UnitBase implements Unit {
  int timeSinceLastUpgradeUpdate;
  int timeBetweenUpgradeUpdates;
  float chanceOfUpgrading;

  Unit_Commercial(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 1;
    checkRules();
    timeSinceLastUpgradeUpdate = millis();
    timeBetweenUpgradeUpdates = 4000;
    chanceOfUpgrading = 0.03f;
  }

  public void upgrading() {
    if (upgradability == true) {
      if (millis() >= timeSinceLastUpgradeUpdate + timeBetweenUpgradeUpdates) {
        if (random(0, 1) <= chanceOfUpgrading) {
          building = 2;
          timeSinceLastUpgradeUpdate = millis();
        }
      }
    }
  }
}

class Unit_Industrial extends UnitBase implements Unit {
  int timeSinceLastUpgradeUpdate;
  int timeBetweenUpgradeUpdates;
  float chanceOfUpgrading;

  Unit_Industrial(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 2;
    checkRules();
    timeSinceLastUpgradeUpdate = millis();
    timeBetweenUpgradeUpdates = 4000;
    chanceOfUpgrading = 0.03f;
  }

  public void upgrading() {
    if (upgradability == true) {
      if (millis() >= timeSinceLastUpgradeUpdate + timeBetweenUpgradeUpdates) {
        if (random(0, 1) <= chanceOfUpgrading) {
          building = 2;
          timeSinceLastUpgradeUpdate = millis();
        }
      }
    }
  }
}

class Unit_Transport extends UnitBase implements Unit {
  Unit_Transport(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 3;
    checkRules();
  }
}

class Unit_Electric extends UnitBase implements Unit {
  Unit_Electric(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 4;
    checkRules();
  }

  public void toggleActivity(boolean on) {
    if (on == true) {
      gameplay.modifyElectricityAmount(unitProperties[maximumAmountOfBuildings * type + building][1]);
      gameplay.modifyPollutionAmount(unitProperties[maximumAmountOfBuildings * type + building][0]);
    } else {
      gameplay.modifyElectricityAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][1]));
      gameplay.modifyPollutionAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][0]));
    }
  }
}

class Unit_Hydro extends UnitBase implements Unit {
  Unit_Hydro(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 5;
    checkRules();
  }

  public void toggleActivity(boolean on) {
    if (on == true) {
      gameplay.modifyWaterAmount(unitProperties[maximumAmountOfBuildings * type + building][0]);
    } else {
      gameplay.modifyWaterAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][0]));
    }
  }
}

class Unit_Garbage extends UnitBase implements Unit {
  Unit_Garbage(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 6;
    checkRules();
  }

  public void toggleActivity(boolean on) {
    if (on == true) {
      gameplay.modifyTrashAmount(unitProperties[maximumAmountOfBuildings * type + building][0]);
      gameplay.modifyPollutionAmount(unitProperties[maximumAmountOfBuildings * type + building][1]);
    } else {
      gameplay.modifyTrashAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][0]));
      gameplay.modifyPollutionAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][1]));
    }
  }
}

class Unit_Fire extends UnitBase implements Unit {
  Unit_Fire(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 7;
    checkRules();
  }

  public void toggleActivity(boolean on) {
    if (on == true) {
      gameplay.modifyFireAmount(unitProperties[maximumAmountOfBuildings * type + building][0]);
    } else {
      gameplay.modifyFireAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][0]));
    }
  }
}

class Unit_Health extends UnitBase implements Unit {
  Unit_Health(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 8;
    checkRules();
  }

  public void toggleActivity(boolean on) {
    if (on == true) {
      gameplay.modifyHealthAmount(unitProperties[maximumAmountOfBuildings * type + building][0]);
    } else {
      gameplay.modifyHealthAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][0]));
    }
  }
}

class Unit_Police extends UnitBase implements Unit {
  Unit_Police(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 9;
    checkRules();
  }

  public void toggleActivity(boolean on) {
    if (on == true) {
      gameplay.modifyPoliceAmount(unitProperties[maximumAmountOfBuildings * type + building][0]);
    } else {
      gameplay.modifyPoliceAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][0]));
    }
  }
}

class Unit_Education extends UnitBase implements Unit {
  Unit_Education(int xPosition, int yPosition, int setBuilding) {
    setupProperties(xPosition, yPosition, setBuilding);
    type = 10;
    checkRules();
  }

  public void toggleActivity(boolean on) {
    if (on == true) {
      gameplay.modifyEducationAmount(unitProperties[maximumAmountOfBuildings * type + building][0]);
    } else {
      gameplay.modifyEducationAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][0]));
    }
  }
}
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
  public float getScaling(int timing) {
    return scaleMagnitude * timing;
  }
  
  // This modifies the scaling.
  public void increaseMagnitude(float incr) {
    scaleMagnitude += incr;
  }

  // returns 'true' if "pix" is less than the current magnitude
  public boolean moving(int pix) {
    if (magnitude == -1 || pix < magnitude) {
      return false;
    }
    return true;
  }
}
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
  public void setDetails(float setDirection, float setMagnitude, float setSpeed) {
    direction = setDirection;
    magnitude = setMagnitude;
    speed = setSpeed;
  }

  // This returns properties.
  public float getMagnitude() {
    return magnitude;
  }

  public float getDirection() {
    return direction;
  }

  public int getHorizontalMovement(int timing) {
    throw new RuntimeException("Use a tranlsation vector and not a regular vector!");
  }

  public int getVerticalMovement(int timing) {
    throw new RuntimeException("Use a tranlsation vector and not a regular vector!");
  }

  // returns 'true' if "pix" is less than the current magnitude
  public boolean moving(int pix) {
    if (magnitude == -1 || pix < magnitude) {
      return false;
    }
    return true;
  }
}
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
  public int getHorizontalMovement(int timing) {
    float vectDisplacement = timing * speed;
    float displacement = cos(radians(getDirection())) * vectDisplacement;
    return (int) displacement;
  }
  
  public int getVerticalMovement(int timing) {
    float vectDisplacement = timing * speed;
    float displacement = sin(radians(getDirection())) * vectDisplacement;
    return (int) displacement;
  }
}
  public void settings() {  size(800, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Robert_Ciborowski_Summative" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

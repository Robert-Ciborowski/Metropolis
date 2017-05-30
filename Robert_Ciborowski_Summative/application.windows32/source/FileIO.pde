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
void loadAllDataFromExternalFiles() {
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
        defaultCameraPosition = int(split(mainLines[ii], ", "));
      } else if (mainLines[ii - 1].equals("Game Rules Amount:")) {
        globalGameRules = new String[int(mainLines[ii])][5];
      }
    }
  }
  // This runs other external file loading.
  loadMenus();
  loadScreens();
  loadGameRules();
}

// This loads data about menus.
void loadMenus() {
  // This initialises the menu array.
  menus = new Menu[numberOfMenus];
  // This loads a menu file for each menu.
  for (int ii = 0; ii < numberOfMenus; ii++) {
    // These are temporary properties that will eventually be used by the menu.
    String imageSource = "";
    int imageCoords[] = null;
    String menuTexts[] = null;
    color textColours[] = null;
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
        imageCoords = int(split(menuLines[jj], ", "));
        // This sets the y position to the height minus a specified value if requested (ex: HEIGHT_MINUS_100).
        if (temporary[1].charAt(0) == 'H' && temporary[1].charAt(7) == 'M') {
          String temporary2[] = split(temporary[1], "_");
          imageCoords[1] = height - int(temporary2[2]);
        }
      } else if (menuLines[jj - 1].equals("Text:")) {
        menuTexts = split(menuLines[jj], ", ");
      } else if (menuLines[jj - 1].equals("Colours:")) {
        String colourData[] = split(menuLines[jj], "; ");
        textColours = new color[colourData.length];
        for (int kk = 0; kk < colourData.length; kk++) {
          int values[] = int(split(colourData[kk], ", "));
          textColours[kk] = color(values[0], values[1], values[2]);
        }
      } else if (menuLines[jj - 1].equals("Text Coordinates:")) {
        String textData[] = split(menuLines[jj], "; ");
        textCoordinates = new int[textData.length][2];
        for (int kk = 0; kk < textData.length; kk++) {
          String values[] = split(textData[kk], ", ");
          textCoordinates[kk][0] = int(values[0]);
          // This sets the y position to the height minus a specified value if requested (ex: HEIGHT_MINUS_100).
          if (values[1].charAt(0) == 'H' && values[1].charAt(7) == 'M') {
            String temporary2[] = split(values[1], "_");
            textCoordinates[kk][1] = height - int(temporary2[2]);
          } else {
            textCoordinates[kk][1] = int(values[1]);
          }
        }
      } else if (menuLines[jj - 1].equals("Buttons:")) {
        buttonsToLoad = int(split(menuLines[jj], ", "));
      } else if (menuLines[jj - 1].equals("Text Size:")) {
        textSizes = int(split(menuLines[jj], "; "));
      }
    }
    // This sets up the menu.
    menus[ii] = new Menu(imageSource, imageCoords, menuTexts, textColours, textCoordinates, buttonsToLoad, textSizes);
  }
}

// This loads data about screens.
void loadScreens() {
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
        menusOfTheScreen = int(split(screenLines[jj], ", "));
      }
    }
    // This sets up each screen.
    screens[ii] = new Screen(screenType, menusOfTheScreen);
  }
}

// This loads maps from external files.
void loadMap(int mapID) {
  String mapLines[] = loadStrings(mapFilePath + mapID + customDataFileExtension);
  int dimensionsOfMap[] = new int[2];
  int tileValues[] = null;
  // This loads the map properties.
  for (int ii = 1; ii < mapLines.length; ii++) {
    if (mapLines[ii - 1].equals("Width:")) {
      dimensionsOfMap[0] = int(mapLines[ii]);
    } else if (mapLines[ii - 1].equals("Height:")) {
      dimensionsOfMap[1] = int(mapLines[ii]);
    } else if (mapLines[ii - 1].equals("Tile Data:")) {
      String data[] = split(mapLines[ii], ", ");
      tileValues = new int[data.length];
      for (int jj = 0; jj < data.length; jj++) {
        tileValues[jj] = int(data[jj]);
      }
    }
  }
  // This sets up the map.
  currentMap = new Map(dimensionsOfMap);
  currentMap.loadTiles(tileValues);
}

// This loads unit properties from external files.
void loadGameplayUnitProperties() {
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
            temporaryCosts[jj][kk] = int(data2[kk]);
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
          temporaryUnitProperties[jj][kk] = int(data2[kk]);
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
void loadGameRules() {
  // This loads the external file.
  String ruleLines[] = loadStrings(gameRuleDataFilePath + customDataFileExtension);
  // This sets the game rules.
  int numberOfRulesRead = 0;
  for (int ii = 1; ii < ruleLines.length; ii++) {
    if (ruleLines[ii - 1].equals("Units can be placed on these tiles:")) {
      String data[] = split(ruleLines[ii], ", ");
      tilesUnitsCanBePlacedOn = new int[data.length];
      for (int jj = 0; jj < data.length; jj++) {
        tilesUnitsCanBePlacedOn[jj] = int(data[jj]);
      }
    } else if (ruleLines[ii - 1].equals("If:")) {
      globalGameRules[numberOfRulesRead] = split(ruleLines[ii], " ");
      numberOfRulesRead++;
    } else if (ruleLines[ii - 1].equals("Time Between Tax Updates:")) {
      timeBetweenTaxChecks = int(ruleLines[ii]);
    } else if (ruleLines[ii - 1].equals("Default Land Value:")) {
      defaultLandValue = int(ruleLines[ii]);
    }
  }
}
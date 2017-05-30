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
void loadTileImages() {
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
  void tileSetup(int setType, int x, int y) {
    type = setType;
    tileCoordinates[0] = x;
    tileCoordinates[1] = y;
    updateVertexCoordinates();
    mouseHover = false;
  }

  // This updates the tile vertex coordinates (based on the pixels on the screen and the camera).
  void updateVertexCoordinates() {
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
  boolean mouseIsHovering() {
    return mouseHover;
  }

  // This updates the tile.
  void update() {
    ontopChecker();
    render();
  }

  // This checks to see if the mosue is ontop of the tile, using slope.
  void ontopChecker() {
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
  void ontopActions() {
    mouseHover = true;
    if (mousePressed == true) {
      onClickActions();
    }
  }

  // This renders the tile (with tinting, if necessary).
  void render() {
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
  int xRenderPositionWithIsometrics() {
    return (tileCoordinates[0] * widthOfTiles / 2) - playerCamera.xPosition();
  }
  
  int yRenderPositionWithIsometrics() {
    // This moves the tile depending on its x position to give an isometric effect.
    if ((tileCoordinates[0] % 2) == 0) {
      isometricY = tileCoordinates[1] * heightOfTiles;
    } else {
      isometricY = (tileCoordinates[1] * heightOfTiles) + (heightOfTiles / 2);
    }
    return isometricY - playerCamera.yPosition();
  }

  // This runs when the mouse button is pressed.
  void onClickActions() {
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
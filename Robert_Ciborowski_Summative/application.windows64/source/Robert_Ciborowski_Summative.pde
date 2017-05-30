/**
* Assignment: Summative
* Date: 2016-06-09
* Author: Robert Ciborowski
* Description: A city-building game.
*
*/

// This sets the background clear colour.
color backgroundClearColour = color(50, 50, 240);

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
void setup() {
  // This makes the window fullscreen and sets the framerate.
  size(800, 600);
  frameRate(30);
  // This laods data from external files.
  loadAllDataFromExternalFiles();
  // This sets up the player camera.
  playerCamera = new Camera(defaultCameraPosition[0], defaultCameraPosition[1], 0.03);
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
void draw() {
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
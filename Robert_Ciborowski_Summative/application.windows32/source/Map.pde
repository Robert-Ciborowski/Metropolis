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
  void loadTiles(int tilesTypes[]) {
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
  void updateTileVertexes() {
   for (int ii = 0; ii < mapTiles.length; ii++) {
     mapTiles[ii].updateVertexCoordinates();
   }
  }
  
  // This returns the map dimensions.
  int getWidth() {
    return dimensions[0];  
  }
  
  int getHeight() {
    return dimensions[1];  
  }
  
  // This renders the map.
  void render() {
   for (int ii = 0; ii < mapTiles.length; ii++) {
     mapTiles[ii].update();
   }
  }
}
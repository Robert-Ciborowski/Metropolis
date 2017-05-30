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
void loadUnitImages() {
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
  void setupProperties(int xPosition, int yPosition, int setBuilding) {
    coordinates = new int[2];
    coordinates[0] = xPosition;
    coordinates[1] = yPosition;
    building = setBuilding;
    upgradability = false;
    timeSinceLastTaxCheck = millis();
  }

  // This updates the unit,.
  void update() {
    render();
  }

  // This returns positioning in the map.
  int getXPosition() {
    return coordinates[0];
  }

  int getYPosition() {
    return coordinates[1];
  }

  // This returns positioning on the screen based on what tile the unit is on.
  int getXWithIsometrics() {
    int positionInArray = (coordinates[1] - 1) * currentMap.getWidth() + coordinates[0] - 1;
    return currentMap.mapTiles[positionInArray].xRenderPositionWithIsometrics();
  }

  int getYWithIsometrics() {
    int positionInArray = (coordinates[1] - 1) * currentMap.getWidth() + coordinates[0] - 1;
    return currentMap.mapTiles[positionInArray].yRenderPositionWithIsometrics();
  }

  // This returns gamplay-related unit properties.
  int getType() {
    return type;
  }

  int getBuilding() {
    return building;
  }

  // This renders the unit.
  void render() {
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
  void checkRules() {
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
  void toggleUpgradability() {
    if (upgradability == true) {
      upgradability = false;
    } else {
      upgradability = true;
    }
  }

  // These are later defined by each subclass.
  void toggleActivity(boolean on) {
  }

  void upgrading() {
  }

  void updateBudget() {
  }
}

// This is the unit interface.
interface Unit {
  int getXPosition();
  int getYPosition();
  int getXWithIsometrics();
  int getYWithIsometrics();
  int getType();
  int getBuilding();
  void update();
  void render();
  void toggleUpgradability();
  void toggleActivity(boolean on);
  void upgrading();
  void updateBudget();
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
      chanceOfUpgrading = 0.01 * gameplay.getDemandForCity();
    } else {
      chanceOfUpgrading = 0.01;
    }
  }

  void upgrading() {
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

  void updateBudget() {
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
    chanceOfUpgrading = 0.03;
  }

  void upgrading() {
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
    chanceOfUpgrading = 0.03;
  }

  void upgrading() {
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

  void toggleActivity(boolean on) {
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

  void toggleActivity(boolean on) {
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

  void toggleActivity(boolean on) {
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

  void toggleActivity(boolean on) {
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

  void toggleActivity(boolean on) {
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

  void toggleActivity(boolean on) {
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

  void toggleActivity(boolean on) {
    if (on == true) {
      gameplay.modifyEducationAmount(unitProperties[maximumAmountOfBuildings * type + building][0]);
    } else {
      gameplay.modifyEducationAmount(-1 * (unitProperties[maximumAmountOfBuildings * type + building][0]));
    }
  }
}
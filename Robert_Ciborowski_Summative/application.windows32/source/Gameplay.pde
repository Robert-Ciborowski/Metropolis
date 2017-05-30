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
  void setProperties(int setCosts[][], String setNames[][]) {
   unitCosts = setCosts;
   unitNames = setNames;
  }
  
  // This returns statistics about the city.
  long getMoney() {
   return money;
  }

  long getPopulation() {
   return population;
  }
  
  // This is used to select a unit or a unit type.
  void selectUnitType(int setType) {
    selectedUnit = 0;
    selectedUnitType = setType;
    updateMenuSelectedText();
    demo = false;
  }
  
  void selectUnit(int setUnit) {
    selectedUnit = setUnit;
  }
  
  // This toggles demolition mode.
  void toggleDemo() {
    if (demo == false) {
      demo = true;
    } else {
     demo = false; 
    }
  }
  
  // This returns whether demolition mode is on.
  boolean demoIsOn() {
    return demo;  
  }
  
  // This updates the "select a unit" menu text.
  void updateMenuSelectedText() {
    if (selectedUnit >= 0 && selectedUnitType >= 0) {
     menus[3].texts[0] = unitNames[selectedUnitType][selectedUnit];
    } else {
      menus[3].texts[0] = "";
    }
  }
  
  // This switches what unit is being selected via the "select a unit" menu.
  void cycleUnit(int setUnit) {
    if (selectedUnit + setUnit > -1 && selectedUnit + setUnit < maximumAmountOfBuildings) {
      File imageFile = new File(dataPath(unitImageFilePath + selectedUnitType + "_" + (selectedUnit + 1) + imageFileExtension));
      if (imageFile.exists()) {
        selectedUnit += setUnit;
      }
    }
    updateMenuSelectedText();
  }

  // This adds a unit.
  void add(int type, int xPosition, int yPosition, int building) {
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
  void remove(int xPosition, int yPosition) {
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
  void add_unit(int type, int xPosition, int yPosition, int building) {
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
  void update() {
    render();
  }

  // This renders the gameplay.
  void render() {
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
  void modifyElectricityAmount(int modifier) {
    electricityAmount += modifier;
    updateDemand();
  }
  
  void modifyWaterAmount(int modifier) {
    waterAmount += modifier;
    updateDemand();
  }
  
  void modifyTrashAmount(int modifier) {
   trashDisposalAmount += modifier;
   updateDemand();
  }
  
  void modifyFireAmount(int modifier) {
   fireAmount += modifier;
   updateDemand();
  }
  
  void modifyHealthAmount(int modifier) {
   healthAmount += modifier;
   updateDemand();
  }
  
  void modifyPoliceAmount(int modifier) {
   policeAmount += modifier;
   updateDemand();
  }
  
  void modifyEducationAmount(int modifier) {
   educationAmount += modifier;
   updateDemand();
  }
  
  void modifyPollutionAmount(int modifier) {
    pollutionAmount += modifier;
    updateDemand();
  }
  
  // This updates city demand (used when citizens move into the city).
  void updateDemand() {
   if (population > 0) {
     demandForCity = (electricityAmount + waterAmount + trashDisposalAmount + fireAmount + healthAmount + policeAmount + educationAmount - pollutionAmount) / population;
   }
  }
  
  // This returns city demand.
  float getDemandForCity() {
   println(demandForCity);
   return demandForCity;
  }
  
  // This modifies city properties.
  void modifyPopulation(int modifier) {
   population += modifier; 
  }
  
  void modifyMoney(float modifier) {
   money += modifier; 
  }
  
  // This returns the tax amounts of the city.
  int getResidentialTax() {
   return residentialTax; 
  }
  
  int getCommericalTax() {
   return commercialTax; 
  }
  
  int getIndustrialTax() {
   return industrialTax; 
  }
}
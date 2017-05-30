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
  color mainColour;
  color mainColourWhenHover;
  color mainColourWhenClicked;
  color currentMainColour;
  color outlineColour;
  color textColour;

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
  Button(color setMainColour, color setMainColourWhenHover, color setMainColourWhenClicked, color setOutlineColour, color setTextColour, String setText, int setTextDimensions[], int setTextSize, int setButtonDimensions[], int setBorderWidth, int setRoundedCornerRadius, String onClickCode) {
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
  void loadButtonFromFile(String fileLocation) {
    // This laods the button data from teh external file.
    String buttonLines[] = loadStrings(fileLocation);
    // This is changed if the text positioning are meant to be the same as the button positioning.
    boolean copyTextDimensionsFromButtonDimensions = false;

    // This loops through the external file's data.
    for (int ii = 1; ii < buttonLines.length; ii++) {
      // This sets the properties.
      if (buttonLines[ii - 1].equals("Main Colour:")) {
        String data[] = split(buttonLines[ii], ", ");
        mainColour = color(int(data[0]), int(data[1]), int(data[2]));
      } else if (buttonLines[ii - 1].equals("Main Colour When Hover:")) {
        String data[] = split(buttonLines[ii], ", ");
        mainColourWhenHover = color(int(data[0]), int(data[1]), int(data[2]));
      } else if (buttonLines[ii - 1].equals("Main Colour When Clicked:")) {
        String data[] = split(buttonLines[ii], ", ");
        mainColourWhenClicked = color(int(data[0]), int(data[1]), int(data[2]));
      } else if (buttonLines[ii - 1].equals("Outline Colour:")) {
        String data[] = split(buttonLines[ii], ", ");
        outlineColour = color(int(data[0]), int(data[1]), int(data[2]));
      } else if (buttonLines[ii - 1].equals("Text Colour:")) {
        String data[] = split(buttonLines[ii], ", ");
        textColour = color(int(data[0]), int(data[1]), int(data[2]));
      } else if (buttonLines[ii - 1].equals("Text:")) {
        text =  buttonLines[ii];
      } else if (buttonLines[ii - 1].equals("Text Dimensions:")) {
        // This checks the see if the text positioning should be the same as the button positioning.
        if (buttonLines[ii].equals("BUTTON_DIMENSIONS")) {
          copyTextDimensionsFromButtonDimensions = true;
        } else {
          String data[] = split(buttonLines[ii], ", ");
          textDimensions = new int[2];
          textDimensions[0] = int(data[0]);
          // This sets the y position to the height minus a specified value if requested (ex: HEIGHT_MINUS_100).
          if (data[1].charAt(0) == 'H' && data[1].charAt(7) == 'M') {
            String temporary[] = split(data[1], "_");
            textDimensions[1] = height - int(temporary[2]);
          } else {
            textDimensions[1] = int(data[1]);
          }
        }
      } else if (buttonLines[ii - 1].equals("Button Dimensions:")) {
        String data[] = split(buttonLines[ii], ", ");
        buttonDimensions = new int[4];
        buttonDimensions[0] = int(data[0]);
        // This sets the y position to the height minus a specified value if requested (ex: HEIGHT_MINUS_100).
        if (data[1].charAt(0) == 'H' && data[1].charAt(7) == 'M') {
          String temporary[] = split(data[1], "_");
          buttonDimensions[1] = height - int(temporary[2]);
        } else {
          buttonDimensions[1] = int(data[1]);
        }
        buttonDimensions[2] = int(data[2]);
        buttonDimensions[3] = int(data[3]);
        if (copyTextDimensionsFromButtonDimensions == true) {
          // This sets the text dimensions to the button dimensions.
          textDimensions = buttonDimensions;
        }
      } else if (buttonLines[ii - 1].equals("Border Width:")) {
        borderWidth = int(buttonLines[ii]);
      } else if (buttonLines[ii - 1].equals("Rounded Corner Radius:")) {
        roundedCornerRadius = int(buttonLines[ii]);
      } else if (buttonLines[ii - 1].equals("OnClick:")) {
        onClickCode = buttonLines[ii];
      } else if (buttonLines[ii - 1].equals("Text Size:")) {
        textSize = int(buttonLines[ii]);
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
  void update() {
    onClick();
    render();
  }

  // This checks mouse-related events.
  void onClick() {
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
  void onClickActions() {
    // This checks the button's code and runs an appropriate action.
    String onClickCodeSplit[] = split(onClickCode, ", ");
    if (onClickCodeSplit[0].equals("GO_TO_SCREEN")) {
      int newScreen = int(onClickCodeSplit[1]);
      if (newScreen <= screens.length) {
        currentScreen = newScreen;
      }
    } else if (onClickCodeSplit[0].equals("SELECT_UNIT_TYPE")) {
      gameplay.selectUnitType(int(onClickCodeSplit[1]));
    } else if (onClickCodeSplit[0].equals("SELECT_UNIT")) {
      gameplay.selectUnit(int(onClickCodeSplit[1]));
    } else if (onClickCodeSplit[0].equals("CYCLE_UNIT")) {
      gameplay.cycleUnit(int(onClickCodeSplit[1]));
    } else if (onClickCodeSplit[0].equals("TOGGLE_DEMO")) {
      gameplay.toggleDemo();
    }
  }

  // This renders the button.
  void render() {
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
      backgroundManager = get(buttonDimensions[0] - int(scale.getScaling(1)), buttonDimensions[1] - int(scale.getScaling(1)), buttonDimensions[2] + int(scale.getScaling(1) * 2), buttonDimensions[3] + int(scale.getScaling(1) * 2));
    } else {
      if (backgroundManager != null) {
        // This draws the button using backgroundManager.
        set(buttonDimensions[0] - int(scale.getScaling(1)), buttonDimensions[1] - int(scale.getScaling(1)), backgroundManager);
      }
    }
  }
}
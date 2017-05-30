The files inside this folder are read by the game. They
provide the game with informaion such as where to place
text in a menu.

The main reason for why this program uses external files
to read data is so that the user is able to create their
own modifications to the program. The modifiability
could be thought of as a feature.

Syntax (main):
	Screen Amount: 
		Specifies the total number of screens in
		the program.
	Menu Amount:
		Specifies the total number of menus in
		the program.
Syntax (screens):
	Type:
		Specifies what type of screen the screen
		is. This can either be set to "gameplay"
		(meaning that the program renders
		gameplay to the screen) or "seperate"
		(meaning that the program does not
		render gameplay to the screen).
	Menus:
		Specifies what menus the screen uses.
		For example, adding "2" to the list will
		make the screen use menu2.
Syntax (menus):
	Image Source:
		Specifies the source of the menu's
		background image.
	Image Coords:
		Specifies the background image's
		x and y coordinates.
	Text:
		Specifies the text that the menu
		displays.
	Colours:
		Specifies the red, green and blue values
		of the texts that are displayed.
	Text Coordinates:
		Specifies the x and y coordinates of the
		texts that are displayed.

*IMPORTANT NOTE*:
	when using multiple values (like in "Image
	Coords:" inside of a menu file), seperate the values
	with a ", " (comma and a space). Some sections such as
	"Colours:" in inside of a menu file require
	the values for multiple colours so each value in a
	colour is seperated with a ", " and each colour is
	seperated with a ";" (semicolon).

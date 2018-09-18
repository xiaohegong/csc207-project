
****************************************************************
PLEASE FULL SCREEN DOCUMENT TO VIEW PROPERLY. 
PLEASE VIEW events.txt AND routes.txt FOR EXAMPLE FORMAT
****************************************************************


There are some methods and variables that are not used and are preserved for future possible features in phase 2.

Right now, there is only one TransitSystem and everything is stored in the .java (e.g. TIME_LIMIT etc.)
In phase 2, we will migrate these parameters into route.txt for multiple TransitSystem.


=================== events.txt file format =====================

##### RIDING ON SUBWAY/BUS #####
<Day> <24 hr time> <TransitSystem Name> <Station Name> Station/Stop entry Card <Card #>
e.g. “31 15:44 TTC St George Station entry Card 1204”
e.g. “45 11:00 TTC College Stop entry card 1204”

<Day> <24 hr time> <TransitSystem Name> <Station Name> Station/Stop exit Card <Card #>
e.g. “89 01:15 TTC Dundas Station exit Card 1204”

NOTE: <Station Name> can only have alphabetic characters.
NOTE: PLEASE MAKE SURE TO DISTINGUISH STATION AND STOP ACCORDING TO WHAT IS WRITTEN IN route.txt
NOTE: PLEASE MAKE SURE the time is less than 23:30 since the workers on the system are lazy :D

NOTE: Please enter time chronologically, and enter CLOSEDAY as indicated below before starting a new day.

##### CARD TOP UP #####
Card <Card #> add balance $ <amount>
e.g. “Card 1024 add balance $10”

NOTE: You can only add amounts in the $10, $20, or $50 denominations (so $50 is acceptable, but $100 is not)

##### VIEW USER DATA #####
User <email> view trips
e.g. “User CSC207@cs.toronto.edu view trips”

User <email> view recent trips
e.g. “User CSC207@cs.toronto.edu view recent trips”
NOTE: this only displays the last three trips of the user

User <email> view recent <number of trips to view> trips
e.g. “User CSC207@cs.toronto.edu view recent 5 trips”

User <email> suspend Card <Card #>
e.g. “User CSC207@cs.toronto.edu suspend Card 1204”

User <email> unsuspend Card <Card #>
e.g. “User CSC207@cs.toronto.edu unsuspend Card 1204”

User <email> track average cost
e.g. “User CSC207@cs.toronto.edu track average cost”

User <email> check balance
e.g. “User CSC207@cs.toronto.edu check balance”

##### USER MANAGE CARDS #####
User <email> add card

##### ADMIN DATA #####
Admin <email> add <Transit System Name>

Note: Admin must add existing transit systems.

e.g. “Admin im.prof@cs.toronto.edu add TTC”
(TTC is a valid transit system, you can change the name in routes.txt)

Admin <email> view daily report
e.g. “Admin im.prof@cs.toronto.edu view statistics”

##### CLOSE DAY #####
CLOSEDAY
e.g. "CLOSEDAY"

====================  routes.txt file format  ======================
Contains different subway stations and bus stops including the
following information:

First Line: TransitSystem Name (e.g. TTC)

NOTE: right now it only supports none intersecting routes and one transit system.

REPEAT:
	<Type of transit> <number of routes> (e.g. Subway 2)
	REPEAT <number of routes> times
		First Line: <route number> <number of stations>
			REPEAT <number of stations> times
				2nd line: Station/Stop name
				3rd line: ID x y transfer(0 or 1) 
			END-REPEAT
	END-REPEAT
END-REPEAT

In particular, the id is categorized as the following:
- 1st digit: 0 —> subway, 1 —> bus
- 2nd digit: route number
- 3rd & 4th: station / stop number
===================================================================


NOTE: the CLOSEDAY profit and viewStats profit might be different due to someone
still on the system prior to closing. The difference should theoretically be a
multiple of $6.
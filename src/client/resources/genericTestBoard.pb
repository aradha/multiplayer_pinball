board name=Bunker gravity = 20.0

ball name=ball1 x=1 y=1 xVelocity=10 yVelocity=0
ball name=ball2 x=10 y=3 xVelocity=-10 yVelocity=0

# define some bumpers
squareBumper name=SquareZ x=0 y=12
squareBumper name=SquareA x=1 y=12
squareBumper name=SquareB x=2 y=12
squareBumper name=SquareC x=3 y=12
squareBumper name=SquareD x=4 y=12
squareBumper name=SquareE x=5 y=12
squareBumper name=SquareF x=6 y=12
squareBumper name=SquareG x=7 y=12
squareBumper name=SquareH x=8 y=12
squareBumper name=SquareI x=9 y=12
squareBumper name=SquareJ x=10 y=12

triangleBumper name=Tri x=19 y=2 orientation=90

# define some flippers
 leftFlipper name=FlipL2 x=4 y=8 orientation=90
 leftFlipper name=FlipL1 x=1 y=8 orientation=180
 leftFlipper name=FlipL3 x=7 y=8 orientation=0
 leftFlipper name=FlipL4 x=4 y=4 orientation=270
rightFlipper name=FlipR1 x=11 y=8 orientation=0
rightFlipper name=FlipR2 x=14 y=8 orientation=90
rightFlipper name=FlipR3 x=17 y=8 orientation=180
rightFlipper name=FlipR4 x=14 y=4 orientation=270

# define an absorber to catch the ball
absorber name=Abs x=0 y=18 width=20 height=2 

# define events between gizmos
fire trigger=SquareZ action=FlipL1
fire trigger=SquareZ action=FlipR2
fire trigger=SquareA action=FlipL1
fire trigger=SquareA action=FlipL2
fire trigger=SquareB action=FlipL2
fire trigger=SquareB action=FlipL3
fire trigger=SquareC action=FlipL3
fire trigger=SquareC action=FlipL1
fire trigger=SquareD action=FlipR1
fire trigger=SquareD action=FlipR2
fire trigger=SquareE action=FlipR2
fire trigger=SquareE action=FlipR3
fire trigger=SquareF action=FlipR3
fire trigger=SquareF action=FlipR1
fire trigger=SquareI action=FlipL3
fire trigger=SquareI action=FlipR1
fire trigger=Tri action=FlipL2
fire trigger=Tri action=FlipR2
fire trigger=FlipR4 action=FlipR4
fire trigger=FlipL4 action=FlipL4

# make the absorber self-triggering
fire trigger=Abs action=Abs 
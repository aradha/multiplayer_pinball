board name=pb1 gravity = 20.0

# define a ball
ball name=BallC x=1.8 y=4.5 xVelocity=-10.4 yVelocity=10.3 
ball name=BallC x=1.8 y=3.5 xVelocity=-9.4 yVelocity=10.3 
ball name=BallC x=1.8 y=2.5 xVelocity=-8.4 yVelocity=10.3 

# define some bumpers
squareBumper name=Square x=0 y=10
squareBumper name=SquareB x=1 y=10
squareBumper name=SquareC x=2 y=10

circleBumper name=Circle x=4 y=3
triangleBumper name=Tri x=19 y=3 orientation=90


# define some flippers
leftFlipper name=FlipL x=13 y=2 orientation=0 
rightFlipper name=FlipR x=13 y=4 orientation=180

#adding some portals
portal name=sender1 x=3 y=10 otherBoard=pb2 otherPortal=receiver2
portal name=receiver1 x=5 y=7 otherPortal=sender1



# define an absorber to catch the ball
 absorber name=Abs x=10 y=17 width=10 height=2 


# make the absorber trigger on 'a'
 fire trigger=Abs action=Abs 
 
# make the flippers trigger on keys
 keydown key=a action=FlipL
 keydown key=d action=FlipR
 


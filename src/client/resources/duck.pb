board name=duck gravity = 20.0

# define a ball
ball name=BallC x=1.8 y=4.5 xVelocity=-10.4 yVelocity=10.3 
ball name=BallC x=1.8 y=3.5 xVelocity=-9.4 yVelocity=10.3 
ball name=BallC x=1.8 y=2.5 xVelocity=-8.4 yVelocity=10.3 

# define some bumpers


squareBumper name=Face1 x=4 y=3
squareBumper name=Face2 x=4 y=4
squareBumper name=Face3 x=4 y=5
squareBumper name=Face4 x=4 y=6
squareBumper name=Face5 x=4 y=7
squareBumper name=Face6 x=4 y=8
squareBumper name=Face7 x=4 y=9
squareBumper name=Face8 x=4 y=10


squareBumper name=Body2 x=3 y=9
squareBumper name=Body3 x=3 y=10
squareBumper name=Body4 x=3 y=11

squareBumper name=Body5 x=2 y=9
squareBumper name=Body6 x=2 y=10
squareBumper name=Body7 x=2 y=11
squareBumper name=Body8 x=2 y=12

squareBumper name=Body9 x=1 y=9
squareBumper name=Body10 x=1 y=10
squareBumper name=Body11 x=1 y=11
squareBumper name=Body12 x=1 y=12

squareBumper name=Body13 x=0 y=9
squareBumper name=Body14 x=0 y=10
squareBumper name=Body15 x=0 y=11
squareBumper name=Body16 x=0 y=12



triangleBumper name=Tri1 x=4 y=11 orientation=0
triangleBumper name=Tri2 x=3 y=12 orientation=0

triangleBumper name=Tri3 x=19 y=1 orientation=90


# define some flippers
  leftFlipper name=FlipL x=5 y=2 orientation=270 
rightFlipper name=FlipR x=5 y=4 orientation=270

#adding some portals
portal name=sender1 x=19 y=10 otherBoard=pb2 otherPortal=receiver2
portal name=receiver1 x=19 y=14 otherPortal=sender1



# define an absorber to catch the ball
 absorber name=Abs x=0 y=17 width=20 height=2 


# make the absorber trigger on 'a'
 fire trigger=Abs action=Abs 
 
# make the flippers trigger on keys
 keydown key=d action=FlipL
 keydown key=d action=FlipR
 


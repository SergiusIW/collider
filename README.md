#Collider 

Collider is a Java library for efficient and precise 2-D collision 
detection with a simple interface. Collider uses [continuous collision 
detection](http://en.wikipedia.org/wiki/Collision_detection#A_posteriori_.28discrete.29_versus_a_priori_.28continuous.29),
which basically means that the time of the collision is determined very 
precisely as opposed to using a time-stepping method. 

###Download 

The latest release build of Collider is available for download at 
http://www.matthewmichelotti.com/projects/collider/. This comes with 
binary and source JAR files and javadocs. 

###Dependencies 

The collider, demos/collider-demos, and demos/collider-demos-desktop 
projects each contain a file named "dependencies.txt" that describes 
which jar libraries are necessary for compilation. 

Collider depends on [LibGDX](http://libgdx.badlogicgames.com/). LibGDX 
is only needed for some of its primitive data structures, so it is 
possible that this dependency may be removed at some point in the 
future. Even so, the Collider demos will continue to depend on LibGDX. 

###Documentation 

Javadocs for Collider come with the distribution that you download. The 
Javadocs for the latest release may be read online at
http://www.matthewmichelotti.com/projects/collider/api/. 

###Demos 

There is currently no tutorial for using the Collider library. Along 
with reading the javadocs, you can learn how to use Collider by studying 
the code for the demos. These demos were used to make the [youtube 
video](http://www.youtube.com/watch?v=sFNw-wYebOc). The code for the 
demos can be found in two projects in the [github 
repository](https://github.com/SergiusIW/collider), demos/collider-demos 
and demos/collider-demos-desktop. You can run these demos by importing 
the two projects into [Eclipse](http://www.eclipse.org/), adding the 
necessary libraries described in the dependencies.txt files, and running 
the main method in the collider-demos-desktop project. 

### License 

Collider is licensed under the [Apache 2.0 
License](http://www.apache.org/licenses/LICENSE-2.0.html). 

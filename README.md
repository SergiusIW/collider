#Collider 

Collider is a Java library for efficient and precise 2-D collision 
detection with a simple interface. Collider uses [continuous collision 
detection](http://en.wikipedia.org/wiki/Collision_detection#A_posteriori_.28discrete.29_versus_a_priori_.28continuous.29),
which basically means that the time of the collision is determined very 
precisely as opposed to using a time-stepping method.

###Download 

Release builds of Collider are available at
https://github.com/SergiusIW/collider/releases.
At some point in the future I will start uploading release builds to Maven Central.

###Documentation 

Javadocs for Collider come with the distribution that you download. The 
Javadocs for the latest release may be read online at
http://www.matthewmichelotti.com/projects/collider/api/.

###Source Code

This project is open-source and available on GitHub.
You can find the source code at https://github.com/SergiusIW/collider.

###Demos 

There is currently no tutorial for using the Collider library. Along 
with reading the javadocs, you can learn how to use Collider by studying 
the code for the demos. These demos were used to make the [youtube 
video](http://www.youtube.com/watch?v=sFNw-wYebOc). The code for the 
demos can be found in two modules in the [github 
repository](https://github.com/SergiusIW/collider), demos-core
and demos-desktop.

###Dependencies

The Collider library does not depend on any third party libraries.
However, the Collider demos depend on [LibGDX](http://libgdx.badlogicgames.com/).
Building with Gradle will download these dependencies for you.
See the build.gradle files for more details.

### License 

Collider is licensed under the [Apache 2.0 
License](http://www.apache.org/licenses/LICENSE-2.0.html). 

### Example Game

I've made a game called [Weaponless](http://www.matthewmichelotti.com/games/weaponless/)
that uses this library to handle collision detection.
Check it out at http://www.matthewmichelotti.com/games/weaponless/.
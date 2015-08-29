#Collider 

Collider is a Java library for efficient and precise 2-D collision 
detection with a simple interface. Collider uses [continuous collision 
detection](http://en.wikipedia.org/wiki/Collision_detection#A_posteriori_.28discrete.29_versus_a_priori_.28continuous.29),
which basically means that the time of the collision is determined very 
precisely as opposed to using a time-stepping method.

### Looking Forward

It's been a couple of years now (August 2015) since I wrote this library.
I like the simple public API that collider currently has, so that is unlikely to change too much.
There are a few plans I have for changes in the future:
* Better examples
* Maven integration
* Removing any object pooling from the public api, or at least making it an experimental option
* Possibly adding support for arbitrary polygons as opposed to just axis-aligned rectangles and circles, although I probably won't do this unless I have a use case for it
* Possibly a [Rust](https://www.rust-lang.org/) port of the library, as I am very interested in this new programming language
* Using builder pattern to configure Collider settings

Collider *is not and will never be* a physics engine library.  It is meant for continuous collision detection only.  In principle someone could develop a physics engine on top of Collider, although a piece of advice: for many 2-D games a realistic physics engine is not helpful.  I suppose I should define what I mean by a physics engine just to be clear: a physics engine is a software library for simulating (at least) rigid body dynamics where the bodies have finite mass.  A physics engine must compute and apply the correct normal forces on these bodies based on where they contact each other.

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

### Games using Collider

Currently, the only game that uses Collider is a free (closed-source) game I made named [Weaponless](http://www.matthewmichelotti.com/games/weaponless/).
Check it out at http://www.matthewmichelotti.com/games/weaponless/.
If you do make a project that uses Collider, I'd be happy to hear about it.

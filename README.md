#Collider

Collider is a Java library for efficient and precise 2-D collision detection
with a simple interface.
Collider uses [continuous collision detection](http://en.wikipedia.org/wiki/Collision_detection),
which basically means that the time of the collision is determined
very precisely as opposed to using a time-stepping method.

Links to website, release jar files, javadocs, and youtube video will be provided shortly.

##Dependencies

Collider depends on [LibGDX](http://libgdx.badlogicgames.com/),
so gdx.jar will need to be in the classpath.
LibGDX is only needed for some of its primitive data structures,
so it is possible that this dependency may be removed at some point
in the future.

##Demos

There is currently no tutorial for using the Collider library.
Along with reading the javadocs, you can learn how to use
Collider by studying the code for the demos.
These demos were used to make the youtube video.
The code for the demos can be found in the "demos" folder
of the [github repository](https://github.com/SergiusIW/collider).

The Demos class implements an ApplicationListener used by LibGDX.
If your not familiar with LibGDX, you can learn about it on [its website](http://libgdx.badlogicgames.com/).
For new LibGDX users, here is a quick summary of how to get the demos running:

*Prerequisite: you should be familiar with [Eclipse](http://www.eclipse.org/) and have it installed, or [ADT](http://developer.android.com/sdk/index.html) if you want to develop for Android
*Download the latest release or nightly version of LibGDX from http://libgdx.badlogicgames.com/download.html
*Unzip this file, though you may want to keep a copy of the zipped file
*Open the folder and run gdx-setup-ui
*Follow the instructions in this GUI to create a new LibGDX project and import it into Eclipse
*Going to the desktop version of the Eclipse project, you should be able to run a main method that will display a LibGDX logo
*At this point, you can either import the Collider demos project and redirect the project references to point to this project, or you can copy and paste the source files from the Collider demos project into the existing project
*You will need to add collider.jar to the "libs" directory alongside gdx.jar
*The Main file should use the Demos class as the application listener
*Note that the screen dimensions of these demos are expected to be 1280x720 (since they were used to make a youtube video), and it will be scaled to fit if this is not the case

## License
Collider is licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).

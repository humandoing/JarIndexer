# JarIndexer - Index Your Jar Files - Reduce Your Headaches

## What is it?

JarIndexer is a simple Swing/GUI app that will recursively search a chosen directory for JAR files, and index the contents.

What this allows you to do is figure out which JAR files contain specific classes. For example, if you end up with a ClassNotFoundException in your app, you can use JarIndexer to figure out which JAR file the class you're missing is stored in, and subsequently you can add that JAR to your classpath.

## Why is the code old and crappy?

The code for JarIndexer is old and crappy because I haven't worked on it for about 5 years. Actually, I can't even remember how long it's been. But I keep getting emails about it, and people keep using it, and it still works. So better to let it out into the world than keep it locked up on my laptop.

## How does it work?

For a brief usage guide, please go to [the JarIndexer home page] [1]

   [1]: http://humandoing.net/jarindexer "JarIndexer Home Page"

## Can I build it?

You sure can. The ANT build file has a bunch of great tasks, one of which "rebuild", completely rebuilds the whole thing. Not only that, but it creates native builds for both Windows and OSX. 

## Resources

[png2ico] [2] - Used to convert PNG files to application icons.

[2]: http://www.winterdrache.de/freeware/png2ico/

[launch4j] [3] - Creates native Windows executable.

[3]: http://launch4j.sourceforge.net/

[JarBundler] [4] - - Creates native Mac OS X executable. 

[4]: http://informagen.com/JarBundler/

[HSQLDB] [5] - Wonderful little embedded database that is used as the storage engine.

[5]: http://hsqldb.org/
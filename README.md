# ImageViewer <img src="src/main/resources/image/branding/app-icon.png" alt="drawing" width="54"/>

### Cross Platform Image Viewer

An open source image viewer. Create local image collections
for recursively looking through directories on your local system.
Images are indexed and sorted based on jpeg exif date. The original
use case was to explore images on a hard disk that were in the
following directory structure.

- root
    - 2011-03-28
    - 2020-06-13
    - 2021-09-16
 
<img src="src/main/resources/image/branding/app-icon.png" alt="drawing" width="200"/>

### Performance

When adding new directories, especially directories with lots
of images, indexing will occur on ALL images and may use a
significant amount of CPU. Once indexing is completed subsequent
loads of the application are fast.

The app is a memory hog even though `-Xmx500m` is supplied for
max heap space. Memory usage could likely be improved by reducing
in memory image cache sizes, however the cache needs to be 
large enough to hold all on screen images, which can be up to 
2,000 (64x64) images when zoomed out. 

### Image Formats

Currently only jpeg images are supported. To add other image
formats a `ImageLoader` and `TypeDefinition` need
to be added in `com.onebyte_llc.imageviewer.backend.image`.
Then the new `ImageLoader`  needs to be registered in the
backend `Context` constructor. An example of this is
`JpegImageLoader` and `JpegImageTypeDefinition`.

### Running

To run the java project on any operating system

```
./gradlew run
```

### Building an OS app image

To build a native app image run the following on the target OS.
It should work on macOS, Windows, and Linux.

```
./gradlew jpackageImage
```
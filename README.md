# ImageViewer

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

### Performance

When adding new directories, especially directories with lots
of images, indexing will occur on ALL images and may use a
significant amount of CPU. Once indexing is completed subsequent
loads of the application are fast.

### Image Formats

Currently only jpeg images are supported. To add other image
formats a `ImageLoader` and `TypeDefinition` need
to be added in `com.onebyte_llc.imageviewer.backend.image`.
Then the new `ImageLoader`  needs to be registered in the
backend `Context` constructor. An example of this is
`JpegImageLoader` and `JpegImageTypeDefinition`.

### Running

To run the java project directly

```
./gradlew run
```

### macOS Build

### Windows Build

### Linux Build
There are no configured `jpackage` scripts for linux, but you can
run it directly using `./gradlew run`

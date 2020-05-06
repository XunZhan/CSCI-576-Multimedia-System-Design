# CSCI 576 Multimedia Project

## Course Information

**Course Name:** CSCI 576 Multimedia System Design

**Instructor:** Prof. [Parag Havaldar](https://viterbi.usc.edu/directory/faculty/Havaldar/Parag), USC

## Project Introduction

The course project is meant to give you an in-depth understanding of some of the areas in multimedia technology. Since this is a broad field, there can be a variety interesting projects that can be done depending on your interests which can also extend to related and complementary topics that are taught in class.

![](https://bloggg-1254259681.cos.na-siliconvalley.myqcloud.com/lbika.jpg)

In this project you will implement a media synopsis algorithm that produces a `synopsis image` summarizing media content. Synopsis is an ancient Greek word that means "general view" or a "summary view". As input you will take a path to a folder that contains various visual media elements – video (with audio) and images. Normally all these media elements at one location (folder, http address) should be contextual similar eg video and images of a personal event such as vacation, graduation, wedding. The synopsis image should give you a good flavor and representation of all the media content. Furthermore, you are also tasked to create an interactive player that can interact with this “synopsis” image so that when you click on some location of the synopsis image, this will trigger the corresponding video to play (with audio synchronized) from that contextual location or show the corresponding image.

## Inputs & Outputs

**Test Data:** [4 Videos (with Audio) & 101 Images](https://drive.google.com/open?id=1OXCDmBHahdG0k7VBfTQ2PRTmHMHfoxmG)

### Inputs

Input to your Process: A folder `TestData` that contains

- Several video folders that contain `.rgb` files in `CIF` format (352 x 288) and a corresponding audio file in `.wav` format, synced to video
- An `image` folder that contains `.rgb` files in `CIF` format (352 x 288)

### Expected Outputs

- **Step 1:** A synopsis image (or a hierarchy of images) for the media elements in the
input folder. This image will a visual representation of all the "important" parts of the media elements. This can be an offline process (let's call it `CreateSynopsisImage`).

- **Step 2:** You are also required to design and implement an interface that loads the synopsis image and allows to explore the visual content. You should be able to click on a location in any "interesting" area in your synopsis image which will result in playing a video (with audio sync) from that contextual location. Or if the "interesting" area came from an image, then show an image(s). Step 1 should also create appropriate pointers/data structures (`synopsis.metafile`) to help the interface index into browsing the A/V content.

**Example Invocations:**

**Step 1:**

```sh
./CreateSynopsisImage <locationToFolder>  # generates synopsis.rgb and synopsis.metafile
```

**Step 2:**

```sh
./ExploreSynopsis synopsis.rgb
```

## Our Work

```sh
[Explore Synopsis] Root Directory: ../
[Explore Synopsis] TestData Directory: TestData
[Parser] Loading Synopsis ................ Completed (1 synopsis).
[Parser] Loading Metafile ................ Completed (1 metafile).
[Parser] Loading Audios .................. Completed (4 audios).
[Parser] Loading Frames for Video 1 ...... Completed (2552 frames).
[Parser] Loading Frames for Video 2 ...... Completed (2972 frames).
[Parser] Loading Frames for Video 3 ...... Completed (1772 frames).
[Parser] Loading Frames for Video 4 ...... Completed (1802 frames).
[Parser] Loading Images .................. Completed (7 images).
[Explore Synopsis] Initialization Finished.
```

![](https://bloggg-1254259681.cos.na-siliconvalley.myqcloud.com/9e6im.png)


## References

- [Video Tapestries with Continuous Temporal Zoom](http://www.cs.princeton.edu/gfx/pubs/Barnes_2010_VTW/index.php)
- [Summarizing Visual Data Using Bidirectional Similarity](http://www.wisdom.weizmann.ac.il/~vision/VisualSummary.html)
- [PatchMatch: A Randomized Correspondence Algorithm for Structural Image Editing](https://gfx.cs.princeton.edu/pubs/Barnes_2009_PAR/index.php)
- [yahoo/hecate](https://github.com/yahoo/hecate)
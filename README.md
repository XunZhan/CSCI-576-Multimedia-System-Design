CSCI-576-Multimedia-System-Design
==========================

### Class Introductions
CSCI 576 Multimedia System Design is provided by Professor Parag Havaldar in USC.

### Assignment Introduction
##### Content Browser
Inputs
Input to your Process: A folder which contains
1. A video file in CIF format (352x288) and a corresponding audio file in WAV
format, synced to video.
2. Images in a CIF format (352x288)

Expected Outputs:
1. A synopsis image (or a hierarchy of images) for the media elements in the
input folder. This image will a visual representation of all the “important” parts of the media elements. This can be an offline process (lets call is CreateSynopsisImage )
2. You are also required to design and implement an interface that loads the synopsis image and allows to explore the visual content. You should be able click on a location in any “interesting” area in your synopsis image which will result in playing a video (with audio sync) from that contextual location. Or if the “interesting” area came from an image, then show an image(s). Step 1 should also create appropriate pointers/data structures to help the interface index into browsing the A/V content

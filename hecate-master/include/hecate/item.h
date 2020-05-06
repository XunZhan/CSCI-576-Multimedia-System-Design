#include <string>

#ifndef HECATE_ITEM_H
#define HECATE_ITEM_H
#define IMAGE_WIDTH 352
#define IMAGE_HEIGHT 288

// class Item
// ----------
enum Type { FRAME, IMAGE };

class Item
{
public:
    Type type;
    
    // frame
    int videoID;
    int shotStart;
    int shotEnd;
    
    // image
    std::string img_filename;
    
    // frame & image
    int index;  // keyFrameIndex (1-based), image order (0-based)
    
    
    // frame constructor
    Item(Type t, int vid, int i)
            : type(t), videoID(vid), index(i) { }
    
    // image constructor
    Item(Type t, int i, std::string str)
            : type(t), index(i), img_filename(str) { }
};

class Generator
{
public:
    
    std::string testdataDir = "../TestData";  // SHOULD BE args[1]
    std::string videoDir = "/video";
    std::string imageDir = "/image";
    
};




#endif //HECATE_ITEM_H


#include <string>
#include <vector>

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
    std::vector<Item> frame_list;
    std::vector<Item> image_list;
    
    Generator(std::string dataDir)
    {
      frame_list = std::vector<Item>();
      image_list = std::vector<Item>();
      
      test_data_dir = dataDir;
      video_dir = "/video";
      image_dir = "/image";
      synopsis_image_dir = "../synopsis.rgb";
      synopsis_metafile_dir = "../synopsis.metafile";
    }
    
private:
    std::string test_data_dir ;
    std::string video_dir ;
    std::string image_dir;
    std::string synopsis_image_dir;
    std::string synopsis_metafile_dir;
    
public:
    void generateSynopsisImage();
    void generateMetafile();
    
};





#endif //HECATE_ITEM_H


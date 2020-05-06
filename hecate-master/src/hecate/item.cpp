
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <fstream>
#include "hecate/Image.h"
#include "hecate/item.h"

using namespace std;

// synopsis image
void testSynopsisImage()
{
//  std::vector<Item> frame_list;
//  std::vector<Item> image_list;
//
//  // generate data
//  // -------------
//
//  // frame
//  frame_list.push_back(Item(FRAME, 1, 30));
//  frame_list.push_back(Item(FRAME, 1, 92));
//  frame_list.push_back(Item(FRAME, 1, 104));
//  frame_list.push_back(Item(FRAME, 1, 152));
//  frame_list.push_back(Item(FRAME, 1, 194));
//  frame_list.push_back(Item(FRAME, 1, 220));
//  frame_list.push_back(Item(FRAME, 1, 247));
//  frame_list.push_back(Item(FRAME, 1, 262));
//  frame_list.push_back(Item(FRAME, 1, 314));
//  frame_list.push_back(Item(FRAME, 1, 362));
//  frame_list.push_back(Item(FRAME, 2, 30));
//  frame_list.push_back(Item(FRAME, 2, 92));
//  frame_list.push_back(Item(FRAME, 3, 30));
//  frame_list.push_back(Item(FRAME, 3, 92));
//  frame_list.push_back(Item(FRAME, 4, 30));
//  frame_list.push_back(Item(FRAME, 4, 92));
//
//  // image
//  image_list.push_back(Item(IMAGE, 0, "image-0003.rgb"));
//  image_list.push_back(Item(IMAGE, 1, "image-0058.rgb"));
//  image_list.push_back(Item(IMAGE, 2, "image-0073.rgb"));
//  image_list.push_back(Item(IMAGE, 3, "image-0091.rgb"));
//  image_list.push_back(Item(IMAGE, 4, "image-0177.rgb"));
//  image_list.push_back(Item(IMAGE, 5, "image-0242.rgb"));
//  image_list.push_back(Item(IMAGE, 6, "image-0320.rgb"));
//
//  generateSynopsisImage(frame_list, image_list, "synopsis.rgb");
//  // SHOULD BE "../synopsis.rgb"
}

void Generator::generateSynopsisImage()
{
  int total_num = frame_list.size() + image_list.size();
  std::vector<MyImage> img_vec(total_num);
  
  // frame
  int count = 0;
  for (int i = 0; i < frame_list.size(); ++i)
  {
    Item item = frame_list[i];
    MyImage img;
    char path[100];
    sprintf(path, "%s%s%d/image-%04d.rgb", test_data_dir.c_str(), video_dir.c_str(), item.videoID, item.index);
    
    img.setWidth(IMAGE_WIDTH);
    img.setHeight(IMAGE_HEIGHT);
    img.setImagePath(path);
    
    if (img.ReadImage())
    {
      img_vec[count] = img;
      ++count;
    }
  }
  
  // image
  for (int i = 0; i < image_list.size(); ++i)
  {
    Item item = image_list[i];
    MyImage img;
    char path[100];
    sprintf(path, "%s%s/%s", test_data_dir.c_str(), image_dir.c_str(), item.img_filename.c_str());
    
    img.setWidth(IMAGE_WIDTH);
    img.setHeight(IMAGE_HEIGHT);
    img.setImagePath(path);
    
    if (img.ReadImage())
    {
      img_vec[count] = img;
      ++count;
    }
  }
  
  // synopsis data
  int synopsis_width = IMAGE_WIDTH * total_num;
  int synopsis_height = IMAGE_HEIGHT;
  MyImage synopsis_img;
  synopsis_img.setWidth(synopsis_width);
  synopsis_img.setHeight(synopsis_height);
  synopsis_img.initData();
  
  std::cout << "[Synopsis Image] Start Writing to file " << synopsis_width << " x " << synopsis_height << "\n";
  
  for (long y = 0; y < synopsis_height; ++y)
  {
    for (long x = 0; x < synopsis_width; ++x)
    {
      int i = y * synopsis_width + x;
      
      int img_index = x / IMAGE_WIDTH;
      int row = y;
      int col = x % IMAGE_WIDTH;
      
      long img_loc = (row * IMAGE_WIDTH + col) * 3;
      long syn_loc = 3 * i;
      // B
      synopsis_img.setDataAt(img_vec[img_index].getDataAt(img_loc), syn_loc);
      // G
      synopsis_img.setDataAt(img_vec[img_index].getDataAt(img_loc + 1), syn_loc + 1);
      // R
      synopsis_img.setDataAt(img_vec[img_index].getDataAt(img_loc + 2), syn_loc + 2);
    }
  }
  
  // blur
  std::cout << "[Synopsis Image] Blurring ..." << "\n";
  unsigned char temp[synopsis_width * synopsis_height * 3];
  for (long i = 0; i < synopsis_width * synopsis_height * 3; ++i)
  {
    temp[i] = synopsis_img.getDataAt(i);
  }
  
  // blurring block info
  int lo = -2;
  int hi = +2;
  int size = 3;
  
  std::vector< std::vector<int> > dir;
  
  for (int i = lo; i <= hi; ++i)
  {
    for (int j = lo; j <= hi; ++j)
    {
      dir.push_back( { i, j } );
    }
  }
  
  for (int x = 0; x < synopsis_width; ++x)
  {
    for (int y = 0; y < synopsis_height; ++y)
    {
      if (x != 0 && x != synopsis_width - 1)
      {
        if (x % IMAGE_WIDTH == 0)
        {
          int left_x = x - size;
          int right_x = x + size - 1;
          
          for (int i = left_x; i <= right_x; ++i)
          {
            for (int j = 0; j <= synopsis_height - 1; ++j)
            {
              long index = (j * synopsis_width + i) * 3;
              unsigned int total = 0;
              unsigned int sum_b = 0;
              unsigned int sum_g = 0;
              unsigned int sum_r = 0;
              for (auto d : dir)
              {
                int ii = i + d[0];
                int jj = j + d[1];
                // check jj
                if (jj >= 0 && jj <= synopsis_height - 1)
                {
                  long the_index = (jj * synopsis_width + ii) * 3;
                  sum_b += (unsigned int) temp[the_index + 0];
                  sum_g += (unsigned int) temp[the_index + 1];
                  sum_r += (unsigned int) temp[the_index + 2];
                  total += 1;
                }
              }
              unsigned int b = sum_b / total;
              unsigned int g = sum_g / total;
              unsigned int r = sum_r / total;
              synopsis_img.setDataAt(b, index + 0);
              synopsis_img.setDataAt(g, index + 1);
              synopsis_img.setDataAt(r, index + 2);
            }
          }
        }
      }
    }
  }
  
  synopsis_img.setImagePath(synopsis_image_dir.c_str());
  synopsis_img.WriteImage();
  
  std::cout << "[Synopsis Image] Finished! (output: " << synopsis_image_dir << ")\n";
}

// metafile
void testMetafile()
{
//  std::vector<Item> frame_list;
//  std::vector<Item> image_list;
//
//  // generate data
//  // -------------
//
//  // frame
//  frame_list.push_back(Item(FRAME, 1, 30));
//  frame_list.push_back(Item(FRAME, 1, 92));
//  frame_list.push_back(Item(FRAME, 1, 104));
//  frame_list.push_back(Item(FRAME, 1, 152));
//  frame_list.push_back(Item(FRAME, 1, 194));
//  frame_list.push_back(Item(FRAME, 1, 220));
//  frame_list.push_back(Item(FRAME, 1, 247));
//  frame_list.push_back(Item(FRAME, 1, 262));
//  frame_list.push_back(Item(FRAME, 1, 314));
//  frame_list.push_back(Item(FRAME, 1, 362));
//  frame_list.push_back(Item(FRAME, 2, 30));
//  frame_list.push_back(Item(FRAME, 2, 92));
//  frame_list.push_back(Item(FRAME, 3, 30));
//  frame_list.push_back(Item(FRAME, 3, 92));
//  frame_list.push_back(Item(FRAME, 4, 30));
//  frame_list.push_back(Item(FRAME, 4, 92));
//
//  // image
//  image_list.push_back(Item(IMAGE, 0, "image-0003.rgb"));
//  image_list.push_back(Item(IMAGE, 1, "image-0058.rgb"));
//  image_list.push_back(Item(IMAGE, 2, "image-0073.rgb"));
//  image_list.push_back(Item(IMAGE, 3, "image-0091.rgb"));
//  image_list.push_back(Item(IMAGE, 4, "image-0177.rgb"));
//  image_list.push_back(Item(IMAGE, 5, "image-0242.rgb"));
//  image_list.push_back(Item(IMAGE, 6, "image-0320.rgb"));
//
//  generateMetafile(frame_list, image_list, "synopsis.metafile");
//  // SHOULD BE "../synopsis.metafile"
}

void Generator::generateMetafile()
{
  std::cout << "[Synopsis Metafile] Writing " << (frame_list.size() + image_list.size()) << " items ..." << " to [" << synopsis_metafile_dir << "]\n";
  int synopsis_width = IMAGE_WIDTH * (frame_list.size() + image_list.size());
  int synopsis_height = IMAGE_HEIGHT;
  int synopsis_span = IMAGE_WIDTH;
  int num_video = frame_list.at(frame_list.size() - 1).videoID;
  // bad design (although we know it must be 4)
  
  ofstream output_file;
  output_file.open(synopsis_metafile_dir);
  if (output_file.is_open())
  {
    // basic
    output_file << "size " << synopsis_width << " " << synopsis_height << "\n";
    output_file << "span " << synopsis_span << "\n";
    output_file << "numVideo " << num_video << "\n";
    // frame
    for (int i = 0; i < frame_list.size(); ++i)
    {
      const Item& item = frame_list[i];
      output_file << "frame " << item.videoID << " " << item.index << "\n";
    }
    // image
    for (int i = 0; i < image_list.size(); ++i)
    {
      const Item& item = image_list[i];
      output_file << "image " << item.index << " " << item.img_filename << "\n";
    }
  }
  else
  {
    std::cout << "[Synopsis Metafile] Cannot open!" << "\n";
  }
  
  output_file.close();
  
  std::cout << "[Synopsis Metafile] Finished writing!" << "\n";
}
/*
 * HECATE Yahoo Video Processing Library - Binary
 *
 * Copyright 2016 Yahoo Inc.
 * Licensed under the terms of the Apache 2.0 License.
 * See LICENSE file in the project root for terms.
 *
 * Developer: Yale Song (yalesong@yahoo-inc.com)
 */

#include "hecate/item.h"
#include "hecate/hecate.hpp"

using namespace std;
using namespace cv;


int main( int argc, char** argv )
{
  hecate_copyright();
  if( argc<3 )
    hecate_usage();

  // Read input params
  hecate_params opt;
  hecate_parse_params( argc, argv, opt );

  // Run VIDSUM
  vector<int> v_thumb_idx;
  vector<int> v_cluster_id;
  vector<hecate::Range> v_gif_range;
  vector<hecate::Range> v_mov_range;

  Generator g = Generator(opt.in_video);
  run_hecate( opt, v_thumb_idx,v_cluster_id, g.frame_list, g.image_list, v_gif_range, v_mov_range );
  
  std::sort(g.frame_list.begin(), g.frame_list.end(), [](Item& a, Item &b)
  {
    if (a.videoID < b.videoID)
      return true;
    else if(a.videoID > b.videoID)
      return false;
      
      return a.index < b.index;
  });
  
  g.generateSynopsisImage();
  g.generateMetafile();
}

/*
 * HECTATE Video Processing Library - Main
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

void run_hecate( hecate_params& opt, vector<int>& v_thumb_idx, vector<int>&v_cluster_id,
                 vector<Item>& frame_list, vector<Item>& image_list,
                 vector<hecate::Range>& v_gif_range,vector<hecate::Range>& v_mov_range)
{
//  if( !hecate::file_exists(opt.in_video) ) {
//    fprintf(stderr, "File not exist: %s\n", opt.in_video.c_str());
//    return;
//  }

  v_thumb_idx.clear();
  v_cluster_id.clear();
  v_gif_range.clear();
  v_mov_range.clear();

  hecate::Clock::time_point t0;
  hecate::VideoParser parser;
  
  vector<hecate::ShotRange> v_shot_range;
  Mat histo_features;
  Mat diff_features;
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // Parse video  and image
  //
  ////////////////////////////////////////////////////////////////////////////
  
  if( opt.debug ) {
    printf("run_hecate: Video segmentation and keyframe extraction\n");
    t0 = hecate::Clock::now();
  }

  hecate::parser_params parser_opt;
  parser_opt.step_sz = opt.step_sz;
  parser_opt.gfl = opt.gfl;
  parser_opt.fltr_begin_sec = ( opt.fltr_begin_sec<0 )
      ? max(0.5, 0.05 * parser.meta.duration) : opt.fltr_begin_sec;
  parser_opt.fltr_end_sec = ( opt.fltr_end_sec<0 )
      ? max(0.5, 0.10 * parser.meta.duration) : opt.fltr_end_sec;
  parser_opt.max_duration = opt.max_duration;
  parser_opt.ignore_rest = (opt.max_duration>0); // ignore parts after max_nfrms
  parser_opt.debug = opt.debug;
  
  // PARSE
  
  if (opt.in_video.empty())
  {
    printf("Error: no input cir path\n");
    return;
  }
  
  /*-----------------------------------------------------------*/
  // for each video dir
  /* -----------------------------------------------------------*/
  for (int i = 0; i<4; i++)
  {
    string vname = "video" + to_string(i+1);
    parser.clear();
    parser.basePath = opt.in_video + "/" + vname;
    
    v_thumb_idx.clear();
    v_cluster_id.clear();
    v_gif_range.clear();
    v_mov_range.clear();
    
    v_shot_range = parser.parse_video( parser.basePath, parser_opt );
    if( v_shot_range.empty() ) {
      fprintf(stderr, "run_hecate: Failed to parse the video\n");
      return;
    }
    
    histo_features = parser.get_frame_features();
    diff_features  = parser.get_frame_diff_features();
    opt.step_sz    = parser.get_effective_step_size();
    
    // If video is shorter than desired summary length
    if( opt.mov && opt.lmov >= parser.meta.duration ) {
      fprintf( stderr, "run_hecate: Video duration is %.2f seconds, "
                       "shorter than the requested summary of length %.2f seconds.\n"
                       "\tVideo summarization is disabled.",
               parser.meta.duration, (double)opt.lmov);
      opt.mov = false;
    }
    
    // Check desired resolution of output
    if( opt.jpg_width_px<0 || opt.jpg_width_px > parser.meta.width ) {
      //fprintf( stderr, "run_hecate: Forcing jpg_width_px to %d\n",parser.meta.width);
      opt.jpg_width_px = parser.meta.width;
    }
    if( opt.gif_width_px<0 || opt.gif_width_px > parser.meta.width ) {
      //fprintf( stderr, "run_hecate: Forcing gif_width_px to %d\n",parser.meta.width);
      opt.gif_width_px = parser.meta.width;
    }
    if( opt.mov_width_px<0 || opt.mov_width_px > parser.meta.width ) {
      //fprintf( stderr, "run_hecate: Forcing mov_width_px to %d\n",parser.meta.width);
      opt.mov_width_px = parser.meta.width;
    }
    
    if( opt.debug ) {
      hecate::print_elapsed_time( t0, "run_hecate" );
      //hecate::print_video_metadata( opt.in_video, parser.meta );
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //
    // Analyze video
    //
    ////////////////////////////////////////////////////////////////////////////
    
    // Print shot info
    if( opt.info_shot ) {
      printf("shots: ");
      for(size_t i=0; i<v_shot_range.size(); i++) {
        printf("[%d:%d]", v_shot_range[i].start, v_shot_range[i].end);
        if( i<v_shot_range.size()-1 )
          printf(",");
      }
      printf("\n");
    }
    
    // Print keyframe indices
    if( opt.info_keyfrm ) {
      vector<int> keyfrms;
      for(size_t i=0; i<v_shot_range.size(); i++) {
        for(size_t j=0; j<v_shot_range[i].v_idx.size(); j++) {
          keyfrms.push_back(v_shot_range[i].v_idx[j]);
        }
      }
      
      printf("keyframes: [");
      for(size_t i=0; i<keyfrms.size(); i++) {
        printf("%d", keyfrms[i]);
        if( i<keyfrms.size()-1 )
          printf(",");
      }
      printf("]\n");
    }
    
    // Thumbnail extraction module
    if( opt.jpg ) {
      if( opt.debug ) {
        printf("run_hecate: Video keyframe detection\n");
        t0 = hecate::Clock::now();
      }
      
      detect_thumbnail_frames( opt, parser.meta, v_shot_range,
                               histo_features, diff_features,
                               v_thumb_idx, v_cluster_id);
      
      if( opt.debug ) {
        hecate::print_elapsed_time( t0, "run_hecate" );
      }
    }
    
    // Print debugging info
    if( opt.debug ) {
      if( opt.jpg ) {
        printf("hecate: video thumbnail indices: [ ");
        for(size_t i=0; i<v_thumb_idx.size(); i++)
          printf("%d ", v_thumb_idx[i]);
        printf("]\n");
      }
    }
    
    // Produce results
    if( opt.jpg ) {
      //generate_thumbnails( opt, v_thumb_idx,v_cluster_id);
      generate_thumbnails( opt, v_thumb_idx,v_cluster_id, frame_list, image_list, parser.basePath);
    }
  }
  
  
  /*-----------------------------------------------------------*/
  // for image dir
  /* -----------------------------------------------------------*/
  v_thumb_idx.clear();
  v_cluster_id.clear();
  v_gif_range.clear();
  v_mov_range.clear();
  opt.in_photo = opt.in_video + "/image";
  parser.basePath = opt.in_photo;
  
  parser.parse_photo( opt.in_photo, parser_opt, v_thumb_idx, v_cluster_id);
  
  // Print debugging info
  if( opt.debug ) {
    if( opt.jpg ) {
      printf("hecate: photo thumbnail indices: [ ");
      for(size_t i=0; i<v_thumb_idx.size(); i++)
        printf("%d ", v_thumb_idx[i]);
      printf("]\n");
    }
  }
  
  // Produce results
  if( opt.jpg ) {
    //generate_thumbnails( opt, v_thumb_idx,v_cluster_id);
    generate_thumbnails( opt, v_thumb_idx,v_cluster_id,frame_list, image_list, parser.basePath);
  }
  
}






/*
 * HECATE Video Processing Library - Thumbnail
 *
 * Copyright 2016 Yahoo Inc.
 * Licensed under the terms of the Apache 2.0 License.
 * See LICENSE file in the project root for terms.
 *
 * Developer: Yale Song (yalesong@yahoo-inc.com)
 */
#include "hecate/Image.h"
#include "hecate/hecate.hpp"

using namespace std;
using namespace cv;

////////////////////////////////////////////////////////////////////////////
//
// Select most representative frames (thumbnails)
//
// Perform k-means++ (k=#thumbnails) over valid frames
// Thumbnails are selected by sorting clusters by their size and
//   selecting a frame from the longest subshot
//
////////////////////////////////////////////////////////////////////////////

void detect_thumbnail_frames( hecate_params& opt, hecate::video_metadata& meta,
                              const vector<hecate::ShotRange>& v_shot_range,
                              const Mat& X, const Mat& diff,
                              vector<int>& v_thumb_idx, vector<int> & v_cluster_id)
{
  v_thumb_idx.clear();
  v_cluster_id.clear();

  const int minK = 5;   // min #clusters
  const int maxK = 30;  // max #clusters
  const int nfrm = meta.nframes;

  vector<bool> v_frm_valid(nfrm,false);
  for(size_t i=0; i<v_shot_range.size(); i++) {
    for(size_t j=0; j<v_shot_range[i].v_idx.size(); j++) {
      v_frm_valid[v_shot_range[i].v_idx[j]] = true;
    }
  }

  int nfrm_valid = accumulate(v_frm_valid.begin(),v_frm_valid.end(),0);
  if( nfrm_valid <= 1 ) {
    // If there's no valid frame, pick one most still frame
    int minidx=-1;
    double minval=numeric_limits<double>::max();
    for( int i=0; i<nfrm; i++ ) {
      double val = diff.at<double>(i);
      if( val<minval ) {
        minval = val;
        minidx = i;
      }
    }
    v_thumb_idx.push_back( minidx );
  }
  else if( nfrm_valid <= opt.njpg ) {
    // If not enough frames are left,
    // include all remaining keyframes sorted by shot length
    vector<int> v_shot_len;
    vector<int> v_keyfrm_idx;
    for(size_t i=0; i<v_shot_range.size(); i++) {
      int max_subshot_id=-1;
      int max_subshot_len=-1;
      for(size_t j=0; j<v_shot_range[i].v_range.size(); j++) {
        int shotlen = v_shot_range[i].v_range[j].length();
        if( shotlen>max_subshot_len ) {
          max_subshot_len = shotlen;
          max_subshot_id = j;
        }
      }
      v_shot_len.push_back(max_subshot_len);
      v_keyfrm_idx.push_back(v_shot_range[i].v_range[max_subshot_id].v_idx[0]);
    }

    // Include keyframes sorted by shot length
    vector<size_t> v_srt_idx;  // contains sorted indices
    vector<int> v_srt_val;     // contains sorted values
    hecate::sort( v_shot_len, v_srt_val, v_srt_idx );
    for(size_t i=0; i<v_srt_idx.size(); i++)
      v_thumb_idx.push_back( v_keyfrm_idx[v_srt_idx[v_srt_idx.size()-1-i]] );
  }
  else {
    vector<int> v_valid_frm_idx;
    vector<int> v_valid_frm_shotlen;
    for(size_t i=0; i<v_shot_range.size(); i++) {
      for(size_t j=0; j<v_shot_range[i].v_range.size(); j++) {
        // do not insert repeating idx
        if (find(v_valid_frm_idx.begin(), v_valid_frm_idx.end(), v_shot_range[i].v_range[j].v_idx[0]) != v_valid_frm_idx.end())
          continue;
        v_valid_frm_idx.push_back( v_shot_range[i].v_range[j].v_idx[0] );
        v_valid_frm_shotlen.push_back( v_shot_range[i].v_range[j].length() );
      }
    }

    Mat km_data(nfrm_valid, X.cols, X.type());
    for(size_t i=0; i<v_valid_frm_idx.size(); i++) {
      X.row(v_valid_frm_idx[i]).copyTo( km_data.row(i) );
    }
    
    // Perform k-means (repeat 5 times)
    Mat km_lbl; // integer row vector; stores cluster IDs for every sample.
    Mat km_ctr; // one row per each cluster center.
    int km_k = min(maxK, min(nfrm_valid, max(minK, opt.njpg)));
   // km_k = 5;
   // hecate::perform_kmeans( km_data, km_lbl, km_ctr, km_k , 5);
    
    //calculate distance between ctr
    // if too small then combine
//    int nCtr = km_ctr.rows;
//    float dist[nCtr][nCtr];
//    // initialize
//    for (int rr = 0; rr < nCtr; rr++)
//    {
//      for (int cc = 0; cc < nCtr; cc++)
//      {
//        dist[rr][cc] = 0;
//      }
//    }
//
//    for (int rr = 0; rr < nCtr-1; rr++)
//    {
//      for (int cc = rr+1; cc < nCtr; cc++)
//      {
//        float sum = 0;
//        for(int kk = 0; kk < km_ctr.cols; kk++)
//          sum += pow(abs(km_ctr.at<float>(rr,kk) - km_ctr.at<float>(cc,kk)),2);
//        dist[rr][cc] = sum;
//      }
//    }
//
//  if ( opt.debug )
//  {
//    for (int rr = 0; rr < nCtr; rr++)
//    {
//      for (int cc = 0; cc < nCtr; cc++)
//      {
//        printf("%f ", dist[rr][cc]);
//      }
//      printf("\n");
//    }
//  }
  
    // For k-means with gap statistics
    vector<int> Kset;
    for(int i=km_k; i<=min(nfrm_valid,(int)(2*km_k)); i++)
      Kset.push_back( i );
    km_k = hecate::perform_kmeans_gs( km_data, km_lbl, km_ctr, Kset, 3, 500 );

    // measure cluster size
    vector<int> clust_sz(km_k,0);
    for(int i=0; i<km_lbl.rows; i++)
      clust_sz[ km_lbl.at<int>(i) ] += v_valid_frm_shotlen[i];

    // sort wrt cluster size in an ascending order
    vector<size_t> v_srt_idx; // contains cluster id
    vector<int> v_srt_val;    // contains cluster size
    hecate::sort( clust_sz, v_srt_val, v_srt_idx );

    // obtain thumbnails -- the most still frame per cluster
    for(int i=0; i<km_k; i++) {
      int diff_min_idx = -1;
      double diff_min_val = numeric_limits<double>::max();
      for(int j=0; j<km_lbl.rows; j++) {
        if( km_lbl.at<int>(j) == v_srt_idx[km_k-i-1] ) {
          double mean_diff_j = diff.at<double>(v_valid_frm_idx[j]);
          if( mean_diff_j<diff_min_val ) {
            // check if this is similar to previous one
            //hecate::calc_difference(i, _v_frm_org[i],_v_frm_org[i+1], 40);
            diff_min_idx = j;
            diff_min_val = mean_diff_j;
          }
        }
      }
      // Convert back to the real index
      v_thumb_idx.push_back( v_valid_frm_idx[diff_min_idx] );
      v_cluster_id.push_back(km_lbl.at<int>(diff_min_idx));
    }
//    for(size_t i=0; i<v_valid_frm_idx.size(); i++)
//    {
//      v_thumb_idx.push_back(v_valid_frm_idx[i]);
//      v_cluster_id.push_back(km_lbl.at<int>(i));
//    }

  }
  
  for(size_t i=0; i<v_thumb_idx.size(); i++)
    v_thumb_idx[i] *= opt.step_sz;
}



////////////////////////////////////////////////////////////////////////////
//
// Thumbnail generation
//
////////////////////////////////////////////////////////////////////////////
void generate_thumbnails( hecate_params& opt, vector<int>& v_thumb_idx, vector<int>& v_cluster_id,const std::string& basePath )
{
  char strbuf[256];
  bool forImg = true;
  string videoName;
  
  string filename;
  if (basePath.substr(basePath.length()-6, 5) == "video")
  {
    forImg = false;
    videoName = basePath.substr(basePath.length()-6, 6);
  }

//  string filename = hecate::get_filename( std::string(opt.in_video) );
  
  //VideoCapture vr( opt.in_video );
  int video_width  = 352;
  int video_height = 288;
  double rsz_ratio = (double)(2+opt.jpg_width_px)/video_width;
  
  vector<string> readPathList;
  int frm_idx = 0;
  
  if (forImg)
  {
    // generate path list for images
    readPathList = read_directory(basePath);
  }
  
  for (int i = 0; i<(int)v_thumb_idx.size() && i<opt.njpg; i++)
  {
    // output jpg
    string readPath;
    if (!forImg)
    {
      frm_idx = v_thumb_idx[i]+1;
      string s = to_string(frm_idx);
      string ss = string(4 - s.length(), '0') + s;
      readPath = basePath + "/" + "image-" + ss + ".rgb";
    }
    else
    {
      readPath = readPathList[v_thumb_idx[i]];
    }
    
    MyImage img;
    img.setWidth(352);
    img.setHeight(288);
    const char * cstr = readPath.c_str();
    img.setImagePath(cstr);
    img.ReadImage();
  
    if (img.getImageData() == NULL)
    {
      printf("can not read %s\n", cstr);
      continue;
    }
    Mat frm(video_height,video_width,CV_8UC3);
    frm.data = new unsigned char[video_width * video_height*3];
    for ( int k=0; k<(video_width * video_height*3);k++ )
    {
      frm.data[k]	= img.getImageData()[k];
    }
    
    
//    resize( frm, frm, Size(), rsz_ratio, rsz_ratio, CV_INTER_LINEAR );
//    frm = frm(Rect(0,0,frm.cols-2,frm.rows));


    if (!forImg)
    {
      sprintf( strbuf, "%s/%s_%d_%d.jpg",
               opt.out_dir.c_str(), videoName.c_str(), frm_idx, v_cluster_id[i]);
    } else
    {
      //get origin index
      string str_idx = readPath.substr(readPath.length()-8, 4);
      sprintf( strbuf, "%s/%s_%s_%d.jpg",
               opt.out_dir.c_str(), "image", str_idx.c_str(), v_cluster_id[i]);
    }
    imwrite( strbuf, frm );
    
  }
}


////////////////////////////////////////////////////////////////////////////
//
// read dir
//
////////////////////////////////////////////////////////////////////////////
vector <string> read_directory( const std::string& path )
{
  std::vector <std::string> result;
  dirent* de;
  DIR* dp;
  errno = 0;
  dp = opendir( path.empty() ? "." : path.c_str() );
  if (dp)
  {
    while (true)
    {
      errno = 0;
      de = readdir( dp );
      if (de == NULL) break;
      string tmp = std::string( de->d_name );
      if (tmp.size() > 4 && tmp.substr(tmp.size()-4,tmp.size()) == ".rgb")
      {
        result.push_back( path +  "/" + tmp );
      }
      
    }
    closedir( dp );
    std::sort( result.begin(), result.end() );
  }
  return result;
}



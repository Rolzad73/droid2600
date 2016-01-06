#include <iostream>
#include <iomanip>
#include <fstream>
#include <sstream>
#include <cstring>

using namespace std;

typedef unsigned char uInt8;
typedef unsigned int uInt32;

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
int searchForBytes(const uInt8* image, uInt32 imagesize,
                   const uInt8* signature, uInt32 sigsize)
{
#if 1
  uInt32 count = 0;
  for(uInt32 i = 0; i < imagesize - sigsize; ++i)
  {
    uInt32 matches = 0;
    for(uInt32 j = 0; j < sigsize; ++j)
    {
      if(image[i+j] == signature[j])
        ++matches;
      else
        break;
    }
    if(matches == sigsize)
      ++count;
  }

  return count;
#else
  uInt32 minhits = 2;
  uInt32 count = 0;
  for(uInt32 i = 0; i < imagesize - 3; ++i)
  {
    if(image[i] == 0xEA && image[i+2] >= 0x60 && image[i+2] <= 0x6F)
    {
      ++count;
      i += 3;
    }
    if(count >= minhits)
      break;
  }

  return count;
#endif
}


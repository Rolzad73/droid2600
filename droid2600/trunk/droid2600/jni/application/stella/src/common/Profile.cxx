#include "Profile.hxx"

#include <string>
#include <map>

#ifdef ANDROID
#include <android/log.h>
#endif

using namespace tslib;

Profile * Profile::_instance = NULL;

Profile::Profile()
{
    _startTime = 0;
    _endTime = 0;
}

Profile::~Profile()
{
}

Profile * Profile::getInstance()
{
   if ( NULL == _instance ) {
      _instance = new Profile();
   }
   return _instance;
}

void Profile::start() {
    if (_startTime == 0) {
        _startTime = getTimeMilliseconds();
    }
}

void Profile::startObservation(const std::string & name) {
    _startTimes.erase(name);
    std::pair<std::string, long> nodePair(name,getTimeMilliseconds());
    _startTimes.insert(nodePair);
}

void Profile::stopObservation(const std::string & name) {

    long now = getTimeMilliseconds();

    // get the start time
    long startTime = 0;
    std::map<std::string, long>::const_iterator it;
    it = _startTimes.find(name);
    if ( it == _startTimes.end() ) {
        // no start time found!
        // TODO : serious error - need to warn user and reset 
        return;
    }
    startTime = (*it).second;
//__android_log_print(ANDROID_LOG_INFO, "Profile", "startTime=%i", startTime);

    // now calculate the sampletime end time
    long sampleTime = now - startTime;
//__android_log_print(ANDROID_LOG_INFO, "Profile", "sampleTime=%i", sampleTime);

    // now get the running total time, if it exists
    long runningTotal = 0;
    std::map<std::string, long>::const_iterator it2;
    it2 = _totalTimes.find(name);
    if (it2 == _totalTimes.end() ) {
        // no running time found
    }
    else {
        runningTotal = (*it2).second;
    }
//__android_log_print(ANDROID_LOG_INFO, "Profile", "runningTotal=%i", runningTotal);

    // and finally, update the runningTotal andwrite it to the map
    long totalTime = runningTotal + sampleTime;
//__android_log_print(ANDROID_LOG_INFO, "Profile", "totalTime=%i", runningTotal);

    _totalTimes.erase(name);
    std::pair<std::string, long> nodePair(name,totalTime);
    _totalTimes.insert(nodePair);
}

void Profile::stop() {
    _endTime = getTimeMilliseconds();;
    long updateTime =  (_endTime - _startTime);
    if (updateTime > 1000) {
#ifdef ANDROID
        __android_log_print(ANDROID_LOG_INFO, "Profile", "Total time=%i", (_endTime - _startTime));

        // log each profiled thing and then clear the map
        std::map<std::string,long>::iterator it = _totalTimes.begin();
        while (it != _totalTimes.end()) {
            std::string t = (*it).first;
            long n = (*it).second;
            __android_log_print(ANDROID_LOG_INFO, "        Profile", "%s=%i", t.c_str(), n);
            it++;
        }
#endif
        _totalTimes.clear();
        _startTime = 0;
        _endTime = 0;
    }
}

long Profile::getTimeMilliseconds()
{
   timeval tv;
   gettimeofday(&tv, NULL);

   // convert the timeValue to a long unsigned int reflecting
   // milliseconds since epoch.

   long microSeconds = (tv.tv_sec*1000);
   microSeconds += (tv.tv_usec/1000);
   return microSeconds;
}

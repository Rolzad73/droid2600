#ifndef __Profile__
#define __Profile__

#include <string>
#include <map>

namespace tslib {

class Profile
{

public:

    static Profile * getInstance();

    void start();
    void stop();

    void startObservation(const std::string & name);
    void stopObservation(const std::string & name);

    long getTimeMilliseconds();
   
protected:

   Profile();
   ~Profile();
   
private:

    long _startTime;
    long _endTime;
    std::map<std::string, long> _totalTimes;
    std::map<std::string, long> _startTimes;
    static Profile * _instance;

    
};

} // namespace tslib

#endif // __Profile__

#include <iostream>
#include <fstream>
#include <string>

using namespace std;

int main()
{
    string line;
    ifstream myfile("lichess_db_standard_rated_2016-08.pgn");
    if ( myfile.is_open())
    {
        while ( getline(myfile, line))
        {
//            cout << line << '\n';
        }
        myfile.close();
    } else cout << "Unable to open file";

    return 0;
}
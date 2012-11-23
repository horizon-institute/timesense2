TimeSense
=============

JApplication to read data into Timestreams from SensePods. Developed as part of the [Relate project](http://horizab1.miniserver.com/relate/)

Installation
-----------

1. Download TimeSense files. You will also need the Bluecove (Bluetooth) libraries in the "lib" subfolder of the folder containing TimeSense2.jar.
2. Java compile files
3. Use Timestreams (for example http://timestreams.wp.horizon.ac.uk) to set up appropriate measurement containers
4. Create a Properties.xml file
5. That's it. You're ready to go!


Usage
-----

    java -jar TimeSense2.jar [properties file path] [type of Sensepod]
    
    Example: 
    java -jar TimeSense2.jar properties.xml ECOsense


Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_markup`)
3. Commit your changes (`git commit -am "Added Snarkdown"`)
4. Push to the branch (`git push origin my_markup`)
5. Create an [Issue][1] with a link to your branch
6. Enjoy a refreshing drink while you wait

License
------------
Copyright (C) 2012  Jesse Blum (jesse.blum@nottingham.ac.uk), Horizon Digital Economy Institute, University of Nottingham

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

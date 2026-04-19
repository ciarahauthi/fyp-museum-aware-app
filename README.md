# Location-Aware Museum Application
This is a three-component system comrised of a Native Android application, a backend that includes the API endpoints, the database and the routing algorithm, and the frontend web application.
It is a project that investigates into using a sparse BLE-only indoor positioning system to infer room-level positioning of a user and whether this can guide users through a route under a museum setting. 

## Running the application
The sytem is containerised and can be deployed and ran using docker. There are separate steps to launch it, as initial launch requires the database to be populated.

### First time
1. `docker-compose up -d db` Run this command to start up the database container.
2. `docker-compose run --rm api python -m api.init_tables` Populate the database using this command.
3. `docker-compose up` Start the other containers up

### Once the database has been populated
1. `docker-compose up`
   
## Notes:
1. The server used to deploy this system is no longer running. Therefore the application is still using the associated URL and won't connect.
2. To run this application on a mopbile device, you must change the `BASE_URL` inside `build.gradle.kts` to your laptop / pc's IP (if it is a tethered device), otherwise you must use `10.0.2.2:<port>` for the emulator.
3. The emulator cannot show the bluetooth capabilities, but can fetch from the internet.
4. The UUID of the beacons in the dummy data in `init_tables.py` does not match the UUID that the phone is scanning for. The beacons must have a UUID of `B38DED04-E6DD-4A33-B3D0-98182F6EFF6D` to be accepted.


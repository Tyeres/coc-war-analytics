## Clash of Clans War Tracker

This project supports the video game *Clash of Clans* by updating data into a database and storing clan war player history. It is designed to help clan leaders track player performance during wars and assess overall player skill. The program is built to support a MySQL database.

### Setup Instructions

1. **Create the database**
   Create a MySQL database named `clash`.

2. **Configure credentials**
   Update your database login credentials in `JDBC.java`.

3. **Create the tables**
   Using the files in the `DatabaseResources` directory, create each table in the order listed (ascending).

4. **Set up your API key**
   Create a file named exactly `.env` in the project directory and add your API key in the correct format.

   When generating your API key on the [Clash of Clans Developer Portal](https://developer.clashofclans.com/#/), ensure it is authorized for your current IP address. You can find your IP at [api.ipify.org](https://api.ipify.org/).

5. **(Optional) Track your own clan's stats**
   To enable tracking for your own clan:
   - Visit the [Clash of Clans API documentation](https://developer.clashofclans.com/#/documentation).
   - Navigate to **Clans** → **GET /clans/{clanTag}/currentwar** ("Retrieve information about clan's current clan war").
   - Enter your clan tag and click **Execute**.
   - Copy the resulting Request URL and paste it into the `endpoint` string within the `getBufferedReader()` method in `Main.java`.

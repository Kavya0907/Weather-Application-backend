
**Weather Application**

This is a full-stack weather application built using **React.js** for the frontend and **Spring Boot** for the backend. It integrates with the OpenWeatherMap API to fetch weather data and the YouTube API to display related videos.

**Features**
- Search weather by city, zip code, or coordinates.
- Get current weather details (temperature, humidity, wind speed, description, and icon).
- View a 5-day weather forecast.
- Save weather requests to a MySQL database.
- View, update, delete, and export saved weather data.
- Fetch related YouTube videos for the searched location.

**Prerequisites**

Ensure the following tools are installed on your system:
- Java 17+: Required for Spring Boot.
- Maven: To build the backend.
- Node.js/npm: For React development.
- MySQL Database for storing weather requests.
- API Keys: Obtain keys from:
  - [OpenWeatherMap](https://openweathermap.org/api )
  - [YouTube Data API] (https://console.cloud.google.com/ )

**Setup Instructions**

**Backend Setup**

    1. Navigate to the backend directory:
   	         cd weatherApllication/backend
             
    2. Configure the application:
          - Open `src/main/resources/application.properties`.
  	      - Replace placeholders with your database credentials and API keys:
            properties
              spring.datasource.url=jdbc:mysql://localhost:3306/schema_name
              spring.datasource.username=root
              spring.datasource.password=your_password
              openweathermap.api.key=your_openweathermap_api_key
              youtube.api.key=your_youtube_api_key
              
 	 3. Start MySQL:
       - Run MySQL server using:
    	      mysql.server start
            
    4. Build and run the backend:
         mvn spring-boot:run
         
    5. Backend will run on `http://localhost:8080`.
         CRUD: 
           - Create: POST /api/weather saves location and weather data to MySQL.
           - Read: GET /api/weather fetches all records.
           - Update: PUT /api/weather/{id} updates a record.
           - Delete: DELETE /api/weather/{id} removes a record.
         API Integration: 
           - OpenWeatherMap: Current weather (/current) and 5-day forecast (/forecast).
           - YouTube: Video URL (/video).
         Export: 
           - GET /api/weather/export generates a CSV file.
       
**Frontend Setup**

    1. Navigate to the frontend directory:
          cd weatherApplication/frontend
          
    2. Install dependencies:
          npm install axios
          
    3. Start the frontend:
          npm start 
          
    4. Frontend will run on `http://localhost:3000`.

**How to Obtain API Keys**

*OpenWeatherMap API Key*
  1. Go to [OpenWeatherMap](https://openweathermap.org/api).
  2. Sign up or log in to your account.
  3. Navigate to the "API Keys" section in your account dashboard.
  4. Generate a new key and copy it.

*YouTube Data API Key*
  1. Sign in to [Google Cloud Console](https://console.cloud.google.com/).
  2. Create a new project (e.g., "WeatherAppYouTube").
  3. Enable the "YouTube Data API v3" for your project under APIs & Services > Library.
  4. Create credentials (API Key) under APIs & Services > Credentials.
  5. Copy the generated key.

**Testing the Application**
  1. Open your browser and navigate to `http://localhost:3000`.
  2. Test features:
     - Enter a location (e.g., "London") and click "Get Weather."
     - Use "Use Current Location" to fetch weather based on geolocation.
     - Save requests by entering start/end dates and clicking "Save."
     - View saved requests under "View History."
     - Export data as CSV using "Export to CSV."
  3. Error Handling:
     - Enter an invalid location (e.g., "xyz") to see error messages.

**Execution Flow**
  1. Backend connects to MySQL server.
  2. Frontend interacts with users via UI components.
  3. Backend fetches data from OpenWeatherMap/YouTube APIs or interacts with MySQL for CRUD operations.
  4. Data is displayed on the frontend, including current weather, forecasts, and error messages.

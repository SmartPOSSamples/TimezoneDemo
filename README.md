# TimezoneDemo
Get timezone from IP address.

## Introduction
This is a demo that allows users to set the device's time zone based on IP address information and provides functionality to manage automatic time zone mode.

## LocationInfo Class
The `LocationInfo` class is used to store various location-related information retrieved from an IP address lookup service. It contains fields such as status, country, country code, region, region name, city, zip code, latitude, longitude, timezone, ISP (Internet Service Provider), organization, autonomous system, and the queried IP address.

## MainActivity
The main activity of the application which performs the following functions:
- Displays a text view to show the current time zone and a button to set the time zone.
- On clicking the "setTimezone" button, it retrieves IP information from `http://ip-api.com/json`, parses the JSON response into a `LocationInfo` object, disables automatic time zone mode using the `ISystemExtApi` service, sets the time zone based on the retrieved timezone information, and displays the time zone on the screen along with a success toast message. In case of an error, a failure toast message is shown.

## Service Interaction
The application binds to a service (`SystemExtApiService`) through the `ISystemExtApi` AIDL interface. This service is used to enable or disable the automatic time zone mode on the device.

## AIDL Files
The application uses two AIDL files (`ISystemExtApi.aidl` and `NetworkType.aidl`) and a corresponding Java class (`NetworkType.class`) located in `com.wizarpos.wizarviewagentassistant.aidl`. The `ISystemExtApi.aidl` file defines methods like `enableAutoTimezone` and `get status for enable/disable auto timezone item`.

## Usage
1. Open the application.
2. Click the "setTimezone" button to set the time zone based on the IP address location.

## Note
Make sure to handle exceptions appropriately and ensure that the necessary permissions are granted for accessing the IP address information and modifying the time zone settings. Also, be aware that modifying system settings might require proper authorization and could have implications on the device's behavior.

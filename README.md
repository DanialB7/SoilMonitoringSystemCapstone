# Comprehensive Soil Monitoring System

## Project Overview

This capstone project delivers a full-spectrum soil monitoring system, meticulously crafted from the hardware layer to the user interface. By integrating a suite of sensors with the ESP32-S3 and ESP32-C6 microcontrollers, our system provides real-time insights into soil conditions, including moisture, light exposure, temperature, and humidity levels. A central hub, leveraging LoRa technology, ensures seamless communication with field devices scattered across vast agricultural or horticultural landscapes. Our backend infrastructure utilizes AWS IoT Core for device management and AWS DynamoDB for secure and scalable storage of measurement data. Complemented by a mobile application, users gain intuitive access to soil data, facilitating informed decision-making for soil management and crop cultivation.

## Hardware Components

- **Microcontrollers**: ESP32-S3, ESP32-C6
- **Sensors**:
  - Capacitive Soil Moisture Sensor
  - Lux Sensor
  - Temperature and Humidity Sensor
- **Communication**: LoRa Module for connectivity between the central hub and field devices

## System Architecture

1. **Field Devices**: Each field device is equipped with sensors and an ESP32 microcontroller. These devices collect soil data and communicate it back to the central hub via LoRa.
2. **Central Hub**: Acts as a bridge between field devices and the cloud. It aggregates data from the field devices and uploads it to AWS IoT Core.
3. **Cloud Services**:
   - **AWS IoT Core**: Manages the IoT device connections and data ingestion.
   - **AWS DynamoDB**: Stores the received data in a scalable, NoSQL database.
4. **Mobile Application**: Provides users with a real-time view of soil conditions, historical data analysis, and actionable insights.

## Getting Started

### Prerequisites

- ESP32-S3 and ESP32-C6 development boards
- Specified sensors for soil moisture, lux, and temperature/humidity
- LoRa modules compatible with ESP32
- AWS account for accessing IoT Core and DynamoDB services
- Android/iOS device for the mobile application

### Installation

1. **Hardware Setup**: Assemble the sensors with the ESP32 boards according to the schematics provided in the `KiCadFiles` directory.
2. **Firmware**: Upload the firmware to each ESP32 device. The firmware code is located in the `Firmware` directory.
3. **Cloud Configuration**: Set up your AWS IoT Core and DynamoDB services following the guidelines in the `CloudSetup` documentation.
4. **Mobile Application**: Install the mobile application on your device. Instructions and necessary files can be found in the `Application` directory.

## Usage

- Deploy the field devices across your desired monitoring location.
- Ensure the central hub is powered on and connected to the internet.
- Open the mobile application to view real-time data and receive insights on soil conditions.

# 📱 Karma — Mobile Application

Karma is an Android application designed to promote positive, sustainable, and socially responsible behavior. Through a gamified system of missions and Karma Points, users are encouraged to perform beneficial actions for themselves, their community, and the environment.

---

## 🌍 Purpose

Karma aims to foster long-term positive habits by transforming everyday actions into engaging challenges. The app supports the following UN Sustainable Development Goals:

- **SDG 3:** Good Health and Well-Being  
- **SDG 11:** Sustainable Cities and Communities  
- **SDG 12:** Responsible Consumption and Production  
- **SDG 13:** Climate Action  

---

## ✨ Features

### 🧭 Main Screen
- Total Karma Points  
- Quick access to missions, leaderboard, and step counter  
- Motivational quotes  
- Simple and accessible layout  

### 🏆 Leaderboard & Statistics
- Comparison of users’ Karma Points  
- Daily Karma Points chart  
- High-contrast, easy-to-read visuals  

### 🎯 Missions System
- Explore community missions  
- Create custom missions (title, description, icon, reward)  
- Delete your own missions  
- Real-time updates via MQTT  

### 👤 Profile
- Total Karma Points  
- Completed and published missions  
- Activity history  

### 🚶 Step Counter Integration
- Uses the device’s built-in step counter  
- Awards **1 Karma Point per 200 steps**  
- Low battery usage  

### 🗺️ Real-World Interaction
- Google Maps integration  
- Open data from Comunidad de Madrid  
- Mission markers for donation centers (blood, books, toys)

---

## 🛠️ Technologies Used
- Android Studio (Java)  
- MQTT (Mosquitto broker + HiveMQ client)  
- Google Maps API  
- Open Data JSON  
- Android step counter sensor  
- SharedPreferences for local persistence  

---

## 🏗️ Architecture Overview

Karma uses a **publish–subscribe MQTT structure**:

| Purpose              | Topic                               |
|----------------------|---------------------------------------|
| Karma updates        | `app/users/+/karmaTotal`             |
| Mission publication  | `app/users/+/missionPublish`         |
| Mission deletion     | `app/users/+/missionDelete`          |

The app processes and stores data using SharedPreferences to synchronize information across users and devices.

---

## ▶️ Running the Environment

1. Connect the Android devices to the host machine  
2. Set the correct IP for the VirtualBox Mosquitto server  
3. Start the Mosquitto broker  
4. Connect devices with `./gnirehtet autorun`  
5. Install and launch the app on each device  

---

## 🧪 Testing

### **Functional Testing**
Validated mission creation, completion, profile, leaderboard, step tracking, and MQTT communication across devices.

### **Accessibility Testing**
Performed with Android Accessibility Scanner; improvements made to:
- Text contrast  
- Labels  
- Touch targets  
- Overall readability  

### **User Testing**
Users of varying ages evaluated clarity, usability, and the motivational impact of the gamification system.

---

## 📌 Conclusions

Karma demonstrates how gamification and mobile technology can encourage sustainable and socially responsible behavior. The app is functional, intuitive, accessible, and aligned with sustainability goals.

---

## 🚀 Future Improvements

- Firebase / Google authentication  
- New mission types and time-limited challenges  
- More advanced analytics  
- Expanded accessibility features (voice, haptics, themes)  
- iOS version  
- Badges, levels, streaks, and daily challenges  

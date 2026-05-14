 Shishu-Sneh 

Baby Healthcare & Growth Monitoring Application

Shishu-Sneh is a modern Android healthcare application designed to help new parents monitor and manage their baby’s health during the early developmental stages. The app acts as a digital companion by providing growth tracking, vaccination reminders, feeding guidance, milestone monitoring, and daily healthcare logging.

✨ Features
 Baby Profile Management

* Store baby details:

  * Name
  * Gender
  * Date & Time of Birth
  * Blood Group
  * Birth Weight & Height
  * Allergies
  * Medical abnormalities

📈 Growth Tracking

* Add baby weight and height records
* Select custom dates for entries
* Interactive growth charts using MPAndroidChart
* Real-time graph updates
* WHO standard growth curve comparison based on gender

💉 Vaccination Management

* Auto-generated vaccination schedule
* Mark vaccines as:
 * Completed
 * Pending
* Upcoming vaccine alerts on dashboard
* WorkManager-based reminder notification

 🍼 Feeding Guide

* Age-based feeding recommendations
* Feeding tips based on baby age in months
* Offline static guidance system

🧠 Milestone Tracking
Track developmental milestones such as:

* Holding head
* Sitting
* Crawling
* Other developmental activities

📝 Daily Log
Track daily baby activities:

* Feeding
* Sleep
* Mood
* Diaper count
* Notes

 🤖 Baby Care Assistant

Simple chatbot assistant capable of answering:

* Feeding questions
* Vaccination information
* Basic baby-care guidance

🛠️ Tech Stack

| Technology        | Purpose                   |
| ----------------- | ------------------------- |
| Kotlin            | Android Development       |
| Jetpack Compose   | UI Design                 |
| Room Database     | Local Storage             |
| MVVM Architecture | App Structure             |
| WorkManager       | Notifications & Reminders |
| MPAndroidChart    | Growth Charts             |


 📂 Project Structure

app/
│
├── data/
├── database/
├── ui/
├── viewmodel/
└── workers/

 📱 Screens

* Onboarding Screen
* Login & Register
* Home Dashboard
* Growth Tracker
* Vaccination Schedule
* Feeding Guide
* Milestone Tracker
* Daily Log
* Chatbot Assistant

 🚀 Key Highlights

* Fully offline functionality
* Real-time health tracking
* Clean modern UI
* Gender-based WHO growth comparison
* Smart vaccination reminder system
* Beginner-friendly MVVM implementation

🔔 Notifications

Vaccination reminders are implemented using:

* WorkManager
* NotificationManager

Users receive alerts for upcoming vaccinations.


📊 Growth Visualization

The app uses MPAndroidChart to:

* Plot baby growth records
* Compare growth trends
* Visualize progress over time
  
 🎯 Purpose

This project was developed to provide a simple and accessible digital healthcare assistant for first-time parents and caregivers, especially in rural areas.

# 📚 Future Improvements

* Cloud sync support
* Multi-child profiles
* Pediatrician consultation
* AI-powered recommendations
* Dark mode support
* Multi-language support




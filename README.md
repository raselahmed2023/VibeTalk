# 🎙️ VibeTalk — AI Voice Speaking Partner

## 📱 About

VibeTalk is an Android AI voice chat app that lets you have real conversations with an AI assistant. Just tap the mic, speak naturally, and the AI replies back — both in text and voice.

Built with **Kotlin + Jetpack Compose** and powered by **Groq AI**.

---

## ✨ Features

- 🎙️ **Voice Input** — Tap mic and speak naturally
- 🔇 **Auto Silence Detection** — Stops recording automatically after 5 seconds of silence
- 📝 **Speech to Text** — Powered by Groq Whisper
- 🤖 **AI Replies** — Powered by Groq LLaMA 3
- 🔊 **Text to Speech** — AI speaks back to you
- 💬 **Chat History** — Full conversation with chat bubbles
- ⌨️ **Text Input** — Type messages too
- ⚙️ **Settings** — Voice speed, auto speak toggle, clear history
- 🌙 **Dark UI** — Beautiful dark purple theme

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM + StateFlow |
| Speech to Text | Groq Whisper API |
| AI Brain | Groq LLaMA 3.3 70B |
| Text to Speech | Android TTS |
| HTTP Client | OkHttp3 |
| Navigation | Jetpack Navigation Compose |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version)
- Android device or emulator (API 26+)
- Groq API key (free at [console.groq.com](https://console.groq.com))

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/VibeTalk.git
cd VibeTalk
```

2. **Add your Groq API key**

Open `local.properties` and add:
```
GROQ_API_KEY=your_groq_api_key_here
```

3. **Build and run**

Open in Android Studio → Click Run ▶️

---

## 🔑 API Key

This app uses [Groq](https://console.groq.com) for both STT and AI — completely **free** to use.

1. Go to [console.groq.com](https://console.groq.com)
2. Sign up for free
3. Create an API key
4. Add it to `local.properties`

---

## 📸 Screenshots

| Home Screen | Chat Screen | Settings |
|---|---|---|
| Mic button, AI avatar | Chat bubbles, text input | Voice speed, auto speak |

---

## 🏗️ Project Structure

```
vibetalk/
├── api/
│   ├── ChatMessage.kt        # Chat message data class
│   └── GroqApiService.kt     # Whisper STT + LLaMA AI
├── audio/
│   ├── AudioRecorder.kt      # Mic recording + silence detection
│   └── AudioPlayer.kt        # Audio playback
├── ui/
│   └── screens/
│       └── SettingsScreen.kt # Settings UI
├── viewmodel/
│   └── MainViewModel.kt      # App logic + state
└── MainActivity.kt           # Main UI + Navigation
```

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first.

---

## 📄 License

MIT License — free to use and modify.

---

## 👨‍💻 Built By

Built with using **Claude AI** as a coding assistant.



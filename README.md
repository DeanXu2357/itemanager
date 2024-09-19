# Personal Asset Management

Personal Asset Management is an Android application designed to help users manage their personal assets efficiently. It provides an easy-to-use interface for tracking various types of assets, organizing them into categories, and generating basic statistics.

## Features

- Add and manage personal assets
- Categorize assets into custom types
- Scan barcodes for quick item addition
- View asset statistics
- Customizable asset fields based on category

## Project Structure

The project follows a typical Android MVVM (Model-View-ViewModel) architecture:

- `data`: Contains data models, DAOs, and the Room database
- `ui`: Contains fragments, adapters, and ViewModels
- `res`: Contains layout files, menus, and other resources

## Current Progress

- Basic project structure set up
- Data models and DAOs implemented
- Main activity with bottom navigation
- Home fragment for displaying asset list
- Placeholder fragments for Categories, Statistics, and Settings

## Documentation

For detailed documentation about the project architecture, implementation details, and future plans, please refer to the [documentation index](docs/index.md).

## Getting Started

To get started with the project, clone the repository and open it in Android Studio. Make sure you have the latest version of Android Studio and the Android SDK installed.

```bash
git clone https://github.com/yourusername/personal-asset-management.git
cd personal-asset-management
```

Open the project in Android Studio and sync the Gradle files. You should then be able to build and run the application on an emulator or physical device.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
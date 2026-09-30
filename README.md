# BILLING-SYSYTEM-APP

Mobile Clothing POS & Shop Management System built with Kotlin and Jetpack Compose.

## Features
- **Retail POS & Billing**: Fast billing, cart management, discount handling, thermal receipt printing formatting.
- **Inventory & Catalog Management**: Product variant tracking (sizes, colors, SKUs, barcodes), stock adjustment, and low-stock alerts.
- **Cloud Backup & Restore**: Encrypted snapshots with configurable Supabase cloud synchronization.
- **Financial Analytics & Reporting**: Daily closing summaries, profit tracking, and expense tracking.
- **Role-Based Access**: Multi-user support with separate Admin and Staff access levels.

## Setup & Running Locally

1. **Prerequisites**: [Android Studio](https://developer.android.com/studio) (latest version recommended)
2. **Open Project**: Launch Android Studio and open this directory.
3. **Environment Configuration**:
   - Copy `.env.example` to `.env` in the root directory:
     ```bash
     cp .env.example .env
     ```
   - Fill in your Supabase credentials or Gemini API keys if required.
4. **Build & Run**: Sync Gradle and run the app on an emulator or connected physical Android device.

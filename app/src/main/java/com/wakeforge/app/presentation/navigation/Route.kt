package com.wakeforge.app.presentation.navigation

/**
 * Sealed interface representing every screen (route) in the WakeForge navigation graph.
 *
 * Each entry exposes a [route] string compatible with `NavHost` and, where applicable,
 * a `createRoute` factory that builds parameterised navigation strings.
 */
sealed interface Route {

    val route: String

    // ──────────────────────────────────────────────────────────────────────────
    // Onboarding & Setup
    // ──────────────────────────────────────────────────────────────────────────

    /** Initial splash / loading screen shown on cold start. */
    data object Splash : Route {
        override val route: String = "splash"
    }

    /** Walkthrough screens introducing WakeForge features. */
    data class Onboarding(val pageIndex: Int = 0) : Route {
        override val route: String = "onboarding?pageIndex={pageIndex}"

        companion object {
            /** Build a route targeting a specific onboarding page. */
            fun createRoute(pageIndex: Int): String = "onboarding?pageIndex=$pageIndex"
        }
    }

    /** Runtime permission request screen (notifications, alarms, battery). */
    data object PermissionSetup : Route {
        override val route: String = "permission_setup"
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Main Tabs
    // ──────────────────────────────────────────────────────────────────────────

    /** Primary home / dashboard tab. */
    data object Home : Route {
        override val route: String = "home"
    }

    /** Alarm list / management tab. */
    data object Alarms : Route {
        override val route: String = "alarms"
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Alarm CRUD
    // ──────────────────────────────────────────────────────────────────────────

    /** Screen for creating a brand-new alarm. */
    data object CreateAlarm : Route {
        override val route: String = "create_alarm"
    }

    /** Screen for editing an existing alarm. */
    data object EditAlarm : Route {
        override val route: String = "edit_alarm/{alarmId}"
        fun createRoute(alarmId: String): String = "edit_alarm/$alarmId"
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Alarm Lifecycle (Ringing / Mission / Success)
    // ──────────────────────────────────────────────────────────────────────────

    /** Full-screen alarm ringing display. */
    data object AlarmRinging : Route {
        override val route: String = "alarm_ringing/{alarmId}"
        fun createRoute(alarmId: String): String = "alarm_ringing/$alarmId"
    }

    /** Mission challenge screen launched during an active alarm. */
    data object MissionChallenge : Route {
        override val route: String =
            "mission_challenge?alarmId={alarmId}&missionType={missionType}&difficulty={difficulty}&snoozeCount={snoozeCount}"

        fun createRoute(
            alarmId: String,
            missionType: String,
            difficulty: String,
            snoozeCount: Int
        ): String =
            "mission_challenge?alarmId=$alarmId&missionType=$missionType&difficulty=$difficulty&snoozeCount=$snoozeCount"
    }

    /** Success screen shown after a wake-up mission is completed. */
    data object WakeSuccess : Route {
        override val route: String = "wake_success/{alarmId}/{wakeRecordId}"
        fun createRoute(alarmId: String, wakeRecordId: String): String =
            "wake_success/$alarmId/$wakeRecordId"
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Secondary Screens
    // ──────────────────────────────────────────────────────────────────────────

    /** Statistics & analytics dashboard. */
    data object Stats : Route {
        override val route: String = "stats"
    }

    /** Premium features & subscription management. */
    data object Premium : Route {
        override val route: String = "premium"
    }

    /** Application settings screen. */
    data object Settings : Route {
        override val route: String = "settings"
    }

    /** Sound picker dialog / screen for selecting alarm tones. */
    data object SoundPicker : Route {
        override val route: String = "sound_picker"
    }
}

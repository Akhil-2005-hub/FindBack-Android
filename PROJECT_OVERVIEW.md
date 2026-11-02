# Project overview

**Title:** FindBack — Secure Lost-Phone Locator (Version B)

**Goal:** Let an owner request a location/status reply from their own prepared Android phone by sending a private WhatsApp command from another phone.

**Flow:** Android NotificationListenerService filters WhatsApp notifications → FindBack matches `FIND` plus the saved eight-digit secret → local lockout/cooldown is applied → Android location service returns a current or cached fix → FindBack formats a map link and available phone status → a WhatsApp notification quick-reply action attempts to send the response in the same conversation. The request is not bound to a particular sender account; anyone who knows the code and can message the protected phone may trigger a location reply.

**Local privacy design:** no FindBack account or server; hashed code and settings live in app-private storage; notification text is not retained. Notification access is nevertheless broad at the Android system level and must be clearly disclosed.

**Deferred feature:** no automatic location capture/upload immediately before shutdown or network loss. That idea can be proposed after the core project is reviewed.

**Important:** notification-based WhatsApp behavior is not guaranteed across Android and WhatsApp versions. The target must be powered, connected, and able to receive notifications. A cached location may be stale. A student demonstration on tested devices is not proof of universal compatibility.

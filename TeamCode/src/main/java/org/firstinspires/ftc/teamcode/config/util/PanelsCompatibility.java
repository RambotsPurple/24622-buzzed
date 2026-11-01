package org.firstinspires.ftc.teamcode.config.util;

import android.content.Context;

import com.bylazar.panels.reflection.ClassFinder;

import org.firstinspires.ftc.ftccommon.external.OnCreate;

/** Prevents Panels' reflection scanner from loading EJML's desktop-only Swing classes. */
public final class PanelsCompatibility {
    private PanelsCompatibility() {
    }

    @OnCreate
    public static void initialize(Context context) {
        ClassFinder.INSTANCE
                .getDEFAULT_IGNORED_PACKAGES()
                .add("org.ejml");
    }
}

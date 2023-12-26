package com.devjethava.dynamiciconchange

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devjethava.dynamiciconchange.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), Application.ActivityLifecycleCallbacks {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val packageName: String = "com.devjethava.dynamiciconchange"

    private val MAIN_ACTVITY_BASE_NAME = ".MainActivity"

    private var componentClass = ""
    private val classesToKill: MutableSet<String> = HashSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgDefault.setOnClickListener {
            changeIcon("Default")
        }

        binding.imgBlack.setOnClickListener {
            changeIcon("Black")
        }

        binding.imgPremium.setOnClickListener {
            changeIcon("Premium")
        }
    }

    // ... getIcon, Constructor, and getName methods
    private fun changeIcon(iconName: String = "") {
        if (iconName.isEmpty()) {
            Toast.makeText(this, "Icon name is missing", Toast.LENGTH_LONG).show()
            return
        }
        if (componentClass.isEmpty()) {
            componentClass = componentName.className
        }
        val newIconName = iconName.ifEmpty { "Default" }
        val activeClass = packageName + MAIN_ACTVITY_BASE_NAME + newIconName
        if (componentClass == activeClass) {
            Toast.makeText(
                this,
                "This icons is the current active icon. $componentClass",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        try {
            packageManager.setComponentEnabledSetting(
                ComponentName(packageName, activeClass),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            Toast.makeText(this, newIconName, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.stackTrace
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            return
        }
        classesToKill.add(componentClass)
        componentClass = activeClass
        application.registerActivityLifecycleCallbacks(this@MainActivity)
        // The completeIconChange() is what makes the current active class disabled.
        // Move it to onActivityPaused or onActivityStopped etc to change the icon only when the app closes or goes to background
        completeIconChange()
    }

    private fun completeIconChange() {
        // Works for minSdkVersion = 23
        for (className in classesToKill) {
            packageManager.setComponentEnabledSetting(
                ComponentName(packageName, className),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
        /*
        // Works for minSdkVersion = 24 and above
        classesToKill.forEach((cls) -> activity.getPackageManager().setComponentEnabledSetting(
                new ComponentName(this.packageName, cls),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        ));
        */
        classesToKill.clear()
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
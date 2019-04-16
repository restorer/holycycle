package com.eightsines.holycyclesample

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ScrollView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val logBuffer = StringBuffer()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        appendToLog("onCreate")
        setContentView(R.layout.activity_main)
    }

    override fun onRestoreInstanceState(savedInstanceState : Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        appendToLog("onRestoreInstanceState")
    }

    override fun onStart() {
        super.onStart()
        appendToLog("onStart")
    }

    override fun onResume() {
        super.onResume()
        appendToLog("onResume")
    }

    override fun onPause() {
        super.onPause()
        appendToLog("onPause")
    }

    override fun onStop() {
        super.onStop()
        appendToLog("onStop")
    }

    override fun onSaveInstanceState(outState : Bundle?) {
        super.onSaveInstanceState(outState)
        appendToLog("onSaveInstanceState")
    }

    private fun appendToLog(message : String) {
        if (!logBuffer.isEmpty()) {
            logBuffer.append("\n")
        }

        logBuffer.append(message)

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            logView.text = logBuffer.toString()
            scrollWrapView.post { scrollWrapView.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }
}

/*
import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.widget.ScrollView
import com.eightsines.holycycle.app.ViewControllerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ViewControllerAppCompatActivity() {
    private val logBuffer = StringBuffer()

    //  TODO: inject dependencies
    override fun onControllerCreate(extras : Bundle?) {
        appendToLog("onControllerCreate")
    }

    // TODO: do something useful
    override fun onControllerRestoreInstanceState(savedInstanceState : Bundle) {
        appendToLog("onControllerRestoreInstanceState")
    }

    override fun onControllerGetContentLayoutId() : Int {
        appendToLog("onControllerGetContentLayoutId")
        return R.layout.activity_main
    }

    // Probably you don't need onControllerContentViewCreated() if you use kotlin android extensions.
    override fun onControllerContentViewCreated() {
        appendToLog("onControllerContentViewCreated")
    }

    // TODO: do something useful
    override fun onControllerStart() {
        appendToLog("onControllerStart")
    }

    // TODO: do something useful
    override fun onControllerResume() {
        appendToLog("onControllerResume")
    }

    // TODO: do something useful
    override fun onControllerFocus() {
        appendToLog("onControllerFocus")
    }

    // TODO: do something useful
    override fun onControllerBlur() {
        appendToLog("onControllerBlur")
    }

    // TODO: do something useful
    override fun onControllerPause() {
        appendToLog("onControllerPause")
    }

    // TODO: do something useful
    override fun onControllerPersistUserData() {
        appendToLog("onControllerPersistUserData")
    }

    // TODO: do something useful
    override fun onControllerStop() {
        appendToLog("onControllerStop")
    }

    // TODO: do something useful
    override fun onControllerSaveInstanceState(outState : Bundle) {
        appendToLog("onControllerSaveInstanceState")
    }

    private fun appendToLog(message : String) {
        if (!logBuffer.isEmpty()) {
            logBuffer.append("\n")
        }

        logBuffer.append(message)

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            logView.text = logBuffer.toString()
            scrollWrapView.post { scrollWrapView.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }
}
*/

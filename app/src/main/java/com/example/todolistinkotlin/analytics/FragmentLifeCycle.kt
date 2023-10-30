package com.example.todolistinkotlin.analytics

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


/**
 * The FragmentLifeCycle class implements the Fragment's lifecycle events and also registered with activity's fragment manager's lifecycle callbacks
 * making it easy to call analytics events on every ui lifecycle events for every fragment.
 * */
class FragmentLifeCycle( private val currentFragment : (String) -> Unit) : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
    }

    override fun onFragmentResumed(fm: FragmentManager, mFragment: Fragment) {
        currentFragment(mFragment.javaClass.simpleName)
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
    }
}
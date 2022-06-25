package com.kokomi.origin.creation

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kokomi.origin.creation.image.ImageUploadFragment
import com.kokomi.origin.creation.start.CreationStartFragment
import com.kokomi.origin.creation.video.VideoUploadFragment

class CreationPagerAdapter(
    fragment: Fragment,
    private val changePager: (Int) -> Unit
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ImageUploadFragment.newInstance()
            1 -> CreationStartFragment.newInstance(changePager)
            2 -> VideoUploadFragment.newInstance()
            else -> throw IllegalStateException()
        }
    }

}
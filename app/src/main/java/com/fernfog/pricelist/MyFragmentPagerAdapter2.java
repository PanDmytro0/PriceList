package com.fernfog.pricelist;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentPagerAdapter2 extends FragmentStateAdapter {
    private List<ImageFragment> fragmentList = new ArrayList<>();

    public MyFragmentPagerAdapter2(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void addFragment(ImageFragment fragment) {
        fragmentList.add(fragment);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageFragment createFragment(int position) {
        return fragmentList.get(position);
    }

    public List<ImageFragment> getAll() {
        return fragmentList;
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public void removeAll() {
        fragmentList.clear();
        notifyDataSetChanged();
    }
}

package com.fernfog.pricelist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentPagerAdapter2 extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();

    public MyFragmentPagerAdapter2(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    public List<Fragment> getAll() {
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

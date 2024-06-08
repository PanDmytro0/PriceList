package com.fernfog.pricelist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentPagerAdapter extends FragmentStateAdapter {
    private final List<ListFragment> fragmentList = new ArrayList<>();

    public MyFragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void addFragment(ListFragment fragment) {
        fragmentList.add(fragment);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListFragment createFragment(int position) {
        return fragmentList.get(position);
    }

    public List<ListFragment> getAll() {
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

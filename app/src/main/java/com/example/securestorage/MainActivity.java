package com.example.securestorage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.securestorage.fragments.DatabaseFragment;
import com.example.securestorage.fragments.EncryptedFilesFragment;
import com.example.securestorage.fragments.ExternalStorageFragment;
import com.example.securestorage.fragments.InternalStorageFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout  = findViewById(R.id.tabLayout);

        viewPager.setAdapter(new PagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Interne"); break;
                case 1: tab.setText("Externe"); break;
                case 2: tab.setText("Base de données"); break;
                case 3: tab.setText("Fichiers Chiffrés"); break;
            }
        }).attach();
    }

    static class PagerAdapter extends FragmentStateAdapter {

        public PagerAdapter(FragmentActivity fa) { super(fa); }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:  return new ExternalStorageFragment();
                case 2:  return new DatabaseFragment();
                case 3:  return new EncryptedFilesFragment();
                default: return new InternalStorageFragment();
            }
        }

        @Override
        public int getItemCount() { return 4; }
    }
}
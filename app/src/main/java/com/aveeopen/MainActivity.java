/*
 * Copyright 2019 Avee Player. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aveeopen;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.aveeopen.Common.Boast;
import com.aveeopen.Common.Events.WeakDelegate3;
import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.SystemUiHider;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.comp.LibraryQueueUI.Fragment1;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.SystemUiHiderHoneycomb;
import com.aveeopen.R;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AlbumArt.ImageLoadedListener;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.ISearchEntry;
import com.aveeopen.comp.LibraryQueueUI.Fragment0;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.SleepTimer.SleepTimerConfig;
import com.aveeopen.comp.VisualUI.Fragment2;
import com.aveeopen.EventsGlobal.EventsGlobalApp;
import com.aveeopen.EventsGlobal.EventsGlobalTextNotifier;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    public static final int LIBRARY_PAGE_INDEX = 0;
    public static final int QUEUE_PAGE_INDEX = 1;
    public static final int VISUAL_PAGE_INDEX = 2;
    private static final int MSG_HIDE = 2;
    private static final int MSG_TICK10 = 3;

    public static WeakEvent1<Activity /*activity*/> onCreate = new WeakEvent1<>();
    public static WeakEvent1<Context /*context*/> onStart = new WeakEvent1<>();
    public static WeakEvent onStop = new WeakEvent();
    public static WeakEvent1<ContextData /*contextData*/> onDestroy = new WeakEvent1<>();
    public static WeakEvent onExit = new WeakEvent();
    public static WeakEvent2<Integer /*page*/, Activity /*activity*/> onViewPagerPageSelected = new WeakEvent2<>();
    public static WeakEvent1<Context /*context*/> onCreateEarly = new WeakEvent1<>();
    public static WeakEvent2<Integer /*id*/, ContextData /*contextData*/> onMainUIAction = new WeakEvent2<>();
    public static WeakEvent4<AlbumArtRequest /*artRequest*/, ImageLoadedListener /*listener*/, Integer /*targetW*/, Integer /*targetH*/> onRequestAlbumArtLarge = new WeakEvent4<>();
    public static WeakEventR<SleepTimerConfig> onMainUIRequestSleepTimerConfig = new WeakEventR<>();
    public static WeakEventR<Boolean> onRequestLockOrientState = new WeakEventR<>();
    public static WeakEvent2<Integer /*index*/, String /*query*/> onUISearchQueryTextChange = new WeakEvent2<>();
    public static WeakEvent1<Boolean /*enabled*/> onUISearchQueryStateChange = new WeakEvent1<>();
    public static WeakEvent1<Integer /*index*/> onSetCurrentSearchIndex = new WeakEvent1<>();
    public static WeakEventR<ISearchEntry> onRequestCurrentSearchEntry = new WeakEventR<>();
    public static WeakDelegate3<View /*view*/, View /*viewCollapsed*/, View /*viewBg*/> onCreateView = new WeakDelegate3<>();
    public static WeakEvent2<List<PlaylistSong> /*list*/, Integer /*startPlayPosition*/> onPreviewOpen = new WeakEvent2<>();
    public static WeakEvent1<Boolean /*fullscreen*/> onFullscreenChanged = new WeakEvent1<>();
    public static WeakEvent1<Context /*context*/> onViewPagerSwipeOutAtStart = new WeakEvent1<>();
    public static WeakEvent2<Float /*val*/, Context /*context*/> onViewPagerSwipeProgressUpdate = new WeakEvent2<>();
    public static WeakEvent1<Integer /*requestCode*/> onRequestPermissionsResult = new WeakEvent1<>();
    public static WeakEventR<PlaylistSong.Data> onRequestTrackInfo = new WeakEventR<>();

    static volatile WeakReference<MainActivity> instanceWeak = new WeakReference<>(null);

    PlayerCore playerCore;

    public int currentFragmentPage = -1;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private CustomViewPager viewPager;

    private boolean slowClosingInProgress = false;
    private Toast slowClosingToast;
    private Timer slowClosingTimer = null;
    private SystemUiHiderHoneycomb systemUiHider;

    private MenuItem searchMenuItem = null;
    private MenuItem sleepTimerIndicatorMenuItem = null;
    private MenuItem lockOrientIndicatorMenuItem = null;

    private Handler handler;
    private List<Object> listenerReferenceHolder = new LinkedList<>();

    public MainActivity() {

        EventsGlobalTextNotifier.onTextMsg.subscribeWeak(new WeakEvent1.Handler<String>() {
            @Override
            public void invoke(String textMsg) {
                Boast toast = Boast.makeText(MainActivity.this, textMsg, Toast.LENGTH_SHORT);
                toast.show();
            }
        }, listenerReferenceHolder);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                switch (msg.what) {

                    case MSG_HIDE: {

                        if (MainActivity.this.currentFragmentPage == VISUAL_PAGE_INDEX) {
                            if (AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_pref_visControlsTimeout)) {
                                MainActivity.this.showControls(false, MainActivity.this.currentFragmentPage);
                            }
                        }

                        //handler.removeMessages(MSG_HIDE);
                        //Message newMsg1 = handler.obtainMessage(MSG_HIDE);
                        //handler.sendMessageDelayed(newMsg1, 4000);

                        break;
                    }

                    case MSG_TICK10: {

                        Message newMsg = MainActivity.this.handler.obtainMessage(MSG_TICK10);
                        MainActivity.this.handler.removeMessages(MSG_TICK10);
                        MainActivity.this.handler.sendMessageDelayed(newMsg, 10000);

                        EventsGlobalApp.onUITick10.invoke();

                        handler.removeMessages(MSG_TICK10);
                        Message newMsg2 = handler.obtainMessage(MSG_TICK10);
                        handler.sendMessageDelayed(newMsg2, 10000);
                        break;
                    }

                    default:
                        break;
                }

                return false;
            }
        });




    }

    public static MainActivity getInstance() {
        return instanceWeak.get();
    }

    public static Fragment0 getFragment0Instance() {
        MainActivity mainActivity = instanceWeak.get();
        if (mainActivity == null) return null;
        return (Fragment0) mainActivity.findFragmentByPosition(0);
    }

    public static Fragment1 getFragment1Instance() {
        MainActivity mainActivity = instanceWeak.get();
        if (mainActivity == null) return null;
        return (Fragment1) mainActivity.findFragmentByPosition(1);
    }

    public static Fragment2 getFragment2Instance() {
        MainActivity mainActivity = instanceWeak.get();
        if (mainActivity == null) return null;
        return (Fragment2) mainActivity.findFragmentByPosition(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instanceWeak = new WeakReference<>(this);

        AppPermissions.isStoragePermissionGranted(this.getApplicationContext(), this);

        playerCore = PlayerCore.s();

        sectionsPagerAdapter = null;
        viewPager = null;

        PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.preferences, false);

        onCreateEarly.invoke(this.getApplicationContext());
        onCreate.invoke(this);

        PlaylistSong songOpened = null;

        //
        Intent intent = getIntent();
        if (intent != null) {
            String intentAction = intent.getAction();

            if (intentAction != null && intentAction.equals(Intent.ACTION_VIEW)) {
                Uri uri = intent.getData();
                if (uri != null)
                    songOpened = new PlaylistSong(-1, uri);
            }
        }
        //

        setContentView(R.layout.main_activity);

        onCreateView.invoke(this.findViewById(R.id.layoutMediaControls),
                this.findViewById(R.id.layoutMediaControlsNarrow),
                this.findViewById(R.id.layoutMediaControlsContainer));

        sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOnSwipeOutListener(new CustomViewPager.OnSwipeOutListener() {
            @Override
            public void onSwipeOutAtStart() {
                onViewPagerSwipeOutAtStart.invoke(getApplicationContext());
            }

            @Override
            public void onSwipeOutAtEnd() {

            }

            @Override
            public void onSwipeProgressUpdate(float val) {
                onViewPagerSwipeProgressUpdate.invoke(val, getApplicationContext());
            }
        });

        systemUiHider = null;
        systemUiHider = new SystemUiHiderHoneycomb(viewPager, SystemUiHider.FLAG_FULLSCREEN | SystemUiHider.FLAG_HIDE_NAVIGATION);
        systemUiHider.setup();
        systemUiHider.show();

        systemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
            @Override
            public void onVisibilityChange(boolean visible) {
                if (visible)
                    showControls(true, currentFragmentPage, true);
            }
        });


        if (songOpened != null) {
            viewPager.setCurrentItem(VISUAL_PAGE_INDEX);
            onViewPagerPageSelected(VISUAL_PAGE_INDEX);

            final List<PlaylistSong> songList = new ArrayList<>();
            songList.add(songOpened);

            onPreviewOpen.invoke(songList, 0);
        } else {
            int pageInx = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_mainPageIndex);
            viewPager.setCurrentItem(pageInx);
            onViewPagerPageSelected(pageInx);
        }

        //OnPageChangeListener wont fire onPageSelected on 0 position,
        // so we call onViewPagerPageSelected separately in onCreate, and set callback later here
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                onViewPagerPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Message newMsg = handler.obtainMessage(MSG_TICK10);
        handler.removeMessages(MSG_TICK10);
        handler.sendMessageDelayed(newMsg, 10000);
    }

    @Override
    protected void onStart() {
        onStart.invoke(this.getApplicationContext());
        super.onStart();
        Message newMsg = handler.obtainMessage(MSG_TICK10);
        handler.removeMessages(MSG_TICK10);
        handler.sendMessageDelayed(newMsg, 10000);
    }


    @Override
    protected void onStop() {
        onStop.invoke();
        AppPreferences.createOrGetInstance().save(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        onDestroy.invoke(new ContextData(this));
        AppPreferences.createOrGetInstance().save(this);
        setScreenLock(false);
        super.onDestroy();
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        String timeStr = AppPreferences.preferencesGetStringSafe(AppPreferences.createOrGetInstance().getPreferences(getApplicationContext()), "pref_holdexit", "0");
        int slowClosing_time = Utils.strToIntSafe(timeStr);

        if (keyCode == KeyEvent.KEYCODE_BACK && slowClosing_time > 0) {
            // a long press of the back key.
            // do our work, returning true to consume it.  by
            // returning true, the framework knows an action has
            // been performed on the long press, so will set the
            // canceled flag for the following up event.

            slowClosingInProgress = true;
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.hold_exit);
            int duration = Toast.LENGTH_SHORT;
            slowClosingToast = Toast.makeText(context, text, duration);
            slowClosingToast.show();

            slowClosingTimer = new Timer();
            slowClosingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    slowClosingInProgress = false;
                    slowClosingToast.cancel();
                    slowClosingTimer.cancel();

                    doExit();
                }
            }, slowClosing_time);

            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (slowClosingInProgress) {
                slowClosingInProgress = false;
                slowClosingTimer.cancel();
                slowClosingTimer = null;

                slowClosingToast.setText(getString(R.string.hold_exit_canceled));
                slowClosingToast.setDuration(Toast.LENGTH_SHORT);
                slowClosingToast.show();

                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            if (closeSearchView()) return true;

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        {
            sleepTimerIndicatorMenuItem = menu.findItem(R.id.action_bar_sleep_timer_indicator);
            SleepTimerConfig timerConfig = onMainUIRequestSleepTimerConfig.invoke(null);

            if (timerConfig == null)
                timerConfig = new SleepTimerConfig();

            updateSleepTimerIndicator(timerConfig.enabled, true);
        }

        {
            lockOrientIndicatorMenuItem = menu.findItem(R.id.action_bar_lock_orient_indicator);
            boolean lockOrientState = onRequestLockOrientState.invoke(false);
            updateLockOrientIndicator(lockOrientState, true);
        }

        {
            searchMenuItem = menu.findItem(R.id.action_bar_search);
            final SearchView searchView = (SearchView) searchMenuItem.getActionView();
            searchView.setIconifiedByDefault(true);
            searchView.setSubmitButtonEnabled(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String query) {
                    searchView.clearFocus();//closes keyboard
                    if (searchView.getTag() != null) {
                        final int searchIndex = (int) searchView.getTag();
                        onUISearchQueryTextChange.invoke(searchIndex, query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    if (searchView.getTag() != null) {
                        final int searchIndex = (int) searchView.getTag();
                        onUISearchQueryTextChange.invoke(searchIndex, newText);
                    }
                    return true;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    onUISearchQueryStateChange.invoke(false);
                    return false;//let it collapse itself
                }
            });

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                }
            });

            setSearchViewStyle(searchView);
            updateSearchView(true);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                break;

            case R.id.menu_close:
                doExit();
                return true;

            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_sleep_timer:
                onMainUIAction.invoke(1, new ContextData(this));
                return true;

            case R.id.menu_equalizer:
                onMainUIAction.invoke(3, new ContextData(this));
                return true;

            case R.id.action_bar_sleep_timer_indicator:
                onMainUIAction.invoke(1, new ContextData(this));
                return true;

            case R.id.menu_lock_orient:
                onMainUIAction.invoke(2, new ContextData(this));
                break;

            case R.id.action_bar_lock_orient_indicator:
                onMainUIAction.invoke(2, new ContextData(this));
                break;
            case R.id.menu_lib:
                viewPager.setCurrentItem(0);
                return true;

            case R.id.menu_queue:
                viewPager.setCurrentItem(1);
                return true;

            case R.id.menu_visual:
                viewPager.setCurrentItem(2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserInteraction() {
        //onUserInteraction isn't called when exitng form fullscreen mode
        resetVideoMaximizeTimeout(true);
        super.onUserInteraction();
    }

    public void updateSearchView(boolean eventFromOnCreateOptionsMenu) {
        ISearchEntry currentSearchEntry = onRequestCurrentSearchEntry.invoke(null);
        updateSearchView(currentSearchEntry, eventFromOnCreateOptionsMenu);
    }

    public void updateSearchView(ISearchEntry currentSearchEntry, boolean eventFromOnCreateOptionsMenu) {

        if (searchMenuItem == null) return;

        if (currentSearchEntry != null && currentSearchEntry.isEnabled()) {
            String currentQuery = currentSearchEntry.getQuery();
            SearchView searchView = (SearchView) searchMenuItem.getActionView();

            searchMenuItem.setVisible(true);
            if (currentQuery != null && !currentQuery.isEmpty()) {
                searchView.setTag(currentSearchEntry.getIndex());//active id
                searchView.setQuery(currentQuery, false);
                if (searchView.isIconified())
                    searchView.setIconified(false);//reopen if closed, eg orientation change
            } else {
                searchView.setTag(currentSearchEntry.getIndex());//active id
                searchView.setQuery("", false);
            }

            searchView.setQueryHint(currentSearchEntry.getHint());
        } else {
            SearchView searchView = (SearchView) searchMenuItem.getActionView();
            searchMenuItem.setVisible(false);
            searchView.setQueryHint("");
        }
    }

    private void setSearchViewStyle(SearchView searchView) {
        UtilsUI.setViewStyle(searchView,
                ContextCompat.getColor(this, R.color.white_alpha_1),
                ContextCompat.getColor(this, R.color.text_color_m2));
    }

    private boolean closeSearchView() {
        if (searchMenuItem == null) return false;

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        if (!searchView.isIconified()) {
            searchView.setTag(currentFragmentPage);//active id
            searchView.setQuery("", false);
            searchView.setIconified(true);
            return true;//handled
        }
        return false;
    }


    public void onViewPagerPageSelected(int position) {
        currentFragmentPage = position;
        showControls(true, currentFragmentPage);

        onViewPagerPageSelected.invoke(currentFragmentPage, this);
        onSetCurrentSearchIndex.invoke(currentFragmentPage);
    }

    private Fragment findFragmentByPosition(int position) {
        if (viewPager == null || sectionsPagerAdapter == null) return null;
        FragmentPagerAdapter fragmentPagerAdapter = sectionsPagerAdapter;
        ViewPager viewPager = this.viewPager;
        return getFragmentManager().findFragmentByTag(
                "android:switcher:" + viewPager.getId() + ":"
                        + fragmentPagerAdapter.getItemId(position));
    }

    void doExit() {
        onExit.invoke();
        this.finish();
    }

    public void doExitFromService() {
        this.finish();
    }

    public void setScreenLock(boolean b) {
        if (b)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void toggleShowControls(int pagePosition) {
        ActionBar actionBar = getActionBar();
        showControls(actionBar != null && !actionBar.isShowing(), pagePosition);
    }

    public void showControls(boolean show, int pagePosition) {
        showControls(show, pagePosition, false);
    }

    private void showControls(boolean show, int pagePosition, boolean eventFromSystemUiHider) {
        resetVideoMaximizeTimeout(show);

        ActionBar actionBar = getActionBar();

        if (show) {
            if (actionBar != null) {
                if (pagePosition == VISUAL_PAGE_INDEX) {
                    actionBar.setDisplayShowTitleEnabled(false);
                    actionBar.setDisplayShowHomeEnabled(false);
                    actionBar.show();
                } else {
                    actionBar.setDisplayShowTitleEnabled(true);
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.show();
                }
            }

            updateActionBar(onRequestTrackInfo.invoke(PlaylistSong.emptyData));

            if (!eventFromSystemUiHider && systemUiHider != null) systemUiHider.show();

        } else {
            if (actionBar != null)
                actionBar.hide();

            if (!eventFromSystemUiHider && systemUiHider != null) systemUiHider.hide();
        }

        onFullscreenChanged.invoke(!show);
    }

    public void updateActionBar(PlaylistSong.Data songData) {
        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setTitle(songData.trackName);
            if (songData.isArtistKnownOrSecondName())
                actionBar.setSubtitle(songData.artistName);
            else
                actionBar.setSubtitle("");
            actionBar.setIcon(R.drawable.placeholderart4);
        }

            ImageLoadedListener imageLoadedListener = new ImageLoadedListener() {
                Object object1;

                @Override
                public void onBitmapLoaded(Bitmap bitmap, String url00, String url0, String url1) {
                    if (bitmap != null)
                        getActionBar().setIcon(new BitmapDrawable(MainActivity.this.getResources(), bitmap));
                    else
                        getActionBar().setIcon(R.drawable.placeholderart4);
                }

                @Override
                public void setUserObject1(Object obj1) {
                    object1 = obj1;
                }
            };

            onRequestAlbumArtLarge.invoke(new AlbumArtRequest(songData.getVideoThumbDataSourceAsStr(),
                    songData.getAlbumArtPath0Str(),
                    songData.getAlbumArtPath1Str(),
                    songData.getAlbumArtGenerateStr()),
                    imageLoadedListener, 200, 200);

            //albumArtCore.loadAlbumArtLarge(

    }


    public void updateSleepTimerIndicator(boolean state, boolean evenFromOnCreateOptionsMenu) {
        if (!evenFromOnCreateOptionsMenu) {
            this.invalidateOptionsMenu();
            return;
        }

        if (sleepTimerIndicatorMenuItem != null) {
            sleepTimerIndicatorMenuItem.setVisible(state);
        }
    }

    public void updateLockOrientIndicator(boolean state, boolean evenFromOnCreateOptionsMenu) {
        if (!evenFromOnCreateOptionsMenu) {
            this.invalidateOptionsMenu();
            return;
        }

        if (lockOrientIndicatorMenuItem != null) {
            lockOrientIndicatorMenuItem.setVisible(state);
        }

    }

    public void resetVideoMaximizeTimeout(boolean resetTimer) {

        if (AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_pref_visControlsTimeout)) {
            handler.removeMessages(MSG_HIDE);
            if(resetTimer) {
                int sec = this.getResources().getInteger(R.integer.video_maximize_timeout);
                Message msg = handler.obtainMessage(MSG_HIDE);
                handler.sendMessageDelayed(msg, sec);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResult.invoke(requestCode);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Fragment0.newInstance();
                case 1:
                    return Fragment1.newInstance();
                case 2:
                    return Fragment2.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            Drawable myDrawable;
            SpannableStringBuilder sb;


            switch (position) {
                case 0:
                    myDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_library_2_s);
                    sb = new SpannableStringBuilder("   ");
                    break;
                case 1:
                    myDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_playlist4);
                    sb = new SpannableStringBuilder("   ");
                    break;
                case 2:
                    myDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_visual2);
                    sb = new SpannableStringBuilder("   ");
                    break;
                default:
                    return " ";
            }

            if (myDrawable == null)
                return "";

            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());

            ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return sb;
        }
    }
}

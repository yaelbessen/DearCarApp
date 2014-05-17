package com.example.dearcar;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Yael on 20/06/13.
 */
public class MusicScreen {

    public static class MusicFragment extends Fragment{
        AbsoluteLayout mainScreen;
        RelativeLayout selectionScreen;
        AbsoluteLayout endScreen;

        ViewGroup lastScreen = mainScreen;
        Song[] currentSongs = null;
        int songIndex = 0;

        boolean isInPlaylist = false;
        Bitmap currentAlbumArt;

        final OnOffSwitch playPauseSwitch = new OnOffSwitch();
        final OnOffSwitch shuffleSwitch = new OnOffSwitch();
        final OnOffSwitch repeatSwitch = new OnOffSwitch();
        private ImageView albumArtBig;
        private TextView songTitle;
        private TextView songArtist;

        HashMap<String, String> song2artist = new HashMap<String, String>();

        VoiceSearchDialog musicDialog;
        boolean isOkPressed =false;
        ListView playlistList;
        TextView selectionHeader;
        ListView artistsList;

        AssetFileDescriptor afd;
        int builtInCurrentSong =0;
        String[] builtInSongs ={"guns.mp3","red.mp3","smash.mp3"};
        private ImageButton playBtn;

        public MusicFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.music_screen, container, false);

            mainScreen = (AbsoluteLayout) rootView.findViewById(R.id.music_start);
            selectionScreen = (RelativeLayout) rootView.findViewById(R.id.music_01);
            endScreen = (AbsoluteLayout) rootView.findViewById(R.id.music_end);

            /* Setting up the Main Screen*/
            Button artistsBtn = (Button) rootView.findViewById(R.id.music_artists);
            Button playlistsBtn = (Button) rootView.findViewById(R.id.music_playlists);
            RelativeLayout allSongs = (RelativeLayout) rootView.findViewById(R.id.all_songs_layout);
            final ImageButton back01 = (ImageButton) rootView.findViewById(R.id.music_back_01);
            final LinearLayout bg = (LinearLayout) rootView.findViewById(R.id.music_selector);
            final ImageView mainCover = (ImageView) rootView.findViewById(R.id.music_album_art);
            final TextView allSongsTitle = (TextView) rootView.findViewById(R.id.all_songs);
            final Context ctx = this.getActivity();

            /* Setting up the Selection Screen */
            selectionHeader = (TextView) rootView.findViewById(R.id.selection_header);

            artistsList = (ListView) rootView.findViewById(R.id.artist_list);
            artistsList.setOnItemClickListener(new ArtistClickListener());
            buildArtistList(artistsList);
            artistsList.setVisibility(View.GONE);

            playlistList = (ListView) rootView.findViewById(R.id.playlist_list);
            playlistList.setOnItemClickListener(new PlaylistClickListener());
            buildPlaylists(playlistList);
            playlistList.setVisibility(View.GONE);

            musicDialog = new VoiceSearchDialog(getActivity(), 4, 3)
            .setOnOK(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] allSongsName = getResources().getStringArray(R.array.Artists_songs);
                    String[] songsName=allSongsName[2].split(";");
                    if(selectionHeader.getText().toString().equals("Playlists")){
                        allSongsName = getResources().getStringArray(R.array.Playlist_songs);
                        songsName=allSongsName[6].split(";");
                    }

                    songIndex = 0;
                    Song[] songs = new Song[songsName.length];
                    for(int i=0; i<songs.length; i++){
                        songs[i] = new Song(songsName[i],song2artist.get(songsName[i]));
                    }
                    currentSongs = songs;
                    lastScreen = selectionScreen;
                    isInPlaylist = (playlistList.getVisibility()==View.VISIBLE);
                    builtInCurrentSong =0;
                    Utils.playMediaFile(ctx.getAssets(), builtInSongs[builtInCurrentSong]);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startPlaying();
                        }
                    });
                }
            }).setOkName("Play")
            .setOnRetry(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetSearchDialog();
                }
            });

            ImageButton search = (ImageButton) rootView.findViewById(R.id.artists_search);
            search.setOnTouchListener(new MicOnClick.SearchListener(search, new MicOnClick.MicAction() {
                @Override
                public void onMicPressed() {
                    resetSearchDialog();
                    musicDialog.show();
                }
            }));

            /* Setting up the Play Screen */
            currentAlbumArt = BitmapFactory.decodeResource(getResources(),R.drawable.music_default);
            albumArtBig = (ImageView) rootView.findViewById(R.id.music_album_art_big);
            playBtn = (ImageButton) rootView.findViewById(R.id.play_btn);
            final ImageView     prevNextBG = (ImageView) rootView.findViewById(R.id.prev_next_btn);
            final Button        nextBtn = (Button) rootView.findViewById(R.id.next_btn);
            final Button        prevBtn = (Button) rootView.findViewById(R.id.prev_btn);
            final ImageView     shuffle = (ImageView) rootView.findViewById(R.id.shuffle_btn);
            final ImageView     repeat = (ImageView) rootView.findViewById(R.id.repeat_btn);
            final ImageButton   back02 = (ImageButton) rootView.findViewById(R.id.music_back02);

            final Bitmap next = BitmapFactory.decodeResource(getResources(), R.drawable.prev_next_n);
            final Bitmap prev = BitmapFactory.decodeResource(getResources(), R.drawable.prev_next_p);
            final Bitmap prevNext = BitmapFactory.decodeResource(getResources(), R.drawable.prev_next);

            songTitle = (TextView) rootView.findViewById(R.id.song_name);
            songArtist = (TextView) rootView.findViewById(R.id.song_artist);

            Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/rondalo.ttf");
            Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/rondalo.ttf");
            songTitle.setTypeface(bold);
            songArtist.setTypeface(regular);
            selectionHeader.setTypeface(bold);

            selectionScreen.setVisibility(View.GONE);
            endScreen.setVisibility(View.GONE);

            /** Setting up the button's actions **/
            artistsBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            bg.setBackgroundResource(R.drawable.music_artists_left);
                            break;
                        case MotionEvent.ACTION_UP:
                            bg.setBackgroundResource(R.drawable.music_artists);
                            selectionHeader.setText(R.string.artists_header);
                            artistsList.setVisibility(View.VISIBLE);
                            selectionScreen.setVisibility(View.VISIBLE);
                            mainScreen.setVisibility(View.GONE);
                    }
                    return true;
                }
            });

            playlistsBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            bg.setBackgroundResource(R.drawable.music_artists_right);
                            break;
                        case MotionEvent.ACTION_UP:
                            selectionHeader.setText(R.string.playlists_header);
                            bg.setBackgroundResource(R.drawable.music_artists);
                            playlistList.setVisibility(View.VISIBLE);
                            selectionScreen.setVisibility(View.VISIBLE);
                            mainScreen.setVisibility(View.GONE);
                    }
                    return true;
                }
            });

            allSongs.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            if (currentSongs == null) {
                                currentSongs = new Song[song2artist.keySet().size()];
                                int i = 0;
                                for (String s : song2artist.keySet()) {
                                    currentSongs[i++] = new Song(s, song2artist.get(s));
                                }
                            } else {
                                allSongsTitle.setText("Playing now");
                            }
                            lastScreen = mainScreen;
                            isInPlaylist = true;
                            Utils.playMediaFile(view.getContext().getAssets(),
                                    builtInSongs[builtInCurrentSong]);
                            startPlaying();
                    }
                    return true;
                }
            });

            back01.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Utils.vibrate(ctx, 200);
                            back01.setImageResource(R.drawable.music_back_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            back01.setImageResource(R.drawable.music_back);
                            mainCover.setImageBitmap(currentAlbumArt);
                            mainScreen.setVisibility(View.VISIBLE);
                            selectionScreen.setVisibility(View.GONE);
                            playlistList.setVisibility(View.GONE);
                            artistsList.setVisibility(View.GONE);
                    }
                    return true;
                }
            });

            playPauseSwitch.state();
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.vibrate(ctx, 200);
                    playBtn.setImageResource(playPauseSwitch.state() ? R.drawable.pause : R.drawable.play);
                    if (!playPauseSwitch.isOn) {
                        MainActivity.mainPlayer.start();
                    } else {
                        MainActivity.mainPlayer.pause();
                    }
                }
            });

            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.vibrate(ctx, 100);
                    shuffle.setImageResource(shuffleSwitch.state() ? R.drawable.shuffle_pressed : R.drawable.shuffle);
                }
            });

            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.vibrate(ctx, 100);
                    repeat.setImageResource(repeatSwitch.state() ? R.drawable.repeat_pressed : R.drawable.repeat);
                }
            });

            nextBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            Utils.vibrate(ctx, 100);
                            prevNextBG.setImageBitmap(next);
                            break;
                        case MotionEvent.ACTION_UP:
                            prevNextBG.setImageBitmap(prevNext);
                            if(currentSongs.length <= 0){
                                Log.i("Music","Error: Empty song list.");
                                return false;
                            }
                            builtInCurrentSong =(builtInCurrentSong +1)%3;
                            Utils.playMediaFile(ctx.getAssets(), builtInSongs[builtInCurrentSong]);
                            playPauseSwitch.isOn=true;
                            playBtn.setImageResource(playPauseSwitch.state() ? R.drawable.pause : R.drawable.play);
                            Song current = getNext();
                            songTitle.setText(current.name);
                            songArtist.setText(current.artist);
                            if(isInPlaylist){
                                currentAlbumArt = getAlbumArtOf(current.artist);
                                albumArtBig.setImageBitmap(currentAlbumArt);
                            }
                    }
                    return true;
                }
            });

            prevBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            Utils.vibrate(ctx, 100);
                            prevNextBG.setImageBitmap(prev);
                            break;
                        case MotionEvent.ACTION_UP:
                            prevNextBG.setImageBitmap(prevNext);
                            if(currentSongs.length <= 0){
                                Log.i("Music","Error: Empty song list.");
                                return false;
                            }
                            builtInCurrentSong--;
                            if(builtInCurrentSong <0){
                                builtInCurrentSong =2;
                            }
                            Utils.playMediaFile(ctx.getAssets(), builtInSongs[builtInCurrentSong]);
                            playPauseSwitch.isOn=true;
                            playBtn.setImageResource(playPauseSwitch.state() ? R.drawable.pause : R.drawable.play);
                            Song current = getPrev();
                            songTitle.setText(current.name);
                            songArtist.setText(current.artist);
                            if(isInPlaylist){
                                currentAlbumArt = getAlbumArtOf(current.artist);
                                albumArtBig.setImageBitmap(currentAlbumArt);
                            }
                    }
                    return true;
                }
            });

            back02.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Utils.vibrate(ctx, 200);
                            back02.setImageResource(R.drawable.music_back_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            back02.setImageResource(R.drawable.music_back);
                            mainCover.setImageBitmap(currentAlbumArt);
                            lastScreen.setVisibility(View.VISIBLE);
                            endScreen.setVisibility(View.GONE);
                    }
                    return true;
                }
            });

            return rootView;
        }

        void resetSearchDialog() {
            if(artistsList.getVisibility()== View.VISIBLE){
                musicDialog.setSearchMessage("Say an artist's name...");
                musicDialog.setSound("artistfound.mp3");
                musicDialog.setResult("Pink Floyd", R.drawable.artist3);
            }
            else if(playlistList.getVisibility()==View.VISIBLE){
                musicDialog.setSearchMessage("Say a playlist name...");
                musicDialog.setSound("playlistfound.mp3");
                musicDialog.setResult("Top 100", R.drawable.playlist);
            }
        }
        private Song getNext(){
            if(++songIndex >= currentSongs.length){
                songIndex = 0;
            }
            return currentSongs[songIndex];
        }

        private Song getPrev(){
            if(--songIndex < 0){
                songIndex = currentSongs.length -1;
            }
            return currentSongs[songIndex];
        }

        private static class OnOffSwitch{
            boolean isOn = true;
            public boolean state(){
                boolean res = isOn;
                isOn = !isOn;
                return res;
            }
        }

        private static class Song{
            final String name;
            final String artist;

            private Song(String name, String artist) {
                this.name = name;
                this.artist = artist;
            }
        }

        private void buildArtistList(ListView artistsList) {
            // storing string resources into Array
            String[] names = getResources().getStringArray(R.array.Artists_name);
            int[] pics = Utils.convertArray(getResources().obtainTypedArray(R.array.Artists_pics));
            String[] songs = getResources().getStringArray(R.array.Artists_songs);
            Entry[] artists = new Entry[names.length];
            for(int i=0; i<artists.length; i++){
                String[] thisSongs = songs[i].split(";");
                for(String s : thisSongs){
                    song2artist.put(s,names[i]);
                }
                artists[i] = new Entry(names[i],pics[i],thisSongs);
            }
            // Binding resources Array to ListAdapter
            artistsList.setAdapter(new MusicListAdapter(getActivity(), artists));
        }

        private Bitmap getAlbumArtOf(String artist){
            String str = artist;
            if (!artist.matches("[A-Za-z ]*")){
                str = "hadagnahash";
            }
            return BitmapFactory.decodeResource(getResources(),
                    getResources().getIdentifier("albumart_"+str.replaceAll(" ","").toLowerCase(),
                    "drawable", getActivity().getPackageName()));
        }

        private void buildPlaylists(ListView artistsList) {
            // storing string resources into Array
            String[] names = getResources().getStringArray(R.array.Playlist_name);
            String[] songs = getResources().getStringArray(R.array.Playlist_songs);
            Entry[] playlist = new Entry[names.length];

            playlist[0] = new Entry(names[0], R.drawable.playlist, song2artist.keySet().toArray(new String[0]));
            for(int i=1; i<playlist.length; i++){
                playlist[i] = new Entry(names[i],R.drawable.playlist,songs[i].split(";"));
            }
            // Binding resources Array to ListAdapter
            artistsList.setAdapter(new MusicListAdapter(getActivity(), playlist));
        }

        private void startPlaying(){
            currentAlbumArt = getAlbumArtOf(currentSongs[songIndex].artist);
            lastScreen.setVisibility(View.GONE);
            playPauseSwitch.isOn=true;
            playBtn.setImageResource(playPauseSwitch.state() ? R.drawable.pause : R.drawable.play);
            songTitle.setText(currentSongs[songIndex].name);
            songArtist.setText(currentSongs[songIndex].artist);
            albumArtBig.setImageBitmap(currentAlbumArt);
            endScreen.setVisibility(View.VISIBLE);
        }

        class ArtistClickListener implements AdapterView.OnItemClickListener{
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                songIndex = 0;
                EntryHolder holder = (EntryHolder)view.getTag();
                Song[] songs = new Song[holder.songs.length];
                TextView artistsName = (TextView) view.findViewById(R.id.artist_item);
                for(int i=0; i<songs.length; i++){
                    songs[i] = new Song(holder.songs[i],artistsName.getText().toString());
                }
                currentSongs = songs;
                lastScreen = selectionScreen;
                isInPlaylist = false;


                builtInCurrentSong =0;
                Utils.playMediaFile(view.getContext().getAssets(), builtInSongs[builtInCurrentSong]);

                startPlaying();
            }
        }

        class PlaylistClickListener implements AdapterView.OnItemClickListener{
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                songIndex = 0;
                EntryHolder holder = (EntryHolder)view.getTag();
                Song[] songs = new Song[holder.songs.length];
                for(int i=0; i<songs.length; i++){
                    songs[i] = new Song(holder.songs[i],song2artist.get(holder.songs[i]));
                }
                currentSongs = songs;
                lastScreen = selectionScreen;
                isInPlaylist = true;
                builtInCurrentSong =0;
                Utils.playMediaFile(view.getContext().getAssets(), builtInSongs[builtInCurrentSong]);
                startPlaying();
            }
        }
    }

    public static class MusicListAdapter extends ArrayAdapter<Entry>{
        Context context;
        int layoutResourceId;
        Entry data[] = null;

        public MusicListAdapter(Context context, Entry[] data) {
            super(context, R.layout.music_item_line, data);
            this.layoutResourceId = R.layout.music_item_line;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            EntryHolder holder;

            if(row == null){
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new EntryHolder();
                holder.albumArt = (ImageView)row.findViewById(R.id.album_art_item);
                holder.name = (TextView)row.findViewById(R.id.artist_item);
                holder.layout = (LinearLayout)row.findViewById(R.id.artist_item_layout);

                row.setTag(holder);
            }
            else{
                holder = (EntryHolder)row.getTag();
            }

            Entry entry = data[position];
            holder.name.setText(entry.name);
            holder.albumArt.setImageBitmap(Utils.roundImageCornersBorder(context.getResources(),
                    entry.albumArtId, 15, Color.BLACK, 2));
            if(position % 2 != 0){
                holder.layout.setBackgroundResource(R.drawable.bg_row2);
            }else {
                holder.layout.setBackgroundResource(R.drawable.bg_row);
            }
            holder.songs = entry.songs;

            return row;
        }
    }

    static class EntryHolder {
        ImageView albumArt;
        TextView name;
        LinearLayout layout;
        String[] songs;
    }

    static class Entry {
        String name;
        int albumArtId;
        String[] songs;

        Entry(String name, int albumArtId, String[] songs) {
            this.name = name;
            this.albumArtId = albumArtId;
            this.songs = songs;
        }
    }
}

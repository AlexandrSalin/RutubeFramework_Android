package ru.rutube.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ru.rutube.RutubePlayer;
import ru.rutube.interfaces.IRutubePlayer;
import ru.rutube.interfaces.IRutubePlayerPlugin;
import ru.rutube.models.RutubePlayerOperation;
import ru.rutube.myapplication.endscreen.EndscreenPlugin;


public class MainActivity extends Activity {

    private Hello plugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        RutubePlayer player = (RutubePlayer)fragmentManager.findFragmentById(R.id.fragment);

        plugin = new Hello();
        player.attachPlugin(plugin);
        player.attachPlugin(new EndscreenPlugin());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            plugin.runFailed();
            return true;
        } else if( id == R.id.action_more ) {
            plugin.runSuccess();
            return true;
        } else {
            plugin.runSmall();
            return true;
        }


    }

    protected class Hello implements IRutubePlayerPlugin {

        private IRutubePlayer player;

        @Override
        public void activate(IRutubePlayer player) {
            this.player = player;
        }

        @Override
        public void deactivate(IRutubePlayer player) {

        }

        @Override
        public boolean hasView() {
            return false;
        }

        @Override
        public View getView(Context context) {
            return null;
        }

        @Override
        public boolean requestOperation( RutubePlayerOperation operation ) {return true;}

        public void runFailed() {
            player.changeVideo("0e55cada5e97feb53a1d81616d7e74fc1", null);
            player.play(this);
        }

        public void runSuccess() {
            player.changeVideo("6ecdd81a078bf0c2f7ebdeef997dd496", null);
            player.play(this);
        }

        public void runSmall() {
            player.changeVideo("c3930488dfc3ee8b433a1c4793e661a5", null);
            player.play(this);
        }



    }

}
